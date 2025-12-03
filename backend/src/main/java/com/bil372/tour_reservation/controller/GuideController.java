package com.bil372.tour_reservation.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bil372.tour_reservation.entity.Guide;
import com.bil372.tour_reservation.service.GuideService;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class GuideController {
    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping("/all")
    public List<Guide> getAllGuides() {
        return guideService.getAllGuides();
    }

    @PostMapping("/create")
    public ResponseEntity<Guide> createGuide(@RequestBody Guide guide) {
        return ResponseEntity.ok(guideService.save(guide));
    }
}