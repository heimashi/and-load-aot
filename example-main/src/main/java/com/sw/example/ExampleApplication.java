package com.sw.example;

import com.sw.aot.api.AotLoad;
import com.sw.example.main.ExampleAotIndex;

import android.app.Application;

public class ExampleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        initAotDemo();
    }

    private void initAotDemo(){
        AotLoad.add(new ExampleAotIndex());
    }

}
