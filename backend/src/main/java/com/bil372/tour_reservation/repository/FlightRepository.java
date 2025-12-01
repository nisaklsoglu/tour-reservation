package com.bil372.tour_reservation.repository;
import com.bil372.tour_reservation.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FlightRepository extends JpaRepository<Flight, Integer> {}