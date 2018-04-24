package com.sw.example;

import com.sw.aot.annotation.AOTLoad;
import com.sw.aot.api.ResultData;

import android.app.Activity;
import android.os.Bundle;

public class ExampleActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AOTLoad(router = "example_load_mock_data")
    public ResultData<String> loadMockData(){
        ResultData<String> result;
        try {
            Thread.sleep(1000);
            result = new ResultData<String>(0, "MOCK: LOAD DATA SUCCESS");
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = new ResultData<String>(-1);
        }
        return result;
    }

}
