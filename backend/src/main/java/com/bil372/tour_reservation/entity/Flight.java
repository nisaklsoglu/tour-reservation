package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "pnr_no", unique = true)
    private String pnrNo;

    @Column(name = "kalkis_konumu")
    private String kalkisKonumu;

    @Column(name = "varis_konumu")
    private String varisKonumu;

    @Column(name = "firma")
    private String firma;

    // Getter & Setter
    public Integer getFlightId() { return flightId; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }
    public String getPnrNo() { return pnrNo; }
    public void setPnrNo(String pnrNo) { this.pnrNo = pnrNo; }
    public String getKalkisKonumu() { return kalkisKonumu; }
    public void setKalkisKonumu(String kalkisKonumu) { this.kalkisKonumu = kalkisKonumu; }
    public String getVarisKonumu() { return varisKonumu; }
    public void setVarisKonumu(String varisKonumu) { this.varisKonumu = varisKonumu; }
    public String getFirma() { return firma; }
    public void setFirma(String firma) { this.firma = firma; }
}