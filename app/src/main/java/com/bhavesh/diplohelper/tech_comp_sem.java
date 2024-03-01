package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class tech_comp_sem extends AppCompatActivity {


    CardView comp_sem1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_comp_sem);

        comp_sem1 = findViewById(R.id.tech_comp_sem1);
        comp_sem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tech_comp_sem.this, tech_comp_sem1.class);
                startActivity(intent);
            }
        });

    }
}