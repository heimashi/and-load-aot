package com.sw.aot.example;

import com.sw.aot.annotation.AOTLoad;
import com.sw.aot.api.AotLoader;
import com.sw.aot.api.ResultData;
import com.sw.aot.api.ResultListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ExampleActivity extends Activity{

    private static final String START_AOT_LOAD_KEY = "AOT_LOADER_TASK_KEY";

    private TextView textView;
    private String aotTaskKey;

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
        aotTaskKey = getIntent().getStringExtra(START_AOT_LOAD_KEY);
        if(AotLoader.isValidTask(aotTaskKey)){
            AotLoader.consume(aotTaskKey, listener);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AotLoader.unRegister(aotTaskKey);
    }

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

    public static void invoke(Context context){
        Intent intent = new Intent(context, ExampleActivity.class);
        intent.putExtra(START_AOT_LOAD_KEY, AotLoader.produce(ExampleAotIndex.EXAMPLE_LOADMOCKDATA));
        context.startActivity(intent);
    }

}
