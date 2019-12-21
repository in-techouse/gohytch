package lcwu.fyp.gohytch.model;

import java.io.Serializable;
import java.util.List;

public class Driver implements Serializable {
    String id,licenseNumber,pastExperience;
    private List<String> expertise;
    double rating;

    public Driver() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
