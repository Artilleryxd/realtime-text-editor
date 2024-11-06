package com.example.realtimetextproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private Button btnCreateDocument;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private LinearLayout documentsLayout;  // To hold clickable document images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize the layout to display document images
        documentsLayout = findViewById(R.id.documentsLayout);

        // Button to create a new document
        btnCreateDocument = findViewById(R.id.btnCreateDocument);

        // Set up the create document button listener
        btnCreateDocument.setOnClickListener(v -> showCreateDocumentDialog());

        // Fetch and display all documents
        fetchDocuments();
    }

    // Show dialog to create a new document
    private void showCreateDocumentDialog() {
        // Create an input dialog to get the document name
        final EditText documentNameEditText = new EditText(this);
        documentNameEditText.setHint("Enter document name");

        new AlertDialog.Builder(this)
                .setTitle("Create New Document")
                .setMessage("Enter a name for your new document")
                .setView(documentNameEditText)
                .setPositiveButton("Create", (dialog, which) -> {
                    String documentName = documentNameEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(documentName)) {
                        createDocumentInFirestore(documentName);
                    } else {
                        Toast.makeText(HomeActivity.this, "Document name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Fetch all documents from Firestore and display them as clickable images
    private void fetchDocuments() {
        firestore.collection("documents")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        // Clear previous documents (if any)
                        documentsLayout.removeAllViews();

                        // Loop through all documents and create an image for each
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();
                            String documentName = documentSnapshot.getString("name");  // Assume document has a 'name' field

                            // Create an ImageView for each document
                            ImageView documentImageView = new ImageView(HomeActivity.this);
                            // Set a placeholder image (e.g., an icon)
                            documentImageView.setImageResource(R.drawable.ic_launcher_background);

                            // Add a click listener to open the document editor
                            documentImageView.setOnClickListener(v -> openDocumentEditor(documentId));

                            // Optionally set the image size and layout properties
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
                            params.setMargins(8, 8, 8, 8);  // Add some margins between images
                            documentImageView.setLayoutParams(params);

                            // Add the image view to the layout
                            documentsLayout.addView(documentImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to fetch documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Create the document in Firestore with initial empty content
    private void createDocumentInFirestore(String documentName) {
        // Get the current user UID
        String userId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Create a document object
            Document document = new Document(documentName, userId, "");

            // Save document to Firestore
            firestore.collection("documents")
                    .add(document)
                    .addOnSuccessListener(documentReference -> {
                        // On success, open the document in editor mode
                        String documentId = documentReference.getId();
                        openDocumentEditor(documentId);
                    })
                    .addOnFailureListener(e -> {
                        // Show error if document creation failed
                        Toast.makeText(HomeActivity.this, "Failed to create document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(HomeActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    // Open the document editor activity
    private void openDocumentEditor(String documentId) {
        Intent intent = new Intent(HomeActivity.this, DocumentEditorActivity.class);
        intent.putExtra("documentId", documentId);  // Pass the document ID to the editor activity
        startActivity(intent);
    }
}
