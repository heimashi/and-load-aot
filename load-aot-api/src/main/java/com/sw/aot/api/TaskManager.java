package com.sw.aot.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class TaskManager {

    private final AtomicInteger taskIdGenerator = new AtomicInteger(0);

    private final ConcurrentHashMap<Integer, String> taskMap = new ConcurrentHashMap<>();

    private List<AotRouterInterface> list = new ArrayList<>();

    private TaskManager taskManager;

    private static class SingletonHolder {
        private static TaskManager instance = new TaskManager();
    }

    static TaskManager getInstance() {
        return SingletonHolder.instance;
    }

    int addTask(String taskRouter) {
        int id = taskIdGenerator.incrementAndGet();
        taskMap.put(id, taskRouter);

        return id;
    }

    void add(AotRouterInterface aotRouterInterface){
        list.add(aotRouterInterface);
    }

    private void invoke(String taskRouter){

    }
}
