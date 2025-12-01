package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.RegisterRequest;
import com.bil372.tour_reservation.entity.User;
import com.bil372.tour_reservation.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {

        // email kayıtlı mı?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email zaten kayitli.");
        }

        // entity oluştur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // HASH

        // DB'ye kaydet
        return userRepository.save(user);
    }
}
