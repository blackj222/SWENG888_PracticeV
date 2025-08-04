package edu.psu.sweng888.practicev;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    final private String TAG = "register_activity";

    // UI elements for user input
    private EditText nameInput, emailInput, passwordInput, confirmInput;
    private Button registerButton;
    private ProgressBar progressBar;

    // Firebase authentication
    private FirebaseAuth mAuth;
    //private DatabaseReference usersRef;

    // Delay before closing activity after registration success
    private static final int LOAD_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind UI elements
        nameInput = findViewById(R.id.editTextName);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmInput = findViewById(R.id.editTextConfirm);
        registerButton = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        // usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Links Register button to registerUser() method
        registerButton.setOnClickListener(v -> registerUser());

        // Enable the back arrow in the top app bar
        Button btnGoBack = findViewById(R.id.goBackButton);
        btnGoBack.setOnClickListener(v -> finish());
    }

    // Validate input fields and attempt to create a new user in Firebase Authentication
    private void registerUser() {
        // Grab and trim input values
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirm = confirmInput.getText().toString().trim();

        // Validate each field
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be more than 6 characters");
            return;
        }
        if (!password.equals(confirm)) {
            confirmInput.setError("Passwords do not match");
            return;
        }

        // Show loading spinner
        progressBar.setVisibility(android.view.View.VISIBLE);

// Create a new user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User account successfully created
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        // Update the user's profile to include display name
                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                });

                        // Delay then move to mainActivity();
                        // so user can’t go back to splash
                        new Handler().postDelayed(this::finish, LOAD_DELAY);

                    } else {
                        // Registration failed
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        // Handle specific error: email already in use
                        if (Objects.requireNonNull(task.getException()).toString().contains("already in use")) {
                            Toast.makeText(RegisterActivity.this, "Email already exists.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        }
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
