package com.sw.aot.api;

public class AotLoader {

    private static TaskManager dispatcher() {
        return TaskManager.getInstance();
    }

    public static void addRouter(AotRouterInterface routerInterface) {
        dispatcher().addRouter(routerInterface);
    }

    public static boolean isHotTask(String taskKey) {
        return dispatcher().isHotTask(taskKey);
    }

    public static String produce(String task) {
        return dispatcher().addTask(task);
    }

    public static void consume(String task, ResultListener listener) {
        dispatcher().consumeTask(task, listener);
    }

}
