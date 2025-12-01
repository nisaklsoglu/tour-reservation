package com.bil372.tour_reservation.repository;
import com.bil372.tour_reservation.entity.TourDestination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourDestinationRepository extends JpaRepository<TourDestination, Integer> {
    // Sadece kaydetme (save) için kullanacağız
}