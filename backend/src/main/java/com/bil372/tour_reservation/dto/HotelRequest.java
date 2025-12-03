package com.bil372.tour_reservation.dto;

public class HotelRequest {
    private String hotelName;
    private Integer hotelRate;
    private String hotelAddress;

    // Getter & Setter (İsimlerin Birebir Böyle Olması Şart)
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    
    public Integer getHotelRate() { return hotelRate; }
    public void setHotelRate(Integer hotelRate) { this.hotelRate = hotelRate; }
    
    public String getHotelAddress() { return hotelAddress; }
    public void setHotelAddress(String hotelAddress) { this.hotelAddress = hotelAddress; }
}