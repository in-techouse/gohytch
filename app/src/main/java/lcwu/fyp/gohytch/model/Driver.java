package lcwu.fyp.gohytch.model;

import java.io.Serializable;
import java.util.List;

public class Driver implements Serializable {
    private String licenseNumber, pastExperience;
    private List<String> expertise;
    private double rating;

    public Driver() { }

    public Driver(String licenseNumber, String pastExperience, List<String> expertise, double rating) {
        this.licenseNumber = licenseNumber;
        this.pastExperience = pastExperience;
        this.expertise = expertise;
        this.rating = rating;
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
}
