package com.bil372.tour_reservation.dto;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddFlightRequest {
    private Integer packageId;
    private Integer flightId;
    private String flightType; // 'Gidiş' veya 'Dönüş'
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime; // Varış Saati
    private Integer duration;      // Süre (Dakika)

    // Getter-Setter
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Integer getFlightId() { return flightId; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }
    public String getFlightType() { return flightType; }
    public void setFlightType(String flightType) { this.flightType = flightType; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
}