package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class User implements Serializable {

    String name,phoneNumber,id,email;
    private int roll;
    public User() {

    }

    public User(String name, String phoneNumber, String id, String email, int roll) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.email = email;
        this.roll = roll;
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

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

}
