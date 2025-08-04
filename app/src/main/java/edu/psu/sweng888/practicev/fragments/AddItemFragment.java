package edu.psu.sweng888.practicev.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.models.Place;

public class AddItemFragment extends Fragment {

    // Input fields and save button
    private EditText editTextName, editTextDescription, editTextRating;
    private Button buttonSave;

    // Firebase database reference
    private DatabaseReference placesRef;

    public AddItemFragment() {
        // Empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout for adding a new item
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind UI elements
        editTextName = view.findViewById(R.id.editTextItemName);
        editTextDescription = view.findViewById(R.id.editTextItemDescription);
        editTextRating = view.findViewById(R.id.editTextItemRating);
        buttonSave = view.findViewById(R.id.buttonSaveItem);

        // Point reference to the "places" node in Firebase
        placesRef = FirebaseDatabase.getInstance().getReference("places");

        // Save button click handler
        buttonSave.setOnClickListener(v -> saveItem());
    }

    // Validate input and save new place to Firebase
    private void saveItem() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String ratingStr = editTextRating.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name required");
            return;
        }
        // Validate rating
        if (TextUtils.isEmpty(ratingStr)) {
            editTextRating.setError("Rating required");
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            editTextRating.setError("Invalid rating");
            return;
        }

        // Create place with unique ID
        String id = UUID.randomUUID().toString();
        Place place = new Place(id, name, description, rating);

        // Push to Firebase
        placesRef.child(id).setValue(place)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Item added!", Toast.LENGTH_SHORT).show();
                        clearFields(); // reset form
                    } else {
                        Toast.makeText(getContext(),
                                "Failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Clear input fields after successful save
    private void clearFields() {
        editTextName.setText("");
        editTextDescription.setText("");
        editTextRating.setText("");
    }
}
