package edu.psu.sweng888.practicev.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import edu.psu.sweng888.practicev.models.Place;

public class ItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private ArrayList<Place> placeList = new ArrayList<>();
    private DatabaseReference placesRef;
    private FloatingActionButton fabAddItem;
    private Button viewMapButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        // Firebase reference (node: places)
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        fabAddItem = view.findViewById(R.id.fabAddItem);
        viewMapButton = view.findViewById(R.id.view_map_button);

        // Adapter with delete functionality
        adapter = new PlaceAdapter(placeList, placesRef);

        recyclerView = view.findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadPlaces();

        // Find the FAB
        fabAddItem.setOnClickListener(v -> {
            // Replace current fragment with AddItemFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddItemFragment())
                    .addToBackStack(null) // allows going back with back button
                    .commit();
        });

        viewMapButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Listen for changes in the "places" node and update list
    private void loadPlaces() {
        placesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                placeList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Place place = data.getValue(Place.class);
                    if (place != null) {
                        placeList.add(place);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
