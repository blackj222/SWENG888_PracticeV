package edu.psu.sweng888.practicev.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.psu.sweng888.practicev.LoginActivity;
import edu.psu.sweng888.practicev.R;

public class AccountFragment extends Fragment {

    private TextView textViewName, textViewEmail;
    private ImageButton deleteAccountButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize UI components
        textViewName = view.findViewById(R.id.textViewAccountName);
        textViewEmail = view.findViewById(R.id.textViewAccountEmail);
        deleteAccountButton = view.findViewById(R.id.delete_account_button);

        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Fetches display name from database if stored during registration
            String nameString = "Name: " + (user.getDisplayName() != null ? user.getDisplayName() : "Not set");
            String emailString = "Email: " + user.getEmail();

            textViewName.setText(nameString);
            textViewEmail.setText(emailString);
        }

        // This delete button uses the user's password to reauthenticate them before deleting their account
        deleteAccountButton.setOnClickListener(v -> {
            EditText editTextPassword = view.findViewById(R.id.editTextPassword);;
            String password = editTextPassword.getText().toString().trim();

            // Validate password
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password is required");
                return;
            }

            if (user != null && user.getEmail() != null) {
                String email = user.getEmail();

                AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                user.reauthenticate(credential)
                    .addOnCompleteListener(reauthTask -> {
                        if (reauthTask.isSuccessful()) {
                            // Now you can safely delete the account
                            user.delete()
                                .addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        // Handle deletion failure
                                        Toast.makeText(getContext(), "Delete failed: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        } else {
                            Toast.makeText(getContext(), "Reauthentication failed: " + reauthTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });

        return view;
    }
}
