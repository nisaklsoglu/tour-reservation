package com.bil372.tour_reservation.dto;

public class CompanyRegisterRequest {

    private Long userId;      // 🔥 hangi user şirket oluyor
    private String tursabNo;
    private String name;
    private String email;
    private String phoneNo;
    private String address;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTursabNo() {
        return tursabNo;
    }

    public void setTursabNo(String tursabNo) {
        this.tursabNo = tursabNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
