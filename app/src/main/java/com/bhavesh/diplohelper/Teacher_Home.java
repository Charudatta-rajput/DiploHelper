package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Teacher_Home extends AppCompatActivity {

    ConstraintLayout comp;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        comp = findViewById(R.id.teacher_comp);
        back = findViewById(R.id.backB);

        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Teacher_Home.this, tech_comp_sem.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Teacher_Home.this, MainLogin_Activity.class);
                startActivity(i);
            }
        });
    }
}