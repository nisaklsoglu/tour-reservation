package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Passenger")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Integer passengerId;

    @Column(name = "passenger_tc_kimlik", unique = true)
    private String passengerTcKimlik;

    @Column(name = "passenger_name")
    private String passengerName;

    @Column(name = "passenger_phone_no")
    private String passengerPhoneNo;

    @Column(name = "pasaport_no", unique = true)
    private String pasaportNo;

    @Column(name = "pasaport_expiration_date")
    private LocalDate pasaportExpirationDate;

    @Column(name = "passenger_email", unique = true)
    private String passengerEmail;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "reservation_id")
    private Integer reservationId;

    // --- Getter ve Setter Metotları ---

    public Integer getPassengerId() { return passengerId; }
    public void setPassengerId(Integer passengerId) { this.passengerId = passengerId; }

    public String getPassengerTcKimlik() { return passengerTcKimlik; }
    public void setPassengerTcKimlik(String passengerTcKimlik) { this.passengerTcKimlik = passengerTcKimlik; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerPhoneNo() { return passengerPhoneNo; }
    public void setPassengerPhoneNo(String passengerPhoneNo) { this.passengerPhoneNo = passengerPhoneNo; }

    public String getPasaportNo() { return pasaportNo; }
    public void setPasaportNo(String pasaportNo) { this.pasaportNo = pasaportNo; }

    public LocalDate getPasaportExpirationDate() { return pasaportExpirationDate; }
    public void setPasaportExpirationDate(LocalDate pasaportExpirationDate) { this.pasaportExpirationDate = pasaportExpirationDate; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
}
