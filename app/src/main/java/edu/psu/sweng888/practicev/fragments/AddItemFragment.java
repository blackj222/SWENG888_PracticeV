package edu.psu.sweng888.practicev.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.models.customPlace;

public class AddItemFragment extends BaseFragment {

    private EditText editTextNickname, editTextDescription;
    private TextView placeName, placeLong, placeLat, ratingTextView;
    private Button buttonSave, editLocationButton;
    Slider ratingSlider;
    private DatabaseReference placesRef;
    private customPlace passedPlace;
    private static final String place_arg = "place_arg";

    float rating;

    public AddItemFragment() {
        // Required empty public constructor
    }

    // Creates a new instance of this fragment with a customPlace argument
    public static AddItemFragment newInstance(customPlace place) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(place_arg, place);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the customPlace argument if available
        if (getArguments() != null) {
            passedPlace = getArguments().getParcelable(place_arg);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Listen for updates from a MapFragment location result
        getParentFragmentManager().setFragmentResultListener(
            place_arg,
            getViewLifecycleOwner(),
            (requestKey, bundle) -> {
                passedPlace = bundle.getParcelable(place_arg);
                if (passedPlace != null) {
                    // Update UI fields with selected location
                    placeName.setText(passedPlace.getAddress());
                    placeLat.setText(String.valueOf(passedPlace.getLatitude()));
                    placeLong.setText(String.valueOf(passedPlace.getLongitude()));
                }
            });

        // Initialize UI components
        editTextNickname = view.findViewById(R.id.nickname_edittext);
        editTextDescription = view.findViewById(R.id.editTextItemDescription);
        ratingSlider = view.findViewById(R.id.ratingSlider);
        buttonSave = view.findViewById(R.id.buttonSaveItem);
        editLocationButton = view.findViewById(R.id.button_edit_location);

        placesRef = FirebaseDatabase.getInstance().getReference("places");

        placeName = view.findViewById(R.id.name_textview);
        placeLong = view.findViewById(R.id.long_textview);
        placeLat = view.findViewById(R.id.lat_textview);
        ratingTextView = view.findViewById(R.id.rating_number);

        ratingSlider.setValue(0);

        // Pre-fill UI if editing an existing place
        if (passedPlace != null) {
            editTextNickname.setText(passedPlace.getNickname());
            editTextDescription.setText(passedPlace.getDescription());
            ratingSlider.setValue(passedPlace.getRating());
            ratingTextView.setText(String.valueOf(passedPlace.getRating()));
            rating = passedPlace.getRating();
            placeName.setText(passedPlace.getAddress());
            placeLat.setText(String.valueOf(passedPlace.getLatitude()));
            placeLong.setText(String.valueOf(passedPlace.getLongitude()));
        }

        // Update rating on slider change
        ratingSlider.addOnChangeListener((slider, value, fromUser) -> {
            rating = value;
            ratingTextView.setText(String.valueOf(value));
        });

        // Save button listener
        buttonSave.setOnClickListener(v -> saveItem());

        // Edit location button opens MapFragment
        editLocationButton.setOnClickListener(v -> {
            MapFragment mapFragment = MapFragment.newInstance(passedPlace, AddItemFragment.class.toString());

            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
        });
    }

    // Save or update item to Firebase
    private void saveItem() {
        String nickname = editTextNickname.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(nickname)) {
            editTextNickname.setError("Nickname required");
            return;
        }

        if (passedPlace == null) {
            editLocationButton.setError("Location required");
            Toast.makeText(getContext(), "You must select a location before saving!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Populate customPlace object
        passedPlace.setNickname(nickname);
        passedPlace.setDescription(description);
        passedPlace.setRating(rating);

        // Assign a UUID if new entry
        String id = passedPlace.getId();
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            passedPlace.setId(id);
        }

        // Save to Firebase Realtime Database
        placesRef.child(id).setValue(passedPlace)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Item saved!", Toast.LENGTH_SHORT).show();

                    // Clear backstack and return to ItemsFragment
                    requireActivity().getSupportFragmentManager().popBackStack(null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ItemsFragment())
                        .commit();

                    clearFields();
                } else {
                    Toast.makeText(getContext(),
                        "Save failed: " + (task.getException() != null
                            ? task.getException().getMessage()
                            : "Unknown error"),
                        Toast.LENGTH_LONG).show();
                }
            });
    }

    // Reset form fields
    private void clearFields() {
        editTextNickname.setText("");
        editTextDescription.setText("");
        ratingSlider.setValue(0);
    }

    // Handle back press with unsaved changes warning
    @Override
    public boolean onBackPressed() {
        if (hasUnsavedChanges()) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("You have unsaved changes. Do you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) ->
                    requireActivity().getSupportFragmentManager().popBackStack())
                .setNegativeButton("Cancel", null)
                .show();
            return true; // back press handled
        }
        return false; // allow normal behavior
    }

    // Check if form has unsaved changes
    private boolean hasUnsavedChanges() {
        return (TextUtils.isEmpty(editTextNickname.getText().toString().trim()) || passedPlace == null);
    }
}
