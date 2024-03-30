package com.bhavesh.diplohelper.ui.slideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bhavesh.diplohelper.R;
import com.bhavesh.diplohelper.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView whatsappImageView = root.findViewById(R.id.whatsapp); // Replace with your WhatsApp ImageView ID
        ImageView linkedinImageView = root.findViewById(R.id.linkedin); // Replace with your LinkedIn ImageView ID
        ImageView instagramImageView = root.findViewById(R.id.instagram); // Replace with your Instagram ImageView ID

        // Set OnClickListener on the WhatsApp ImageView
        whatsappImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat("9112270961"); // Replace with your WhatsApp number
            }
        });

        // Set OnClickListener on the LinkedIn ImageView
        linkedinImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedInProfile("your_linkedin_username"); // Replace with your LinkedIn username or profile URL
            }
        });

        // Set OnClickListener on the Instagram ImageView
        instagramImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagramProfile("your_instagram_username"); // Replace with your Instagram username
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openWhatsAppChat(String phoneNumber) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions if any, such as if WhatsApp is not installed on the device
        }
    }

    private void openLinkedInProfile(String username) {
        try {
            String url = "https://www.linkedin.com/in/charudatta-patil-828546283?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app" + username;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions if any, such as if the LinkedIn app is not installed on the device
        }
    }

    private void openInstagramProfile(String username) {
        try {
            String url = "https://www.instagram.com/charudatta_rajput?igsh=aHI0emQwcDcyZTdq" + username;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions if any, such as if the Instagram app is not installed on the device
        }
    }
}
