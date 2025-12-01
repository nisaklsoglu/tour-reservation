package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.AddHotelRequest;
import com.bil372.tour_reservation.dto.HotelCreateRequest;
import com.bil372.tour_reservation.entity.Hotel;
import com.bil372.tour_reservation.entity.HotelPackage;
import com.bil372.tour_reservation.repository.HotelPackageRepository;
import com.bil372.tour_reservation.repository.HotelRepository;
import com.bil372.tour_reservation.repository.TourPackageRepository;
import org.springframework.stereotype.Service;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelPackageRepository hotelPackageRepository;
    private final TourPackageRepository tourPackageRepository;

    public HotelService(HotelRepository hotelRepository,
                        HotelPackageRepository hotelPackageRepository,
                        TourPackageRepository tourPackageRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelPackageRepository = hotelPackageRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    // --- 1. SIFIRDAN OTEL EKLEME ---
    public Hotel createHotel(HotelCreateRequest request) {
        Hotel h = new Hotel();
        h.setHotelName(request.getName());
        h.setHotelAddress(request.getAddress());
        h.setHotelRate(request.getRate());
        return hotelRepository.save(h);
    }

    // --- 2. VAR OLAN OTELİ PAKETE BAĞLAMA ---
    public void addHotelToPackage(AddHotelRequest request) {
        // Kontroller
        if (!hotelRepository.existsById(request.getHotelId())) {
            throw new RuntimeException("Otel bulunamadı! ID: " + request.getHotelId());
        }
        if (!tourPackageRepository.existsById(request.getPackageId())) {
            throw new RuntimeException("Paket bulunamadı! ID: " + request.getPackageId());
        }

        HotelPackage hp = new HotelPackage();
        hp.setPackageId(request.getPackageId());
        hp.setHotelId(request.getHotelId());
        hp.setEntryDate(request.getEntryDate());
        hp.setExitDate(request.getExitDate());

        hotelPackageRepository.save(hp);
    }
}