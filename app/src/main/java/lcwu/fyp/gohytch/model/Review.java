package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Review implements Serializable {
    private String id, review, userId, vendorId, bookingId, dateTime;
    private double rating;

    public Review() {
    }

    public Review(String id, String review, String userId, String vendorId, String bookingId, String dateTime, double rating) {
        this.id = id;
        this.review = review;
        this.userId = userId;
        this.vendorId = vendorId;
        this.bookingId = bookingId;
        this.dateTime = dateTime;
        this.rating = rating;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
