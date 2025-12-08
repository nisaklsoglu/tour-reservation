const API_BASE = "http://localhost:8080/api";
const container = document.getElementById("reservations-container");
const reviewsContainer = document.getElementById("user-reviews-container");

// Login/register sırasında localStorage'a kaydedilen bilgiler:
const USER_ID = localStorage.getItem("userId");
const USER_EMAIL = localStorage.getItem("userEmail");
const IS_LOGGED_IN = localStorage.getItem("isLoggedIn") === "true";

let REVIEWED_TOUR_IDS = new Set();

// Eğer kullanıcı giriş yapmamışsa login sayfasına at
if (!IS_LOGGED_IN || !USER_ID) {
    alert("Bu sayfayı görmek için lütfen giriş yapın.");
    window.location.href = "login.html";
}

function formatDateTime(value) {
    if (!value) return "-";
    try {
        // Eğer sadece YYYY-MM-DD geliyorsa (saat yoksa)
        if (typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value)) {
            const [year, month, day] = value.split("-");
            const d = new Date(Number(year), Number(month) - 1, Number(day));
            // Sadece tarihi göster
            return d.toLocaleDateString("tr-TR");
        }

        const d = new Date(value);
        if (isNaN(d.getTime())) return value;

        return d.toLocaleString("tr-TR");
    } catch {
        return value;
    }
}



function renderReservationCard(r) {
    const pkg = r.tourPackage || {};
    const tour = pkg.tour || {};
    const review = r.review || null;

    const packageName = tour.packageName || pkg.packageName || "Bilinmeyen Tur";
    const packageId = pkg.packageId || "-";
    const totalPriceText = r.totalPrice != null ? `${r.totalPrice} TL` : "—";
    const statusText = r.status || "—";
    const rezDateText = formatDateTime(r.reservationDate);

    // Hem detay sayfası, hem review için kullanacağımız kimlik:
    const tourId = tour.tourId || pkg.tourId || pkg.packageId || null;
    const detailId = tourId || "";

    const hasReviewForThisTour = !!review || (tourId && REVIEWED_TOUR_IDS.has(tourId));

    let reviewHtml = "";

    if (review) {
        // Rezervasyonun kendi üzerinde review objesi varsa ayrıntılı göster
        reviewHtml = `
            <div class="review-block">
                <div><strong>Puan:</strong> ${review.rating} / 5</div>
                <div><strong>Yorum:</strong> ${review.comment || "-"}</div>
                <small class="muted">Yorum tarihi: ${formatDateTime(review.reviewDate)}</small>
            </div>
        `;
    } else if (hasReviewForThisTour) {
        // Bu tura zaten yorum yapılmış
        reviewHtml = `
            <div class="review-block">
                <strong>Bu tura zaten yorum yaptınız.</strong>
                <div class="muted" style="font-size:0.9em;">
                    Aynı tura ikinci kez yorum ekleyemezsiniz.
                </div>
            </div>
        `;
    } else {
       
        reviewHtml = `
            <div class="review-form">
                <label>Puan (1-5):</label>
                <select class="input-rating" data-reservation-id="${r.reservationId}" data-tour-id="${tourId || ""}">
                    <option value="">Seç</option>
                    <option value="1">1 - Kötü</option>
                    <option value="2">2</option>
                    <option value="3">3 - Orta</option>
                    <option value="4">4</option>
                    <option value="5">5 - Mükemmel</option>
                </select>
                <label>Yorum:</label>
                <textarea class="input-comment" data-reservation-id="${r.reservationId}" rows="2" placeholder="Deneyimini kısaca yaz..."></textarea>
                <button class="btn-review" data-reservation-id="${r.reservationId}" data-tour-id="${tourId || ""}">
                    Yorumu Kaydet
                </button>
            </div>
        `;
    }

    return `
        <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center">
                <div>
                    <div style="font-weight:700">${packageName}</div>
                    <div class="muted">Paket ID: ${packageId}</div>
                </div>
                <div style="text-align:right">
                    <div style="font-weight:600">Toplam: ${totalPriceText}</div>
                    <div class="muted">Durum: ${statusText}</div>
                </div>
            </div>
            <div style="margin-top:10px">Rezervasyon Tarihi: ${rezDateText}</div>
            <div style="margin-top:8px">
                ${
                    detailId
                        ? `<a href="detail.html?id=${detailId}" class="btn-detail">Tura Git</a>`
                        : `<span class="muted">Tur detayı bulunamadı</span>`
                }
            </div>
            <hr/>
            ${reviewHtml}
        </div>
    `;
}

