package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainLogin_Activity extends AppCompatActivity {

    ImageButton StudenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        StudenButton = findViewById(R.id.Student_Button);
        StudenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLogin_Activity.this, Test_Signup.class);
                startActivity(intent);
            }
        });

    }
}