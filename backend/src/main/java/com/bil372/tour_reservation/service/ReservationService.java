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

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findReservationsByUserId(userId);
    }

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        // 1. İlgili Tur Paketini Bul
        TourPackage tourPackage = tourPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Paket bulunamadı!"));

        // 2. KAPASİTE KONTROLÜ (YENİ MANTIK)
        // Frontend'den gelen yolcu sayısı ile veritabanındaki kalan koltuğu kıyasla
        int totalPassengers = request.getPassengers().size(); // Listeden alıyoruz
        
        if (tourPackage.getAvailableSeats() < totalPassengers) {
            throw new RuntimeException("Kapasite Dolu! Kalan yer: " + tourPackage.getAvailableSeats());
        }

        // 3. STOKTAN DÜŞ
        tourPackage.setAvailableSeats(tourPackage.getAvailableSeats() - totalPassengers);
        tourPackageRepository.save(tourPackage);

        // 4. Rezervasyon Ana Kaydını Oluştur
        Reservation reservation = new Reservation();
        reservation.setUser_id(request.getUserId()); // Mevcut kullanıcı
       // DOĞRU: Yukarıda veritabanından çektiğimiz 'tourPackage' nesnesini veriyoruz
        reservation.setTourPackage(tourPackage);
        reservation.setReservationDate(java.time.LocalDate.now());
        reservation.setStatus("Beklemede"); 

        // Fiyat Hesapla (Basit mantık: Kişi Başı * Kişi Sayısı)
        // İleride oda tipine göre burada switch-case yapabiliriz.
        if (tourPackage.getBasePrice() != null) {
            BigDecimal totalPrice = tourPackage.getBasePrice().multiply(BigDecimal.valueOf(totalPassengers));
            reservation.setTotalPrice(totalPrice);
        }

        // Rezervasyonu Kaydet (ID oluşsun diye)
        Reservation savedReservation = reservationRepository.save(reservation);

        // 5. YOLCULARI KAYDET (Döngü ile)
        for (PassengerRequest pReq : request.getPassengers()) {
            Passenger passenger = new Passenger();
            
            passenger.setPassengerName(pReq.getName());
            passenger.setPassengerTcKimlik(pReq.getTcKimlik());
            passenger.setPassengerPhoneNo(pReq.getPhone());
            passenger.setPassengerEmail(pReq.getEmail());
            passenger.setPasaportNo(pReq.getPasaportNo());
            passenger.setBirthDate(pReq.getBirthDate());
            
            // Yolcuyu bu rezervasyona bağla (Foreign Key)
            passenger.setReservationId(savedReservation.getReservationId());

            passengerRepository.save(passenger);
        }

        return savedReservation;
    }

        
    public List<Reservation> getReservationByUserId(Integer userId) {
        return reservationRepository.findReservationByUserId(userId);
    }


}