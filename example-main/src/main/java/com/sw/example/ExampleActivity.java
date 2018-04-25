package com.sw.example;

import com.sw.aot.annotation.AOTLoad;
import com.sw.aot.api.AotLoader;
import com.sw.aot.api.ResultData;
import com.sw.aot.api.ResultListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ExampleActivity extends Activity{

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        textView = new TextView(this);
        textView.setText("\nLoading...");
        setContentView(textView);
    }

    private void initData(){
        String taskKey = getIntent().getStringExtra("AOT_LOADER_TASK");
        if(AotLoader.isHotTask(taskKey)){
            AotLoader.consume(taskKey, listener);
        }else {
            loadMockData().setResultListener(listener);
        }
    }

    private ResultListener<String> listener = new ResultListener<String>() {
        @Override
        public void onDataChange(final ResultData<String> data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(data.getData());
                }
            });

        }
    };

    @AOTLoad(router = "/Example/LoadMockData", desc = "mock load async data")
    public ResultData<String> loadMockData(){
        final ResultData<String> result = new ResultData<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    result.setCode(0);
                    result.setData("MOCK: LOAD DATA SUCCESS");
                    result.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    result.setCode(-1);
                    result.flush();
                }
            }
        }).start();
        return result;
    }


}
