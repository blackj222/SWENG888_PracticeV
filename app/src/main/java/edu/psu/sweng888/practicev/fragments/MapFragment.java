package edu.psu.sweng888.practicev.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import edu.psu.sweng888.practicev.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the settings layout
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Back button that goes back a fragment
        Button button = view.findViewById(R.id.button_return);
        button.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                requireActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // callback will go to onMapReady()
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // ✅ now you can safely interact with the map
        mMap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        createPolylinesOnMap();
    }

    private void createPolylinesOnMap() {
        // Define the Coordinates
        LatLng sydney = new LatLng(-34, 151);
        LatLng tokyo = new LatLng(35.67, 139.65);
        LatLng singapore = new LatLng(1.35, 103.81);

        // Add markers to specific location
        mMap.addMarker(new MarkerOptions().position(sydney).title("Sydney, Australia"));
        mMap.addMarker(new MarkerOptions().position(singapore).title("Singapore, Singapore"));
        mMap.addMarker(new MarkerOptions().position(tokyo).title("Tokyo, Japan"));

        // Configure the PolylineOptions to be displayed in map
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(15)
                .color(Color.RED)
                .startCap(new SquareCap())
                .endCap(new SquareCap());

        // Add the coordinates to be included in the Polyline
        polylineOptions.add(sydney, tokyo, singapore);

        // Add the Polyline to the map
        mMap.addPolyline(polylineOptions);

        // Call the move camera method to the new coordinate, and adjust the zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, 2));
    }

}
