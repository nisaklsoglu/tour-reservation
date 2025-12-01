package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Tour_Package")
public class TourPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "base_price")
    private BigDecimal basePrice;

   // ESKİSİ SİLİNDİ: private Integer guideId; 
    
    // YENİSİ: Gerçek Rehber Objesi Bağlantısı
    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    // --- Guide için Getter/Setter ---
    
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }

    // --- ID Uyumluluğu İçin (Sanal) Getter/Setter ---
    // Bu sayede eski kodların "getGuideId" dediğinde hata almaz.
    
    public Integer getGuideId() {
        return guide != null ? guide.getGuideId() : null;
    }

    public void setGuideId(Integer guideId) {
        if (guideId == null) {
            this.guide = null;
        } else {
            Guide g = new Guide();
            g.setGuideId(guideId);
            this.guide = g;
        }
    }
    

    @Column(name = "booked_count")
    private Integer bookedCount;

    // Getter - Setter
public Tour getTour() {
    return tour;
}
public void setTour(Tour tour) {
    this.tour = tour;
}
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public Integer getTourId() { return tour!=null ? tour.getTourId() : null; }
    public void setTourId(Integer tourId) {
        if (tourId ==null) {
            this.tour = null;
        }
        else {
            Tour t = new Tour();
            t.setTourId(tourId);
            this.tour = t;


        }
        

    
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    
    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }
    
 
    

}
