package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Review;
import com.bil372.tour_reservation.entity.TourPackage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    @Query(
        value = """
            SELECT DISTINCT tp.*              
            FROM tour_package tp              
            
            JOIN hotel_package hp             
                ON tp.package_id = hp.package_id
            
            JOIN hotel h                      
                ON hp.hotel_id = h.hotel_id
            
            WHERE h.hotel_rate = :stars       
            """,
        nativeQuery = true
    )
    List<TourPackage> findPackagesByHotelRate(@Param("stars") Integer stars);

}
