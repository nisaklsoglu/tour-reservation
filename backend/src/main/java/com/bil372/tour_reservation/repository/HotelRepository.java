package com.bil372.tour_reservation.repository;
import com.bil372.tour_reservation.entity.Hotel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    List<Hotel> findByHotelAddressContainingIgnoreCase(String city);
}