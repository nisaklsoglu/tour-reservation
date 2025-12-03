package com.bil372.tour_reservation.dto;
import lombok.Data;

@Data
public class GuideRequest {
    // JavaScript'teki "data" objesindeki anahtarlar bunlar olmalı
    private String guideName;
    private String guidePhone;
    private String guideEmail;
}