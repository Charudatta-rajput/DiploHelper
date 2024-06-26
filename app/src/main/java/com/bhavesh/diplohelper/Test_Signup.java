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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Test_Signup extends AppCompatActivity {

    EditText SignupName, SignupEmail, SignupUsername;
    EditText SignupPassword;
    Button SignupButton, LoginButton;
    DatabaseReference usersReference;
    FirebaseAuth firebaseAuth;

    ImageView GoogleAutha, TwitterAuth, Background_iamge;
    GoogleSignInClient client;

    TextView txtview, timetextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_signup);

        SignupName = findViewById(R.id.Test_Name);
        SignupEmail = findViewById(R.id.Test_email);
        SignupUsername = findViewById(R.id.Test_Username);
        SignupPassword = findViewById(R.id.Test_password);
        SignupButton = findViewById(R.id.Signup_Submit);
        LoginButton = findViewById(R.id.Login_Submit);
        GoogleAutha = findViewById(R.id.GoogleAuthantication);
        TwitterAuth = findViewById(R.id.TwitterAuthanticaion);
        Background_iamge = findViewById(R.id.background_img);
        txtview = findViewById(R.id.textView1);
        timetextview = findViewById(R.id.timetextview);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("users");

        getTimeFromAndroid();

        // Google Sign-In setup
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);

        GoogleAutha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i, 1234);
            }
        });

        TwitterAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_Signup.this, TwitterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String name = SignupName.getText().toString();
                String email = SignupEmail.getText().toString();
                String username = SignupUsername.getText().toString();
                String password = SignupPassword.getText().toString();

                // Check if input is valid
                if (validateInput(name, email, username, password)) {
                    // Create user with email and password
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Test_Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User registration successful
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            String userId = user.getUid();
                                            // Create a HelperClass object with user data
                                            HelperClass helperClass = new HelperClass(name, username, email, password);
                                            // Save user data in the Realtime Database
                                            usersReference.child(userId).setValue(helperClass);
                                        }
                                        // Navigate to the desired activity
                                        Intent intent = new Intent(Test_Signup.this, SliderActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Test_Signup.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_Signup.this, Test_Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getTimeFromAndroid() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            Background_iamge.setImageResource(R.drawable.good_morning_img);
            timetextview.setText("Good Morning..!");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            Background_iamge.setImageResource(R.drawable.good_morning_img);
            timetextview.setText("Good Afternoon..!");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            Background_iamge.setImageResource(R.drawable.good_night_img);
            timetextview.setText("Good Evening..!");
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            Background_iamge.setImageResource(R.drawable.good_night_img);
            timetextview.setText("Good Night..!");
        }
    }

    // Validate user input
    private boolean validateInput(String name, String email, String username, String password) {
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), SliderActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Test_Signup.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, SliderActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
