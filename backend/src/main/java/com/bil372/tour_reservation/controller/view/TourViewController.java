package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.entity.view.TourFullView;
import com.bil372.tour_reservation.repository.view.TourReviewProjection;
import com.bil372.tour_reservation.service.ViewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5173"
})
public class TourViewController {

    private final ViewService viewService;

    public TourViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping("/full")
    public List<TourFullView> getAllToursFull() {
        return viewService.getAllTourFull();
    }

    @GetMapping("/{tourId}/reviews/view")
    public List<TourReviewProjection> getTourReviews(@PathVariable Integer tourId) {
        return viewService.getReviewsForTour(tourId);
    }
}

