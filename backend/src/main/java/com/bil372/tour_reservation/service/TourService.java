package com.bil372.tour_reservation.service;

import com.bil372.tour_reservation.dto.PackageCreateRequest;
import com.bil372.tour_reservation.dto.TourCreateRequest;
import com.bil372.tour_reservation.entity.Tour;
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
    private final TourDestinationRepository tourDestinationRepository;
    private final DestinationRepository destinationRepository
    ;
    public TourService(TourRepository tourRepository, TourPackageRepository tourPackageRepository,
                       TourDestinationRepository tourDestinationRepository,
                       DestinationRepository destinationRepository) {
        this.tourRepository = tourRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.tourDestinationRepository = tourDestinationRepository;
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
        
        // Önce Turu Bul (Yoksa hata ver)
        Tour existingTour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı! ID: " + request.getTourId()));

        // Yeni Paketi Hazırla
        TourPackage newPackage = new TourPackage();
        newPackage.setTour(existingTour); // İlişkiyi kuruyoruz
        
        newPackage.setStartDate(request.getStartDate());
        newPackage.setEndDate(request.getEndDate());
        newPackage.setBasePrice(request.getBasePrice());
        newPackage.setGuideId(request.getGuideId());
        newPackage.setBookedCount(0); // Yeni olduğu için boş

        // Kaydet
        return tourPackageRepository.save(newPackage);

    }
    @Transactional // Bu çok önemli! Biri hata verirse ikisi de kaydolmaz.
    public Tour createTourWithPackage(TourCreateRequest request) {
        
        // 1. Önce Genel TUR Bilgisini Oluştur ve Kaydet
        Tour tour = new Tour();
        tour.setCompanyId(request.getCompanyId());
        tour.setDestinationId(request.getDestinationId());
        tour.setPackageName(request.getPackageName());
        tour.setDescription(request.getDescription());
        tour.setTourType(request.getTourType());
        tour.setCapacity(request.getCapacity());
        
        // Başlangıç değerleri (Trigger bunları güncelleyecek ama boş kalmasın)
        tour.setReview_count(0); 
        tour.setAvg_rating(null);

        // KAYDET (Artık tour nesnesinin bir ID'si var!)
        Tour savedTour = tourRepository.save(tour);

        // 2. Şimdi Bu Tura Ait İlk PAKETİ Oluştur
        TourPackage tourPackage = new TourPackage();
        
        // İlişkiyi Kuruyoruz: Bu paket, az önce kaydettiğimiz tura ait.
        tourPackage.setTour(savedTour); 
        
        tourPackage.setStartDate(request.getStartDate());
        tourPackage.setEndDate(request.getEndDate());
        tourPackage.setBasePrice(request.getBasePrice());
        tourPackage.setGuideId(request.getGuideId());
        tourPackage.setBookedCount(0); // Yeni paket boş başlar

        // Paketi Kaydet
        tourPackageRepository.save(tourPackage);

        return savedTour;
    }
    public void addDestinationToTour(Integer tourId, Integer destinationId) {
        // Kontroller
        if (!tourRepository.existsById(tourId)) {
            throw new RuntimeException("Tur bulunamadı!");
        }
        // DestinationRepository oluşturduysan kontrol et:
        // if (!destinationRepository.existsById(destinationId))...

        TourDestination td = new TourDestination();
        td.setTourId(tourId);
        td.setDestinationId(destinationId);
        
        tourDestinationRepository.save(td);
    }


    public List<Tour> getToursByCity(String cityName) {
        return tourRepository.findToursByCity(cityName);
    }



    

}
