package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
import com.bil372.tour_reservation.dto.TourSearchDto;
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
import java.util.Comparator;
import java.util.List;

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
    
    

    public List<TourSearchDto> searchTours(
            String country,
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minDur,
            Integer maxDur,
            Integer guests,
            String sort
    ) {
        // 1) Repository'den filtrelenmiş veriyi al
        List<TourPackage> list = tourPackageRepository.searchByFilters(
                country, city, minPrice, maxPrice, minDur, maxDur, guests
        );

        // 2) Sıralama uygula
        if ("priceAsc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(TourPackage::getBasePrice));
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(TourPackage::getBasePrice).reversed());
        }

        // 3) DTO'ya map et
        return list.stream()
                   .map(this::toDtoFromPackage)
                   .toList();
    }

    private TourSearchDto toDtoFromPackage(TourPackage tp) {
    Destination d = tp.getTour() != null && tp.getTour().getDestinations() != null && !tp.getTour().getDestinations().isEmpty()
                    ? tp.getTour().getDestinations().get(0)
                    : null;

    TourSearchDto dto = new TourSearchDto();
    dto.setTourId(tp.getPackageId());
    dto.setBasePrice(tp.getBasePrice());
    // packageName/description live on the parent Tour entity
    if (tp.getTour() != null) {
        dto.setPackageName(tp.getTour().getPackageName());
        dto.setDescription(tp.getTour().getDescription());
    }
    dto.setStartDate(tp.getStartDate());
    dto.setEndDate(tp.getEndDate());
    if (d != null) {
        dto.setCountry(d.getDestinationCountry());
        dto.setCity(d.getDestinationCity());
    }
    return dto;
}




    

}
