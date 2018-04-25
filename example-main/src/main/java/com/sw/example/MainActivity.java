package com.sw.example;

import com.sw.aot.api.AotLoader;
import com.sw.example.main.ExampleAotIndex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("\nStart Data AOT Activity");
        setContentView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAOTActivity();
            }
        });
    }

    private void startAOTActivity(){
        Intent intent = new Intent(this, ExampleActivity.class);
        intent.putExtra("AOT_LOADER_TASK", AotLoader.produce(ExampleAotIndex.example_load_mock_data));
        startActivity(intent);
    }
}
