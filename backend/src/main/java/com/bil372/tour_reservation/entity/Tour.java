package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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


    @Column(name = "duration")
    private Integer duration;

    @Column(name = "tour_type")
    private String tourType;

    @Column(name = "description")
    private String description;

    @Column(name = "capacity")
    private Integer capacity;



    // Tour.java içine eklenecek:
    // Tour.java içine eklenecek:

    @ManyToMany
    @JoinTable(
        name = "Tour_Destination", // MySQL'de oluşturduğumuz tablo adı
        joinColumns = @JoinColumn(name = "tour_id"),
        inverseJoinColumns = @JoinColumn(name = "destination_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Destination> destinations;

    // Getter & Setter
    public List<Destination> getDestinations() { return destinations; }
    public void setDestinations(List<Destination> destinations) { this.destinations = destinations; }
   

    
    @Column(name = "review_count")
    private Integer review_count;
    public Integer getReview_count() {
        return review_count;
    }
    public void setReview_count(Integer review_count) {
        this.review_count = review_count;
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



    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getTourType() { return tourType; }
    public void setTourType(String tourType) { this.tourType = tourType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    
    
}
