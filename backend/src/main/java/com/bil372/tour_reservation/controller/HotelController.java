package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.HotelCreateRequest;
import com.bil372.tour_reservation.entity.Hotel;
import com.bil372.tour_reservation.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/create")
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelCreateRequest request) {
        return ResponseEntity.ok(hotelService.createHotel(request));
    }

    @PostMapping("/link-to-package")
    public ResponseEntity<String> linkHotel(@RequestBody AddHotelRequest request) {
        hotelService.addHotelToPackage(request);
        return ResponseEntity.ok("Otel pakete başarıyla bağlandı!");
    }
}