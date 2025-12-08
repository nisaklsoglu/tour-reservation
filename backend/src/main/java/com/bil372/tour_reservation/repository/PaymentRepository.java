package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
}