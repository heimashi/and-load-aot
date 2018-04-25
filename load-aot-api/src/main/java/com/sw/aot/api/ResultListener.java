package com.sw.aot.api;


public interface ResultListener<T> {

    void onDataChange(ResultData<T> data);

}