/**
 * Kullanıcının yaptığı yorumları listelemek için kart.
 * /api/users/{USER_ID}/reviews endpoint'inden gelen Review objesini alır.
 */
function renderUserReviewCard(review) {
    const tour = review.tour || {};
    const tourName = tour.packageName || tour.tourName || "Bilinmeyen Tur";
    const tourId = tour.tourId || "";
    const ratingText = review.rating != null ? `${review.rating} / 5` : "—";
    const dateText = formatDateTime(review.reviewDate);

    return `
        <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center">
                <div>
                    <div style="font-weight:700">${tourName}</div>
                    ${tourId ? `<div class="muted">Tur ID: ${tourId}</div>` : ""}
                </div>
                <div style="text-align:right">
                    <div style="font-weight:600">Puan: ${ratingText}</div>
                    <div class="muted" style="font-size:0.85em;">${dateText}</div>
                </div>
            </div>
            ${
                review.comment
                    ? `<div style="margin-top:8px;">${review.comment}</div>`
                    : ""
            }
            ${
                tourId
                    ? `<div style="margin-top:8px;">
                            <a href="detail.html?id=${tourId}" class="btn-detail">Tura Git</a>
                       </div>`
                    : ""
            }
        </div>
    `;
}

function attachReviewHandlers() {
    const buttons = document.querySelectorAll(".btn-review");
    buttons.forEach((btn) => {
        btn.addEventListener("click", () => {
            const reservationId = btn.getAttribute("data-reservation-id");
            const tourId = btn.getAttribute("data-tour-id");

            console.log("Review POST -> userId:", USER_ID, "tourId:", tourId, "reservationId:", reservationId);

            if (!tourId) {
                alert("Bu rezervasyonla ilişkili tur ID bulunamadı.");
                return;
            }

            const ratingEl = document.querySelector(`.input-rating[data-reservation-id="${reservationId}"]`);
            const commentEl = document.querySelector(`.input-comment[data-reservation-id="${reservationId}"]`);

            const rating = ratingEl.value;
            const comment = commentEl.value.trim();

            if (!rating) {
                alert("Lütfen bir puan seçin.");
                return;
            }

            const payload = { rating: Number(rating), comment: comment };

            fetch(`${API_BASE}/users/${USER_ID}/tours/${tourId}/review`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            })
                .then((res) => {
                    if (!res.ok) {
                        return res.text().then((t) => {
                            console.error("Review error body:", t);
                            throw new Error(t || "Yorum kaydedilemedi");
                        });
                    }
                    return res.json();
                })
                .then(() => {
                    alert("Yorum kaydedildi!");
                    // "Yorumlarım" listesini yenile 
                    loadUserReviews(() => {
                        // sonra rezervasyon kartlarını güncelle (formlar kaybolsun)
                        loadReservations();
                    });
                })
                .catch((err) => {
                    console.error(err);
                    alert("Hata: " + err.message);
                });
        });
    });
}

