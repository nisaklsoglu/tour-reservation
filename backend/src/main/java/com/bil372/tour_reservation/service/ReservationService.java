package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PassengerRequest;
import com.bil372.tour_reservation.dto.ReservationRequest;
import com.bil372.tour_reservation.entity.Passenger;
import com.bil372.tour_reservation.entity.Reservation;
import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.repository.PassengerRepository;
import com.bil372.tour_reservation.repository.ReservationRepository;
import com.bil372.tour_reservation.repository.TourPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TourPackageRepository tourPackageRepository;
    private final PassengerRepository passengerRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TourPackageRepository tourPackageRepository,
                              PassengerRepository passengerRepository) {
        this.reservationRepository = reservationRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.passengerRepository = passengerRepository;
    }

    public List<Reservation> getReservationsByUserId(Integer userId) {
        return reservationRepository.findReservationsByUserId(userId);
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        // 1. İlgili Tur Paketini Bul
        TourPackage tourPackage = tourPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Paket bulunamadı!"));

        // 2. KAPASİTE KONTROLÜ
        int capacity = tourPackage.getTour().getCapacity();
        int currentPassengers = reservationRepository.countPassengersInPackage(request.getPackageId());
        int newPassengersCount = request.getPassengers().size();

        if (currentPassengers + newPassengersCount > capacity) {
            throw new RuntimeException("Kapasite Dolu! Kalan yer: " + (capacity - currentPassengers));
        }

        // 3. Rezervasyon Nesnesini Oluştur
        Reservation reservation = new Reservation();
        
        // --- İSTEĞİN ÜZERİNE BURASI setUser_id OLARAK KALDI ---
        reservation.setUser_id(request.getUserId());
        // -----------------------------------------------------
        
        reservation.setTourPackage(tourPackage);
        reservation.setReservationDate(LocalDate.now());
        reservation.setStatus("Onaylandı");

        // Fiyat Hesapla
        if (tourPackage.getBasePrice() != null) {
            BigDecimal price = tourPackage.getBasePrice().multiply(BigDecimal.valueOf(newPassengersCount));
            reservation.setTotalPrice(price);
        }

        // 4. Önce Rezervasyonu Kaydet (ID oluşması için)
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. Şimdi Yolcuları Kaydet
        for (PassengerRequest pReq : request.getPassengers()) {
            Passenger passenger = new Passenger();
            
            // --- VERİ AKTARIMI (MAPPING) ---
            passenger.setPassengerName(pReq.getName());
            passenger.setPassengerTcKimlik(pReq.getTcKimlik());
            passenger.setPassengerPhoneNo(pReq.getPhone());
            passenger.setPassengerEmail(pReq.getEmail());
            
            // YENİ EKLENEN ZORUNLU ALANLAR (Pasaport ve Doğum Tarihi)
            passenger.setBirthDate(pReq.getBirthDate());
            passenger.setPasaportNo(pReq.getPasaportNo());
            passenger.setPasaportExpirationDate(pReq.getPasaportExpirationDate());
            
            // Yolcuyu bu rezervasyona bağla
            passenger.setReservationId(savedReservation.getReservationId());

            passengerRepository.save(passenger);
        }

        return savedReservation;
    }
}