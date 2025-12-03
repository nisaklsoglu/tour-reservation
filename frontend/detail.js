// URL'den tourId al
const urlParams = new URLSearchParams(window.location.search);
const tourId = urlParams.get('id');
const API_BASE = "http://localhost:8080/api";

const CURRENT_USER_ID = localStorage.getItem("userId");

// --- GLOBAL DEĞİŞKENLER ---
let globalPackages = []; // Tüm paketleri burada tutacağız (Hafızada)

// --- REGEX TANIMLARI ---
const NAME_REGEX = /^[a-zA-ZÇçĞğİıÖöŞşÜü\s]+$/;
const PHONE_REGEX = /^5[0-9]{9}$/; 
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSPORT_REGEX = /^[SE][0-9]{8}$/i;

// Giriş Kontrolü
if (!CURRENT_USER_ID) {
    alert("Rezervasyon yapmak için önce giriş yapmalısınız.");
    window.location.href = "login.html";
}

// ---- Sayfa Yüklendiğinde ----
document.addEventListener("DOMContentLoaded", () => {
    if (!tourId) {
        document.getElementById('loading').innerText = "Geçersiz ID!";
    } else {
        initPage();
    }
});

// ---------- ANA YÜKLEME FONKSİYONU ----------
async function initPage() {
    try {
        // 1. TUR BİLGİLERİ
        const tourRes = await fetch(`${API_BASE}/tours/${tourId}`);
        const tour = await tourRes.json();

        document.getElementById('t-name').innerText = tour.packageName;
        document.getElementById('t-duration').innerText = tour.duration;
        document.getElementById('t-rating').innerText = tour.avg_rating || "Yeni";
        document.getElementById('t-desc').innerText = tour.description;

        if (tour.destinations && tour.destinations.length > 0) {
            const sehirler = tour.destinations
                .map(d => d.destinationCity)
                .join(", ");
            document.getElementById('t-dest').innerText = sehirler;
        }

        // 2. PAKETLERİ ÇEK VE HAFIZAYA AL
        const pkgRes = await fetch(`${API_BASE}/tour-packages/by-tour/${tourId}`);
        globalPackages = await pkgRes.json(); // Global değişkene atadık!

        // Listeyi Ekrana Bas
        renderPackages(globalPackages);

        // UI Güncellemeleri
        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-container').style.display = 'block';

        // 3. YORUMLARI YÜKLE
        await loadTourReviews();

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerText = "Hata oluştu!";
    }
}

// ---------- FİLTRELEME FONKSİYONU (YENİ) ----------
function filterPackages() {
    const hotelQuery = document.getElementById("searchHotel").value.toLocaleLowerCase('tr');
    const flightQuery = document.getElementById("searchFlight").value.toLocaleLowerCase('tr');

    const filtered = globalPackages.filter(pkg => {
        // 1. Otel Kontrolü (Paketin içindeki herhangi bir otel eşleşiyor mu?)
        const hotels = pkg.hotelPackages || [];
        const hasHotel = hotels.some(hp => 
            hp.hotel.hotelName.toLocaleLowerCase('tr').includes(hotelQuery)
        );

        // 2. Uçuş Kontrolü (Paketin içindeki herhangi bir uçuş eşleşiyor mu?)
        const flights = pkg.flightPackages || [];
        const hasFlight = flights.some(fp => 
            fp.flight.firma.toLocaleLowerCase('tr').includes(flightQuery)
        );

        // Arama kutusu boşsa "true" kabul et, doluysa eşleşme ara
        const hotelMatch = (hotelQuery === "") || hasHotel;
        const flightMatch = (flightQuery === "") || hasFlight;

        return hotelMatch && flightMatch;
    });

    renderPackages(filtered);
}

function clearFilters() {
    document.getElementById("searchHotel").value = "";
    document.getElementById("searchFlight").value = "";
    renderPackages(globalPackages);
}

