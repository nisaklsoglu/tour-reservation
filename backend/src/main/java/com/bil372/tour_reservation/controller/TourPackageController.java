package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.service.TourPackageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/tour-packages")
@CrossOrigin(origins = "*")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    public TourPackageController(TourPackageService tourPackageService){
        this.tourPackageService = tourPackageService;
    }

    @GetMapping("/by-price-range")
    public List<TourPackage> getPackagesByPriceRange(@RequestParam("min") BigDecimal minPrice,
                                                     @RequestParam("max") BigDecimal maxPrice) {
        return tourPackageService.getPackagesByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/order-by-price-asc")
    public List<TourPackage> getPackagesOrderByPriceAsc() {
        return tourPackageService.getPackagesOrderByPriceAsc();
    }

    @GetMapping("/order-by-price-desc")
    public List<TourPackage> getPackagesOrderByPriceDesc() {
        return tourPackageService.getPackagesOrderByPriceDesc();    
    }

    @GetMapping("/by-date-range")
    public List<TourPackage> getPackagesByDateRange(@RequestParam("start") String startDate,
                                                    @RequestParam("end") String endDate) {
        return tourPackageService.getPackagesByDateRange(java.time.LocalDate.parse(startDate), java.time.LocalDate.parse(endDate));
    }
    @GetMapping("/by-airline/{airlineName}")
    public List<TourPackage> getPackagesByAirline(@PathVariable("airlineName") String airlineName) {
        return tourPackageService.getPackagesByAirline(airlineName);
    }
    
    @GetMapping("/by-airport/{airportCode}")
    public List<TourPackage> getPackagesByDepartureAirport(@PathVariable("airportCode") String airportCode) {
        return tourPackageService.getPackagesByDepartureAirport(airportCode);
    }
    // Controller içine ekle:
    
    // Örnek: http://localhost:8080/api/tour-packages/by-hotel/Lumiere
    @GetMapping("/by-hotel/{hotelName}")
    public List<TourPackage> getPackagesByHotelName(@PathVariable("hotelName") String hotelName) {
        return tourPackageService.getPackagesByHotelName(hotelName);
    }

    // TourPackageController.java içine:

    // Örnek: POST http://localhost:8080/api/tour-packages/1/assign-guide/2
    // (Paket 1'e, Rehber 2'yi ata)
    @PostMapping("/{packageId}/assign-guide/{guideId}")
    public ResponseEntity<String> assignGuide(@PathVariable Integer packageId, @PathVariable Integer guideId) {
        tourPackageService.assignGuideToPackage(packageId, guideId);
        return ResponseEntity.ok("Rehber pakete başarıyla atandı! 🧢🚩");
    }


}
