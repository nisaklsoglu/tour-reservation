package com.bil372.tour_reservation.dto;
public class FlightCreateRequest {
    private String pnrNo;
    private String kalkis;
    private String varis;
    private String firma;
    // Getter-Setter
    public String getPnrNo() { return pnrNo; }
    public void setPnrNo(String pnrNo) { this.pnrNo = pnrNo; }
    public String getKalkis() { return kalkis; }
    public void setKalkis(String kalkis) { this.kalkis = kalkis; }
    public String getVaris() { return varis; }
    public void setVaris(String varis) { this.varis = varis; }
    public String getFirma() { return firma; }
    public void setFirma(String firma) { this.firma = firma; }
}