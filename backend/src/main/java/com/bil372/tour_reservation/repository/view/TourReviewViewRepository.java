package com.bil372.tour_reservation.repository.view;

import com.bil372.tour_reservation.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Bu repository aslında Review entity'sine bağlı,
 * ama view_tour_reviews üzerinden projection dönüyor.
 */
@Repository
public interface TourReviewViewRepository extends JpaRepository<Review, Integer> {

    @Query(value = """
            SELECT
                rv.review_id   AS reviewId,
                rv.tour_id     AS tourId,
                rv.user_id     AS userId,
                rv.user_name   AS userName,
                rv.user_email  AS userEmail,
                rv.rating      AS rating,
                rv.comment     AS comment,
                rv.review_date AS reviewDate
            FROM view_tour_reviews rv
            WHERE rv.tour_id = :tourId
            """,
            nativeQuery = true)
    List<TourReviewProjection> findViewByTourId(@Param("tourId") Integer tourId);
}
