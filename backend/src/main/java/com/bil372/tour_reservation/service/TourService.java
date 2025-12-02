package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
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

        // 1. TUR BLUEPRINT OLUŞTUR
        Tour tour = new Tour();
        tour.setCompanyId(request.getCompanyId());
        tour.setPackageName(request.getPackageName());
        tour.setDescription(request.getDescription());
        tour.setTourType(request.getTourType());
        tour.setCapacity(request.getCapacity());     // kapasite
        tour.setDuration(request.getDuration());     // ✅ EKSİK OLAN KISIM

        tour.setReview_count(0);
        tour.setAvg_rating(null);

        // Çoklu destinasyon
        if (request.getDestinationIds() != null && !request.getDestinationIds().isEmpty()) {
            List<Destination> destinations = destinationRepository.findAllById(request.getDestinationIds());
            tour.setDestinations(destinations);
        }

        // 2. Sadece TUR'u kaydet (paket yok)
        return tourRepository.save(tour);
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
    
    




    

}
