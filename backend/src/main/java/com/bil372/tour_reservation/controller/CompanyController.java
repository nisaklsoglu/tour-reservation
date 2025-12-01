package com.bil372.tour_reservation.controller;

import com.bil372.tour_reservation.dto.CompanyRegisterRequest;
import com.bil372.tour_reservation.entity.Company;
import com.bil372.tour_reservation.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@RequestBody CompanyRegisterRequest request) {

        // basit validation
        if (request.getUserId() == null ||
            request.getTursabNo() == null || request.getTursabNo().isBlank() ||
            request.getName() == null || request.getName().isBlank() ||
            request.getEmail() == null || request.getEmail().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("userId, TÜRSAB no, şirket adı ve email zorunludur.");
        }

        try {
            Company created = companyService.registerCompanyForUser(request);

            // frontende döneceğimiz kısa cevap
            return ResponseEntity.ok(
                    new Object() {
                        public Long id = created.getId();
                        public Long userId = created.getUser().getId();
                        public String tursabNo = created.getTursabNo();
                        public String name = created.getName();
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
