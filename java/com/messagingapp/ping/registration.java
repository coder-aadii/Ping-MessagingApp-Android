package com.messagingapp.ping;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {

    // UI elements
    TextView loginBut;
    EditText rg_fullName, rg_email, rg_password, rg_conf_password, rg_dob;
    Button rg_signup;
    RadioGroup rg_gender;
    RadioButton rg_male, rg_female;
    CircleImageView rg_profileImg;

    // Firebase instances
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog progressDialog;

    // URI for selected image
    Uri imageURI;
    String imageUri;

    // Email pattern for validation
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    // SharedPreferences constants for saving gender
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_GENDER = "gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Adjust layout insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up new account");
        progressDialog.setCancelable(false);

        // Initialize UI components
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        loginBut = findViewById(R.id.rgLogin);
        rg_fullName = findViewById(R.id.rgFullname);
        rg_email = findViewById(R.id.rgEmail);
        rg_password = findViewById(R.id.rgPassword);
        rg_conf_password = findViewById(R.id.rgConfPassword);
        rg_gender = findViewById(R.id.rgRadioGroupGender);
        rg_male = findViewById(R.id.rgRadioMale);
        rg_female = findViewById(R.id.rgRadioFemale);
        rg_profileImg = findViewById(R.id.profilerg0);
        rg_signup = findViewById(R.id.signupButton);
        rg_dob = findViewById(R.id.rgDob);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Load saved gender preference
        loadGenderPreference();

        // Date of Birth picker dialog
        rg_dob.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    registration.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        rg_dob.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Redirect to login activity
        loginBut.setOnClickListener(view -> {
            Intent intent = new Intent(registration.this, login.class);
            startActivity(intent);
            finish();
        });

        // Handle gender selection and profile picture change
        rg_gender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rgRadioMale) {
                rg_profileImg.setImageResource(R.drawable.male); // Set male image
                saveGenderPreference("Male");
            } else if (checkedId == R.id.rgRadioFemale) {
                rg_profileImg.setImageResource(R.drawable.female); // Set female image
                saveGenderPreference("Female");
            }
        });

        // Signup button click listener
        rg_signup.setOnClickListener(view -> {
            String nameString = rg_fullName.getText().toString();
            String emailString = rg_email.getText().toString();
            String passwordString = rg_password.getText().toString();
            String conPasswordString = rg_conf_password.getText().toString();
            String dobString = rg_dob.getText().toString();
            String genderString = rg_gender.getCheckedRadioButtonId() == R.id.rgRadioMale ? "Male" : "Female";
            String lastMessageString = "Last message not available";
            String status = "Hey, I'm on Ping.";

            //Gender selection logic
            int selectedGenderId = rg_gender.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(registration.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                return;
            } else if (selectedGenderId == R.id.rgRadioMale) {
                genderString = "Male";
            } else if (selectedGenderId == R.id.rgRadioFemale) {
                genderString = "Female";
            }

            // Validate user inputs
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(conPasswordString)) {
                progressDialog.dismiss();
                Toast.makeText(registration.this, "Please enter valid information", Toast.LENGTH_SHORT).show();
            } else if (!emailString.matches(emailPattern)) {
                progressDialog.dismiss();
                rg_email.setError("Enter a valid Email.");
            } else if (passwordString.length() < 6) {
                progressDialog.dismiss();
                rg_password.setError("Password must be at least 6 characters.");
            } else if (!passwordString.equals(conPasswordString)) {
                progressDialog.dismiss();
                rg_password.setError("Passwords do not match.");
            } else {
                // Register the user in Firebase
                String finalGenderString1 = genderString;
                auth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        DatabaseReference reference = database.getReference().child("user").child(id);

                        // Handle profile image logic
                        final String finalGenderString = finalGenderString1;  // Make genderString final by assigning it to a new variable
                        if (imageURI != null) {
                            StorageReference storageReference = storage.getReference().child("upload").child(id);
                            storageReference.putFile(imageURI).addOnCompleteListener(imageTask -> {
                                if (imageTask.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        imageUri = uri.toString();
                                        Users user = new Users(id, nameString, emailString, passwordString, imageUri, finalGenderString, dobString, lastMessageString, status);
                                        saveUserToDatabase(reference, user);
                                    });
                                }
                            });
                        } else {
                            // Use default profile image based on gender
                            imageUri = finalGenderString.equals("Male")
                                    ? "https://firebasestorage.googleapis.com/v0/b/ping-3c0cc.appspot.com/o/male.png?alt=media&token=803ce5d8-e23a-464f-a120-f4c7d69ec512"
                                    : "https://firebasestorage.googleapis.com/v0/b/ping-3c0cc.appspot.com/o/female.png?alt=media&token=77ef2d5a-3526-4377-82e8-20b00f62f990";
                            Users user = new Users(id, nameString, emailString, passwordString, imageUri, finalGenderString, dobString, lastMessageString, status);
                            saveUserToDatabase(reference, user);
                        }
                    } else {
                        Toast.makeText(registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Handle profile image selection
        rg_profileImg.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            imageURI = data.getData();
            rg_profileImg.setImageURI(imageURI); // Display selected image in ImageView
        }
    }


    // Save gender preference to SharedPreferences
    private void saveGenderPreference(String gender) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    // Load gender preference from SharedPreferences
    private void loadGenderPreference() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String gender = preferences.getString(KEY_GENDER, null); // Use null for no preference

        // If no gender preference is saved, do not change the default image (photocamera)
        if (gender != null) {
            if (gender.equals("Male")) {
                rg_male.setChecked(true);
                rg_profileImg.setImageResource(R.drawable.male);
            } else if (gender.equals("Female")) {
                rg_female.setChecked(true);
                rg_profileImg.setImageResource(R.drawable.female);
            }
        }
    }


    // Save user data to Firebase Realtime Database
    private void saveUserToDatabase(DatabaseReference reference, Users user) {
        reference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Show the progress dialog after successful registration
                progressDialog.show();

                // Add a delay to show the progress dialog for 1 second
                new android.os.Handler().postDelayed(() -> {
                    // Dismiss the progress dialog after 1 second
                    progressDialog.dismiss();

                    // Show a success message
                    Toast.makeText(registration.this, "Registration successful", Toast.LENGTH_SHORT).show();

                    // Redirect to MainActivity
                    startActivity(new Intent(registration.this, MainActivity.class));
                    finish();
                }, 1000); // 1 second delay (1000 milliseconds)
            } else {
                // Show error message if registration fails
                Toast.makeText(registration.this, "Failed to register. Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
