package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.entity.*;
import com.bil372.tour_reservation.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TourPackageService {

    // --- TÜM REPOSITORY'LERİ TANIMLIYORUZ ---
    private final TourPackageRepository tourPackageRepository;
    private final HotelRepository hotelRepository;
    private final FlightRepository flightRepository;
    private final HotelPackageRepository hotelPackageRepository;
    private final FlightPackageRepository flightPackageRepository;
    private final GuideRepository guideRepository;

    // --- CONSTRUCTOR INJECTION (Hepsini buraya ekledik) ---
    public TourPackageService(TourPackageRepository tourPackageRepository,
                              HotelRepository hotelRepository,
                              FlightRepository flightRepository,
                              HotelPackageRepository hotelPackageRepository,
                              FlightPackageRepository flightPackageRepository,
                              GuideRepository guideRepository) {
        this.tourPackageRepository = tourPackageRepository;
        this.hotelRepository = hotelRepository;
        this.flightRepository = flightRepository;
        this.hotelPackageRepository = hotelPackageRepository;
        this.flightPackageRepository = flightPackageRepository;
        this.guideRepository = guideRepository;
    }

    // =========================================================
    // 1. ESKİ METOTLARIN (Filtreleme & Sıralama)
    // =========================================================

    public List<TourPackage> getPackagesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return tourPackageRepository.findPackagesInPriceRange(minPrice, maxPrice);
    }

    public List<TourPackage> getPackagesOrderByPriceAsc() {
        return tourPackageRepository.findPackageOrderByPriceAsc();
    }

    public List<TourPackage> getPackagesOrderByPriceDesc() {
        return tourPackageRepository.findPackageOrderByPriceDesc();
    }

    public List<TourPackage> getPackagesByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return tourPackageRepository.findPackagesByDateRange(startDate, endDate);
    }
   
    public List<TourPackage> getPackagesByAirline(String airlineName) {
        return tourPackageRepository.findPackagesByAirline(airlineName);
    }
    
    public List<TourPackage> getPackagesByDepartureAirport(String airportCode) {
        return tourPackageRepository.findPackagesByDepartureAirport(airportCode);
    }
    
    public List<TourPackage> getPackagesByHotelName(String hotelName) {
        return tourPackageRepository.findPackagesByHotelName(hotelName);
    }

    // =========================================================
    // 2. YENİ METOTLAR (Otel, Uçuş, Rehber Ekleme)
    // =========================================================

    // --- OTELİ PAKETE EKLEME ---
    public void addHotelToPackage(AddHotelRequest request) {
        // Kontroller
        if (!hotelRepository.existsById(request.getHotelId())) {
            throw new RuntimeException("Otel bulunamadı! ID: " + request.getHotelId());
        }
        if (!tourPackageRepository.existsById(request.getPackageId())) {
            throw new RuntimeException("Paket bulunamadı! ID: " + request.getPackageId());
        }

        // Ara Tabloya Kayıt
        HotelPackage hp = new HotelPackage();
        hp.setPackageId(request.getPackageId());
        hp.setHotelId(request.getHotelId());
        hp.setEntryDate(request.getEntryDate());
        hp.setExitDate(request.getExitDate());

        hotelPackageRepository.save(hp);
    }

    // --- UÇUŞU PAKETE EKLEME ---
    public void addFlightToPackage(AddFlightRequest request) {
        // Kontroller
        if (!flightRepository.existsById(request.getFlightId())) {
            throw new RuntimeException("Uçuş bulunamadı! ID: " + request.getFlightId());
        }
        if (!tourPackageRepository.existsById(request.getPackageId())) {
            throw new RuntimeException("Paket bulunamadı! ID: " + request.getPackageId());
        }

        // Ara Tabloya Kayıt
        FlightPackage fp = new FlightPackage();
        fp.setPackageId(request.getPackageId());
        fp.setFlightId(request.getFlightId());
        fp.setFlightType(request.getFlightType());
        fp.setDepartureDate(request.getDepartureDate());
        fp.setDepartureTime(request.getDepartureTime());
        
        // Eksik olan alanlar (DTO ve Entity güncellemiştik)
        fp.setArrivalTime(request.getArrivalTime());
        fp.setDuration(request.getDuration());

        flightPackageRepository.save(fp);
    }

    // --- REHBER ATAMA ---
    public void assignGuideToPackage(Integer packageId, Integer guideId) {
        // 1. Paket var mı?
        TourPackage tourPackage = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Paket bulunamadı! ID: " + packageId));

        // 2. Rehber var mı?
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Rehber bulunamadı! ID: " + guideId));

        // 3. Atamayı Yap (Entity'deki setGuide metodunu kullanır)
        tourPackage.setGuide(guide);
        
        // 4. Güncellemeyi Kaydet
        tourPackageRepository.save(tourPackage);
    }
}