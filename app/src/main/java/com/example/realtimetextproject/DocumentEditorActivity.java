package com.example.realtimetextproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.IOException;

public class DocumentEditorActivity extends AppCompatActivity {

    private EditText etDocumentContent;
    private TextView tvLivePreview;  // TextView for live preview
    private String documentId;
    private FirebaseFirestore firestore;
    private DocumentReference documentRef;
    private Handler handler;
    private Runnable updateContentRunnable;

    private boolean isBold = false;
    private boolean isItalic = false;
    private int textColor = Color.BLACK;  // Default text color
    private int textSize = 16;  // Default text size

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_editor);

        firestore = FirebaseFirestore.getInstance();
        etDocumentContent = findViewById(R.id.etDocumentContent);
        tvLivePreview = findViewById(R.id.tvLivePreview);  // Initialize TextView

        handler = new Handler();
        documentId = getIntent().getStringExtra("documentId");

        if (documentId != null) {
            documentRef = firestore.collection("documents").document(documentId);
            fetchDocumentContent();
            setupTextWatcher();
            listenForDocumentChanges();
        }

        Button btnSaveToFile = findViewById(R.id.btnSaveToFile);
        Button btnSaveAndReturn = findViewById(R.id.btnSaveAndReturn);

        btnSaveToFile.setOnClickListener(v -> saveTextToFile());

        btnSaveAndReturn.setOnClickListener(v -> {
            saveDocumentAndReturn();
        });

        Button btnBold = findViewById(R.id.btnBold);
        Button btnItalic = findViewById(R.id.btnItalic);
        Button btnTextColor = findViewById(R.id.btnTextColor);
        Spinner spinnerTextSize = findViewById(R.id.spinnerTextSize);

        // Set up text size spinner
        setupTextSizeSpinner(spinnerTextSize);

        btnBold.setOnClickListener(v -> toggleBoldText());
        btnItalic.setOnClickListener(v -> toggleItalicText());
        btnTextColor.setOnClickListener(v -> changeTextColor());
    }

    private void setupTextWatcher() {
        etDocumentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (handler != null) {
                    handler.removeCallbacks(updateContentRunnable);
                }
                updateContentRunnable = () -> updateDocumentContent(s.toString());
                handler.postDelayed(updateContentRunnable, 500);

                // Update TextView with live content from EditText
                tvLivePreview.setText(s.toString());
                updateTextViewStyle();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateTextViewStyle() {
        tvLivePreview.setTypeface(null, (isBold ? Typeface.BOLD : Typeface.NORMAL) | (isItalic ? Typeface.ITALIC : Typeface.NORMAL));
        tvLivePreview.setTextColor(textColor);
        tvLivePreview.setTextSize(textSize);
    }

    private void toggleBoldText() {
        isBold = !isBold;
        updateTextViewStyle();
    }

    private void toggleItalicText() {
        isItalic = !isItalic;
        updateTextViewStyle();
    }

    private void changeTextColor() {
        // Cycle through a few colors for demo purposes
        if (textColor == Color.BLACK) {
            textColor = Color.RED;
        } else if (textColor == Color.RED) {
            textColor = Color.BLUE;
        } else {
            textColor = Color.BLACK;
        }
        updateTextViewStyle();
    }

    private void setupTextSizeSpinner(Spinner spinner) {
        Integer[] textSizeOptions = {12, 14, 16, 18, 20, 24, 30};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, textSizeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                textSize = textSizeOptions[position];
                updateTextViewStyle();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void fetchDocumentContent() {
        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Document document = documentSnapshot.toObject(Document.class);
                if (document != null) {
                    etDocumentContent.setText(document.getContent());
                    tvLivePreview.setText(document.getContent());  // Set initial content to TextView
                    updateTextViewStyle();  // Apply initial style
                }
            } else {
                Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateDocumentContent(String content) {
        documentRef.update("content", content)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {
                    Toast.makeText(DocumentEditorActivity.this, "Failed to update content: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void listenForDocumentChanges() {
        documentRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(DocumentEditorActivity.this, "Error fetching document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Document document = documentSnapshot.toObject(Document.class);
                if (document != null) {
                    String currentContent = etDocumentContent.getText().toString();
                    if (!currentContent.equals(document.getContent())) {
                        etDocumentContent.setText(document.getContent());
                        tvLivePreview.setText(document.getContent());  // Update live preview
                    }
                }
            }
        });
    }
    private void saveDocumentAndReturn() {
        String content = etDocumentContent.getText().toString();
        if (documentRef != null) {
            documentRef.update("content", content)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Document saved", Toast.LENGTH_SHORT).show();
                        // Navigate back to HomeActivity
                        finish();  // Ends current activity and returns to previous (HomeActivity)
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void saveTextToFile() {
        String content = etDocumentContent.getText().toString();
        String filename = "document_" + documentId + ".txt"; // Generate file name based on document ID

        try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
            fos.write(content.getBytes());
            Toast.makeText(this, "File saved: " + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
}
};
