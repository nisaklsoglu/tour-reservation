package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.FlightCreateRequest;
import com.bil372.tour_reservation.entity.Flight;
import com.bil372.tour_reservation.repository.FlightPackageRepository;
import com.bil372.tour_reservation.repository.FlightRepository;
import com.bil372.tour_reservation.service.FlightService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")    // <--- İZİN
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /* @PostMapping("/create")
    public ResponseEntity<Flight> createFlight(@RequestBody FlightCreateRequest request) {
        return ResponseEntity.ok(flightService.createFlight(request));
    } */
   @Autowired
    private FlightRepository flightRepository; // En tepeye ekle
    @Autowired
    private FlightPackageRepository flightPackageRepository; // En tepeye ekle

    

    @PostMapping("/link-to-package")
    public ResponseEntity<String> linkFlight(@RequestBody AddFlightRequest request) {
        flightService.addFlightToPackage(request);
        return ResponseEntity.ok("Uçuş pakete başarıyla bağlandı!");
    }

    @GetMapping("/all")
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @PostMapping("/create")
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.save(flight));
    }

    
}