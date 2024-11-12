package com.messagingapp.ping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class splash extends AppCompatActivity {
    private ImageView logo;
    private TextView slogan, footerOne, footerTwo;
    private Animation topAnime, bottomAnime;
    private FirebaseAuth mAuth;  // FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        logo = findViewById(R.id.logoImg);
        slogan = findViewById(R.id.sloganText);
        footerOne = findViewById(R.id.footer1);
        footerTwo = findViewById(R.id.footer2);

        // Load animations
        topAnime = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnime = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Set animations
        slogan.setAnimation(bottomAnime);
        footerOne.setAnimation(bottomAnime);
        footerTwo.setAnimation(bottomAnime);

        // Delay for splash screen
        new Handler().postDelayed(() -> {
            Intent intent;
            // Check if the user is already logged in
            if (mAuth.getCurrentUser() != null) {
                // User is already logged in, navigate to MyProfileActivity
                intent = new Intent(splash.this, MainActivity.class);
            } else {
                // User is not logged in, navigate to LoginActivity
                intent = new Intent(splash.this, login.class);
            }
            startActivity(intent);
            finish(); // Finish the splash activity to prevent returning to it
        }, 3000); // 3 seconds delay
    }
}
