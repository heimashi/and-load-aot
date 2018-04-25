package com.sw.aot.api;


public class ResultData<T> {

    private static final int INIT_CODE = -99999;

    private int code;

    private T data;

    private String msg;

    public ResultData() {
        code = INIT_CODE;
    }

    private ResultListener<T> resultListener;

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

    public void setResultListener(ResultListener<T> listener) {
        resultListener = listener;
    }

    public void flush() {
        if (resultListener != null && code != INIT_CODE) {
            resultListener.onDataChange(this);
        }
    }
}
