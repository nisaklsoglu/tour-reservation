package com.bil372.tour_reservation.controller;

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

        // basit validation
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email ve şifre zorunludur.");
        }

        try {
            User created = userService.register(request);

            // frontende sadece id ve email dönelim
            return ResponseEntity.ok(
                    new Object() {
                        public Long id = created.getId();
                        public String email = created.getEmail();
                    }
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sunucu hatası");
        }
    }
}
