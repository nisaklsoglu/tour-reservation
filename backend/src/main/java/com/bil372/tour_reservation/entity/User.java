package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")   // tablo adı: user
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")   // PK kolon adı
    private Long id;

    @Column(name = "user_mail", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "user_psw", nullable = false, length = 255)
    private String password;

    // ----- Getter & Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

