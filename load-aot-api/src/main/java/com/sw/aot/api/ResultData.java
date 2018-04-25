package com.sw.aot.api;

public class ResultData<T> {

    private static final int INIT_CODE = -99999;

    private int code;

    private T data;

    private String msg;

    private String taskKey;

    public ResultData() {
        code = INIT_CODE;
    }

    private ResultListener<T> resultListener;

    private UnRegisterCallback unRegisterCallback;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    void setTaskKey(String taskKey, UnRegisterCallback unRegisterCallback) {
        this.taskKey = taskKey;
        this.unRegisterCallback = unRegisterCallback;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setResultListener(ResultListener<T> listener) {
        resultListener = listener;
    }

    public synchronized void flush() {
        if (resultListener != null && code != INIT_CODE) {
            resultListener.onDataChange(this);
            if (unRegisterCallback != null) {
                unRegisterCallback.unRegister(taskKey);
            }
        }
    }

    public interface UnRegisterCallback {
        void unRegister(String taskKey);
    }
}
