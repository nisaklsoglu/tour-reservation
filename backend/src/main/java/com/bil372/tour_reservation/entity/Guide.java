package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Guide")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Integer guideId;

    @Column(name = "guide_name")
    private String guideName;

    @Column(name = "guide_phone")
    private String guidePhone;

    @Column(name = "guide_email", unique = true)
    private String guideEmail;

    // --- Getter & Setter ---
    public Integer getGuideId() { return guideId; }
    public void setGuideId(Integer guideId) { this.guideId = guideId; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public String getGuidePhone() { return guidePhone; }
    public void setGuidePhone(String guidePhone) { this.guidePhone = guidePhone; }

    public String getGuideEmail() { return guideEmail; }
    public void setGuideEmail(String guideEmail) { this.guideEmail = guideEmail; }
}