package com.sw.example.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rong360.srouter.annotation.SRouter;
import com.rong360.srouter.api.SimpleRouter;

@SRouter(value = "/module/test2", desc = "副模块页面2")
public class Module2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("\nI'm Module2Activity\n\n\nTo main Activity");
        setContentView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleRouter.getInstance().navigateTo(Module2Activity.this,
                        "/main/home",
                        new Intent());
            }
        });
    }
}
