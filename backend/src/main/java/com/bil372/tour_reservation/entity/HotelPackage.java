package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Sonsuz döngü önleyici
import java.time.LocalDate;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Hotel_Package")
@IdClass(HotelPackageId.class)
public class HotelPackage {

    @Id
    @Column(name = "package_id")
    private Integer packageId;

    @Id
    @Column(name = "hotel_id")
    private Integer hotelId;

    // --- EKLENEN İLİŞKİLER (Veriyi çekebilmek için ŞART) ---
    
    @ManyToOne
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel; // Otel ismini buradan alacağız

    @ManyToOne
    @JoinColumn(name = "package_id", insertable = false, updatable = false)
    @JsonIgnore // Sonsuz döngüye girmesin diye
    private TourPackage tourPackage;

    // -------------------------------------------------------

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "exit_date")
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

    // Yeni Nesne Getter'ları
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public TourPackage getTourPackage() { return tourPackage; }
    public void setTourPackage(TourPackage tourPackage) { this.tourPackage = tourPackage; }
}

// Composite Key (Equals ve HashCode EKLENMELİ, yoksa Hibernate hata verebilir)
class HotelPackageId implements Serializable {
    private Integer packageId;
    private Integer hotelId;

    public HotelPackageId() {}
    public HotelPackageId(Integer packageId, Integer hotelId) {
        this.packageId = packageId;
        this.hotelId = hotelId;
    }

    // Bu ikisi Hibernate için zorunludur
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelPackageId that = (HotelPackageId) o;
        return Objects.equals(packageId, that.packageId) && Objects.equals(hotelId, that.hotelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId, hotelId);
    }
}