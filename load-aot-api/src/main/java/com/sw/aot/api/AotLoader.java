package com.sw.aot.api;


public class AotLoader {


    public static boolean isHotTask(String taskKey) {
        return TaskManager.getInstance().isHotTask(taskKey);
    }

    public static void add(AotRouterInterface routerInterface) {
        TaskManager.getInstance().addRouter(routerInterface);
    }

    public static String produce(String task) {
        return TaskManager.getInstance().addTask(task);
    }

    public static void consume(String task, ResultListener listener) {
        TaskManager.getInstance().consumeTask(task, listener);
    }

}
