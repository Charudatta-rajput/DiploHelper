package com.bhavesh.diplohelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    EditText SignupName,SignupEmail,SignupUsername;
    EditText SignupPassword;
    TextView LoginRedirectText;
    Button SignupButton;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SignupName = findViewById(R.id.Signup_name);
        SignupEmail = findViewById(R.id.Signup_email);
        SignupUsername = findViewById(R.id.Signup_username);
        SignupPassword = findViewById(R.id.Signup_password);
        SignupButton = findViewById(R.id.Signup_Button);
        LoginRedirectText = findViewById(R.id.SignupRedirectText);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = SignupName.getText().toString();
                String email = SignupEmail.getText().toString();
                String Username = SignupUsername.getText().toString();
                String password = SignupPassword.getText().toString();

                HelperClass helperclass = new HelperClass(name, Username, email, password);
                reference.child(name).setValue(helperclass);

                Toast.makeText(Signup.this, "You Have SignUp Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);

            }
        });
        LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Test_Login.class);
                startActivity(intent);
                finish();

            }
        });
    }
}