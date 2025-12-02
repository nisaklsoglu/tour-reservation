package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.LoginRequest;
import com.bil372.tour_reservation.dto.RegisterRequest;
import com.bil372.tour_reservation.entity.User;
import com.bil372.tour_reservation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // --------- REGISTER ENDPOINT ---------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email ve şifre zorunludur.");
        }

        try {
            User created = userService.register(request);

            return ResponseEntity.ok(
                    new Object() {
                        public Integer id = created.getId();
                        public String email = created.getEmail();
                    }
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sunucu hatası");
        }
    }

    // --------- LOGIN ENDPOINT ---------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email ve şifre zorunludur.");
        }

        try {
            // UserService tarafında böyle bir metod yoksa birazdan stubını da yazarız
            User user = userService.login(request.getEmail(), request.getPassword());

            if (user == null) {
                return ResponseEntity.status(401).body("Geçersiz email veya şifre.");
            }

            // Frontend login.html JSON {id, email} bekliyor
            return ResponseEntity.ok(
                    new Object() {
                        public Integer id = user.getId();
                        public String email = user.getEmail();
                    }
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sunucu hatası");
        }
    }
}
