package com.example.realtimetextproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class DocumentEditorActivity extends AppCompatActivity {

    private EditText etDocumentContent;
    private String documentId;
    private FirebaseFirestore firestore;
    private DocumentReference documentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_editor);

        firestore = FirebaseFirestore.getInstance();

        etDocumentContent = findViewById(R.id.etDocumentContent);
        documentId = getIntent().getStringExtra("documentId");

        if (documentId != null) {
            documentRef = firestore.collection("documents").document(documentId);

            // Fetch and display document content
            documentRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Document document = documentSnapshot.toObject(Document.class);
                    if (document != null) {
                        etDocumentContent.setText(document.getContent());
                    }
                } else {
                    Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show();
                }
            });

            // Add real-time listener to update Firestore when content changes
            etDocumentContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    // Update the document content in Firestore in real-time
                    updateDocumentContent(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
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
}
