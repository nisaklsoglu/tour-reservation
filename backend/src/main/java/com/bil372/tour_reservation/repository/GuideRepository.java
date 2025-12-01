package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, Integer> {
}