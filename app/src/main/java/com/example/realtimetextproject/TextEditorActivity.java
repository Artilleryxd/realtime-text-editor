package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class TextEditorActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btnLogout;
    private EditText etTextEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        btnLogout = findViewById(R.id.btnLogout);
        etTextEditor = findViewById(R.id.etTextEditor);

        // Set up the Logout button listener
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    // Method to log out the user
    private void logoutUser() {
        firebaseAuth.signOut();
        Toast.makeText(TextEditorActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to AuthActivity (Login screen)
        Intent authIntent = new Intent(TextEditorActivity.this, AuthActivity.class);
        startActivity(authIntent);
        finish(); // Close the TextEditorActivity
    }
}
