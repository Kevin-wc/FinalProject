package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText email;
    private Button resetPasswordBtn;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        email = findViewById(R.id.editTextTextEmailAddress);

        auth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(resetPassword_onClick);

    }

    public View.OnClickListener resetPassword_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            String emailStr = email.getText().toString().trim();
            if(emailStr.isEmpty()){
                Toast.makeText(ForgotPasswordActivity.this, "Email field must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(emailStr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                             Log.e("ForgotPasswordActivity", "Error sending password reset email", task.getException());
                        }
                    }
                });
        }
    };
}
