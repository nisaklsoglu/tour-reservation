package com.bil372.tour_reservation.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInit {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInit(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Uygulama ayağa kalkınca çalışır
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            System.out.println("🔄 Veritabanı triggerları kontrol ediliyor...");

            // 1. Modül: Yorum Triggerlarını Yükle
            initReviewTriggers();

            // 2. Modül: Rezervasyon Triggerlarını Yükle
            initReservationTriggers();

            System.out.println("✅ Tüm veritabanı triggerları başarıyla yüklendi.");

        } catch (Exception e) {
            System.err.println("⚠️ Trigger kurulumunda hata: " + e.getMessage());
        }
    }

    // --- MODÜL 1: Yorum (Review) İşlemleri ---
    private void initReviewTriggers() {
        // Temizlik
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_tour_stats_insert");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_tour_stats_update");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_tour_stats_delete");
        // Eski isimleri de silelim
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_update_avg_after_insert");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_update_avg_after_update");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_update_avg_after_delete");

        // INSERT (Yorum Eklendiğinde)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_tour_stats_insert
            AFTER INSERT ON Review
            FOR EACH ROW
            BEGIN
                UPDATE Tour t
                SET 
                    review_count = (SELECT COUNT(*) FROM Review WHERE tour_id = NEW.tour_id),
                    avg_rating = (SELECT AVG(rating) FROM Review WHERE tour_id = NEW.tour_id)
                WHERE t.tour_id = NEW.tour_id;
            END;
        """);

        // UPDATE (Puan Değiştiğinde)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_tour_stats_update
            AFTER UPDATE ON Review
            FOR EACH ROW
            BEGIN
                UPDATE Tour t
                SET 
                    review_count = (SELECT COUNT(*) FROM Review WHERE tour_id = NEW.tour_id),
                    avg_rating = (SELECT AVG(rating) FROM Review WHERE tour_id = NEW.tour_id)
                WHERE t.tour_id = NEW.tour_id;
            END;
        """);

        // DELETE (Yorum Silindiğinde)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_tour_stats_delete
            AFTER DELETE ON Review
            FOR EACH ROW
            BEGIN
                UPDATE Tour t
                SET 
                    review_count = (SELECT COUNT(*) FROM Review WHERE tour_id = OLD.tour_id),
                    avg_rating = (SELECT AVG(rating) FROM Review WHERE tour_id = OLD.tour_id)
                WHERE t.tour_id = OLD.tour_id;
            END;
        """);
        
        System.out.println("   -> Review (Yorum) triggerları hazır.");
    }

    // --- MODÜL 2: Rezervasyon (Reservation) İşlemleri ---
    private void initReservationTriggers() {
        // Temizlik
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_pkg_booked_insert");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_pkg_booked_update");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_pkg_booked_delete");

        // Bu triggerlar, 'Tour_Package' tablosundaki 'booked_count' alanını günceller.
        // İptal edilmemiş (Status != 'Iptal') tüm rezervasyonları sayar.

        // INSERT (Yeni Rezervasyon)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_pkg_booked_insert
            AFTER INSERT ON Reservation
            FOR EACH ROW
            BEGIN
                UPDATE Tour_Package tp
                SET booked_count = (
                    SELECT COUNT(*) 
                    FROM Reservation r 
                    WHERE r.package_id = NEW.package_id 
                    AND r.status != 'Iptal'
                )
                WHERE tp.package_id = NEW.package_id;
            END;
        """);

        // UPDATE (Rezervasyon İptal Edilirse veya Onaylanırsa)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_pkg_booked_update
            AFTER UPDATE ON Reservation
            FOR EACH ROW
            BEGIN
                UPDATE Tour_Package tp
                SET booked_count = (
                    SELECT COUNT(*) 
                    FROM Reservation r 
                    WHERE r.package_id = NEW.package_id 
                    AND r.status != 'Iptal'
                )
                WHERE tp.package_id = NEW.package_id;
            END;
        """);

        // DELETE (Rezervasyon Silinirse)
        jdbcTemplate.execute("""
            CREATE TRIGGER trg_pkg_booked_delete
            AFTER DELETE ON Reservation
            FOR EACH ROW
            BEGIN
                UPDATE Tour_Package tp
                SET booked_count = (
                    SELECT COUNT(*) 
                    FROM Reservation r 
                    WHERE r.package_id = OLD.package_id 
                    AND r.status != 'Iptal'
                )
                WHERE tp.package_id = OLD.package_id;
            END;
        """);

        System.out.println("   -> Reservation (Kontenjan) triggerları hazır.");
    }
}