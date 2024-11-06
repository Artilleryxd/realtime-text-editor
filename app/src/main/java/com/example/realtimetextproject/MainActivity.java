package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth

        // Check Firebase initialization status
        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.d("Firebase", "Firebase not initialized");
        } else {
            Log.d("Firebase", "Firebase initialized successfully");
        }

        // Check if user is already signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Auth", "User is already logged in: " + currentUser.getEmail());
            // If user is logged in, navigate to the TextEditorActivity
            navigateToTextEditor();
        } else {
            Log.d("Auth", "No user is logged in. Redirecting to AuthActivity.");
            // If not logged in, redirect to the login/register activity
            navigateToAuth();
        }

        // Example button to navigate manually to text editor (in case you want a button on this screen)
        Button editorButton = findViewById(R.id.button_open_editor);
        editorButton.setOnClickListener(v -> navigateToTextEditor());
    }

    private void navigateToTextEditor() {
        Intent editorIntent = new Intent(MainActivity.this, TextEditorActivity.class);
        startActivity(editorIntent);
        finish();
    }

    // Navigate to the Authentication Activity (for login/register)
    private void navigateToAuth() {
        Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(authIntent);
        finish(); // Close MainActivity after navigation
    }
}
