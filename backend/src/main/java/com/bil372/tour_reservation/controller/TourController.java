package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
import com.bil372.tour_reservation.entity.Tour;
import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.service.TourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public List<Tour> getAllTours() {
        return tourService.getAllTours();
    }


    @GetMapping("/by-destination/{destinationId}")
    public List<Tour> getToursByDestination(@PathVariable("destinationId") Integer destinationId) {
        return tourService.getToursByDestination(destinationId);
    }

    @GetMapping("/by-company/{companyId}")
    public List<Tour> getToursByCompany(@PathVariable("companyId") Integer companyId) {
        return tourService.getToursByCompany(companyId);
    }

    @GetMapping("/top-rated")
    public List<Tour> getTopRatedTours() {
        return tourService.getTopRatedTours();
    }
    @GetMapping("/most-reviewed")
    public List<Tour> getMostReviewedTours() {
        return tourService.getMostReviewedTours();
    }
    @GetMapping("/by-destination-sorted/{destinationId}/sorted")
    public List<Tour> getToursByDestinationSorted(@PathVariable("destinationId") Integer destinationId) {
        return tourService.getToursByDestinationSorted(destinationId);
    }
    // TourController içine ekle:
    
    @PostMapping("/create")
    public ResponseEntity<Tour> createTour(@RequestBody TourCreateRequest request) {
        Tour newTour = tourService.createTourWithPackage(request);
        return ResponseEntity.ok(newTour);
    }
    // TourController içine ekle:

    @PostMapping("/{tourId}/add-package")
    public ResponseEntity<TourPackage> addPackage(@PathVariable Integer tourId, @RequestBody PackageCreateRequest request) {
        // URL'den gelen ID'yi DTO'ya da set edelim ki garanti olsun
        request.setTourId(tourId);
        
        TourPackage createdPackage = tourService.addPackageToExistingTour(request);
        return ResponseEntity.ok(createdPackage);
    }

    @PostMapping("/{tourId}/add-destination/{destId}")
    public ResponseEntity<String> addDestination(@PathVariable Integer tourId, @PathVariable Integer destId) {
        tourService.addDestinationToTour(tourId, destId);
        return ResponseEntity.ok("Destinasyon tura eklendi! 🌍");
    }

    // TourController.java içine:

    // Örnek: http://localhost:8080/api/tours/search-city/Paris
    @GetMapping("/search-city/{cityName}")
    public List<Tour> getToursByCity(@PathVariable String cityName) {
        return tourService.getToursByCity(cityName);
    }
}
