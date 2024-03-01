package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class tech_comp_sem1 extends AppCompatActivity {

    ImageButton eng_upload, math_upload;
    EditText eng_et, math_et;
    DatabaseReference reference;
    StorageReference engStorageReference, mathStorageReference; // Separate StorageReference for Maths
    Uri pdfData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_comp_sem1);
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");
        eng_et = findViewById(R.id.eng_et);
        eng_upload = findViewById(R.id.eng_upload);
        math_et = findViewById(R.id.math_et);
        math_upload = findViewById(R.id.math_upload);

        reference = FirebaseDatabase.getInstance().getReference();
        engStorageReference = FirebaseStorage.getInstance().getReference("UploadPDF/test");
        mathStorageReference = FirebaseStorage.getInstance().getReference("UploadPDF/math"); // Folder for Maths

        eng_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1); // RequestCode for English
            }
        });

        math_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 2); // RequestCode for Maths
            }
        });
    }

    ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();

            pdfData = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            String pdfname;

            if (requestCode == 1) { // Check if the request is for English
                pdfname = eng_et.getText().toString().trim();
                StorageReference engFilepath = engStorageReference.child(pdfname + "." + "pdf");
                uploadFile(engFilepath, pdfname);
            } else if (requestCode == 2) { // Check if the request is for Maths
                pdfname = math_et.getText().toString().trim();
                StorageReference mathFilepath = mathStorageReference.child(pdfname + "." + "pdf");
                uploadFile(mathFilepath, pdfname);
            }
        }
    }

    private void uploadFile(StorageReference filepath, final String pdfname) {
        filepath.putFile(pdfData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();

                        putPDF putPDF = new putPDF(pdfname, uri.toString());
                        reference.child(reference.push().getKey()).setValue(putPDF);

                        Toast.makeText(tech_comp_sem1.this, "File Upload", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        dialog.setMessage("File Uploaded.." + (int) progress + "%");
                    }
                });
    }
}
