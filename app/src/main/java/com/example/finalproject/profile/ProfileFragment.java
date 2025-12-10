package com.example.finalproject.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView usernameTV;
    private TextView realNameTV;
    private ListView settingsList;
    private Button editProfileB;
    // Empty public constructor required for fragments
    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        // Connecting UI elements
        profileImage = view.findViewById(R.id.profile_image);
        usernameTV = view.findViewById(R.id.username_text);
        realNameTV = view.findViewById(R.id.realname_text);
        settingsList = view.findViewById(R.id.settings_list);
        editProfileB = view.findViewById(R.id.edit_profile_btn);


        // Use observer to display user data from ViewModel.
        setupUserObserver();
        // ListView navigation
        setupSettingsList();
        // Edit Profile screen button
        setupEditProfileButton();

        return view;
    }

    /**
     * setupUserObserver()
     *
     * The SharedViewModel stores the currently logged-in user's information.
     * Because it uses LiveData, this Fragment automatically receives updates
     * whenever user data is changed in Firebase.
     *
     */
    private void setupUserObserver() {
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        model.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Update username & full name
                usernameTV.setText("@" + user.getUserName());
                realNameTV.setText(user.getFullName());
                // Load Base64 profile image into ImageView
                loadProfilePicture(user.getProfileImageBase64());
            } else {
                // Fallback text if user object is null
                usernameTV.setText("@Unknown User");
                realNameTV.setText("No name");
            }
        });
    }

    /**
     * Converts a Base64-encoded profile image string into a Bitmap.
     * We switched to Base64 (instead of Firebase Storage URLs)
     * because it avoids Firebase billing and simplifies loading.
     *
     * Steps:
     * 1. Decode Base64 string to byte array
     * 2. Convert bytes to Bitmap
     * 3. Display in ImageView
     */
    private void loadProfilePicture(String base64) {
        if (base64 == null || base64.isEmpty()) {
            // Default placeholder image
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            // If something goes wrong, show default image
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    /**
     * Creates a simple settings menu with 3 items using a ListView
     * Items:
     *  - Personal
     *  - Notifications
     *  - Friends
     * SettingsAdapter is a custom adapter that allows us to show icons + text
     * together in each row.
     */
    private void setupSettingsList() {
        // Titles for each menu item
        String[] items = {"Personal", "Notifications", "Friends"};
        // Corresponding icons (stored in /res/drawable)
        int[] icons = {R.drawable.ic_user, R.drawable.ic_notifications, R.drawable.ic_friends};
        // Custom adapter inflates the row layout for each item
        SettingsAdapter adapter = new SettingsAdapter(requireContext(), items, icons);
        settingsList.setAdapter(adapter);
        /**
         * ListView navigation:
         * Clicking a row launches a specific activity.
         */

        settingsList.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) startActivity(new Intent(getActivity(), PersonalActivity.class));
            if (position == 1) startActivity(new Intent(getActivity(), NotificationActivity.class));
            if (position == 2) startActivity(new Intent(getActivity(), FriendsActivity.class));
        });
    }
    /**
     * Opens the EditProfileActivity where the user can:
     * - Change username
     * - Change full name
     * - Upload a new profile picture
     */
    private void setupEditProfileButton() {
        editProfileB.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class))
        );
    }
}
