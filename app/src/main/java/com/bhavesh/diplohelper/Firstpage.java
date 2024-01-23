package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Firstpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);
         Handler handler = new Handler();


        int delayMillis = 2500;


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Firstpage.this, MainLogin_Activity.class);
                startActivity(intent);
                finish();
            }
        }, delayMillis);
    }
}
