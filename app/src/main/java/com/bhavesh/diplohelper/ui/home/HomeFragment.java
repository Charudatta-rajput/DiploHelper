package com.bhavesh.diplohelper.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bhavesh.diplohelper.Computer_Select_Sem;
import com.bhavesh.diplohelper.R;
import com.bhavesh.diplohelper.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

  

ImageButton computer;

private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);



    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();


        return root;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      computer  = (ImageButton) view.findViewById(R.id.computer);

       computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Computer_Select_Sem.class);
                getActivity().startActivity(intent);
            }
        });


    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }
}