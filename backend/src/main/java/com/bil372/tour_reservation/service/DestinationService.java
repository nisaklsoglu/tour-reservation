package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.entity.Destination;
import com.bil372.tour_reservation.repository.DestinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<String> getAllCountries() {
        return destinationRepository.findDistinctCountries();
    }

    public List<Destination> getDestinationsByCountry(String country) {
        return destinationRepository.findByDestinationCountryOrderByDestinationCityAsc(country);
    }
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }
    // YENİ VE AKILLI KAYIT METODU
    public Destination createDestination(Destination destination) {
        
        // 1. Gelen verileri temizle ve formatla (Baş harf büyük, gerisi küçük)
        // Örn: "pARis" -> "Paris", "  fransa " -> "Fransa"
        String cleanCity = capitalize(destination.getDestinationCity());
        String cleanCountry = capitalize(destination.getDestinationCountry());
        String cleanName = cleanCity + " Merkez"; // İsmi otomatik oluşturuyoruz

        // Formatlanmış hallerini nesneye geri yaz
        destination.setDestinationCity(cleanCity);
        destination.setDestinationCountry(cleanCountry);
        destination.setDestinationName(cleanName);

        // 2. KONTROL ET: Bu şehir veritabanında zaten var mı?
        Destination existingDest = destinationRepository.findByDestinationCityAndDestinationCountry(cleanCity, cleanCountry);

        if (existingDest != null) {
            // VARSA: Hiçbir şey yapma, var olanı döndür (Böylece ID'si aynı kalır)
            return existingDest;
        }

        // YOKSA: Yeni kaydı oluştur
        return destinationRepository.save(destination);
    }

    // --- YARDIMCI METOT (En alta ekle) ---
    // Kelimelerin ilk harfini büyütür: "ankara" -> "Ankara"
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        
        str = str.trim(); // Kenardaki boşlukları sil
        
        // Türkçe karakter sorununu çözmek için Locale kullanabilirsin ama şimdilik basit tutalım:
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    

    public List<Destination> getCitiesByCountry(String country) {
        // Repository'deki uzun isimli metodu çağırıyoruz
        return destinationRepository.findByDestinationCountryOrderByDestinationCityAsc(country);
    }
}
