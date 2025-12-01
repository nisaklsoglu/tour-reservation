package com.bil372.tour_reservation.dto;

// --- GEREKLİ IMPORTLAR (Bunlar olmazsa hata verir) ---
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import java.time.LocalDate; // <--- BU EKSİKTİ, O YÜZDEN LOCALDATE HATASI ALIYORDUN
// -----------------------------------------------------

public class PassengerRequest {

    @NotBlank(message = "TC Kimlik numarası boş olamaz!")
    @Size(min = 11, max = 11, message = "TC Kimlik 11 haneli olmalıdır.")
    private String tcKimlik;

    @NotBlank(message = "Yolcu adı boş olamaz!")
    private String name;

    @NotBlank(message = "Telefon numarası zorunludur!")
    private String phone;

    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    private String email;

    @NotBlank(message = "Yurtdışı turları için Pasaport Numarası zorunludur!")
    private String pasaportNo;

    @NotNull(message = "Pasaport geçerlilik tarihi zorunludur!")
    @Future(message = "Pasaport süresi geçmiş olamaz, ileri bir tarih giriniz!")
    private LocalDate pasaportExpirationDate;

    @NotNull(message = "Doğum tarihi zorunludur!")
    private LocalDate birthDate;

    // --- GETTER VE SETTERLAR ---

    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tcKimlik) { this.tcKimlik = tcKimlik; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasaportNo() { return pasaportNo; }
    public void setPasaportNo(String pasaportNo) { this.pasaportNo = pasaportNo; }

    public LocalDate getPasaportExpirationDate() { return pasaportExpirationDate; }
    public void setPasaportExpirationDate(LocalDate pasaportExpirationDate) { this.pasaportExpirationDate = pasaportExpirationDate; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}