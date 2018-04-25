package com.sw.aot.api;

public class AotLoader {

    private static TaskManager handler() {
        return TaskManager.getInstance();
    }

    public static void enableLog(boolean enable) {
        AotLog.setEnableLog(enable);
    }

    public static void addRouter(AotRouterInterface routerInterface) {
        handler().addRouter(routerInterface);
        AotLog.printRouter(routerInterface);
    }

    public static boolean isValidTask(String taskKey) {
        return handler().isValidTask(taskKey);
    }

    public static String produce(String taskName) {
        return handler().produceTask(taskName);
    }

    public static void consume(String taskKey, ResultListener listener) {
        handler().consumeTask(taskKey, listener);
    }

}
