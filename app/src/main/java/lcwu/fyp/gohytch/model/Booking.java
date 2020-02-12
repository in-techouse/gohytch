package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private String id, bookingTime, userId, driverId, carId, startTime, type, status, address;
    private int fare;
    private double lat, lng;

    public Booking() { }

    public Booking(String id, String bookingTime, String userId, String driverId, String carId, String startTime, String type, String status, String address, int fare, double lat, double lng) {
        this.id = id;
        this.bookingTime = bookingTime;
        this.userId = userId;
        this.driverId = driverId;
        this.carId = carId;
        this.startTime = startTime;
        this.type = type;
        this.status = status;
        this.address = address;
        this.fare = fare;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