function loadReservations() {
    if (!container) {
        console.error("reservations-container bulunamadı.");
        return;
    }

    container.innerHTML = "⏳ Yükleniyor...";

    fetch(`${API_BASE}/reservations/user/${USER_ID}`)
        .then((res) => {
            if (!res.ok) {
                throw new Error("Rezervasyonlar alınırken hata oluştu (HTTP " + res.status + ")");
            }
            return res.json();
        })
        .then((list) => {
            if (!list || list.length === 0) {
                container.innerHTML = "<p>Henüz rezervasyonunuz yok.</p>";
                return;
            }

            container.innerHTML = "";
            list.forEach((r) => {
                container.innerHTML += renderReservationCard(r);
            });

            attachReviewHandlers();
        })
        .catch((err) => {
            console.error(err);
            container.innerHTML = `<p style="color:red">Hata: ${err.message}</p>`;
        });
}

/**
 * Kullanıcının yaptığı tüm yorumları çeker ve #user-reviews-container içine basar.
 * GET /api/users/{USER_ID}/reviews
 */
function loadUserReviews(callback) {
    if (!reviewsContainer) return;

    reviewsContainer.innerHTML = `
        <p class="muted">Yükleniyor...</p>
    `;

    fetch(`${API_BASE}/users/${USER_ID}/reviews`)
        .then(res => {
            if (!res.ok) {
                throw new Error("Yorumlar alınırken hata oluştu (HTTP " + res.status + ")");
            }
            return res.json();
        })
        .then(list => {
            
            REVIEWED_TOUR_IDS = new Set(
                (list || [])
                    .map(r => (r.tour && r.tour.tourId) ? r.tour.tourId : null)
                    .filter(id => id !== null)
            );

            reviewsContainer.innerHTML = "";

            if (!list || list.length === 0) {
                reviewsContainer.innerHTML = `<p class="muted">Henüz yorum yapmadınız.</p>`;
            } else {
                list.forEach(review => {
                    reviewsContainer.innerHTML += renderUserReviewCard(review);
                });
            }

            if (typeof callback === "function") {
                callback();
            }
        })
        .catch(err => {
            console.error(err);
            reviewsContainer.innerHTML = `
                <p style="color:red">Hata: ${err.message}</p>
            `;
            if (typeof callback === "function") {
                callback();
            }
        });
}

// ---- ÇIKIŞ YAP ----
function setupLogout() {
    const logoutBtn = document.getElementById("logout-btn");
    if (!logoutBtn) return;

    logoutBtn.addEventListener("click", () => {
        const sure = confirm("Çıkış yapmak istediğinize emin misiniz?");
        if (!sure) return;

        // Oturumu temizle
        localStorage.removeItem("userId");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("isLoggedIn");
        localStorage.removeItem("isCompany");
        localStorage.removeItem("companyId");

        alert("Başarıyla çıkış yapıldı!");
        window.location.href = "login.html";
    });
}


function setupBackButton() {
    const backBtn = document.getElementById("back-btn");
    if (!backBtn) return;

    backBtn.addEventListener("click", () => {
        // Eğer önceki sayfa yoksa (örn. direkt girilmişse) ana sayfaya yönlendir
        if (document.referrer === "") {
            window.location.href = "index.html";
        } else {
            history.back();
        }
    });
}



// Sayfa yüklendiğinde:
document.addEventListener("DOMContentLoaded", () => {
    const emailEl = document.getElementById("user-email");
    const nameEl = document.getElementById("user-name");
    const avatarEl = document.getElementById("user-avatar");

    if (emailEl && USER_EMAIL) {
        emailEl.textContent = USER_EMAIL;
    }

    // isim yoksa email'in @ öncesini isim gibi kullan
    if (nameEl) {
        if (USER_EMAIL) {
            const approxName = USER_EMAIL.split("@")[0];
            nameEl.textContent = approxName;
        } else {
            nameEl.textContent = "Kullanıcı";
        }
    }

    // Profil fotoğrafını sabit default görsel yap
    if (avatarEl) {
        avatarEl.src = "profile_picture.jpg";
    }

    setupBackButton();
    setupLogout();

  
    loadUserReviews(() => {
        loadReservations();
    });
    
});
