package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    // JpaRepository sayesinde save(), findById() gibi metotlar hazır gelir.
    // Ekstra kod yazmana gerek yok.
}