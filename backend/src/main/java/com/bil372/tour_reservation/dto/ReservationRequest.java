package com.bil372.tour_reservation.dto;

import java.util.List;

// Kullanıcıdan gelen isteği karşılayan sınıf
public class ReservationRequest {
    private Integer userId;
    private Integer packageId;
    private List<PassengerRequest> passengers; // Yolcu listesi

    // Getter ve Setter'lar
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public List<PassengerRequest> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerRequest> passengers) { this.passengers = passengers; }
}