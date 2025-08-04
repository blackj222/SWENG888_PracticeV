package edu.psu.sweng888.practicev.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.psu.sweng888.practicev.R;
import edu.psu.sweng888.practicev.models.customPlace;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private ArrayList<customPlace> customPlaces;        // list of places to display
    private DatabaseReference placesRef;      // reference to Firebase "places" node

    public interface OnPlaceClickListener {
        void onPlaceClick(customPlace place);
    }

    private OnPlaceClickListener clickListener;

    public PlaceAdapter(ArrayList<customPlace> customPlaces, DatabaseReference placesRef,
                        OnPlaceClickListener clickListener) {
        this.customPlaces = customPlaces;
        this.placesRef = placesRef;
        this.clickListener = clickListener;
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
        customPlace customPlace = customPlaces.get(position);

        holder.descriptionTextView.setText(customPlace.getDescription());
        holder.nicknameTextView.setText(customPlace.getNickname());
        holder.ratingTextView.setText(String.valueOf(customPlace.getRating()));
        holder.addressTextView.setText(customPlace.getAddress());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPlaceClick(customPlace);
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (customPlace.getId() == null) {
                Toast.makeText(holder.itemView.getContext(),
                        "Invalid place ID, cannot delete.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show confirmation dialog before deleting
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"" + customPlace.getAddress() + "\"?")
                    .setPositiveButton("Yes", (dialog, which) ->
                            // Remove place from Firebase on confirmation
                            placesRef.child(customPlace.getId()).removeValue()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(holder.itemView.getContext(),
                                                    "Deleted " + customPlace.getAddress(),
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
        return customPlaces.size(); // number of places in the list
    }

    // ViewHolder holds references to the UI elements in each row
    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView, ratingTextView, nicknameTextView, addressTextView;
        ImageButton deleteButton;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.nickname_textview);
            descriptionTextView = itemView.findViewById(R.id.desc_textview);
            ratingTextView = itemView.findViewById(R.id.rating_textview);
            addressTextView = itemView.findViewById(R.id.address_textview);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
