package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    private EditText etName, etEmail, etPassword;
    private Button btnRegister, btnRedirectToLogin;

    // Firebase instances
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnRedirectToLogin = findViewById(R.id.btnRedirectToLogin);

        // Register Button Listener
        btnRegister.setOnClickListener(v -> registerUser());

        // Redirect to LoginActivity Button Listener
        btnRedirectToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Finish RegisterActivity so the user can't go back to it
        });
    }

    // Method to register the user
    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully created the user
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            // Create a user document in Firestore with additional fields
                            createUserInFirestore(user, name, email);
                        }

                    } else {
                        // If registration failed, show error message
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Create a document for the user in Firestore with name, email, and other fields
    private void createUserInFirestore(FirebaseUser firebaseUser, String name, String email) {
        // Create a User object
        User user = new User(name, email, false, null);  // Initial presence is false, no active document yet

        // Add the user to Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .document(firebaseUser.getUid())  // Use user UID as the document ID
                .set(user)  // Save the User object to Firestore
                .addOnSuccessListener(aVoid -> {
                    // Successfully created user document
                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                    // Redirect to HomeActivity
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();  // Finish the RegisterActivity so the user can't go back
                })
                .addOnFailureListener(e -> {
                    // Error occurred while creating user document
                    Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
