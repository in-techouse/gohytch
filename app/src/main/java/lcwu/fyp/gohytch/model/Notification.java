package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id, bookingId, userId, userText, driverText, status, driverId, date;
    private boolean read;

    public Notification() { }

    public Notification(String id, String bookingId, String userId, String userText, String driverText, String status, String driverId, String date, boolean read) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.userText = userText;
        this.driverText = driverText;
        this.status = status;
        this.driverId = driverId;
        this.date = date;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getDriverText() {
        return driverText;
    }

    public void setDriverText(String driverText) {
        this.driverText = driverText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
