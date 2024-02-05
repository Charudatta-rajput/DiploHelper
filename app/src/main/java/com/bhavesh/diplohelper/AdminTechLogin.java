package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminTechLogin extends AppCompatActivity {


    EditText et_username,et_password,b;
    Button btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tech_login);

Login();

        } void Login(){
            et_username = (EditText)findViewById(R.id.tech_username);
            et_password = (EditText)findViewById(R.id.Tech_password);
            btn_login = (Button)findViewById(R.id.Login_Teacher);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_username.getText().toString().trim().equals("admin") && et_password.getText().toString().trim().equals("admin")){
                        Toast.makeText(AdminTechLogin.this, "Username and Password is correct", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminTechLogin.this,TeacherLogin.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AdminTechLogin.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
