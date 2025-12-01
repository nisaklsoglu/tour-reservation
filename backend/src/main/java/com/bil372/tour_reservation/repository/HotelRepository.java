package com.bil372.tour_reservation.repository;
import com.bil372.tour_reservation.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HotelRepository extends JpaRepository<Hotel, Integer> {}