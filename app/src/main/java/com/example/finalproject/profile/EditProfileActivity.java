package com.example.finalproject.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.R;
import com.example.finalproject.SharedViewModel;
import com.example.finalproject.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText editUsername;
    private EditText editFullName;
    private Button changePicB;
    private Button saveB;

    // URI that points to the image the user picked from the gallery
    private Uri selectedImageUri = null;
    private SharedViewModel model;
    /**
     * ActivityResultLauncher replaces the old "startActivityForResult" method.
     * It safely receives data BACK from another app, like the device gallery.
     *
     * When the user selects a picture, this callback runs automatically,
     * giving us the image's URI so we can show it + convert it to Base64.
     */
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImage.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profileImageView);
        editUsername = findViewById(R.id.usernameET);
        editFullName = findViewById(R.id.fullNameET);
        changePicB = findViewById(R.id.changePictureB);
        saveB = findViewById(R.id.saveProfileB);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        model = new ViewModelProvider(this).get(SharedViewModel.class);
        model.fetchUserData(uid);
        model.getUser().observe(this, user -> {
            if (user != null) {
                editUsername.setText(user.getUserName());
                editFullName.setText(user.getFullName());
                loadCurrentProfilePicture(user.getProfileImageBase64());
            }
        });
        changePicB.setOnClickListener(v -> openGallery());
        saveB.setOnClickListener(v -> saveChanges());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
    /**
     * Loads a Base64-encoded profile picture.
     * Base64 means the image is stored as text, not a file, inside Firebase.
     */
    private void loadCurrentProfilePicture(String base64) {
        if (base64 == null || base64.isEmpty()) {
            // Default picture if the user has no custom image
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
        } catch (Exception e){
            Toast.makeText(this, "Failed to load picture", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Converts the selected gallery image (URI) to raw bytes, and finally to Base64 text string.
     * Firebase Realtime Database cannot store files, so this method converts it.
     */
    private String uriToBase64(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            byte[] bytes = input.readAllBytes();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Validates profile input, checks username availability,
     * and updates Firebase with new username/fullname/profile picture.
     */
    private void saveChanges() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        // Get user data from SharedViewModel
        User currentUser = model.getUser().getValue();
        if (currentUser == null) {
            Toast.makeText(this, "Error: user data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get updated user input
        String newUsername = editUsername.getText().toString().trim();
        String newFullName = editFullName.getText().toString().trim();
        String newImageBase64;
        // Convert picture to Base64 if the user selected a new one
        if (selectedImageUri != null) {
            newImageBase64 = uriToBase64(selectedImageUri);
        } else {
            newImageBase64 = null;
        }
        // Old values for comparison
        String oldUsername = currentUser.getUserName();
        String oldFullName = currentUser.getFullName();
        String oldImageBase64 = currentUser.getProfileImageBase64();
        // Check if the user actually changed anything
        boolean sameUsername = newUsername.equals(oldUsername);
        boolean sameName = newFullName.equals(oldFullName) || newFullName.isEmpty();
        boolean samePicture = (selectedImageUri == null);

        if (sameUsername && sameName && samePicture) {
            // No actual changes → nothing to update
            finish();
            return;
        }



        /**
         * Username validation — this prevents invalid usernames.
         * These rules are similar to Twitter/Instagram.
         */

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newUsername.contains(" ")) {
            editUsername.setError("Username cannot contain spaces");
            return;
        }

        if (newUsername.length() < 3) {
            editUsername.setError("Username must be at least 3 characters");
            return;
        }

        if (!newUsername.matches("[A-Za-z0-9_.]+")) {
            editUsername.setError("Only letters, numbers, _, and . is allowed");
            return;
        }

        // If username is unchanged, skip availability check
        if (sameUsername) {
            updateProfile(userRef, newFullName, oldFullName, newImageBase64);
            return;
        }

        /**
         * Username *did* change → check if another user already has it.
         * This loops through every user in Firebase.
         * This is asynchronous — it runs later, not instantly.
         */
        FirebaseDatabase.getInstance().getReference("Users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    boolean taken = false;
                    // Loop through all users
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (!child.getKey().equals(uid)) {  // don't compare with yourself (was a former problem)
                            String existing = child.child("userName").getValue(String.class);
                            if (newUsername.equalsIgnoreCase(existing)) {
                                taken = true;
                                break;
                            }
                        }
                    }

                    if (taken) {
                        // Username already exists, reject
                        Toast.makeText(this, "Username already taken!", Toast.LENGTH_SHORT).show();
                        editUsername.setError("Username already taken");
                        return;
                    }
                    // Username is available, save and update
                    userRef.child("userName").setValue(newUsername);
                    updateProfile(userRef,newFullName, oldFullName, newImageBase64);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking usernames", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates full name + picture.
     * This is called both when username changes AND when it stays the same.
     */
    private void updateProfile(DatabaseReference ref, String newName,
                               String oldName, String newImageBase64) {
        // Update full name only if user typed something different
        if (!newName.isEmpty() && !newName.equals(oldName)) {
            ref.child("fullName").setValue(newName);
        }
        // Save new Base64 profile picture if user selected one
        if (newImageBase64 != null){
            ref.child("profileImageBase64").setValue(newImageBase64);
        }

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
        finish();
        }
    }

