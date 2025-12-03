package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.entity.Flight;
import com.bil372.tour_reservation.entity.FlightPackage;
import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.repository.FlightPackageRepository;
import com.bil372.tour_reservation.repository.FlightRepository;
import com.bil372.tour_reservation.repository.HotelRepository;
import com.bil372.tour_reservation.repository.TourPackageRepository;
import com.bil372.tour_reservation.service.TourPackageService;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private TourPackageRepository tourPackageRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightPackageRepository flightPackageRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
  

    // --- UÇUŞ PAKETİ EKLEME ENDPOINT'İ ---
    @PostMapping("/add-flight")
    public ResponseEntity<?> addFlightToPackage(@RequestBody AddFlightRequest request) {
        try {
            // 1. Paketi Bul
            TourPackage tPackage = tourPackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new RuntimeException("Paket bulunamadı!"));

            // 2. Uçuşu Bul
            Flight flight = flightRepository.findById(request.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Uçuş bulunamadı!"));

            // 3. İlişkiyi Kur
            FlightPackage fp = new FlightPackage();
            fp.setTourPackage(tPackage);
            fp.setFlight(flight);
            fp.setFlightType(request.getFlightType()); // "Gidiş", "Dönüş"
            fp.setDepartureDate(request.getDepartureDate());
            fp.setDepartureTime(request.getDepartureTime());
            fp.setArrivalTime(request.getArrivalTime());
            fp.setDuration(request.getDuration());

            // 4. Kaydet
            flightPackageRepository.save(fp);

            return ResponseEntity.ok("Uçuş pakete eklendi!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Hata: " + e.getMessage());
        }
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



    @GetMapping("/by-start-date")
    public List<TourPackage> getPackagesByStartDate(@RequestParam("start") String startDate) {
        return tourPackageService.getPackagesByStartDate(java.time.LocalDate.parse(startDate));
    }      
    
    @GetMapping("/by-end-date")
    public List<TourPackage> getPackagesByEndDate(@RequestParam("end") String endDate) {
        return tourPackageService.getPackagesByEndDate(java.time.LocalDate.parse(endDate));
    }

    @GetMapping("/by-hotel-rate")
    public List<TourPackage> getPackagesByHotelRate(@RequestParam("stars") Integer stars) {
        return tourPackageService.getPackagesByHotelRate(stars);
    }
    // Endpoint'i ekle
    // Adres: /api/tour-packages/by-tour/1
    @GetMapping("/by-tour/{tourId}")
    public List<TourPackage> getPackagesByTourId(@PathVariable Integer tourId) {
        return tourPackageService.getPackagesByTourId(tourId);
    }
    // Endpoint: /api/tour-packages/by-availability?seats=3
    @GetMapping("/by-availability")
    public List<TourPackage> getPackagesByAvailability(@RequestParam("seats") Integer seats) {
        return tourPackageService.getPackagesByAvailableSeats(seats);
    }
    // POST /api/tour-packages/add-hotel
    @PostMapping("/add-hotel")
    public ResponseEntity<String> addHotel(@RequestBody AddHotelRequest request) {
        tourPackageService.addHotelToPackage(request);
        return ResponseEntity.ok("Otel pakete eklendi! 🏨");
    }
}
