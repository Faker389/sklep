package com.example.gymapp;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExpManager {

    private static final String TAG = "ExpManager";

    private ProgressBar customProgressBar;
    private TextView currentLevelText, nextLevelText;
    private int currentXP;  // XP over the current level
    private int xpToNextLevel;
    private int currentLevel;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Handler handler = new Handler();

    public ExpManager(ProgressBar progressBar, TextView currentLevelText, TextView nextLevelText, int currentLevel, int currentXP) {
        this.customProgressBar = progressBar;
        this.currentLevelText = currentLevelText;
        this.nextLevelText = nextLevelText;

        this.currentLevel = currentLevel;
        this.xpToNextLevel = 100 + (currentLevel * 50);  // XP needed for the next level
        this.currentXP = currentXP;  // Ensure currentXP doesn't exceed xpToNextLevel

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Update UI initially
        updateUI();
    }

    // Gain XP and update progress, possibly leveling up
    public void gainXP(int xp) {
        int previousXP = currentXP;
        currentXP += xp;

        if (currentXP >= xpToNextLevel) {
            int remainingXP = currentXP - xpToNextLevel;
            currentLevel++;
            currentXP = 0;  // Reset XP to zero after leveling up
            xpToNextLevel = 100 + (currentLevel * 50);
                    levelUp(); // Handle leveling up
                    gainXP(remainingXP);  // Continue with the remaining XP after leveling up
        } else {
            customProgressBar.setProgress(previousXP+xp);
        }

        updateFirestore();
    }

    // Level up the user, reset current XP, and update Firestore
    private void levelUp() {
        // Calculate XP for the next level

        // Update the level and next level text
        currentLevelText.setText(String.valueOf(currentLevel));
        nextLevelText.setText(String.valueOf(currentLevel + 1));

        // Reset the progress bar
        customProgressBar.setProgress(0);
        customProgressBar.setMax(xpToNextLevel);

        // Update Firestore with the new total XP and level
        updateFirestore();
    }

    // Calculate progress percentage based on current XP and XP needed for the next level
    private void updateFirestore() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userDoc = db.collection("users").document(uid);

            userDoc.update("level", currentLevel, "XPAmount", currentXP)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User data updated successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating Firestore: " + e.getMessage(), e));
        } else {
            Log.e(TAG, "User is not authenticated.");
        }
    }

    // Method to update the UI for the current level and XP
    public void updateUI() {
        currentLevelText.setText(String.valueOf(currentLevel));
        nextLevelText.setText(String.valueOf(currentLevel + 1));
        customProgressBar.setMax(xpToNextLevel);
        customProgressBar.setProgress(currentXP);
    }
}
