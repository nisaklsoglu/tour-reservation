package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)   // 🔥 hangi user’ın şirketi
    private User user;

    @Column(name = "tursab_no", nullable = false, unique = true, length = 50)
    private String tursabNo;

    @Column(name = "company_name", nullable = false, length = 100)
    private String name;

    @Column(name = "company_email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "company_phone_no", length = 20)
    private String phoneNo;

    @Column(name = "company_address", length = 255)
    private String address;

    // ---------- GETTER / SETTER ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
