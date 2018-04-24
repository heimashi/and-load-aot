package com.sw.example.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rong360.srouter.annotation.SRouter;
import com.rong360.srouter.api.SimpleRouter;

@SRouter("/module/test")
public class ModuleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("\nI'm ModuleActivity\n\n\nTo Module 2 Activity");
        setContentView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleRouter.getInstance().navigateTo(ModuleActivity.this,
                        "/module/test2",
                        new Intent());
            }
        });
    }
}
