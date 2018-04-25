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

    //private final ConcurrentHashMap<String, String> taskMap = new ConcurrentHashMap<>();

    private final HashSet<AotTask> hotTask = new HashSet<>();

    private List<AotRouterInterface> routerList = new ArrayList<>();

    private final ConcurrentHashMap<String, ResultData> taskResult = new ConcurrentHashMap<>();

    private TaskManager(){}

    private static class SingletonHolder {
        private static TaskManager instance = new TaskManager();
    }

    static TaskManager getInstance() {
        return SingletonHolder.instance;
    }

    String addTask(String taskRouter) {
        int id = taskIdGenerator.incrementAndGet();
        AotTask aotTask = new AotTask(id, taskRouter);
//        taskMap.put(id, taskRouter);
        invokeTask(aotTask);
        return aotTask.getTaskKey();
    }

    void addRouter(AotRouterInterface aotRouterInterface){
        routerList.add(aotRouterInterface);
    }

    boolean isHotTask(String taskKey){
        return hotTask.contains(taskKey);
    }

    private void invokeTask(AotTask aotTask){
        for(AotRouterInterface routerInterface : routerList){
            if(routerInterface.getMethodMap().containsKey(aotTask.router)){
                String methodName = routerInterface.getMethodMap().get(aotTask.router);
                Class<?> clazz = routerInterface.getClassMap().get(aotTask.router);
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    String modifier = Modifier.toString(method.getModifiers());
                    hotTask.add(aotTask);
                    ResultData resultData;
                    if(modifier.contains("static")){
                        resultData = (ResultData) method.invoke(null);
                    }else {
                        resultData = (ResultData) method.invoke(clazz.newInstance());
                    }
                    resultData.setTaskKey(aotTask.getTaskKey(), new ResultData.UnRegisterCallback() {
                        @Override
                        public void unRegister(String taskKey) {
                            taskResult.remove(taskKey);
                        }
                    });
                    taskResult.put(aotTask.getTaskKey(), resultData);
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

    void consumeTask(String taskKey, ResultListener listener){
        ResultData resultData = taskResult.get(taskKey);
        if(resultData!=null){
            resultData.setResultListener(listener);
            resultData.flush();
        }
    }

}

