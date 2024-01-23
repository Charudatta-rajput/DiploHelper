package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_pass extends AppCompatActivity {
    Button Back_btn,Forgot_btn;
    EditText txtemail;
    public String email;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        Back_btn = findViewById(R.id.Back_btn);
        txtemail = findViewById(R.id.Forgot_Email);
        Forgot_btn = findViewById(R.id.Forgotpass);

        auth = FirebaseAuth.getInstance();

        Forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }

        });





        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forget_pass.this, Test_Login.class);
                startActivity(intent);
            }
        });
    }

    private void validateData() {
        email =txtemail.getText().toString().trim();
        if (email.isEmpty()){
            txtemail.setError("Required");
        }
        else {
            forgetpassword();
        }
    }

    private void  forgetpassword(){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Forget_pass.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Forget_pass.this, Test_Login.class));
                            finish();

                        }
                        else {
                            Toast.makeText(Forget_pass.this, "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
