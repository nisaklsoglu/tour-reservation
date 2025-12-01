package com.bil372.tour_reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TourCreateRequest {
    // --- TOUR Tablosu İçin ---
    private Integer companyId;
    private Integer destinationId;
    private String packageName; // Turun adı
    private String description;
    private String tourType;
    private Integer capacity;
    // Oda kontenjanları vs. de eklenebilir

    // --- TOUR_PACKAGE Tablosu İçin ---
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal basePrice;
    private Integer guideId;

    // --- Getter ve Setter'lar ---
    // (Hepsini oluşturmalısın: Sağ tık -> Source Action -> Generate Getters/Setters)
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
    // ... Diğerleri ...
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    // ... Hepsini ekle ...
    public Integer getDestinationId() { return destinationId; }
    public void setDestinationId(Integer destinationId) { this.destinationId = destinationId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTourType() { return tourType; }
    public void setTourType(String tourType) { this.tourType = tourType; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getGuideId() { return guideId; }
    public void setGuideId(Integer guideId) { this.guideId = guideId; }
}