package lcwu.fyp.gohytch.model;

import java.io.Serializable;

public class Booking implements Serializable {
    String id,bookingTime,userId,driverId,carId,startTime, type;
    int fare;
    double lat , lng;
    String status;

    public double getLat(){return  lat;}
    public double getLng(){return  lng;}

    public void setLat(double lat){this.lat = lat;}
    public void setLng(double lng){this.lng = lng;}

    public String getType(){return type;}

    public void setType(String type){this.type = type;}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }



//    public Location getDestination() {
//        return destination;
//    }

//    public void setDestination(Location destination) {
//        this.destination = destination;
//    }

    public Booking() {
    }

//    private Location pickupLocation,destination;

}

