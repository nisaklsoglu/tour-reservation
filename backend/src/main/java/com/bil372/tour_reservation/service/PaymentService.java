package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PaymentRequest;
import com.bil372.tour_reservation.entity.Payment;
import com.bil372.tour_reservation.entity.Reservation;
import com.bil372.tour_reservation.repository.PaymentRepository;
import com.bil372.tour_reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        // 1. Rezervasyonu Bul
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı!"));

        // 2. Ödemeyi "Sanal Olarak" Başarılı Sayalım
        Payment payment = new Payment();
        payment.setReservation(reservation); // İlişkiyi kur
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod("Credit Card");
        payment.setProvider("System-Simulation"); // Iyzico yerine System
        payment.setProviderRefNo(UUID.randomUUID().toString()); // Rastgele bir referans no
        payment.setStatus("Succeeded"); // ENUM değerine dikkat! (Succeeded)
        payment.setPaymentDate(LocalDateTime.now());
        payment.setWebhookVerified(true);
        payment.setIdempotencyKey(UUID.randomUUID().toString());

        // 3. Ödemeyi Kaydet
        Payment savedPayment = paymentRepository.save(payment);

        // 4. KRİTİK NOKTA: Rezervasyon Durumunu Güncelle
        reservation.setStatus("Onaylandı");
        reservationRepository.save(reservation);

        return savedPayment;
    }
}
