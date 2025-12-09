package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText email;
    private Button register;
    private DatabaseReference ref;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.nameET);
        password = findViewById(R.id.passwordET);
        email = findViewById(R.id.emailET);
        register = findViewById(R.id.registerButton);

        register.setOnClickListener(register_onClick);
    }

    public View.OnClickListener register_onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String usernameStr = username.getText().toString();
            String passwordStr = password.getText().toString();
            String emailStr = email.getText().toString();

            if (usernameStr.isEmpty() || passwordStr.isEmpty() || emailStr.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "All Fields Must be Filled", Toast.LENGTH_SHORT).show();
            } else {
                auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("RegisterSuccess", "createUserWithEmail:success");
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                User user = new User();
                                user.setUserName(usernameStr);
                                user.setEmail(emailStr);
                                user.setPoints(0);

                                if (firebaseUser != null) {
                                    ref.child("Users").child(firebaseUser.getUid()).setValue(user);
                                }
                                
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("RegisterFail", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    };
}