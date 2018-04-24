package com.sw.aot.api;


public class AotLoad {


    public static void add(AotRouterInterface routerInterface){

    }

    public static int load(String task){
        return TaskManager.getInstance().addTask(task);
    }

}
