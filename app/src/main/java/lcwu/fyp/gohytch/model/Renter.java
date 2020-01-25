package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Renter implements Serializable {
    String id,Image1,Image2,licenseNumber;
    Car car;

    public Renter() {
    }

    public Renter(String id, String image1, String image2, String licenseNumber, Car car) {
        this.id = id;
        Image1 = image1;
        Image2 = image2;
        this.licenseNumber = licenseNumber;
        this.car = car;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
