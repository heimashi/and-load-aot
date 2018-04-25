package com.sw.aot.compiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.sw.aot.annotation.AOTLoad;

@AutoService(Processor.class)
public class AOTAnnotationProcessor extends AbstractProcessor {

    private static final String OPTION_AOT_INDEX = "AOT_INDEX";
    private Messager messager;
    private final HashMap<String, String> routerMethodMap = new HashMap<>();
    private final HashMap<String, String> routerDescMap = new HashMap<>();
    private final HashMap<String, String> routerClassMap = new HashMap<>();
    private final HashMap<String, String> routerConstMap = new HashMap<>();
    private String aotIndex;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        aotIndex = processingEnv.getOptions().get(OPTION_AOT_INDEX);
        if (aotIndex == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "AOTLoad::No option " + OPTION_AOT_INDEX +
                    " passed to annotation processor");
            throw new RuntimeException("AOTLoad::Please set AOT_INDEX in build.gradle");
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "AOTLoad::aotIndex is " + aotIndex);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(AOTLoad.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations.isEmpty()) {
            return false;
        }
        collectMethodRouters(roundEnvironment);
        if (!routerMethodMap.isEmpty()) {
            createInfoIndexFile();
        } else {
            messager.printMessage(Diagnostic.Kind.WARNING, "AOTLoad::No @AOTLoad annotations found");
        }
        return false;
    }

    private void collectMethodRouters(RoundEnvironment env) {
        Set<? extends Element> routeElements = env.getElementsAnnotatedWith(AOTLoad.class);
        if (routeElements == null || routeElements.isEmpty()) {
            return;
        }
        for (Element element : routeElements) {
            if (element instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) element;
                if (checkHasNoErrors(method, messager)) {
                    TypeElement classElement = (TypeElement) method.getEnclosingElement();
                    String fullClassName = classElement.getQualifiedName().toString();
                    String methodName = method.getSimpleName().toString();
                    AOTLoad aotLoad = element.getAnnotation(AOTLoad.class);
                    String router = aotLoad.router();
                    String desc = aotLoad.desc();
                    if (!"".equals(desc)) {
                        routerDescMap.put(router, desc);
                    }
                    if (routerMethodMap.containsKey(router)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "AOTLoad::Duplicate method router: " + router);
                        throw new RuntimeException("AOTLoad::Please fix duplicate method router: " + router);
                    }
                    messager.printMessage(Diagnostic.Kind.NOTE, "AOTLoad::add router alias:" + router + " class:" +
                            fullClassName);
                    routerMethodMap.put(router, methodName);
                    routerClassMap.put(router, fullClassName);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@AOTLoad is only valid for methods", element);
            }

        }
    }

    private boolean checkHasNoErrors(ExecutableElement element, Messager messager) {

        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "AOTLoad method must be public", element);
            return false;
        }

        List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
        if (parameters.size() != 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "AOTLoad method must have exactly 0 parameter", element);
            return false;
        }
        return true;
    }

    private void createInfoIndexFile() {
        BufferedWriter writer = null;
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(aotIndex);
            int period = aotIndex.lastIndexOf('.');
            String myPackage = period > 0 ? aotIndex.substring(0, period) : null;
            String clazz = aotIndex.substring(period + 1);
            writer = new BufferedWriter(sourceFile.openWriter());
            if (myPackage != null) {
                writer.write("package " + myPackage + ";\n\n");
            }
            writer.write("import java.util.HashMap;\n");
            writer.write("import com.sw.aot.api.AotRouterInterface;\n\n");
            writer.write("/** This class is generated by AOTLoad, do not edit. */\n");
            writer.write("public class " + clazz + " implements AotRouterInterface{\n\n");
            writeConstLines(writer);
            writer.write("\n    private final HashMap<String, String> routerMethodMap = new HashMap<String, String>();"
                    + "\n");
            writer.write("    private final HashMap<String, Class<?>> routerClassMap = new HashMap<String, Class<?>>"
                    + "();\n");
            writer.write("\n    public " + clazz + "() {\n");
            writeIndexLines(writer);
            writer.write("    }\n\n");
            writer.write("    @Override\n"
                    + "    public HashMap<String, String> getMethodMap() {\n"
                    + "        return routerMethodMap;\n"
                    + "    }\n\n");
            writer.write("    @Override\n"
                    + "    public HashMap<String, Class<?>> getClassMap() {\n"
                    + "        return routerClassMap;\n"
                    + "    }");
            writer.write("\n\n}\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not write source for " + aotIndex, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //Silent
                }
            }
        }
    }

    private void writeConstLines(BufferedWriter writer) throws IOException {
        for (String router : routerMethodMap.keySet()) {
            String[] strings = router.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                sb.append(strings[i].toUpperCase());
                if (i > 0 && i < strings.length - 1) {
                    sb.append("_");
                }
            }
            if (routerDescMap.containsKey(router)) {
                writer.write("    /* " + routerDescMap.get(router) + " */\n");
            }
            routerConstMap.put(router, sb.toString());
            writer.write("    public static String " + sb.toString() + " = \"" + router + "\";\n");
        }
    }

    private void writeIndexLines(BufferedWriter writer) throws IOException {
        for (String router : routerMethodMap.keySet()) {
            String method = routerMethodMap.get(router);
            String fullClass = routerClassMap.get(router);
            writer.write("         routerMethodMap.put(" + routerConstMap.get(router) + ", \"" + method + "\");\n");
            writer.write("         routerClassMap.put(" + routerConstMap.get(router) + ", " + fullClass + ".class );\n");
        }
    }
}
