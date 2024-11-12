package com.messagingapp.ping;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// The login class handles user authentication and login using Firebase.
public class login extends AppCompatActivity {

    // UI elements and Firebase authentication instance
    Button button;           // Button for login action
    EditText email, password; // Input fields for email and password
    FirebaseAuth auth;        // Firebase Authentication instance
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Regex pattern for validating email format
    TextView signUpText;
    android.app.ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display for immersive mode
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Adjust the padding of the main view based on system window insets (like status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in, please wait....");
        progressDialog.setCancelable(false);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // **Check if the user is already logged in**
//        if (auth.getCurrentUser() != null) {
//            // User is logged in, navigate to MainActivity and finish the login activity
//            Intent intent = new Intent(login.this, MainActivity.class);
//            startActivity(intent);
//            finish(); // Close the login activity
//            return;
//        }

        // Link UI elements to corresponding views in the layout
        button = findViewById(R.id.logButton);
        email = findViewById(R.id.editTextLogEmail);
        password = findViewById(R.id.editTextLogPassword);
        signUpText = findViewById(R.id.newUserSignUp);

        // Set onClickListener to handle login button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the email and password entered by the user
                String Email = email.getText().toString();
                String Password = password.getText().toString();

                // Check if email is empty
                if (TextUtils.isEmpty(Email)) {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                }
                // Check if password is empty
                else if (TextUtils.isEmpty(Password)) {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
                }
                // Check if email matches the pattern
                else if (!Email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Provide a proper email address");
                }
                // Check if password is at least 6 characters long
                else if (password.length() < 6) {
                    progressDialog.dismiss();
                    password.setError("Password must be more than 6 characters.");
                    Toast.makeText(login.this, "Password should be longer than 6 characters", Toast.LENGTH_SHORT).show();
                }
                // If all inputs are valid, attempt Firebase login
                else {
                    auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Show the progress dialog when login is successful
                                progressDialog.show();

                                // Add a delay to ensure the progress dialog is shown for 1 second
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Dismiss the progress dialog after 1 second
                                        progressDialog.dismiss();

                                        // Navigate to the main activity after login is successful
                                        try {
                                            Intent intent = new Intent(login.this, MainActivity.class);
                                            startActivity(intent);
                                            finish(); // Close the login activity
                                        } catch (Exception e) {
                                            // Show error message if any exception occurs during intent creation
                                            Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, 1000); // Delay for 1 second (1000 milliseconds)
                            } else {
                                // Display error message if login fails
                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // Set onClickListener to handle Sign Up TextView click
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the registration activity
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
            }
        });
    }
}
