package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupNavigationButtons();
    }
    private void setupNavigationButtons() {
        ImageView navButton1 = findViewById(R.id.nav_icon_1);
        navButton1.setOnClickListener(v -> {
            Intent nav = new Intent(getApplicationContext(), MainPage.class);
            startActivity(nav);
            finish();
        });

        // Navigation button 2 listener
        ImageView navButton2 = findViewById(R.id.nav_icon_2);
        navButton2.setOnClickListener(v ->{
                    Intent nav = new Intent(getApplicationContext(), Calendar.class);
                    startActivity(nav);
                    finish();
                }
        );

        // Navigation button 3 listener
        ImageView navButton3 = findViewById(R.id.nav_icon_3);
        navButton3.setOnClickListener(v -> {
            Intent nav = new Intent(getApplicationContext(), Training.class);
            startActivity(nav);
            finish();
        });

        // Navigation button 4 listener (Sign out)
        ImageView navButton4 = findViewById(R.id.nav_icon_4);
        navButton4.setOnClickListener(v -> {
           /* FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginPage.class));
            finish();*/
            Intent nav = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(nav);
            finish();
        });
    }
}