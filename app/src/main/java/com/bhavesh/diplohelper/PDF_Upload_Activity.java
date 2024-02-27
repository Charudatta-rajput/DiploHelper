package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PDF_Upload_Activity extends AppCompatActivity {

    DatabaseReference reference;
    StorageReference Storagereference;

    Button Upload_pdf,Access_PDF;

    Uri pdfData;
    String fcmToken;

    EditText et;

    private final int REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);
        FirebaseApp.initializeApp(this);



        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        et = findViewById(R.id.edtext);
        Upload_pdf =findViewById(R.id.Upload_pdf_btn);
        Access_PDF = findViewById(R.id.Access_pdf_btn);

        reference = FirebaseDatabase.getInstance().getReference();
        Storagereference = FirebaseStorage.getInstance().getReference("UploadPDF/test");






        Upload_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1);

            }
        });


    }



    ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            pdfData = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("UploadPDF/Try");
            final String messagePushID = timestamp;
            String pdfname = et.getText().toString().trim();
            Toast.makeText(PDF_Upload_Activity.this, pdfData.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(pdfname + "." + "pdf");
            Toast.makeText(PDF_Upload_Activity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(pdfData)// Inside the onSuccess block
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete());
                            Uri uri = uriTask.getResult();

                            putPDF putPDF = new putPDF(et.getText().toString(),uri.toString());
                            reference.child(reference.push().getKey()).setValue(putPDF);

                            // Send FCM notification
                            getFCMtoken();
                            sendFCMNotification("New PDF Uploaded", "A new PDF has been uploaded!");


                            Toast.makeText(PDF_Upload_Activity.this, "File Upload", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    dialog.setMessage("File Uploaded.."+(int) progress+"%");

                }
            });

        }


    }

    public void retrivePDF(View view){
        startActivity(new Intent(getApplicationContext(),RetrivePDF.class));
    }


    public void retrievePDF(View view) {
        startActivity(new Intent(getApplicationContext(),RetrivePDF.class));
    }
    void getFCMtoken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fcmToken = task.getResult();
                Log.e("My token is", fcmToken);
            } else {
                Log.e("FCM Token Error", "Failed to get FCM token: " + task.getException());
            }
        });
    }





    private void sendFCMNotification(String title, String message) {
        try {
            // Construct the FCM message payload
            JSONObject payload = new JSONObject();
            payload.put("to", fcmToken);

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);

            payload.put("notification", notification);

            // Create a request to send the FCM message
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://fcm.googleapis.com/fcm/send",
                    payload,
                    response -> {
                        Log.d("FCM Notification", "Notification sent successfully!");
                    },
                    error -> {
                        Log.e("FCM Notification", "Error sending notification: " + error.getMessage());
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=AAAANGHNfEI:APA91bHpU267YJgyHSj27lmziM8iBj5Enw0M4YdjI0Y7jjHB5a5oVC3l86Wud80LrtVYrvcvfu3oXn84cC94fUTh5Is_7ihMyz3LZZe-yDIqOUbVulWHxHHfuZb1IQsuMdBdQjDdVHcM");
                    return headers;
                }
            };

            // Add the request to the Volley request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





}