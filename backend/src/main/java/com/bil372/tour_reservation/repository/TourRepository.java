package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    //Tüm turları getir
    @Query(
        value = "SELECT * FROM Tour",
        nativeQuery = true
    )
    List<Tour> findAllTours();


    // Belirli destinasyondaki turları getir
    @Query(
        value = """
            SELECT DISTINCT t.* FROM Tour t
            JOIN Tour_Destination td ON t.tour_id = td.tour_id
            WHERE td.destination_id = :destId
        """,
        nativeQuery = true
    )
    List<Tour> findByDestinationId(@Param("destId") Integer destId);

    // Kullanıcı "Roma" yazarsa hem Roma şehrini hem Roma ismini içeren turları bulur.
    @Query(
        value = """
            SELECT DISTINCT t.* FROM Tour t
            JOIN Tour_Destination td ON t.tour_id = td.tour_id
            JOIN Destination d ON td.destination_id = d.destination_id
            WHERE d.destination_city LIKE %:cityName% 
            OR d.destination_name LIKE %:cityName%
        """,
        nativeQuery = true
    )
    List<Tour> findToursByCity(@Param("cityName") String cityName);


    @Query(
        value = "SELECT * FROM Tour WHERE company_id = :compId",
        nativeQuery = true
    )
    List<Tour> findByCompanyId(@Param("compId") Integer compId);


    @Query(
        value = "SELECT * FROM Tour ORDER BY avg_rating DESC",
        nativeQuery = true
    )
    List<Tour> findTopRatedTours();

    @Query(
        value = "SELECT * FROM Tour ORDER BY review_count DESC",
        nativeQuery = true
    )
    List<Tour> findMostReviewedTours();
    // Belirli bir destinasyona ait turları, PUANA GÖRE AZALAN (En yüksekten düşüğe) getir
    @Query(
        value = "SELECT * FROM Tour WHERE destination_id = :destId ORDER BY avg_rating DESC",
        nativeQuery = true
    )
    List<Tour> findByDestinationIdSorted(@Param("destId") Integer destId);

    // Bir Turun (tüm paketleri dahil) toplam kaç YOLCUSU olduğunu sayar
    @Query(
        value = """
            SELECT COUNT(p.passenger_id) 
            FROM Passenger p
            JOIN Reservation r ON p.reservation_id = r.reservation_id
            JOIN Tour_Package tp ON r.package_id = tp.package_id
            WHERE tp.tour_id = :tourId
            AND r.status != 'Iptal'
        """,
        nativeQuery = true
    )
    Long countTotalPassengersByTour(@Param("tourId") Integer tourId);
}
