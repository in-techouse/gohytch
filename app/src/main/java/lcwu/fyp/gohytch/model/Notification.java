package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id, bookingId, userId, notification, status;

    public Notification() { }

    public Notification(String id, String bookingId, String userId, String notification, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
        this.notification = notification;
        this.status = status;
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
