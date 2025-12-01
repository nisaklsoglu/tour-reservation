package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Tour_Destination")
@IdClass(TourDestinationId.class)
public class TourDestination {

    @Id
    @Column(name = "tour_id")
    private Integer tourId;

    @Id
    @Column(name = "destination_id")
    private Integer destinationId;

    // Getter & Setter
    public Integer getTourId() { return tourId; }
    public void setTourId(Integer tourId) { this.tourId = tourId; }

    public Integer getDestinationId() { return destinationId; }
    public void setDestinationId(Integer destinationId) { this.destinationId = destinationId; }
}

// Composite Key Sınıfı
class TourDestinationId implements Serializable {
    private Integer tourId;
    private Integer destinationId;
}