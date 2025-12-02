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
                        public Integer userId = created.getUser().getId();
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


        @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        try {
            Company c = companyService.getCompanyById(id);

            return ResponseEntity.ok(
                    new Object() {
                        public Long companyId = c.getId();
                        public Integer userId = c.getUser().getId();
                        public String tursabNo = c.getTursabNo();
                        public String name = c.getName();
                        public String email = c.getEmail();
                        public String phoneNo = c.getPhoneNo();
                        public String address = c.getAddress();
                    }
            );
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sunucu hatası");
        }
    }



        // Kullanıcı ID'sine göre şirketi döner
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getCompanyByUser(@PathVariable Integer userId) {
        try {
            Company c = companyService.getCompanyByUserId(userId);

            // Frontend için sade bir JSON dönüyoruz
            return ResponseEntity.ok(
                    new Object() {
                        public Long id = c.getId();
                        public Integer userId = c.getUser().getId();
                        public String tursabNo = c.getTursabNo();
                        public String name = c.getName();
                        public String email = c.getEmail();
                    }
            );
        } catch (RuntimeException e) {
            // Bu kullanıcıya ait company yoksa 404 dönelim
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sunucu hatası");
        }
    }

}
