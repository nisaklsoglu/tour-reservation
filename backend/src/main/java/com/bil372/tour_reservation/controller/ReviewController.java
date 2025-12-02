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
@RequestMapping("/api/users")
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
    @PostMapping("/{userId}/tours/{tourId}/review")
    public ResponseEntity<Review> addReview(
            @PathVariable Integer userId,
            @PathVariable Integer tourId,
            @RequestBody ReviewRequest request
    ) {
        Review review = reviewService.addReview(userId, tourId, request);
        return ResponseEntity.ok(review);
    }

    // TUR DETAY: GET /api/users/tours/{tourId}/reviews (istersen sonra başka controller'a taşırsın)
    @GetMapping("/tours/{tourId}/reviews")
    public ResponseEntity<List<Review>> getReviewsForTour(@PathVariable Integer tourId) {
        List<Review> list = reviewRepository.findByTour_TourId(tourId);
        return ResponseEntity.ok(list);
    }

    // GET /api/users/{userId}/reviews  -> Bu kullanıcının yaptığı tüm yorumlar
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable Integer userId) {
        List<Review> list = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(list);
    }
    
}