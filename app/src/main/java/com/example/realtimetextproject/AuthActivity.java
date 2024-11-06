package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth firebaseAuth;

    // UI components
    private EditText etEmail, etPassword;
    private Button btnSignIn, btnSignUp;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        tv1 = findViewById(R.id.tv1);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Sign In Button Listener
        btnSignIn.setOnClickListener(v -> signInUser());

        // Sign Up Button Listener
        btnSignUp.setOnClickListener(v -> signUpUser());
    }

    // Method to sign in the user
    private void signInUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful, navigate to the main screen (TextEditorActivity)
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d("Auth", "signInWithEmail:success: " + user.getEmail());
                        Toast.makeText(AuthActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();  // Success Toast
                        navigateToTextEditor();
                    } else {
                        // Sign-in failed, show error
                        Log.w("Auth", "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to sign up the user
    private void signUpUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up successful, navigate to the main screen (TextEditorActivity)
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d("Auth", "createUserWithEmail:success: " + user.getEmail());
                        Toast.makeText(AuthActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();  // Success Toast
                        navigateToTextEditor();
                    } else {
                        // Sign-up failed, show error
                        Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Navigate to the Text Editor activity after successful sign-in or sign-up
    private void navigateToTextEditor() {
        Intent editorIntent = new Intent(AuthActivity.this, TextEditorActivity.class);
        startActivity(editorIntent);
        finish(); // Close the AuthActivity after navigation
    }
}
