package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Bu import önemli
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List; // List için gerekli

@Entity
@Table(name = "Tour_Package")
public class TourPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    // --- 1. DÜZELTME: JSON Koruması Eklendi ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "destinations", "tourPackages"}) 
    private Tour tour;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Column(name = "booked_count")
    private Integer bookedCount;

    // --- REHBER İLİŞKİSİ ---
    @ManyToOne
    @JoinColumn(name = "guide_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Guide guide;

    // --- 2. DÜZELTME: OTEL VE UÇUŞ LİSTELERİ EKLENDİ ---
    // (Frontend'de "Detaylar" butonuna basınca bunları göstereceğiz)
    
    @OneToMany(mappedBy = "packageId", fetch = FetchType.LAZY) 
    // Not: HotelPackage entity'sinde "packageId" diye bir alan varsa mappedBy="packageId" olur. 
    // Eğer orada @ManyToOne TourPackage varsa, mappedBy="tourPackage" olur.
    // Şimdilik hata vermemesi için burayı yorum satırı yapıyorum veya
    // HotelPackage entity'ni kontrol etmen gerekebilir.
    // En basiti: Eğer HotelPackage entity'nde ilişki yoksa burayı boşverelim.
    // Ama "Detaylar" kısmında göstermek istiyorsan bu ilişkiler şart.
    @JsonIgnoreProperties("tourPackage")
    private List<HotelPackage> hotelPackages;

    @OneToMany(mappedBy = "packageId", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("tourPackage")
    private List<FlightPackage> flightPackages;


    // --- GETTER & SETTER ---

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }

    // Listeler için Getter/Setter
    public List<HotelPackage> getHotelPackages() { return hotelPackages; }
    public void setHotelPackages(List<HotelPackage> hotelPackages) { this.hotelPackages = hotelPackages; }

    public List<FlightPackage> getFlightPackages() { return flightPackages; }
    public void setFlightPackages(List<FlightPackage> flightPackages) { this.flightPackages = flightPackages; }


    // --- SANAL GETTER/SETTER (Eski kodları bozmamak için) ---

    public Integer getTourId() { return tour != null ? tour.getTourId() : null; }
    public void setTourId(Integer tourId) {
        if (tourId == null) {
            this.tour = null;
        } else {
            Tour t = new Tour();
            t.setTourId(tourId);
            this.tour = t;
        }
    }

    public Integer getGuideId() { return guide != null ? guide.getGuideId() : null; }
    public void setGuideId(Integer guideId) {
        if (guideId == null) {
            this.guide = null;
        } else {
            Guide g = new Guide();
            g.setGuideId(guideId);
            this.guide = g;
        }
    }
}