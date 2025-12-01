package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.CompanyRegisterRequest;
import com.bil372.tour_reservation.entity.Company;
import com.bil372.tour_reservation.entity.User;
import com.bil372.tour_reservation.repository.CompanyRepository;
import com.bil372.tour_reservation.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company registerCompanyForUser(CompanyRegisterRequest request) {

        // 1) user var mı?
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        // 2) Bu user zaten şirket mi?
        if (companyRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Bu kullanıcı zaten şirket olarak kayıtlı.");
        }

        // 3) TÜRSAB ve email benzersiz mi?
        if (companyRepository.existsByTursabNo(request.getTursabNo())) {
            throw new RuntimeException("Bu TÜRSAB numarası zaten kayıtlı.");
        }

        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu şirket emaili zaten kayıtlı.");
        }

        // 4) Company entity oluştur
        Company company = new Company();
        company.setUser(user);
        company.setTursabNo(request.getTursabNo());
        company.setName(request.getName());
        company.setEmail(request.getEmail());
        company.setPhoneNo(request.getPhoneNo());
        company.setAddress(request.getAddress());

        // 5) DB'ye kaydet
        return companyRepository.save(company);
    }
}
