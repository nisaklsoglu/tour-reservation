package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.AddFlightRequest;
import com.bil372.tour_reservation.dto.FlightCreateRequest;
import com.bil372.tour_reservation.entity.Flight;
import com.bil372.tour_reservation.entity.FlightPackage;
import com.bil372.tour_reservation.repository.FlightPackageRepository;
import com.bil372.tour_reservation.repository.FlightRepository;
import com.bil372.tour_reservation.repository.TourPackageRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightPackageRepository flightPackageRepository;
    private final TourPackageRepository tourPackageRepository;

    public FlightService(FlightRepository flightRepository,
                         FlightPackageRepository flightPackageRepository,
                         TourPackageRepository tourPackageRepository) {
        this.flightRepository = flightRepository;
        this.flightPackageRepository = flightPackageRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    // --- 1. SIFIRDAN UÇUŞ EKLEME ---
    public Flight createFlight(FlightCreateRequest request) {
        Flight f = new Flight();
        f.setPnrNo(request.getPnrNo());
        f.setFirma(request.getFirma());
        f.setKalkisKonumu(request.getKalkis());
        f.setVarisKonumu(request.getVaris());
        return flightRepository.save(f);
    }

    // --- 2. VAR OLAN UÇUŞU PAKETE BAĞLAMA ---
    public void addFlightToPackage(AddFlightRequest request) {
        // Kontroller
        if (!flightRepository.existsById(request.getFlightId())) {
            throw new RuntimeException("Uçuş bulunamadı! ID: " + request.getFlightId());
        }
        if (!tourPackageRepository.existsById(request.getPackageId())) {
            throw new RuntimeException("Paket bulunamadı! ID: " + request.getPackageId());
        }

        FlightPackage fp = new FlightPackage();
        fp.setPackageId(request.getPackageId());
        fp.setFlightId(request.getFlightId());
        fp.setFlightType(request.getFlightType());
        fp.setDepartureDate(request.getDepartureDate());
        fp.setDepartureTime(request.getDepartureTime());
        fp.setArrivalTime(request.getArrivalTime()); 
        fp.setDuration(request.getDuration());
        flightPackageRepository.save(fp);
    }
}