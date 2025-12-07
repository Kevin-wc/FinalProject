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

    private Uri selectedImageUri = null;
    private SharedViewModel model;

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

    private void loadCurrentProfilePicture(String base64) {
        if (base64 == null || base64.isEmpty()) {
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
            return;
        }

        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
        } catch (Exception e){
            // Don't know what to put here, HI DR. PARRA
        }
    }

    // Transform to Base64 from Uri because selecting a picture from the gallery provides URI
    private String uriToBase64(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            byte[] bytes = input.readAllBytes();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    private void saveChanges() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        User currentUser = model.getUser().getValue();
        if (currentUser == null) {
            Toast.makeText(this, "Error: user data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        String newUsername = editUsername.getText().toString().trim();
        String newFullName = editFullName.getText().toString().trim();
        String newImageBase64;
        if (selectedImageUri != null) {
            newImageBase64 = uriToBase64(selectedImageUri);
        } else {
            newImageBase64 = null;
        }
        String oldUsername = currentUser.getUserName();
        String oldFullName = currentUser.getFullName();
        String oldImageBase64 = currentUser.getProfileImageBase64();

        boolean sameUsername = newUsername.equals(oldUsername);
        boolean sameName = newFullName.equals(oldFullName) || newFullName.isEmpty();
        boolean samePicture = (selectedImageUri == null);

        if (sameUsername && sameName && samePicture) {
            finish();
            return;
        }


        // Username Error Handling

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

        // If username didn't change
        if (sameUsername) {
            updateProfile(userRef, newFullName, oldFullName, newImageBase64);
            return;
        }

        // Check if username IS being changed, we check to see if the username already exists
        FirebaseDatabase.getInstance().getReference("Users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    boolean taken = false;

                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (!child.getKey().equals(uid)) {
                            String existing = child.child("userName").getValue(String.class);
                            if (newUsername.equalsIgnoreCase(existing)) {
                                taken = true;
                                break;
                            }
                        }
                    }

                    if (taken) {
                        Toast.makeText(this, "Username already taken!", Toast.LENGTH_SHORT).show();
                        editUsername.setError("Username already taken");
                        return;
                    }

                    userRef.child("userName").setValue(newUsername);
                    updateProfile(userRef,newFullName, oldFullName, newImageBase64);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking usernames", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile(DatabaseReference ref, String newName,
                               String oldName, String newImageBase64) {
        if (!newName.isEmpty() && !newName.equals(oldName)) {
            ref.child("fullName").setValue(newName);
        }
        if (newImageBase64 != null){
            ref.child("profileImageBase64").setValue(newImageBase64);
        }

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
        finish();
        }
    }

