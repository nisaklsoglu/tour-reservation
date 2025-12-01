package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.FlightCreateRequest;
import com.bil372.tour_reservation.entity.Flight;
import com.bil372.tour_reservation.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/create")
    public ResponseEntity<Flight> createFlight(@RequestBody FlightCreateRequest request) {
        return ResponseEntity.ok(flightService.createFlight(request));
    }

    @PostMapping("/link-to-package")
    public ResponseEntity<String> linkFlight(@RequestBody AddFlightRequest request) {
        flightService.addFlightToPackage(request);
        return ResponseEntity.ok("Uçuş pakete başarıyla bağlandı!");
    }
}