package lcwu.fyp.gohytch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Driver implements Serializable {
    private String licenseNumber, pastExperience;
    private List<String> expertise;
    private double rating;
    private List<Integer> ratings;

    public Driver() {
        expertise = new ArrayList<>();
        ratings = new ArrayList<>();
    }

    public Driver(String licenseNumber, String pastExperience, List<String> expertise, double rating, List<Integer> ratings) {
        this.licenseNumber = licenseNumber;
        this.pastExperience = pastExperience;
        this.expertise = expertise;
        this.rating = rating;
        this.ratings = ratings;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPastExperience() {
        return pastExperience;
    }

    public void setPastExperience(String pastExperience) {
        this.pastExperience = pastExperience;
    }

    public List<String> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<String> expertise) {
        this.expertise = expertise;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }
}
