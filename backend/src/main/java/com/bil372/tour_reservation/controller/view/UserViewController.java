package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.entity.view.UserReservationView;
import com.bil372.tour_reservation.service.ViewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5173"
})
public class UserViewController {

    private final ViewService viewService;

    public UserViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping("/{userId}/reservation-summary")
    public List<UserReservationView> getUserReservationSummary(@PathVariable Integer userId) {
        return viewService.getUserReservationSummary(userId);
    }
}
