package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.io.Serializable;

@Entity
@Table(name = "Hotel_Package")
@IdClass(HotelPackageId.class) // Composite Key Sınıfı
public class HotelPackage {

    @Id
    @Column(name = "package_id")
    private Integer packageId;

    @Id
    @Column(name = "hotel_id")
    private Integer hotelId;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "exit_date")
    private LocalDate exitDate;

    // Getter-Setter...
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Integer getHotelId() { return hotelId; }
    public void setHotelId(Integer hotelId) { this.hotelId = hotelId; }
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
}

// Composite Key Sınıfı (Aynı dosyanın altına yazabilirsin veya ayrı dosya yapabilirsin)
class HotelPackageId implements Serializable {
    private Integer packageId;
    private Integer hotelId;
    // Default constructor, equals() ve hashCode() gereklidir ama basit proje için şimdilik geçiyoruz.
}