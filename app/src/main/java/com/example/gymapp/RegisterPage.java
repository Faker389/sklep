package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;

    private TextView backToLoginText;
    private TextInputEditText emailField, passwordField, confirmPasswordField, usernameField;
    private Button registerButton;

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        initFirebase();
        initUI();
        setupGoogleSignIn();
        handleWindowInsets();
        setOnClickListeners();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initUI() {
        registerButton = findViewById(R.id.registerButton);
        backToLoginText = findViewById(R.id.backToLoginText);
        usernameField = findViewById(R.id.usernameInput);
        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        confirmPasswordField = findViewById(R.id.confirmPasswordInput);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), options);
    }

    private void setOnClickListeners() {
        registerButton.setOnClickListener(v -> handleUserRegistration());

        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.signIn).setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(intent);
        });
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    handleGoogleSignInResult(result.getData());
                }
            }
    );

    private void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            if (account != null) {
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(authCredential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                navigateToMainPage();
                            } else {
                                showToast("Authentication failed");
                            }
                        });
            }
        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Google sign-in failed", e);
        }
    }

    private void handleUserRegistration() {
        String emailText = emailField.getText().toString().trim();
        String passwordText = passwordField.getText().toString().trim();
        String confirmPasswordText = confirmPasswordField.getText().toString().trim();
        String usernameText = usernameField.getText().toString().trim();

        if (validateRegistrationFields(emailText, passwordText, confirmPasswordText)) {
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserData(usernameText, emailText);
                        } else {
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            showToast("Authentication failed: " + errorMessage);
                        }
                    });
        }
    }

    private boolean validateRegistrationFields(String emailText, String passwordText, String confirmPasswordText) {
        if (TextUtils.isEmpty(emailText)) {
            showToast("Email cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)) {
            showToast("Password cannot be empty");
            return false;
        }
        if (!passwordText.equals(confirmPasswordText)) {
            showToast("Passwords do not match");
            return false;
        }
        if (!emailText.contains("@")) {
            showToast("Invalid email");
            return false;
        }
        return true;
    }

    private void saveUserData(String username, String email) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", email);
            userData.put("exp", 0);
            userData.put("level", 1);

            db.collection("users").document(uid)
                    .set(userData)
                    .addOnFailureListener(e -> showToast("Failed to store user data"));

            navigateToMainPage();
        }
    }

    private void checkIfUserLoggedIn() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainPage();
        }
    }

    private void navigateToMainPage() {
        Intent nav = new Intent(getApplicationContext(), MainPage.class);
        startActivity(nav);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
