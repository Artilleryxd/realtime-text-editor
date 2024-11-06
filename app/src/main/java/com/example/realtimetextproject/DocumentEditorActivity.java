package com.example.realtimetextproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.IOException;

public class DocumentEditorActivity extends AppCompatActivity {

    private EditText etDocumentContent;
    private String documentId;
    private FirebaseFirestore firestore;
    private DocumentReference documentRef;

    private Handler handler;  // Declare Handler here
    private Runnable updateContentRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_editor);

        firestore = FirebaseFirestore.getInstance();
        etDocumentContent = findViewById(R.id.etDocumentContent);

        // Initialize the handler here to avoid the null pointer exception
        handler = new Handler();

        // Get documentId from intent extras
        documentId = getIntent().getStringExtra("documentId");

        if (documentId != null) {
            documentRef = firestore.collection("documents").document(documentId);

            // Fetch initial document content
            fetchDocumentContent();

            // Add real-time listener to update Firestore when content changes
            etDocumentContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // Remove any previous scheduled update task
                    if (handler != null) {
                        handler.removeCallbacks(updateContentRunnable);
                    }

                    // Schedule the update of Firestore after a delay (debouncing)
                    updateContentRunnable = new Runnable() {
                        @Override
                        public void run() {
                            updateDocumentContent(charSequence.toString());
                        }
                    };

                    // Run the update after 500ms
                    handler.postDelayed(updateContentRunnable, 500);
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            // Listen for changes to the document content in real-time
            listenForDocumentChanges();
        }

        // Save document button functionality
        Button btnSaveDocument = findViewById(R.id.btnSaveDocument);
        btnSaveDocument.setOnClickListener(v -> saveDocumentToFirestore());

        // Save to file button functionality
        Button btnSaveToFile = findViewById(R.id.btnSaveToFile);
        btnSaveToFile.setOnClickListener(v -> saveDocumentToFile());
    }

    // Fetch document content from Firestore
    private void fetchDocumentContent() {
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Document document = documentSnapshot.toObject(Document.class);
                if (document != null) {
                    etDocumentContent.setText(document.getContent());
                }
            } else {
                Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Real-time listener for changes in the document
    private void listenForDocumentChanges() {
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(DocumentEditorActivity.this, "Error fetching document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Document document = documentSnapshot.toObject(Document.class);
                if (document != null) {
                    // Update the EditText only if the content in Firestore changes
                    String currentContent = etDocumentContent.getText().toString();
                    if (!currentContent.equals(document.getContent())) {
                        etDocumentContent.setText(document.getContent());
                    }
                }
            }
        });
    }

    // Update the document content in Firestore
    private void updateDocumentContent(String content) {
        documentRef.update("content", content)
                .addOnSuccessListener(aVoid -> {
                    // Content successfully updated in Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(DocumentEditorActivity.this, "Failed to update content: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Save document content to Firestore
    private void saveDocumentToFirestore() {
        String content = etDocumentContent.getText().toString();
        if (!content.isEmpty()) {
            updateDocumentContent(content);

            // Show success toast
            Toast.makeText(this, "Document saved successfully!", Toast.LENGTH_SHORT).show();

            // Navigate back to the HomeActivity
            Intent intent = new Intent(DocumentEditorActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            Toast.makeText(this, "Cannot save empty document", Toast.LENGTH_SHORT).show();
        }
    }

    // Save document content to a local file
    private void saveDocumentToFile() {
        String content = etDocumentContent.getText().toString();
        if (!content.isEmpty()) {
            // Save to a file
            try {
                FileOutputStream fos = openFileOutput("document.txt", Context.MODE_PRIVATE);
                fos.write(content.getBytes());
                fos.close();
                Toast.makeText(this, "Document saved to file!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save document to file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot save empty document", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler
        if (handler != null) {
            handler.removeCallbacks(updateContentRunnable);
        }
    }
}
