package com.sw.aot.example;

import com.sw.aot.api.AotLoader;

import android.app.Application;

public class ExampleApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        initAotDemo();
    }

    private void initAotDemo(){
        AotLoader.enableLog(true);
        AotLoader.addRouter(new ExampleAotIndex());
    }

}
