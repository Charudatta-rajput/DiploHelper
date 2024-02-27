package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Test_Login extends AppCompatActivity {
    EditText LoginUsername, LoginPassword;
    Button LoginButton, SignupRedirectText;
    TextView Forgotpass_text, Login_Time;
    ImageView Login_image;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        LoginUsername = findViewById(R.id.LoginUsername);
        LoginPassword = findViewById(R.id.Loginpassword);
        LoginButton = findViewById(R.id.LoginButton);
        SignupRedirectText = findViewById(R.id.SignInButton);
        Forgotpass_text = findViewById(R.id.ForgotPasswordtext);
        Login_image = findViewById(R.id.Login_bg_img);
        Login_Time = findViewById(R.id.Login_Time);

        getTimeFromAndroid();

        Forgotpass_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_Login.this, Forget_pass.class);
                startActivity(intent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() || !validatePassword()) {
                    // Handle validation failure
                    return;
                }

                // Call the Firebase Authentication method to sign in
                String email = LoginUsername.getText().toString().trim();
                String password = LoginPassword.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Test_Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI
                                Intent intent = new Intent(Test_Login.this, SliderActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Test_Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        SignupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_Login.this, Test_Signup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is already authenticated, navigate to the main activity
            Intent intent = new Intent(this, SliderActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Validate user input
    private boolean validateUsername() {
        String val = LoginUsername.getText().toString().trim();
        if (val.isEmpty()) {
            LoginUsername.setError("Username cannot be empty");
            return false;
        } else {
            LoginUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = LoginPassword.getText().toString().trim();
        if (val.isEmpty()) {
            LoginPassword.setError("Password cannot be empty");
            return false;
        } else {
            LoginPassword.setError(null);
            return true;
        }
    }

    private void getTimeFromAndroid() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            Login_image.setImageResource(R.drawable.good_morning_img);
            Login_Time.setText("Good Morning..!");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            Login_image.setImageResource(R.drawable.good_morning_img);
            Login_Time.setText("Good Afternoon..!");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            Login_image.setImageResource(R.drawable.good_night_img);
            Login_Time.setText("Good Evening..!");
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            Login_image.setImageResource(R.drawable.good_night_img);
            Login_Time.setText("Good Night..!");
        }
    }
}
