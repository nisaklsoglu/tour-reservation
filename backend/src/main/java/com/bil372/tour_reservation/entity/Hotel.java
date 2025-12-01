package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer hotelId;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "hotel_rate")
    private Integer hotelRate;

    @Column(name = "hotel_address")
    private String hotelAddress;

    // Getter & Setter
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public Integer getHotelRate() { return hotelRate; }
    public void setHotelRate(Integer hotelRate) { this.hotelRate = hotelRate; }
    public String getHotelAddress() { return hotelAddress; }
    public void setHotelAddress(String hotelAddress) { this.hotelAddress = hotelAddress; }
}