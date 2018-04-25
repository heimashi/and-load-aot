package com.sw.aot.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class TaskManager {

    private final AtomicInteger taskIdGenerator = new AtomicInteger(0);

    private final ConcurrentHashMap<String, String> taskMap = new ConcurrentHashMap<>();

    private final HashSet<String> hotTask = new HashSet<>();

    private List<AotRouterInterface> routerList = new ArrayList<>();

    private final ConcurrentHashMap<String, ResultData> taskResult = new ConcurrentHashMap<>();

    private TaskManager taskManager;

    private static class SingletonHolder {
        private static TaskManager instance = new TaskManager();
    }

    static TaskManager getInstance() {
        return SingletonHolder.instance;
    }

    String addTask(String taskRouter) {
//        int id = taskIdGenerator.incrementAndGet();
//        taskMap.put(id, taskRouter);
        invokeTask(taskRouter);
        return taskRouter;
    }

    void addRouter(AotRouterInterface aotRouterInterface){
        routerList.add(aotRouterInterface);
    }

    boolean isHotTask(String taskKey){
        return hotTask.contains(taskKey);
    }

    private void invokeTask(String taskRouter){
        for(AotRouterInterface routerInterface : routerList){
            if(routerInterface.getMethodMap().containsKey(taskRouter)){
                String methodName = routerInterface.getMethodMap().get(taskRouter);
                Class<?> clazz = routerInterface.getClassMap().get(taskRouter);
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    String modifier = Modifier.toString(method.getModifiers());
                    hotTask.add(taskRouter);
                    ResultData resultData;
                    if(modifier.contains("static")){
                        resultData = (ResultData) method.invoke(null);
                    }else {
                        resultData = (ResultData) method.invoke(clazz.newInstance());
                    }
                    taskResult.put(taskRouter, resultData);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    void consumeTask(String taskRouter, ResultListener listener){
        ResultData resultData = taskResult.get(taskRouter);
        if(resultData!=null){
            resultData.setResultListener(listener);
            resultData.flush();
        }
    }

}

