package com.bil372.tour_reservation.repository.view;

import java.time.LocalDateTime;

public interface TourReviewProjection {

    Integer getReviewId();
    Integer getTourId();
    Integer getUserId();
    String getUserName();
    String getUserEmail();
    Integer getRating();
    String getComment();
    LocalDateTime getReviewDate();
}
