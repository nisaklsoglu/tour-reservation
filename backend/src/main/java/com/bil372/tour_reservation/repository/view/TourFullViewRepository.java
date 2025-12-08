package com.bil372.tour_reservation.repository.view;

import com.bil372.tour_reservation.entity.view.TourFullView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourFullViewRepository extends JpaRepository<TourFullView, Integer> {

    List<TourFullView> findByCompanyId(Integer companyId);

    List<TourFullView> findByTourId(Integer tourId);
}
