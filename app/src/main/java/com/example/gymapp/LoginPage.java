package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    private TextView registerText;
    private TextInputEditText emailField, passwordField;
    private Button loginButton;

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        initFirebase();
        initUI();
        setupGoogleSignIn();
        setOnClickListeners();
        handleWindowInsets();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        registerText = findViewById(R.id.registerText);
        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), options);
    }

    private void setOnClickListeners() {
        loginButton.setOnClickListener(v -> handleEmailPasswordLogin());

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterPage.class);
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
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            if (account != null) {
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToMainPage();
                    } else {
                        showToast("Authentication failed");
                    }
                });
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void handleEmailPasswordLogin() {
        String emailText = emailField.getText().toString().trim();
        String passwordText = passwordField.getText().toString().trim();

        if (validateLoginFields(emailText, passwordText)) {
            auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Authentication successful.");
                            navigateToMainPage();
                        } else {
                            showToast("Authentication failed.");
                        }
                    });
        }
    }

    private boolean validateLoginFields(String emailText, String passwordText) {
        if (TextUtils.isEmpty(emailText)) {
            showToast("Email cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(passwordText)) {
            showToast("Password cannot be empty");
            return false;
        }
        return true;
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
