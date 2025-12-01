package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @Column(name = "user_id")
    private Integer user_id;

    
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;
    
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    
    // Örn: PENDING / CONFIRMED / CANCELLED
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    // --- GETTER / SETTER ---
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    // public TourPackage getTourPackage() {
    //     return tourPackage;
    // }

    // public void setTourPackage(TourPackage tourPackage) {
    //     this.tourPackage = tourPackage;
    // }
    public TourPackage getTourPackage() {
        return tourPackage;
    }

    public void setTourPackage(TourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }


    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
}
