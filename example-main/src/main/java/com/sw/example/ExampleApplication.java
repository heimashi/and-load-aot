package com.sw.example;

import com.sw.aot.api.AotLoader;
import com.sw.example.main.ExampleAotIndex;

import android.app.Application;

public class ExampleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        initAotDemo();
    }

    private void initAotDemo(){
        AotLoader.add(new ExampleAotIndex());
    }

}
