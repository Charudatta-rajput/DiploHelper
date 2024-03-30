package com.bhavesh.diplohelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class tech_comp_sem1 extends AppCompatActivity {

    ImageButton eng_upload, math_upload;
    EditText eng_et, math_et;
    DatabaseReference reference;
    StorageReference engStorageReference, mathStorageReference;
    Uri pdfData;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_comp_sem1);

        FirebaseApp.initializeApp(this);

        eng_et = findViewById(R.id.eng_et);
        eng_upload = findViewById(R.id.eng_upload);
        math_et = findViewById(R.id.math_et);
        math_upload = findViewById(R.id.math_upload);

        reference = FirebaseDatabase.getInstance().getReference();
        engStorageReference = FirebaseStorage.getInstance().getReference("UploadPDF/test");
        mathStorageReference = FirebaseStorage.getInstance().getReference("UploadPDF/math");


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d("FCM", "FCM Token"+token);
                    } else {
                        Log.e("FCM", "Error getting FCM token", task.getException());
                    }
                });


        eng_upload.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("application/pdf");
            startActivityForResult(galleryIntent, 1);
        });

        math_upload.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("application/pdf");
            startActivityForResult(galleryIntent, 2);
        });

        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();

            pdfData = data.getData();
            final String pdfname;

            if (requestCode == 1) {
                pdfname = eng_et.getText().toString().trim();
                StorageReference engFilepath = engStorageReference.child(pdfname + ".pdf");
                uploadFile(engFilepath, pdfname);
            } else if (requestCode == 2) {
                pdfname = math_et.getText().toString().trim();
                StorageReference mathFilepath = mathStorageReference.child(pdfname + ".pdf");
                uploadFile(mathFilepath, pdfname);
            }
        }
    }

    private void uploadFile(StorageReference filepath, final String pdfname) {
        UploadTask uploadTask = filepath.putFile(pdfData);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                    putPDF putPDF = new putPDF(pdfname, uri.toString());
                    String pushKey = reference.push().getKey();
                    reference.child(pushKey).setValue(putPDF);

                    sendNotification("Hello", pdfname);

                    Toast.makeText(tech_comp_sem1.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            } else {
                Toast.makeText(tech_comp_sem1.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            dialog.setMessage("File Uploaded.." + (int) progress + "%");
        });
    }

    void sendNotification(String msg, String pdfName) {
        JsonObject notification = new JsonObject();
        notification.addProperty("title", msg);
        notification.addProperty("body", "New PDF Uploaded: " + pdfName);

        JsonObject data = new JsonObject();
        data.addProperty("pdfName", pdfName);

        JsonObject payload = new JsonObject();
        payload.add("notification", notification);
        payload.add("data", data);

        callApi(payload);
    }

    void callApi(JsonObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "key=AAAANGHNfEI:APA91bHpU267YJgyHSj27lmziM8iBj5Enw0M4YdjI0Y7jjHB5a5oVC3l86Wud80LrtVYrvcvfu3oXn84cC94fUTh5Is_7ihMyz3LZZe-yDIqOUbVulWHxHHfuZb1IQsuMdBdQjDdVHcM")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request);
    }
}


