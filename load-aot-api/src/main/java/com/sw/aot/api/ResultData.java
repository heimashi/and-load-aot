package com.sw.aot.api;


public class ResultData<T> {

    private int code;

    private T data;

    private String msg;

    public ResultData(int code) {
        this.code = code;
    }

    public ResultData(int code, T data){
        this.code = code;
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
}
