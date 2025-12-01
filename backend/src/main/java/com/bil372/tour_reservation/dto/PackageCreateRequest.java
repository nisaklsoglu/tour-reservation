package com.bil372.tour_reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PackageCreateRequest {
    
    private Integer tourId; // Hangi tura ekliyoruz? (Örn: 5)
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal basePrice;
    private Integer guideId;
    
    // --- Getter ve Setter'lar ---
    public Integer getTourId() { return tourId; }
    public void setTourId(Integer tourId) { this.tourId = tourId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    public Integer getGuideId() { return guideId; }
    public void setGuideId(Integer guideId) { this.guideId = guideId; }
}