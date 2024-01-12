

package com.example.raghunandan.ragumysuru.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.raghunandan.ragumysuru.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent i=new Intent(SplashActivity.this, LoginActivity.class);
               startActivity(i);
               finish();
           }
       },5000);
    }
}