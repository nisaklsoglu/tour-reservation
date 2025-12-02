package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.Serializable;
import java.util.Objects;

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

    // --- EKLENEN İLİŞKİLER ---
    @ManyToOne
    @JoinColumn(name = "flight_id", insertable = false, updatable = false)
    private Flight flight; // Uçuş detaylarını buradan alacağız

    @ManyToOne
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    @JsonIgnore
    private TourPackage tourPackage;
    // -------------------------

    @Column(name = "flight_type")
    private String flightType;

    @Column(name = "departure_date")
    private LocalDate departureDate;
    
    @Column(name = "departure_time")
    private LocalTime departureTime; 

    @Column(name = "arrival_time")
    private LocalTime arrivalTime; 

    @Column(name = "duration")
    private Integer duration; 

    // Getter-Setter
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

    // Yeni Nesne Getter'ları
    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
}

class FlightPackageId implements Serializable {
    private Integer packageId;
    private Integer flightId;

    public FlightPackageId() {}
    public FlightPackageId(Integer packageId, Integer flightId) {
        this.packageId = packageId;
        this.flightId = flightId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightPackageId that = (FlightPackageId) o;
        return Objects.equals(packageId, that.packageId) && Objects.equals(flightId, that.flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, flightId);
    }
}
