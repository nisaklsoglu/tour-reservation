package com.bil372.tour_reservation.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // Sonsuz döngü önleyici
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    // --- REZERVASYON İLİŞKİSİ ---
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonIgnore // JSON çevriminde sonsuz döngüyü engeller
    private Reservation reservation;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod; // "Credit Card", "Bank Transfer" vs.

    @Column(name = "provider")
    private String provider; // "Iyzico", "Stripe", "System"

    @Column(name = "provider_ref_no")
    private String providerRefNo; // Bankadan dönen işlem numarası

    @Column(name = "status")
    private String status; // "Succeeded", "Failed"

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "webhook_verified")
    private Boolean webhookVerified; // Güvenlik doğrulaması

    @Column(name = "idempotency_key")
    private String idempotencyKey; // Çifte ödemeyi önleyen anahtar

    // --- GETTER & SETTER ---

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderRefNo() { return providerRefNo; }
    public void setProviderRefNo(String providerRefNo) { this.providerRefNo = providerRefNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public Boolean getWebhookVerified() { return webhookVerified; }
    public void setWebhookVerified(Boolean webhookVerified) { this.webhookVerified = webhookVerified; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}