package com.bil372.tour_reservation.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private Integer reservationId;
    private BigDecimal amount;
    
    // Kart Bilgileri (Veritabanına kaydetmeyeceğiz, sadece simülasyon için)
    private String cardHolderName;
    private String cardNumber;
    private String expireDate;
    private String cvv;

    // Getter & Setter
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpireDate() { return expireDate; }
    public void setExpireDate(String expireDate) { this.expireDate = expireDate; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}