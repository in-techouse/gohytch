package lcwu.fyp.gohytch.model;

import java.io.Serializable;
import java.lang.reflect.Type;

public class User implements Serializable {

    String name,phoneNumber,id,email;
    String type;
    public User() {

    }

    public User(String name, String phoneNumber, String id, String email, String type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.email = email;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
