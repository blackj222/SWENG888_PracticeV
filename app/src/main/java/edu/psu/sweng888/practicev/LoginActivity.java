package edu.psu.sweng888.practicev;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // UI fields
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignup = findViewById(R.id.textViewSignup);

        // Binds buttonLogin to attemptLogin() method
        buttonLogin.setOnClickListener(v -> attemptLogin());

        // Allows user to sign up
        textViewSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Called when the login button is clicked.
     * Validates input and tries to sign in with Firebase Authentication.
     */
    private void attemptLogin() {
        // Get the input values and trim whitespace
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }
        // Validate password
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }

        // Attempt to sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed in
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d("LoginActivity", "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this,
                                "Welcome " + (user != null ? user.getEmail() : ""),
                                Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity and close LoginActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Sign in failed, log the error and show a message
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this,
                                "Authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If so, skip the login screen and go directly to MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}