package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.ReservationRequest;
import com.bil372.tour_reservation.entity.Reservation;
import com.bil372.tour_reservation.service.ReservationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // <-- BU IMPORT ÇOK ÖNEMLİ

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {
    
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    // Removed duplicate mapping; use the ResponseEntity-returning method below.

    @PostMapping("/create")
    // DÜZELTME BURADA: @Valid ekledik.
    // Artık Pasaport no girilmezse veya TC eksikse, kod Service'e bile girmeden hata verir.
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            Reservation newReservation = reservationService.createReservation(request);
            return ResponseEntity.ok(newReservation);
        } catch (RuntimeException e) {
            // Kapasite dolduysa (Service'den gelen hata) buraya düşer
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUser(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

}
