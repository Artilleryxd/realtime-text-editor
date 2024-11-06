package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delay to show the splash screen for 2 seconds
        new Handler().postDelayed(() -> {
            // Check if the user is already logged in
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();

            firebaseAuth.signOut();

            if (user != null) {
                // If user is logged in, send them to HomeActivity
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            } else {
                // If user is not logged in, send them to RegisterActivity
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }

            // Close MainActivity so the user can't return to it
            finish();
        }, 2000); // 2000ms = 2 seconds
    }
}
