package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.entity.Hotel;
import com.bil372.tour_reservation.service.HotelService;

import java.util.List;

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
    public Hotel createHotel(@RequestBody Hotel hotel) { return hotelService.save(hotel); }    

    @PostMapping("/link-to-package")
    public ResponseEntity<String> linkHotel(@RequestBody AddHotelRequest request) {
        hotelService.addHotelToPackage(request);
        return ResponseEntity.ok("Otel pakete başarıyla bağlandı!");
    }
    @GetMapping("/all")
    public List<Hotel> getAllHotels() { return hotelService.getAllHotels(); }

    @GetMapping("/search/{city}")
    public List<Hotel> searchHotelsByCity(@PathVariable String city) {
        return hotelService.searchByCity(city);
    }
}