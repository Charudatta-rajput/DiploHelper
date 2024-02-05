package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class PDF_Upload_Activity extends AppCompatActivity {

    DatabaseReference reference;
    StorageReference Storagereference;

    Button Upload_pdf,Access_PDF;

    Uri pdfData;

    EditText et;

    private final int REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_upload);

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
            filepath.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();

                    putPDF putPDF = new putPDF(et.getText().toString(),uri.toString());
                    reference.child(reference.push().getKey()).setValue(putPDF);
                    Toast.makeText(PDF_Upload_Activity.this,"File Upload",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
}