package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.TourPackage;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TourPackageRepository extends JpaRepository<TourPackage, Integer> {

    //fiyat aralığına göre tur paketlerini getiren sorgu
    @Query(
        value = "SELECT * FROM Tour_Package WHERE base_price BETWEEN :minPrice AND :maxPrice",
        nativeQuery = true
    )
    List<TourPackage> findPackagesInPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    //tüm tur paketlerini base_price'a göre artan sırada getiren sorgu
    @Query(
        value = "SELECT * FROM Tour_Package ORDER BY base_price ASC",
        nativeQuery = true
    )
    List<TourPackage> findPackageOrderByPriceAsc();


    //tüm tur paketlerini base_price'a göre azalan sırada getiren sorgu
    @Query(
        value = "SELECT * FROM Tour_Package ORDER BY base_price DESC",
        nativeQuery = true
    )
    List<TourPackage> findPackageOrderByPriceDesc();


    //belirtilen tarih aralığındaki tur paketlerini getiren sorgu
    @Query(
        value = "SELECT * FROM Tour_Package WHERE start_date >= :startDate AND end_date <= :endDate",
        nativeQuery = true
    )
    List<TourPackage> findPackagesByDateRange(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    @Query(
        value = """
            SELECT DISTINCT tp.* FROM Tour_Package tp
            JOIN Flight_Package fp ON tp.package_id = fp.package_id
            JOIN Flight f ON fp.flight_id = f.flight_id
            WHERE f.firma = :airlineName
        """,
        nativeQuery = true
    )
    List<TourPackage> findPackagesByAirline(@Param("airlineName") String airlineName);

    
    @Query(
        value = """
            SELECT DISTINCT tp.* FROM Tour_Package tp
            JOIN Flight_Package fp ON tp.package_id = fp.package_id
            JOIN Flight f ON fp.flight_id = f.flight_id
            WHERE f.kalkis_konumu = :airportCode
        """,
        nativeQuery = true
    )
    List<TourPackage> findPackagesByDepartureAirport(@Param("airportCode") String airportCode);
    // İsmi verilen oteli içeren paketleri getir (Arama kelimesini içerenler)
    @Query(
        value = """
            SELECT DISTINCT tp.* FROM Tour_Package tp
            JOIN Hotel_Package hp ON tp.package_id = hp.package_id
            JOIN Hotel h ON hp.hotel_id = h.hotel_id
            WHERE h.hotel_name LIKE %:hotelName%
        """,
        nativeQuery = true
    )
    List<TourPackage> findPackagesByHotelName(@Param("hotelName") String hotelName);

}