// ---------- PAKETLERİ EKRANA BASMA (Refactored) ----------
function renderPackages(packageList) {
    const listContainer = document.getElementById("package-list-container");
    listContainer.innerHTML = ""; // Önce temizle

    if (!packageList || packageList.length === 0) {
        listContainer.innerHTML = "<p style='color:red; text-align:center;'>⚠️ Kriterlere uygun paket bulunamadı.</p>";
        return;
    }

    packageList.forEach(pkg => {
        const stock = pkg.availableSeats || 0;
        const isFull = stock < 1;

        const card = document.createElement("div");
        card.className = "package-card";
        card.style.flexDirection = "column";
        card.style.alignItems = "stretch";

        // HTML oluşturma (Eski kodunun aynısı)
        card.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <div>
                    <div class="pkg-date">📅 ${pkg.startDate} ➝ ${pkg.endDate}</div>
                    <div class="pkg-stock" style="color:${isFull ? 'red' : '#666'}">
                        ${isFull ? 'KONTENJAN DOLU' : `Kalan Kontenjan: ${stock} Kişi`}
                    </div>
                </div>
                <div style="text-align:right;">
                    <div class="pkg-price">${pkg.basePrice} TL</div>
                    <div style="margin-top:10px;">
                        <button class="btn-info-pkg" onclick="paketDetayGoster(this)">
                            ℹ️ Detaylar
                        </button>
                        
                        <button class="btn-select-pkg" 
                                onclick="rezervasyonaBasla(${pkg.packageId}, ${pkg.basePrice}, '${pkg.startDate}')" 
                                ${isFull ? 'disabled style="background:#ccc; cursor:not-allowed;"' : ''}>
                            ${isFull ? 'Dolu' : 'Seç & İlerle 👉'}
                        </button>
                    </div>
                </div>
            </div>

            <div class="pkg-details-box" style="display:none; margin-top:15px; background:#f8f9fa; padding:15px; border-top:1px dashed #ccc;">
                <h4 style="margin-top:0; color:#333;">📦 Paket İçeriği</h4>
                
                <p><strong>🧢 Rehber:</strong> ${pkg.guide ? pkg.guide.guideName : 'Belirlenmedi'}</p>

                <div style="margin-top:10px;">
                    <strong>🏨 Konaklama:</strong>
                    <ul style="margin:5px 0; padding-left:20px;">
                        ${
                            pkg.hotelPackages && pkg.hotelPackages.length > 0
                                ? pkg.hotelPackages.map(hp => 
                                    `<li>${hp.hotel.hotelName} (${hp.hotel.hotelRate} Yıldız)</li>`
                                  ).join('')
                                : '<li>Otel bilgisi girilmedi.</li>'
                        }
                    </ul>
                </div>

                <div style="margin-top:10px;">
                    <strong>✈️ Ulaşım:</strong>
                    <ul style="margin:5px 0; padding-left:20px;">
                        ${
                            pkg.flightPackages && pkg.flightPackages.length > 0
                                ? pkg.flightPackages.map(fp => 
                                    `<li>${fp.flight.firma} (${fp.flight.kalkisKonumu} ➝ ${fp.flight.varisKonumu})</li>`
                                  ).join('')
                                : '<li>Uçuş bilgisi girilmedi.</li>'
                        }
                    </ul>
                </div>
            </div>
        `;
        listContainer.appendChild(card);
    });
}

// ... (Geri kalan tüm fonksiyonlar aynı kalacak: loadTourReviews, rezervasyonaBasla, yolcuFormlari vs.) ...
// ---- KODUN GERİ KALANINI BOZMADAN AYNEN KULLANABİLİRSİN ----
// (Aşağıdaki isValidBirthDate, markInvalid vb. fonksiyonlar olduğu gibi kalmalı)

function isValidBirthDate(dateStr) {
    if (!dateStr) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const birth = new Date(dateStr);
    birth.setHours(0, 0, 0, 0);
    return birth <= today;
}

function isValidPassportExpiry(dateStr) {
    if (!dateStr) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const exp = new Date(dateStr);
    exp.setHours(0, 0, 0, 0);
    return exp > today;
}

function markInvalid(el) { if (!el) return; el.classList.add("error"); el.classList.remove("valid"); }
function markValid(el) { if (!el) return; el.classList.remove("error"); el.classList.add("valid"); }

async function loadTourReviews() {
    // ... (Mevcut kodunun aynısı) ...
     const container = document.getElementById("tour-reviews-container");
    if (!container) return;
    container.innerHTML = `<p class="muted">Yorumlar yükleniyor...</p>`;

    try {
        const res = await fetch(`${API_BASE}/tours/${tourId}/reviews`);
        if (!res.ok) throw new Error("Yorumlar alınamadı");
        const list = await res.json();

        if (!Array.isArray(list) || list.length === 0) {
            container.innerHTML = `<p class="muted">Bu tur için henüz yorum yapılmamış.</p>`;
            return;
        }

        container.innerHTML = "";
        list.forEach(r => {
            const user = r.user || {};
            const userLabel = user.email || user.name || "Anonim kullanıcı";
            const rating = r.rating != null ? r.rating : "-";
            const comment = r.comment || "";
            const dateText = r.reviewDate ? new Date(r.reviewDate).toLocaleString("tr-TR") : "";

            container.innerHTML += `
                <div class="review-card">
                    <div class="review-header">
                        <span class="review-user">${userLabel}</span>
                        <span class="review-rating">⭐ ${rating} / 5</span>
                    </div>
                    ${comment ? `<p class="review-comment">${comment}</p>` : ""}
                    <div class="review-date">${dateText}</div>
                </div>
            `;
        });
    } catch (err) {
        console.error(err);
        container.innerHTML = `<p style="color:red;">Yorumlar yüklenemedi.</p>`;
    }
}

function rezervasyonaBasla(pkgId, price, dateStr) {
    document.getElementById("selectedPackageId").value = pkgId;
    document.getElementById("selectedPackagePrice").value = price;
    document.getElementById("res-title").innerText = `Rezervasyon: ${dateStr} Tarihli Paket`;
    document.getElementById("tour-showcase").style.display = "none";
    document.getElementById("reservation-panel").style.display = "block";
    yolcuFormlariniOlustur();
    fiyatiHesapla();
    document.getElementById("reservation-panel").scrollIntoView({ behavior: "smooth" });
}

function detayaDon() {
    document.getElementById("reservation-panel").style.display = "none";
    document.getElementById("tour-showcase").style.display = "block";
}

function fiyatiHesapla() {
    const kisiSayisi = parseInt(document.getElementById("guest-count").value) || 1;
    const birimFiyat = parseFloat(document.getElementById("selectedPackagePrice").value) || 0;
    const toplam = kisiSayisi * birimFiyat;
    document.getElementById("total-amount").innerText = toplam.toLocaleString();
}

function yolcuFormlariniOlustur() {
    // ... (Mevcut kodunun aynısı - uzun olduğu için kısalttım ama senin kodunda aynı kalmalı) ...
    // Sadece yolcuFormlariniOlustur, attachPassengerValidationListeners, yolcuSil, yenidenNumaralandir, 
    // paketDetayGoster ve rezervasyonuTamamla fonksiyonlarını AYNEN KORU.
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const container = document.getElementById("passenger-forms-container");
    container.innerHTML = "";
    for (let i = 1; i <= count; i++) {
        const deleteButton = i > 1 ? `<button class="btn-remove-passenger" onclick="yolcuSil(this)" type="button">✕</button>` : "";
        // ... (HTML string aynı) ...
        const html = `
            <div class="passenger-card">
                ${deleteButton}
                <div class="passenger-header"><span class="p-num">${i}</span>. Yolcu Bilgileri</div>
                <div class="form-row">
                    <div class="form-col"><small>Ad Soyad (Zorunlu):</small><input type="text" class="p-name"></div>
                    <div class="form-col"><small>TC Kimlik (11 Hane):</small><input type="text" class="p-tc" maxlength="11"></div>
                </div>
                <div class="form-row">
                    <div class="form-col"><small>Doğum Tarihi:</small><input type="date" class="p-birth"></div>
                    <div class="form-col"><small>Pasaport No:</small><input type="text" class="p-passport"></div>
                </div>
                 <div class="form-row"><div class="form-col"><small>Pasaport Geçerlilik:</small><input type="date" class="p-pass-exp"></div></div>
                <div class="form-row">
                    <div class="form-col"><input type="text" class="p-phone" placeholder="Telefon"></div>
                    <div class="form-col"><input type="email" class="p-email" placeholder="E-posta"></div>
                </div>
            </div>`;
        container.innerHTML += html;
    }
    attachPassengerValidationListeners();
    fiyatiHesapla();
}

// ... (Attach listeners, validation, yolcu silme vs. hepsi aynı kalacak) ...
function attachPassengerValidationListeners() {
    // ... (Mevcut kodunun aynısı)
    const cards = document.querySelectorAll(".passenger-card");
    cards.forEach(card => {
        const nameInput = card.querySelector(".p-name");
        if(nameInput) nameInput.addEventListener("input", () => {
             const val = nameInput.value.trim();
             if(!val) { nameInput.classList.remove("error", "valid"); return; }
             if(val.length >= 2 && NAME_REGEX.test(val)) markValid(nameInput); else markInvalid(nameInput);
        });
        // Diğer validasyonlar aynen kalacak...
    });
}

function yolcuSil(btn) {
    btn.parentElement.remove();
    const input = document.getElementById("guest-count");
    input.value = parseInt(input.value) - 1;
    yenidenNumaralandir();
    fiyatiHesapla();
}

function yenidenNumaralandir() {
    const cards = document.querySelectorAll(".passenger-card");
    cards.forEach((card, index) => {
        const numSpan = card.querySelector(".p-num");
        if (numSpan) numSpan.innerText = index + 1;
    });
}

function paketDetayGoster(btn) {
    const card = btn.closest(".package-card");
    const detailsBox = card.querySelector(".pkg-details-box");
    if (detailsBox.style.display === "none") {
        detailsBox.style.display = "block";
        btn.innerHTML = "🔼 Gizle";
    } else {
        detailsBox.style.display = "none";
        btn.innerHTML = "ℹ️ Detaylar";
    }
}

function rezervasyonuTamamla() {
    // ... (Rezervasyon logic'i aynen kalacak) ...
     const pkgId = document.getElementById("selectedPackageId").value;
    const count = document.getElementById("guest-count").value;
    // ... Validasyonlar ...
    // Fetch isteği ...
    // Aynen kopyala ...
     // Basitçe alert koyuyorum örnek için, sen kendi uzun fonksiyonunu koru:
     alert("Rezervasyon fonksiyonu çalıştı (Kodunun orjinal halini kullan).");
}