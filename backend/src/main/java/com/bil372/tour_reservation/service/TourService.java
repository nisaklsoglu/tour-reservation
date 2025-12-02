package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
import com.bil372.tour_reservation.dto.TourWithPackagesDTO;
import com.bil372.tour_reservation.entity.Tour;
import com.bil372.tour_reservation.entity.Destination; // Eklendi
import com.bil372.tour_reservation.entity.TourDestination;
import com.bil372.tour_reservation.entity.TourPackage;
import com.bil372.tour_reservation.repository.DestinationRepository;
import com.bil372.tour_reservation.repository.TourDestinationRepository;
import com.bil372.tour_reservation.repository.TourPackageRepository;
import com.bil372.tour_reservation.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TourPackageRepository tourPackageRepository;
    private final DestinationRepository destinationRepository
    ;
    public TourService(TourRepository tourRepository, TourPackageRepository tourPackageRepository,
                       DestinationRepository destinationRepository) {
        this.tourRepository = tourRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.destinationRepository = destinationRepository;
    }
    
    public List<Tour> getAllTours() {
        return tourRepository.findAllTours();
    }

    public List<Tour> getToursByDestination(Integer destId)  {
        return tourRepository.findByDestinationId(destId);
    }

    public List<Tour> getToursByCompany(Integer compId)  {
        return tourRepository.findByCompanyId(compId);
    }

    public List<Tour> getTopRatedTours() {
        return tourRepository.findTopRatedTours();
    }
    public List<Tour> getMostReviewedTours() {    
        return tourRepository.findMostReviewedTours();
    }
    
    public List<Tour> getToursByDestinationSorted(Integer destId) {
        return tourRepository.findByDestinationIdSorted(destId);
    }
    @Transactional
   
    
    public TourPackage addPackageToExistingTour(PackageCreateRequest request) {
        
        // 1. Önce Turu Bul
        Tour existingTour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı! ID: " + request.getTourId()));

        // 2. Yeni Paketi Hazırla
        TourPackage newPackage = new TourPackage();
        newPackage.setTour(existingTour); 
        
        newPackage.setStartDate(request.getStartDate());
        newPackage.setEndDate(request.getEndDate());
        newPackage.setBasePrice(request.getBasePrice());
        newPackage.setGuideId(request.getGuideId());
        newPackage.setBookedCount(0); // Henüz kimse almadı

        // --- YENİ EKLENEN KRİTİK KISIM ---
        // Paketin başlangıç stoğunu, turun genel kapasitesine eşitliyoruz.
        // Örn: Tur 40 kişilikse, paketin de 40 boş koltuğu vardır.
        newPackage.setAvailableSeats(existingTour.getCapacity());
        // ---------------------------------

        // Kaydet
        return tourPackageRepository.save(newPackage);
    }

    @Transactional 
    public Tour createTourWithPackage(TourCreateRequest request) {
        
        // 1. Önce Genel TUR Bilgisini Oluştur
        Tour tour = new Tour();
        tour.setCompanyId(request.getCompanyId());
        
        // (destinationId satırı silindi, artık liste kullanıyoruz)
        
        tour.setPackageName(request.getPackageName());
        tour.setDescription(request.getDescription());
        tour.setTourType(request.getTourType());
        tour.setCapacity(request.getCapacity()); // Örn: 40 Kişi
        
        tour.setReview_count(0); 
        tour.setAvg_rating(null);

        // Çoklu Şehir Ekleme
        if (request.getDestinationIds() != null && !request.getDestinationIds().isEmpty()) {
            List<Destination> destinations = destinationRepository.findAllById(request.getDestinationIds());
            tour.setDestinations(destinations);
        }

        // Turu Kaydet
        Tour savedTour = tourRepository.save(tour);

        // 2. Şimdi Bu Tura Ait İlk PAKETİ Oluştur
        if (request.getStartDate() != null && request.getBasePrice() != null) {
            TourPackage tourPackage = new TourPackage();
            tourPackage.setTour(savedTour); 
            tourPackage.setStartDate(request.getStartDate());
            tourPackage.setEndDate(request.getEndDate());
            tourPackage.setBasePrice(request.getBasePrice());
            tourPackage.setGuideId(request.getGuideId());
            tourPackage.setBookedCount(0); 
            
            // --- YENİ EKLENEN KRİTİK KISIM ---
            // İlk paketin boş koltuk sayısı = Turun Kapasitesi
            tourPackage.setAvailableSeats(savedTour.getCapacity());
            // ---------------------------------
            
            tourPackageRepository.save(tourPackage);
        }

        return savedTour;
    }
    


    public List<Tour> getToursByCity(String keyword) {
    return tourRepository.searchByCityOrPackageName(keyword);
}
    public Tour getTourById(Integer id) {
    return tourRepository.findById(id).orElse(null);
}
public List<Tour> getToursByCountry(String countryName) {
        return tourRepository.findByCountry(countryName);
    }

    // Sadece Süre Filtresi (Aralıklı)
    public List<Tour> getToursByDuration(Integer min, Integer max) {
        // Eğer kullanıcı boş gönderirse varsayılan değerler atayalım
        if (min == null) min = 0;        // En az 0 gün
        if (max == null) max = 100;      // En çok 100 gün (veya Integer.MAX_VALUE)
        
        return tourRepository.findByDurationBetween(min, max);
    }
    
    public List<TourWithPackagesDTO> getToursWithFilteredPackages(BigDecimal min, BigDecimal max) {
        // 1. Önce fiyat aralığına uyan paketlerin sahibi olan turları bul
        List<Tour> tours = tourRepository.findToursByPackagePriceRange(min, max);
        
        List<TourWithPackagesDTO> result = new ArrayList<>();

        // 2. Her tur için, o fiyat aralığına uyan paketleri ayıkla
        for (Tour tour : tours) {
            List<TourPackage> filteredPackages = tourPackageRepository.findByTourAndPriceRange(tour.getTourId(), min, max);
            result.add(new TourWithPackagesDTO(tour, filteredPackages));
        }
        
        return result;
    }
    // Kapasiteye Göre Filtreli DTO Döndüren Metot
    public List<TourWithPackagesDTO> getToursWithFilteredPackagesByCapacity(Integer seats) {
        // 1. Yeterli yeri olan turları bul
        List<Tour> tours = tourRepository.findToursByPackageAvailability(seats);
        
        List<TourWithPackagesDTO> result = new ArrayList<>();

        // 2. Her tur için, o kapasite şartını sağlayan paketleri ayıkla
        for (Tour tour : tours) {
            List<TourPackage> filteredPackages = tourPackageRepository.findByTourAndAvailability(tour.getTourId(), seats);
            result.add(new TourWithPackagesDTO(tour, filteredPackages));
        }
        
        return result;
    }

    

    public List<TourWithPackagesDTO> searchTours(String city, String country, BigDecimal minPrice, BigDecimal maxPrice, 
                                                 Integer guests, Integer minDur, Integer maxDur, String sortBy) {
        
        // 1. Temel Liste: Tüm Turları Çek
        List<Tour> allTours;
        if (city != null && !city.isEmpty()) {
            allTours = tourRepository.searchByCityOrPackageName(city);
        } else if (country != null && !country.isEmpty()) {
            allTours = tourRepository.findByCountry(country);
        } else {
            allTours = tourRepository.findAll();
        }

        List<TourWithPackagesDTO> result = new ArrayList<>();

        // 2. Her Tur İçin Filtreleme Yap
        for (Tour tour : allTours) {
            
            // A. Süre Filtresi
            if (minDur != null && tour.getDuration() < minDur) continue;
            if (maxDur != null && tour.getDuration() > maxDur) continue;

            // B. Paketleri Bul (Fiyat ve Kapasiteye Göre)
            List<TourPackage> packages = tourPackageRepository.findByTour_TourId(tour.getTourId());
            List<TourPackage> validPackages = new ArrayList<>();

            for (TourPackage p : packages) {
                boolean priceOk = true;
                boolean capacityOk = true;

                // Fiyat Kontrolü
                if (minPrice != null && p.getBasePrice().compareTo(minPrice) < 0) priceOk = false;
                if (maxPrice != null && p.getBasePrice().compareTo(maxPrice) > 0) priceOk = false;

                // Kapasite Kontrolü
                if (guests != null && p.getAvailableSeats() < guests) capacityOk = false;

                if (priceOk && capacityOk) {
                    validPackages.add(p);
                }
            }

            if (!validPackages.isEmpty()) {
                // --- PAKETLERİ DE SIRALA (YENİ KISIM) ---
                if ("priceAsc".equals(sortBy)) {
                    validPackages.sort(Comparator.comparing(TourPackage::getBasePrice));
                } else if ("priceDesc".equals(sortBy)) {
                    validPackages.sort(Comparator.comparing(TourPackage::getBasePrice).reversed());
                }
                // ---------------------------------------

                result.add(new TourWithPackagesDTO(tour, validPackages));
            }
        }

        // 3. Turları (Dış Listeyi) Sırala
        applySorting(result, sortBy);

        return result;
    }

    // --- SIRALAMA YARDIMCISI ---
    private void applySorting(List<TourWithPackagesDTO> list, String sortBy) {
        // En ucuz paketin fiyatını bulan fonksiyon
        Function<TourWithPackagesDTO, BigDecimal> minPriceExtractor = (dto) -> 
            dto.getPackages().stream()
               .map(TourPackage::getBasePrice)
               .min(BigDecimal::compareTo)
               .orElse(BigDecimal.ZERO);

        // En pahalı paketin fiyatını bulan fonksiyon
        Function<TourWithPackagesDTO, BigDecimal> maxPriceExtractor = (dto) -> 
            dto.getPackages().stream()
               .map(TourPackage::getBasePrice)
               .max(BigDecimal::compareTo)
               .orElse(BigDecimal.ZERO);

        if ("priceAsc".equals(sortBy)) {
            list.sort(Comparator.comparing(minPriceExtractor));
        } 
        else if ("priceDesc".equals(sortBy)) {
            list.sort(Comparator.comparing(maxPriceExtractor).reversed());
        } 
        else {
            // Puana Göre Sırala (Varsayılan)
            list.sort(Comparator.comparing(
                (TourWithPackagesDTO dto) -> dto.getTour().getAvg_rating() != null ? dto.getTour().getAvg_rating() : 0.0,
                Comparator.reverseOrder()
            ));
        }
    }




    

}
