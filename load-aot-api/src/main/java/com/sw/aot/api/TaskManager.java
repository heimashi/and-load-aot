package com.sw.aot.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class TaskManager {

    private final AtomicInteger taskIdGenerator = new AtomicInteger(0);

    private List<AotRouterInterface> routerList = new ArrayList<>();

    final ConcurrentHashMap<String, AotTask> validTaskMap = new ConcurrentHashMap<>();

    final ConcurrentHashMap<String, ResultData> taskResult = new ConcurrentHashMap<>();

    private TaskManager() {
    }

    private static class SingletonHolder {
        private static TaskManager instance = new TaskManager();
    }

    static TaskManager getInstance() {
        return SingletonHolder.instance;
    }

    String produceTask(String taskRouter) {
        int id = taskIdGenerator.incrementAndGet();
        AotTask aotTask = new AotTask(id, taskRouter);
        invokeTask(aotTask);
        AotLog.dumpTaskList();
        return aotTask.getTaskKey();
    }

    void addRouter(AotRouterInterface aotRouterInterface) {
        routerList.add(aotRouterInterface);
    }

    boolean isValidTask(String taskKey) {
        return validTaskMap.containsKey(taskKey);
    }

    private void invokeTask(AotTask aotTask) {
        for (AotRouterInterface routerInterface : routerList) {
            if (routerInterface.getMethodMap().containsKey(aotTask.router)) {
                String methodName = routerInterface.getMethodMap().get(aotTask.router);
                Class<?> clazz = routerInterface.getClassMap().get(aotTask.router);
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    String modifier = Modifier.toString(method.getModifiers());
                    validTaskMap.put(aotTask.getTaskKey(), aotTask);
                    AotLog.d("PRODUCE: " + aotTask.getTaskKey());
                    ResultData resultData;
                    if (modifier.contains("static")) {
                        resultData = (ResultData) method.invoke(null);
                    } else {
                        resultData = (ResultData) method.invoke(clazz.newInstance());
                    }
                    resultData.setTaskKey(aotTask.getTaskKey(), new ResultData.UnRegisterCallback() {
                        @Override
                        public void unRegister(String taskKey) {
                            AotLog.d("CONSUME: " + taskKey);
                            taskResult.remove(taskKey);
                            validTaskMap.remove(taskKey);
                            AotLog.dumpTaskList();
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

    void consumeTask(String taskKey, ResultListener listener) {
        ResultData resultData = taskResult.get(taskKey);
        if (resultData != null) {
            resultData.setResultListener(listener);
            resultData.flush();
        }
    }

}

