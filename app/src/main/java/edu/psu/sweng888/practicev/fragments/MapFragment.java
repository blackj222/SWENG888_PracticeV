// MapFragment.java - Displays a Google Map with either saved or selected locations, allows place search and location selection

package edu.psu.sweng888.practicev.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.models.customPlace;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;
    private customPlace currentCustomPlace;
    private String source; // Tracks who launched the fragment
    private RelativeLayout buttonLayout;
    private static final String place_arg = "place_arg";
    private static final String source_arg = "source_arg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout containing map, search bar, and buttons
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        buttonLayout = view.findViewById(R.id.button_layout);

        // Retrieve source and place (if any) from arguments
        if (getArguments() != null) {
            source = getArguments().getString(source_arg, "");
            currentCustomPlace = getArguments().getParcelable(place_arg);
            if (AddItemFragment.class.toString().equals(source)) {
                buttonLayout.setVisibility(View.VISIBLE);
            } else {
                buttonLayout.setVisibility(View.GONE);
            }
        }

        // Initialize Google Map and search
        setupMap();
        setupAutocomplete();
        setupAddButton(view);

        return view;
    }

    // Initializes map fragment and sets callback
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
            getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "MapFragment is null", Toast.LENGTH_SHORT).show();
        }
    }

    // Initializes autocomplete search and place selection behavior
    private void setupAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
            getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            ));

            // Pre-fill autocomplete with existing place if available
            if (currentCustomPlace != null && currentCustomPlace.getAddress() != null) {
                autocompleteFragment.setText(currentCustomPlace.getAddress());
            }

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    if (mMap != null && place.getLatLng() != null) {
                        LatLng latLng = place.getLatLng();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(place.getAddress()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        currentCustomPlace = new customPlace(
                            place.getAddress(),
                            latLng.latitude,
                            latLng.longitude
                        );
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(getContext(),
                        "Place error: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Triggered once the Google Map is fully loaded
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng targetLatLng;
        String markerTitle = "Location";

        // Determine what to display depending on caller
        if (getArguments() != null) {
            source = getArguments().getString(source_arg, "");
            currentCustomPlace = getArguments().getParcelable(place_arg);

            if (AddItemFragment.class.toString().equals(source)) {
                buttonLayout.setVisibility(View.VISIBLE);

                // If valid place provided, show marker
                if (currentCustomPlace != null && currentCustomPlace.getLatitude() != 0 && currentCustomPlace.getLongitude() != 0) {
                    targetLatLng = new LatLng(currentCustomPlace.getLatitude(), currentCustomPlace.getLongitude());
                    markerTitle = currentCustomPlace.getAddress();
                    mMap.addMarker(new MarkerOptions().position(targetLatLng).title(markerTitle));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 14));
                } else {
                    // Default to Penn State if not available
                    targetLatLng = new LatLng(40.7934, -77.8600);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 5));
                }

                mMap.setMinZoomPreference(12);
            } else {
                buttonLayout.setVisibility(View.GONE);
                loadPlacesFromFirebaseAndAddMarkers();
            }
        }

        // Setup zoom controls
        FloatingActionButton zoomIn = requireView().findViewById(R.id.button_zoom_in);
        FloatingActionButton zoomOut = requireView().findViewById(R.id.button_zoom_out);

        zoomIn.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomIn()));
        zoomOut.setOnClickListener(v -> mMap.animateCamera(CameraUpdateFactory.zoomOut()));
    }

    // Loads all saved places from Firebase and adds them to the map
    private void loadPlacesFromFirebaseAndAddMarkers() {
        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference("places");

        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    customPlace place = child.getValue(customPlace.class);
                    if (place != null && place.getLatitude() != 0 && place.getLongitude() != 0) {
                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(place.getNickname() + " (" + place.getAddress() + ")")
                            .snippet(place.getDescription()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MapFragment", "Failed to load places: " + error.getMessage());
            }
        });
    }

    // Adds a button that sends the selected location back to the calling fragment
    public void setupAddButton(View view){
        Button addButton = view.findViewById(R.id.button_add);
        addButton.setOnClickListener(v -> {
            if (currentCustomPlace != null) {
                Bundle args = new Bundle();
                args.putParcelable(place_arg, currentCustomPlace);
                getParentFragmentManager().setFragmentResult(place_arg, args);
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Please select a place first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to create new MapFragment with arguments
    public static MapFragment newInstance(customPlace place, String source) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(place_arg, place);
        args.putString(source_arg, source);
        fragment.setArguments(args);
        return fragment;
    }
}
