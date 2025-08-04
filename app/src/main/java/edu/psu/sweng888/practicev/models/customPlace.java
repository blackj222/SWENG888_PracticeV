package edu.psu.sweng888.practicev.models;

import android.os.Parcel;
import android.os.Parcelable;

public class customPlace implements Parcelable {

    private String id;
    private String address;
    private String nickname;
    private String description;
    private float rating;
    private double latitude;
    private double longitude;

    // Required empty constructor for Firebase
    public customPlace() {
    }

    public customPlace(String id, String address, String description, String nickname,
                       double latitude, double longitude, float rating) {
        this.id = id;
        this.address = address;
        this.description = description;
        this.nickname = nickname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
    }

    public customPlace(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = "";
        this.nickname = "";
        this.rating = 0;
    }

    // Parcelable constructor
    protected customPlace(Parcel in) {
        id = in.readString();
        address = in.readString();
        nickname = in.readString();
        description = in.readString();
        rating = in.readFloat();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<customPlace> CREATOR = new Creator<customPlace>() {
        @Override
        public customPlace createFromParcel(Parcel in) {
            return new customPlace(in);
        }

        @Override
        public customPlace[] newArray(int size) {
            return new customPlace[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(address);
        dest.writeString(nickname);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters (for Firebase)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
