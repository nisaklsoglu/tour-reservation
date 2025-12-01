package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tour")  // MySQL'deki tablo adı
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private Integer tourId;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "destination_id")
    private Integer destinationId;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "tour_type")
    private String tourType;

    @Column(name = "description")
    private String description;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "bir_kisilik_oda")
    private Integer bir_kisilik_oda;

    public Integer getBir_kisilik_oda() {
        return bir_kisilik_oda;
    }
    public void setBir_kisilik_oda(Integer bir_kisilik_oda) {
        this.bir_kisilik_oda = bir_kisilik_oda;
    }
    @Column(name = "iki_kisilik_oda")
    private Integer iki_kisilik_oda;
    @Column(name = "review_count")
    private Integer review_count;
    public Integer getReview_count() {
        return review_count;
    }
    public void setReview_count(Integer review_count) {
        this.review_count = review_count;
    }

    public Integer getIki_kisilik_oda() {
        return iki_kisilik_oda;
    }
    public void setIki_kisilik_oda(Integer iki_kisilik_oda) {
        this.iki_kisilik_oda = iki_kisilik_oda;
    }
    @Column(name = "uc_kisilik_oda")
    private Integer uc_kisilik_oda;

    public Integer getUc_kisilik_oda() {
        return uc_kisilik_oda;
    }
    public void setUc_kisilik_oda(Integer uc_kisilik_oda) {
        this.uc_kisilik_oda = uc_kisilik_oda;
    }
    @Column(name = "dort_kisilik_oda")
    private Integer dort_kisilik_oda;

    public Integer getDort_kisilik_oda() {
        return dort_kisilik_oda;
    }
    public void setDort_kisilik_oda(Integer dort_kisilik_oda) {
        this.dort_kisilik_oda = dort_kisilik_oda;
    }
    @Column(name = "avg_rating")
    private Double avg_rating;


    public Double getAvg_rating() {
        return avg_rating;
    }
    public void setAvg_rating(Double avg_rating) {
        this.avg_rating = avg_rating;
    }
    // getter - setter'lar (Lombok kullanıyorsan @Data ile de geçebilirsin)
    public Integer getTourId() { return tourId; }
    public void setTourId(Integer tourId) { this.tourId = tourId; }

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Integer getDestinationId() { return destinationId; }
    public void setDestinationId(Integer destinationId) { this.destinationId = destinationId; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getTourType() { return tourType; }
    public void setTourType(String tourType) { this.tourType = tourType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    
    
}
