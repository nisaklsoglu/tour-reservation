package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.entity.Destination;
import com.bil372.tour_reservation.service.DestinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// DÜZELTME 1: Ana adresi "/api/destinations" yaptık. 
// Artık aşağıdaki tüm metodlar bu adresin altına eklenecek.
@RequestMapping("/api/destinations") 
@CrossOrigin(origins = "*")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    // 1. Ülke Listesi
    // Adres: /api/destinations/countries
    @GetMapping("/countries")
    public List<String> getCountries() {
        return destinationService.getAllCountries();
    }

    // 2. Ülkeye Göre Şehirler
    // Adres: /api/destinations/cities/{country}
    @GetMapping("/cities/{country}")
    public List<Destination> getCitiesByCountry(@PathVariable("country") String country) {
        return destinationService.getCitiesByCountry(country);
    }

    // 3. Tüm Şehirler (Listeden seçim yapmak için)
    // Adres: /api/destinations/all
    @GetMapping("/all")
    public List<Destination> getAllDestinations() {
        return destinationService.getAllDestinations();
    }

    // 4. YENİ ŞEHİR EKLEME
    // Adres: /api/destinations/create
    // DÜZELTME 2: Başındaki "destinations/" yazısını sildik çünkü yukarıda zaten tanımlı.
    @PostMapping("/create")
    public ResponseEntity<Destination> createDestination(@RequestBody Destination destination) {
        Destination savedDest = destinationService.createDestination(destination);
        return ResponseEntity.ok(savedDest);
    }
}