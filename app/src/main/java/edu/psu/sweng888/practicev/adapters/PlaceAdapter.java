package edu.psu.sweng888.practicev.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.models.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private ArrayList<Place> places;        // list of places to display
    private DatabaseReference placesRef;      // reference to Firebase "places" node

    public PlaceAdapter(ArrayList<Place> places, DatabaseReference placesRef) {
        this.places = places;
        this.placesRef = placesRef;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single row layout for each place
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        // Bind place data to UI
        Place place = places.get(position);

        holder.nameTextView.setText(place.getName());
        holder.descriptionTextView.setText(place.getDescription());
        holder.ratingTextView.setText(String.valueOf(place.getRating()));

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (place.getId() == null) {
                Toast.makeText(holder.itemView.getContext(),
                        "Invalid place ID, cannot delete.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show confirmation dialog before deleting
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"" + place.getName() + "\"?")
                    .setPositiveButton("Yes", (dialog, which) ->
                            // Remove place from Firebase on confirmation
                            placesRef.child(place.getId()).removeValue()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(holder.itemView.getContext(),
                                                    "Deleted " + place.getName(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(holder.itemView.getContext(),
                                                    "Delete failed: " + (task.getException() != null
                                                            ? task.getException().getMessage()
                                                            : ""),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return places.size(); // number of places in the list
    }

    // ViewHolder holds references to the UI elements in each row
    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView, ratingTextView;
        Button deleteButton;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            descriptionTextView = itemView.findViewById(R.id.textViewDesc);
            ratingTextView = itemView.findViewById(R.id.textViewRating);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
