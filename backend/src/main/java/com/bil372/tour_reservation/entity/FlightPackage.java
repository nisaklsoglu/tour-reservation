package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.Serializable;

@Entity
@Table(name = "Flight_Package")
@IdClass(FlightPackageId.class)
public class FlightPackage {

    @Id
    @Column(name = "package_id")
    private Integer packageId;

    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "flight_type")
    private String flightType;

    @Column(name = "departure_date")
    private LocalDate departureDate;
    
    @Column(name = "departure_time")
    private LocalTime departureTime; // Veritabanında TIME ise LocalTime kullan
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;   // Varış Saati

    @Column(name = "duration")
    private Integer duration;        // Süre (Dakika)

    // Getter-Setter... (Hepsini ekle)
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime;}
    public LocalTime getArrivalTime() {return arrivalTime;}
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
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
}

class FlightPackageId implements Serializable {
    private Integer packageId;
    private Integer flightId;
}