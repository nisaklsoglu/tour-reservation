package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.GuideRequest;
import com.bil372.tour_reservation.entity.Guide;
import com.bil372.tour_reservation.repository.GuideRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*") // Frontend erişimi için
public class GuideController {

    @Autowired
    private GuideRepository guideRepository;

    // 1. Tüm Rehberleri Getir
    @GetMapping("/all")
    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    // 2. Yeni Rehber Ekle
    @PostMapping("/create")
    public ResponseEntity<?> createGuide(@RequestBody GuideRequest request) {
        try {
            // DTO'dan Entity'e çevirme
            Guide newGuide = new Guide();
            newGuide.setGuideName(request.getGuideName());
            newGuide.setGuidePhone(request.getGuidePhone());
            newGuide.setGuideEmail(request.getGuideEmail());

            Guide savedGuide = guideRepository.save(newGuide);
            return ResponseEntity.ok(savedGuide);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Hata: " + e.getMessage());
        }
    }
}