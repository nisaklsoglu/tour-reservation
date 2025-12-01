package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {

    // DISTINCT ülke listesi
    @Query("SELECT DISTINCT d.destinationCountry FROM Destination d ORDER BY d.destinationCountry")
    List<String> findDistinctCountries();

    // Belirli ülkeye ait destinasyonlar
    List<Destination> findByDestinationCountryOrderByDestinationCityAsc(String destinationCountry);

    Destination findByDestinationCityAndDestinationCountry(String city, String country);
}
