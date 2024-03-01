package com.bhavesh.diplohelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Math extends AppCompatActivity {

    ListView pdfListView;
    List<String> pdfFileNames;
    long downloadId;

    EditText searchEditText;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        pdfListView = findViewById(R.id.math_listview);
        pdfFileNames = new ArrayList<>();
        searchEditText  =findViewById(R.id.math_search);


        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference nestedFolderRef = storage.getReference().child("UploadPDF/math");
        DatabaseReference pdfNamesRef = FirebaseDatabase.getInstance().getReference().child("pdfFileNames");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter the PDF names based on the search query
                filterPdfNames(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed
            }
        });

        pdfListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFileName = pdfFileNames.get(position);
            StorageReference pdfReference = nestedFolderRef.child(selectedFileName);


            pdfReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        downloadAndOpenPdf(selectedFileName, downloadUrl);
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Error getting download URL for PDF", e));
        });



        // List all PDF files in the folder
        nestedFolderRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        pdfFileNames = new ArrayList<>();
                        for (StorageReference item : listResult.getItems()) {
                            // Access individual file references
                            String fileName = item.getName();
                            if (fileName.toLowerCase().endsWith(".pdf")) {
                                // This file is a PDF, add its name to the list
                                pdfFileNames.add(fileName);
                            }
                        }

                        adapter = new ArrayAdapter<String>(Math.this, android.R.layout.simple_list_item_1, pdfFileNames){

                            // Display the list of PDF file names in the TextView
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view =  super.getView(position, convertView, parent);
                                TextView textView =(TextView) view
                                        .findViewById(android.R.id.text1);

                                textView.setTextColor(Color.BLACK);
                                textView.setTextSize(20);
                                return view;
                            }
                        };

                        pdfListView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure by logging the error
                        Log.e("Firebase", "Error getting PDF files", e);
                    }
                });
    }
    private void downloadAndOpenPdf(String fileName, String downloadUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle(fileName);
        request.setDescription("Downloading PDF");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    openDownloadedPDF(fileName);
                    context.unregisterReceiver(this);
                }
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void openDownloadedPDF(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("PDFView", "Error opening PDF file", e);
        }
    }
    private void filterPdfNames(String query) {
        adapter.getFilter().filter(query);
    }
}