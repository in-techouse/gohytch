package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class User implements Serializable {

    private String name, phoneNumber, id, email, image;
    private String type;
    private Driver driver;
    private Renter renter;

    public User() { }

    public User(String name, String phoneNumber, String id, String email, String image, String type, Driver driver, Renter renter) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.email = email;
        this.image = image;
        this.type = type;
        this.driver = driver;
        this.renter = renter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }
}
