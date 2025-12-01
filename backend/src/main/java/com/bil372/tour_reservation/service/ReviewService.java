package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.entity.Tour;
//import com.bil372.tour_reservation.entity.Review;
import com.bil372.tour_reservation.repository.ReviewRepository;

import java.util.List;

import org.springframework.stereotype.Service;

//import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    
}

    

