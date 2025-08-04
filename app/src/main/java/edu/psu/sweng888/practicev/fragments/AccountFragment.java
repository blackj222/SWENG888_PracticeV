package edu.psu.sweng888.practicev.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.psu.sweng888.practicev.R;

public class AccountFragment extends Fragment {

    private TextView textViewName;
    private TextView textViewEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        textViewName = view.findViewById(R.id.textViewAccountName);
        textViewEmail = view.findViewById(R.id.textViewAccountEmail);

        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Fetches display name from database if stored during registration
            String nameString = "Name: " + (user.getDisplayName() != null ? user.getDisplayName() : "Not set");
            String emailString = "Email: " + user.getEmail();

            textViewName.setText(nameString);
            textViewEmail.setText(emailString);
        }

        return view;
    }
}
