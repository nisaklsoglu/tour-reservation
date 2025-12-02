package com.bil372.tour_reservation.dto;

import com.bil372.tour_reservation.entity.Tour;
import com.bil372.tour_reservation.entity.TourPackage;
import java.util.List;

public class TourWithPackagesDTO {
    private Tour tour;
    private List<TourPackage> packages; // Sadece filtreye uyan paketler buraya konacak

    public TourWithPackagesDTO(Tour tour, List<TourPackage> packages) {
        this.tour = tour;
        this.packages = packages;
    }

    // Getter Setter
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public List<TourPackage> getPackages() { return packages; }
    public void setPackages(List<TourPackage> packages) { this.packages = packages; }
}