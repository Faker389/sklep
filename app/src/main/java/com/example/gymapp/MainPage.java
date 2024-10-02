package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainPage extends AppCompatActivity {

    private ProgressBar customProgressBar;
    private TextView currentLevelText, nextLevelText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ExpManager experience;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        initUI();

        // Retrieve user data from Firestore
        retrieveUserData();

        // Setup event listeners for navigation buttons
        setupNavigationButtons();
    }

    private void initUI() {
        customProgressBar = findViewById(R.id.customProgressBar);
        currentLevelText = findViewById(R.id.currentLevelText);
        nextLevelText = findViewById(R.id.nextLevelText);
    }

    private void retrieveUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();  // Get the current user's UID

            // Firestore document reference for the user's data
            DocumentReference docRef = db.collection("users").document(uid);

            // Retrieve user data (XP and level) from Firestore
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get XP and level from Firestore and update the UI
                    int expValue = documentSnapshot.getLong("XPAmount").intValue();
                    int level = documentSnapshot.getLong("level").intValue();
                    experience = new ExpManager(customProgressBar, currentLevelText, nextLevelText, level, expValue);
                }
            }).addOnFailureListener(e -> {
                // Handle Firestore retrieval failure
                Log.e("Firestore Error", "Error fetching document", e);
            });
        }
    }

    private void setupNavigationButtons() {
        // Navigation button 1 (example: gain XP)
        ImageView navButton1 = findViewById(R.id.navButton1);
        navButton1.setOnClickListener(v -> {
           experience.gainXP(20);
        });

        // Navigation button 2 listener
        ImageView navButton2 = findViewById(R.id.navButton2);
        navButton2.setOnClickListener(v ->
                Toast.makeText(this, "Navigation 2 Clicked", Toast.LENGTH_SHORT).show()
        );

        // Navigation button 3 listener
        ImageView navButton3 = findViewById(R.id.navButton3);
        navButton3.setOnClickListener(v ->
                Toast.makeText(this, "Navigation 3 Clicked", Toast.LENGTH_SHORT).show()
        );

        // Navigation button 4 listener (Sign out)
        ImageView navButton4 = findViewById(R.id.navButton4);
        navButton4.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
            finish();
        });
    }
}
