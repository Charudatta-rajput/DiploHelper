package com.bhavesh.diplohelper.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhavesh.diplohelper.Message;
import com.bhavesh.diplohelper.MessagesAdapter;
import com.bhavesh.diplohelper.R;
import com.bhavesh.diplohelper.databinding.FragmentGalleryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private DatabaseReference messagesDatabaseReference;
    private FragmentGalleryBinding binding;
    private EditText editTextMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database
        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("messages");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI elements
        editTextMessage = root.findViewById(R.id.editTextMessage);
        Button buttonSendMessage = root.findViewById(R.id.buttonSendMessage);
        RecyclerView recyclerViewMessages = root.findViewById(R.id.recyclerViewMessages);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewMessages.setLayoutManager(layoutManager);

        // Set up adapter for RecyclerView
        MessagesAdapter messagesAdapter = new MessagesAdapter();
        recyclerViewMessages.setAdapter(messagesAdapter);

        // Set up click listener for the "Send" button
        buttonSendMessage.setOnClickListener(view -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Get a unique key for the new message
                String messageId = messagesDatabaseReference.push().getKey();

                // Create a Message object
                Message message = new Message(messageText, "user_id", System.currentTimeMillis());

                // Save the message to the database
                messagesDatabaseReference.child(messageId).setValue(message);

                // Clear the input field
                editTextMessage.setText("");
            }
        });

        messagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                messagesAdapter.setMessages(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("FirebaseError", "Database read error: " + error.getMessage());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
