package edu.psu.sweng888.practicev.models;

// The Place class is a data model that represents an item stored in Firebase and displayed in your app.

public class Place {

    private String id;
    private String name;
    private String description;
    private double rating;


    public Place() {
    }

    public Place(String id, String name, String description, double rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    // Getters and Setters (Firebase uses these for mapping)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
