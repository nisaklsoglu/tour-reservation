package com.bil372.tour_reservation.dto;
public class HotelCreateRequest {
    private String name;
    private String address;
    private Integer rate;
    // Getter-Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getRate() { return rate; }
    public void setRate(Integer rate) { this.rate = rate; }
}