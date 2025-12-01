package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    @Query(
    value = """
        SELECT r.*
        FROM Reservation r
        JOIN Tour_Package tp ON r.package_id = tp.package_id
        WHERE r.user_id = :userId
        """,
    nativeQuery = true
    )
    List<Reservation> findReservationsByUserId(@Param("userId") Integer userId);

    @Query(
        value = """
            SELECT COUNT(p.passenger_id) 
            FROM Passenger p
            JOIN Reservation r ON p.reservation_id = r.reservation_id
            WHERE r.package_id = :packageId 
            AND r.status != 'Iptal'
        """,
        nativeQuery = true
    )
    int countPassengersInPackage(@Param("packageId") Integer packageId);
   
}