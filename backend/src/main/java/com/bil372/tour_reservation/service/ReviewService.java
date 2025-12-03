package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.ReviewRequest;
import com.bil372.tour_reservation.entity.Review;
import com.bil372.tour_reservation.entity.Tour;
import com.bil372.tour_reservation.entity.User;
import com.bil372.tour_reservation.repository.ReviewRepository;
import com.bil372.tour_reservation.repository.TourRepository;
import com.bil372.tour_reservation.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         TourRepository tourRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    @Transactional
    public Review addReview(Integer userId, Integer tourId, ReviewRequest request) {
        // 1) User var mı?
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "User bulunamadı: " + userId));

        // 2) Tur var mı?
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tur bulunamadı: " + tourId));

        // 3) Aynı user + tur için daha önce yorum yapılmış mı?
        reviewRepository.findByUser_IdAndTour_TourId(userId, tourId).ifPresent(r -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Bu tur için zaten bir yorum yapmışsınız."
            );
        });

        // 4) Rating kontrolü
        Integer rating = request.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating 1 ile 5 arasında olmalı."
            );
        }

        // 5) Review oluştur
        Review review = new Review();
        review.setUser(user);
        review.setTour(tour);
        review.setRating(rating);
        review.setComment(request.getComment());
        review.setReviewDate(LocalDateTime.now());

        try {
            return reviewRepository.save(review);
        } catch (DataIntegrityViolationException e) {
            // Örn: DB'de kolon ismi ya da not-null constraint patlarsa
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Veritabanı hatası: " + e.getMostSpecificCause().getMessage(),
                    e
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(Integer userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    // belli bir tura ait yorumlar
    @Transactional(readOnly = true)
    public List<Review> getReviewsByTour(Integer tourId) {
        return reviewRepository.findByTour_TourId(tourId);
    }


    
}
