package com.bil372.tour_reservation.service.view;

import com.bil372.tour_reservation.entity.view.TourFullView;
import com.bil372.tour_reservation.entity.view.UserReservationView;
import com.bil372.tour_reservation.repository.view.TourFullViewRepository;
import com.bil372.tour_reservation.repository.view.TourReviewProjection;
import com.bil372.tour_reservation.repository.view.TourReviewViewRepository;
import com.bil372.tour_reservation.repository.view.UserReservationViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewService {

    private final UserReservationViewRepository userReservationViewRepository;
    private final TourFullViewRepository tourFullViewRepository;
    private final TourReviewViewRepository tourReviewViewRepository;

    public ViewService(UserReservationViewRepository userReservationViewRepository,
                       TourFullViewRepository tourFullViewRepository,
                       TourReviewViewRepository tourReviewViewRepository) {
        this.userReservationViewRepository = userReservationViewRepository;
        this.tourFullViewRepository = tourFullViewRepository;
        this.tourReviewViewRepository = tourReviewViewRepository;
    }

    public List<UserReservationView> getUserReservationSummary(Integer userId) {
        return userReservationViewRepository.findByUserId(userId);
    }

    public List<TourFullView> getAllTourFull() {
        return tourFullViewRepository.findAll();
    }

    public List<TourReviewProjection> getReviewsForTour(Integer tourId) {
        return tourReviewViewRepository.findViewByTourId(tourId);
    }
}

