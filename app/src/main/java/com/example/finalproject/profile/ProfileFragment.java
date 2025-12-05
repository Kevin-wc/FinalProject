package com.example.finalproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.R;
import com.example.finalproject.SharedViewModel;
import com.example.finalproject.User;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView usernameTV;
    private TextView realNameTV;
    private ListView settingsList;
    private Button editProfileB;

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        profileImage = view.findViewById(R.id.profile_image);
        usernameTV = view.findViewById(R.id.username_text);
        realNameTV = view.findViewById(R.id.realname_text);
        settingsList = view.findViewById(R.id.settings_list);
        editProfileB = view.findViewById(R.id.edit_profile_btn);



        setupUserObserver();
        setupSettingsList();
        setupProfileImage();
        setupEditProfileButton();
        return view;
    }

    private void setupUserObserver() {
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        model.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                usernameTV.setText("@" + user.getUserName());
                realNameTV.setText(user.getFullName());
            } else {
                usernameTV.setText("@Unknown User");
                realNameTV.setText("No name");
            }
        });
    }


        // Creates a list of settings using a custom adapter.
        // We use a ListView because it supports rows with icons and text.
        // The custom adapter allows us to control exactly how the row looks.
    private void setupSettingsList() {
        String[] items = {"Personal", "Notifications", "Friends"};
        // Corresponding icons that haven't been added yet.
        //int[] icons = {R.drawable.ic_user, R.drawable.ic_bell, R.drawable.ic_friends};

       // SettingsAdapter adapter = new SettingsAdapter(requireContext(), items, icons);
       // settingsList.setAdapter(adapter);

        settingsList.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) startActivity(new Intent(getActivity(), PersonalActivity.class));
            if (position == 1) startActivity(new Intent(getActivity(), NotificationActivity.class));
            if (position == 2) startActivity(new Intent(getActivity(), FriendsActivity.class));
        });
    }

    private void setupProfileImage() {
     //   profileImage.setOnClickListener(v -> openGallery());
    }

    private void setupEditProfileButton() {
        editProfileB.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class))
        );
    }
}
