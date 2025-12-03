package com.bil372.tour_reservation.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bil372.tour_reservation.entity.Guide;
import com.bil372.tour_reservation.repository.GuideRepository;

@Service
public class GuideService {
    private final GuideRepository guideRepository;

    public GuideService(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    public Guide save(Guide guide) {
        return guideRepository.save(guide);
    }
}