package com.bil372.tour_reservation.dto;
import java.time.LocalDate;

public class AddHotelRequest {
    private Integer packageId; // Hangi pakete ekliyoruz?
    private Integer hotelId;   // Hangi otel?
    private LocalDate entryDate;
    private LocalDate exitDate;

    // Getter-Setter
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
}