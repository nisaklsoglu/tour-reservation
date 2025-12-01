package com.bil372.tour_reservation.repository;

import com.bil372.tour_reservation.entity.Company;
import com.bil372.tour_reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByTursabNo(String tursabNo);

    boolean existsByEmail(String email);

    Optional<Company> findByUser(User user);   // bu user zaten şirket mi diye kontrol
}
