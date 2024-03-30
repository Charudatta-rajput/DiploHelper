package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Computer_Select_Sem extends AppCompatActivity {

    CardView comp_sem1;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_select_sem);

        comp_sem1 = findViewById(R.id.Comp_sem1);
        back = findViewById(R.id.backB);
        comp_sem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Computer_Select_Sem.this, Computer_Sem1.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Computer_Select_Sem.this, SliderActivity.class);
                startActivity(intent);
            }
        });
    }
}