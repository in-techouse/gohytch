package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id, bookingId, userId, notification, status, driverId, date;
    private boolean read;

    public Notification() { }

    public Notification(String id, String bookingId, String userId, String notification, String status, String driverId, String date, boolean read) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.notification = notification;
        this.status = status;
        this.driverId = driverId;
        this.date = date;
        this.read = read;
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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
