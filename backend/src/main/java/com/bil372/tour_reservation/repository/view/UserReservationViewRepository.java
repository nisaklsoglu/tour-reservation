package com.bil372.tour_reservation.repository.view;

import com.bil372.tour_reservation.entity.view.UserReservationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReservationViewRepository extends JpaRepository<UserReservationView, Integer> {

    List<UserReservationView> findByUserId(Integer userId);
}
