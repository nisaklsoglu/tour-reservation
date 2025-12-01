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
    // MANY-TO-MANY İÇİN GÜNCELLENMİŞ SORGU:
    // "t.destinations" diyerek Java üzerinden ilişkiye gidiyoruz.
    // JOIN işlemi otomatik yapılıyor.
    
    // MANY-TO-MANY İÇİN GÜNCELLENMİŞ SORGU:
    // "t.destinations" diyerek Java üzerinden ilişkiye gidiyoruz.
    // JOIN işlemi otomatik yapılıyor.
    
    // ÇOKLU ŞEHİR ARAMA SORGUSU (JPQL)
    // Mantık: Turun içindeki 'destinations' listesine gir (JOIN)
    // Şehir adında (d.destinationCity) VEYA Tur adında (t.packageName) arama yap.
    
    // NATIVE QUERY (Garanti Yöntem)
    // Direkt veritabanı tablolarına sorgu atıyoruz.
    
   @ Query("SELECT DISTINCT t FROM Tour t " +
           "LEFT JOIN t.destinations d " +
           "WHERE LOWER(t.packageName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR (d.destinationCity IS NOT NULL AND LOWER(d.destinationCity) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Tour> searchByCityOrPackageName(@Param("keyword") String keyword);

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

// Belirli bir ÜLKEYE ait turları getir
    @Query("SELECT DISTINCT t FROM Tour t " +
           "JOIN t.destinations d " +
           "WHERE LOWER(d.destinationCountry) = LOWER(:countryName)")
    List<Tour> findByCountry(@Param("countryName") String countryName);
    
    List<Tour> findByDurationBetween(Integer min, Integer max);
    
    // (Capacity metodu kalsın, ona dokunmuyoruz)
 
}
