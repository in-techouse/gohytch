package lcwu.fyp.gohytch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Renter implements Serializable {
    private List<String> images;
    private String licenseNumber, carCompany, carModel, carRegistrationNumber, sittingCapacity;

    public Renter() {
        images = new ArrayList<>();
    }

    public Renter(List<String> images, String licenseNumber, String carCompany, String carModel, String carRegistrationNumber, String sittingCapacity) {
        this.images = images;
        this.licenseNumber = licenseNumber;
        this.carCompany = carCompany;
        this.carModel = carModel;
        this.carRegistrationNumber = carRegistrationNumber;
        this.sittingCapacity = sittingCapacity;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getCarCompany() {
        return carCompany;
    }

    public void setCarCompany(String carCompany) {
        this.carCompany = carCompany;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getSittingCapacity() {
        return sittingCapacity;
    }

    public void setSittingCapacity(String sittingCapacity) {
        this.sittingCapacity = sittingCapacity;
    }
}
