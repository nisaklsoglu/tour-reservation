package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.ReviewRequest;
import com.bil372.tour_reservation.entity.Review;
//import com.bil372.tour_reservation.entity.Review;
import com.bil372.tour_reservation.service.ReviewService;
import com.bil372.tour_reservation.repository.ReviewRepository;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService,
                            ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    // FRONTEND: POST /api/users/{userId}/tours/{tourId}/review
    @PostMapping("/users/{userId}/tours/{tourId}/review")
    public ResponseEntity<Review> addReview(
            @PathVariable Integer userId,
            @PathVariable Integer tourId,
            @RequestBody ReviewRequest request
    ) {
        Review review = reviewService.addReview(userId, tourId, request);
        return ResponseEntity.ok(review);
    }

    // TUR DETAY: GET /api/users/tours/{tourId}/reviews 
    @GetMapping("/tours/{tourId}/reviews")
    public List<Review> getReviewsForTour(@PathVariable Integer tourId) {
        return reviewService.getReviewsByTour(tourId);
    }

    // Kullanıcının tüm yorumları
    @GetMapping("/users/{userId}/reviews")
    public List<Review> getReviewsByUser(@PathVariable Integer userId) {
        return reviewService.getReviewsByUser(userId);
    }

  

    
}