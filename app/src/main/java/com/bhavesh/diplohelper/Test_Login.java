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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Test_Login extends AppCompatActivity {
    EditText LoginUsername,LoginPassword;
    Button LoginButton,SignupRedirectText;
    TextView Forgotpass_text, Login_Time;
    ImageView GoogleAutha,Login_image;
    GoogleSignInClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);

        LoginUsername = findViewById(R.id.LoginUsername);
        LoginPassword = findViewById(R.id.Loginpassword);
        LoginButton = findViewById(R.id.LoginButton);
        SignupRedirectText = findViewById(R.id.SignInButton);
   //     GoogleAutha = findViewById(R.id.GoogleAuth);
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

//        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        client = GoogleSignIn.getClient(this,options);
//        GoogleAutha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = client.getSignInIntent();
//                startActivityForResult(i,1234);
//
//            }
//        });



        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()){

                }
                else {
                    checkUser();
                }
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
    //Google Auth
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1234){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
//                FirebaseAuth.getInstance().signInWithCredential(credential)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful()){
//                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                    startActivity(intent);
//
//                                }else {
//                                    Toast.makeText(Test_Login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        });
//
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user!= null){
//            Intent intent = new Intent(this,MainActivity.class);
//            startActivity(intent);
//        }
//    }

    ///Complete
    private void getTimeFromAndroid() {
        Calendar c = Calendar.getInstance();
        int timeOfDay= c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            Login_image.setImageResource(R.drawable.good_morning_img);
            Login_Time.setText("Good Morning..!");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            Login_image.setImageResource(R.drawable.good_morning_img);
            Login_Time.setText("Good Afternoon..!");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            Login_image.setImageResource(R.drawable.good_night_img);
            Login_Time.setText("Good Evening..!");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            Login_image.setImageResource(R.drawable.good_night_img);
            Login_Time.setText("Good Night..!");
        }
    }

    public boolean validateUsername(){

        String val = LoginUsername.getText().toString();
        if (val.isEmpty()){
            LoginUsername.setError("Username Cannot be empty");
            return false;
        }
        else {
            LoginUsername.setError(null);
            return  true;
        }
    }

    public boolean validatePassword(){

        String val = LoginPassword.getText().toString();
        if (val.isEmpty()){
            LoginUsername.setError("Password Cannot be empty");
            return false;
        }
        else {
            LoginPassword.setError(null);
            return  true;
        }
    }

    public void checkUser()
    {
        String userUsername = LoginUsername.getText().toString().trim();
        String userPassword = LoginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    LoginUsername.setError(null);
                    String passwordFromDB =snapshot.child(userPassword).getValue(String.class);
                    if (!Objects.equals(passwordFromDB,userPassword)){
                        LoginUsername.setError(null);
                        Intent intent = new Intent(Test_Login.this, SliderActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        LoginPassword.setError("Invalid Credentials");
                        LoginPassword.requestFocus();
                    }
                }
                else {
                    LoginUsername.setError("User Does not Exits");
                    LoginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

