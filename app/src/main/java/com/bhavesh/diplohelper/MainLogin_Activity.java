package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainLogin_Activity extends AppCompatActivity {

    ImageButton StudenButton,Teacher_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        StudenButton = findViewById(R.id.Student_Button);
        Teacher_Button = findViewById(R.id.Teacher_Button);
        StudenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLogin_Activity.this, Test_Signup.class);
                startActivity(intent);
            }
        });

        Teacher_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainLogin_Activity.this, Teacher_Home.class);
                startActivity(intent);
            }
        });

    }
}