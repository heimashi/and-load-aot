package com.sw.aot.api;

import android.util.Log;

class AotLog {

    private static boolean enableLog = false;

    static void setEnableLog(boolean enable) {
        enableLog = enable;
    }

    static void d(String msg) {
        if (enableLog) {
            Log.i("AOT_LOG", "_____________________\n\n" + msg + "\n_____________________");
        }
    }

    static void printRouter(AotRouterInterface routerInterface) {
        if (enableLog && routerInterface != null) {
            Log.i("AOT_LOG", "_____________________\n\nAOT ROUTER INFO:");
            for (String key : routerInterface.getMethodMap().keySet()) {
                String method = routerInterface.getMethodMap().get(key);
                Log.i("AOT_LOG", key + "   " + method);
            }
            Log.i("AOT_LOG", "_____________________");
        }
    }

    static void dumpTaskList() {
        if (enableLog) {
            Log.i("AOT_LOG", "_____________________\n\nDUMP TASK LIST:");
            Log.i("AOT_LOG", "SIZE:" + TaskManager.getInstance().taskResult.size() + " " + TaskManager.getInstance()
                    .validTaskMap.size());
            for (String taskKey : TaskManager.getInstance().taskResult.keySet()) {
                ResultData resultData = TaskManager.getInstance().taskResult.get(taskKey);
                Log.i("AOT_LOG", taskKey + "   " + resultData);

            }
            Log.i("AOT_LOG", "_____________________");
        }

    }
}
