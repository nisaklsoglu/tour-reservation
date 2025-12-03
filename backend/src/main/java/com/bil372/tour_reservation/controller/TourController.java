package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
import com.bil372.tour_reservation.dto.TourWithPackagesDTO;
import com.bil372.tour_reservation.entity.Tour;
import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.service.TourService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "*")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    // --- ANA LİSTELEME & FİLTRELEME & SIRALAMA ---
    // Adres: /api/tours/search?city=...&minPrice=...&sortBy=...
    @GetMapping("/search")
    public List<TourWithPackagesDTO> searchTours(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false, defaultValue = "rating") String sortBy
    ) {
        return tourService.searchTours(city, country, minPrice, maxPrice, guests, minDuration, maxDuration, sortBy);
    }
     @GetMapping("/top-rated")
    public List<Tour> getTopRatedTours() {
        return tourService.getTopRatedTours();
    }
    
    // --- TEKİL İŞLEMLER ---
    
    // Tek Bir Turu Getir (ID ile)
    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Integer id) {
        Tour tour = tourService.getTourById(id);
        return (tour != null) ? ResponseEntity.ok(tour) : ResponseEntity.notFound().build();
    }

    // --- YÖNETİM İŞLEMLERİ (Create) ---

    @PostMapping("/create")
    public ResponseEntity<Tour> createTour(@RequestBody TourCreateRequest request) {
        return ResponseEntity.ok(tourService.createTourWithPackage(request));
    }

    @PostMapping("/{tourId}/add-package")
    public ResponseEntity<TourPackage> addPackage(@PathVariable Integer tourId, @RequestBody PackageCreateRequest request) {
        request.setTourId(tourId);
        return ResponseEntity.ok(tourService.addPackageToExistingTour(request));
    }

    // --- YARDIMCI METOTLAR (İstersen tutabilirsin ama searchTours bunları da kapsıyor) ---
    
    @GetMapping
    public List<Tour> getAllTours() {
        return tourService.getAllTours();
    }

    @GetMapping("/by-company/{companyId}")
    public List<Tour> getToursByCompany(@PathVariable("companyId") Integer companyId) {
        return tourService.getToursByCompany(companyId);
    }

    
}