// ItemsFragment.java - Displays a list of saved places and provides navigation to add or view them on the map

package edu.psu.sweng888.practicev.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.adapters.PlaceAdapter;
import edu.psu.sweng888.practicev.models.customPlace;

public class ItemsFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private ArrayList<customPlace> customPlaceList = new ArrayList<>();
    private DatabaseReference placesRef;
    private FloatingActionButton fabAddItem;
    private Button viewMapButton;
    private static final String place_arg = "place_arg";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase reference
        placesRef = FirebaseDatabase.getInstance().getReference("places");

        // Listen for results from other fragments, like MapFragment
        getParentFragmentManager().setFragmentResultListener(
            place_arg, this, (requestKey, bundle) -> {
                customPlace selectedCustomPlace = bundle.getParcelable(place_arg);

                if (selectedCustomPlace != null) {
                    // Assign a new ID and save to Firebase
                    String id = placesRef.push().getKey();
                    selectedCustomPlace.setId(id);
                    if (id != null) {
                        placesRef.child(id).setValue(selectedCustomPlace);
                    } else {
                        Log.d("practice_V_log", "selectedPlace's id is null");
                    }

                    Toast.makeText(getContext(), "Place added: " + selectedCustomPlace.getAddress(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout for the fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        // Initialize UI components
        fabAddItem = view.findViewById(R.id.fabAddItem);
        viewMapButton = view.findViewById(R.id.view_map_button);
        recyclerView = view.findViewById(R.id.recyclerViewItems);

        // Set up adapter with click listener to open AddItemFragment for editing
        adapter = new PlaceAdapter(customPlaceList, placesRef, place -> {
            AddItemFragment fragment = AddItemFragment.newInstance(place);
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load places from Firebase
        loadPlaces();

        // FAB opens AddItemFragment to create new place
        fabAddItem.setOnClickListener(v ->
            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddItemFragment())
                .addToBackStack(null)
                .commit()
        );

        // View map button opens MapFragment with source info
        Bundle args = new Bundle();
        args.putString("source", "Items");

        viewMapButton.setOnClickListener(v -> {
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(args);

            requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
        });

        return view;
    }

    // Load list of places from Firebase Realtime Database
    private void loadPlaces() {
        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customPlaceList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    customPlace place = data.getValue(customPlace.class);
                    if (place != null) {
                        customPlaceList.add(place);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("itemsFragment", "Failed to load items.");
            }
        });
    }
}
