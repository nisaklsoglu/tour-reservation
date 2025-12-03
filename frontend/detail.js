// URL'den tourId al
const urlParams = new URLSearchParams(window.location.search);
const tourId = urlParams.get('id');
const API_BASE = "http://localhost:8080/api";

const CURRENT_USER_ID = localStorage.getItem("userId");

// --- REGEX TANIMLARI ---
const NAME_REGEX = /^[a-zA-ZÇçĞğİıÖöŞşÜü\s]+$/;
const PHONE_REGEX = /^5[0-9]{9}$/;                     // 5 ile başlar, toplam 10 hane
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;      // basit ve genel email pattern
const PASSPORT_REGEX = /^[SE][0-9]{8}$/i;              // S veya E ile başlar, toplam 9 karakter (1 harf + 8 sayı)

// Eğer giriş yoksa rezervasyon yapamasın (detay sayfasına girmeden önce login'e atıyorsun)
if (!CURRENT_USER_ID) {
    alert("Rezervasyon yapmak için önce giriş yapmalısınız.");
    window.location.href = "login.html";
}

// ---- Yardımcı fonksiyonlar: Tarih & input stilleri ----
function isValidBirthDate(dateStr) {
    if (!dateStr) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const birth = new Date(dateStr);
    birth.setHours(0, 0, 0, 0);

    // Doğum tarihi bugünden küçük veya eşit olmalı
    return birth <= today;
}

function isValidPassportExpiry(dateStr) {
    if (!dateStr) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const exp = new Date(dateStr);
    exp.setHours(0, 0, 0, 0);

    // Pasaport geçerlilik tarihi mutlaka bugünden ileri olmalı
    return exp > today;
}

function markInvalid(el) {
    if (!el) return;
    el.classList.add("error");
    el.classList.remove("valid");
}

function markValid(el) {
    if (!el) return;
    el.classList.remove("error");
    el.classList.add("valid");
}

// Sayfa yüklendiğinde
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

        // 2. PAKETLERİ ÇEK VE LİSTELE
        const pkgRes = await fetch(`${API_BASE}/tour-packages/by-tour/${tourId}`);
        const packages = await pkgRes.json();
        const listContainer = document.getElementById("package-list-container");

        listContainer.innerHTML = ""; // Temizle

        if (packages.length === 0) {
            listContainer.innerHTML =
                "<p style='color:red;'>⚠️ Bu tur için şu an açık tarih bulunmamaktadır.</p>";
        } else {
            packages.forEach(pkg => {
                const stock = pkg.availableSeats || 0;
                const isFull = stock < 1;

                const card = document.createElement("div");
                card.className = "package-card";
                card.style.flexDirection = "column";
                card.style.alignItems = "stretch";

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
                                        ? pkg.hotelPackages
                                              .map(
                                                  hp =>
                                                      `<li>${hp.hotel.hotelName} (${hp.hotel.hotelRate} Yıldız)</li>`
                                              )
                                              .join('')
                                        : '<li>Otel bilgisi girilmedi.</li>'
                                }
                            </ul>
                        </div>

                        <div style="margin-top:10px;">
                            <strong>✈️ Ulaşım:</strong>
                            <ul style="margin:5px 0; padding-left:20px;">
                                ${
                                    pkg.flightPackages && pkg.flightPackages.length > 0
                                        ? pkg.flightPackages
                                              .map(
                                                  fp =>
                                                      `<li>${fp.flight.firma} (${fp.flight.kalkisKonumu} ➝ ${fp.flight.varisKonumu})</li>`
                                              )
                                              .join('')
                                        : '<li>Uçuş bilgisi girilmedi.</li>'
                                }
                            </ul>
                        </div>
                    </div>
                `;
                listContainer.appendChild(card);
            });
        }

        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-container').style.display = 'block';

        // 🔹 YORUMLARI YÜKLE
        await loadTourReviews();

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerText = "Hata oluştu!";
    }
}

// ---------- TUR YORUMLARINI ÇEKEN FONKSİYON ----------
async function loadTourReviews() {
    const container = document.getElementById("tour-reviews-container");
    console.log("loadTourReviews() çağrıldı, tourId:", tourId);

    if (!container) {
        console.error("tour-reviews-container bulunamadı.");
        return;
    }

    container.innerHTML = `<p class="muted">Yorumlar yükleniyor...</p>`;

    try {
        const url = `${API_BASE}/tours/${tourId}/reviews`;
        console.log("Yorumlar için istek:", url);

        const res = await fetch(url);
        console.log("Yorumlar response status:", res.status);

        const text = await res.text();
        console.log("Raw response body:", text);

        if (!res.ok) {
            throw new Error("Yorumlar alınamadı (HTTP " + res.status + ")");
        }

        let list;
        try {
            list = JSON.parse(text);
        } catch (e) {
            console.error("JSON parse hatası:", e);
            throw new Error("Yorum cevabı JSON formatında değil.");
        }

        console.log("Parse edilmiş yorum listesi:", list);

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
            const dateText = r.reviewDate
                ? new Date(r.reviewDate).toLocaleString("tr-TR")
                : "";

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
        console.error("loadTourReviews HATASI:", err);
        container.innerHTML = `<p style="color:red;">Yorumlar yüklenirken hata oluştu: ${err.message}</p>`;
    }
}

// ---------- REZERVASYON EKRANI ----------
function rezervasyonaBasla(pkgId, price, dateStr) {
    document.getElementById("selectedPackageId").value = pkgId;
    document.getElementById("selectedPackagePrice").value = price;

    document.getElementById("res-title").innerText = `Rezervasyon: ${dateStr} Tarihli Paket`;

    document.getElementById("tour-showcase").style.display = "none";
    document.getElementById("reservation-panel").style.display = "block";

    yolcuFormlariniOlustur();
    fiyatiHesapla();

    document
        .getElementById("reservation-panel")
        .scrollIntoView({ behavior: "smooth" });
}

function detayaDon() {
    document.getElementById("reservation-panel").style.display = "none";
    document.getElementById("tour-showcase").style.display = "block";
}

// ---------- FİYAT HESAPLAMA ----------
function fiyatiHesapla() {
    const kisiSayisi =
        parseInt(document.getElementById("guest-count").value) || 1;
    const birimFiyat =
        parseFloat(document.getElementById("selectedPackagePrice").value) || 0;

    const toplam = kisiSayisi * birimFiyat;
    document.getElementById("total-amount").innerText =
        toplam.toLocaleString();
}

// ---------- DİNAMİK YOLCU FORMLARI ----------
function yolcuFormlariniOlustur() {
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const container = document.getElementById("passenger-forms-container");

    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {
        const deleteButton =
            i > 1
                ? `<button class="btn-remove-passenger" onclick="yolcuSil(this)" type="button" title="Bu yolcuyu çıkar">✕</button>`
                : "";

        const html = `
            <div class="passenger-card">
                ${deleteButton}
                <div class="passenger-header"><span class="p-num">${i}</span>. Yolcu Bilgileri</div>
                
                <div class="form-row">
                    <div class="form-col">
                        <small>Ad Soyad (Zorunlu):</small>
                        <input type="text" class="p-name" placeholder="Ad Soyad">
                    </div>
                    <div class="form-col">
                        <small>TC Kimlik (11 Hane):</small>
                        <input type="text" class="p-tc" placeholder="TC Kimlik" maxlength="11">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <small style="color:#e67e22; font-weight:bold;">Doğum Tarihi:</small>
                        <input type="date" class="p-birth" title="Doğum Tarihi">
                    </div>
                    <div class="form-col">
                        <small>Pasaport No:</small>
                        <input type="text" class="p-passport" placeholder="Örn: S12345678">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <small style="color:#e67e22; font-weight:bold;">Pasaport Geçerlilik Tarihi:</small>
                        <input type="date" class="p-pass-exp" title="Pasaport Bitiş Tarihi">
                    </div>
                </div>

                <div style="margin-top:10px; border-top:1px dashed #ddd; padding-top:10px;">
                    <small style="display:block; margin-bottom:5px; font-weight:bold;">İletişim Bilgileri (Zorunlu):</small>
                    <div class="form-row">
                        <div class="form-col">
                            <input type="text" class="p-phone" placeholder="Telefon (5xx...)">
                        </div>
                        <div class="form-col">
                            <input type="email" class="p-email" placeholder="E-posta Adresi">
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += html;
    }

    // Yeni oluşturulan inputlar için canlı validasyon bağla
    attachPassengerValidationListeners();
    fiyatiHesapla();
}

// Canlı (anlık) validasyon event'lerini bağlayan fonksiyon
function attachPassengerValidationListeners() {
    const cards = document.querySelectorAll(".passenger-card");
    cards.forEach(card => {
        const nameInput      = card.querySelector(".p-name");
        const tcInput        = card.querySelector(".p-tc");
        const phoneInput     = card.querySelector(".p-phone");
        const emailInput     = card.querySelector(".p-email");
        const passportInput  = card.querySelector(".p-passport");
        const birthInput     = card.querySelector(".p-birth");
        const passExpInput   = card.querySelector(".p-pass-exp");

        // Ad Soyad
        if (nameInput) {
            nameInput.addEventListener("input", () => {
                const val = nameInput.value.trim();
                if (!val) {
                    nameInput.classList.remove("error", "valid");
                    return;
                }
                if (val.length >= 2 && NAME_REGEX.test(val)) {
                    markValid(nameInput);
                } else {
                    markInvalid(nameInput);
                }
            });
        }

        // 🔴 TC Kimlik: 11 haneli sadece sayı
        if (tcInput) {
            tcInput.addEventListener("input", () => {
                const val = tcInput.value.trim();
                if (!val) {
                    tcInput.classList.remove("error", "valid");
                    return;
                }
                if (/^[0-9]{11}$/.test(val)) {
                    markValid(tcInput);
                } else {
                    markInvalid(tcInput);
                }
            });
        }

        // Telefon
        if (phoneInput) {
            phoneInput.addEventListener("input", () => {
                const val = phoneInput.value.trim();
                if (!val) {
                    phoneInput.classList.remove("error", "valid");
                    return;
                }
                if (PHONE_REGEX.test(val)) {
                    markValid(phoneInput);
                } else {
                    markInvalid(phoneInput);
                }
            });
        }

        // Email
        if (emailInput) {
            emailInput.addEventListener("input", () => {
                const val = emailInput.value.trim();
                if (!val) {
                    emailInput.classList.remove("error", "valid");
                    return;
                }
                if (EMAIL_REGEX.test(val)) {
                    markValid(emailInput);
                } else {
                    markInvalid(emailInput);
                }
            });
        }

        // Pasaport No
        if (passportInput) {
            passportInput.addEventListener("input", () => {
                const val = passportInput.value.trim();
                if (!val) {
                    passportInput.classList.remove("error", "valid");
                    return;
                }
                if (PASSPORT_REGEX.test(val)) {
                    markValid(passportInput);
                } else {
                    markInvalid(passportInput);
                }
            });
        }

        // Doğum Tarihi
        if (birthInput) {
            birthInput.addEventListener("change", () => {
                const val = birthInput.value;
                if (!val) {
                    birthInput.classList.remove("error", "valid");
                    return;
                }
                if (isValidBirthDate(val)) {
                    markValid(birthInput);
                } else {
                    markInvalid(birthInput);
                }
            });
        }

        // Pasaport Geçerlilik
        if (passExpInput) {
            passExpInput.addEventListener("change", () => {
                const val = passExpInput.value;
                if (!val) {
                    passExpInput.classList.remove("error", "valid");
                    return;
                }
                if (isValidPassportExpiry(val)) {
                    markValid(passExpInput);
                } else {
                    markInvalid(passExpInput);
                }
            });
        }
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

// ---------- PAKET DETAY AÇ/KAPA ----------
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

// ---------- REZERVASYONU KAYDET ----------
function rezervasyonuTamamla() {
    const pkgId = document.getElementById("selectedPackageId").value;
    const count = document.getElementById("guest-count").value;
    const forms = document.querySelectorAll("#passenger-forms-container > div");
    const passengers = [];
    let error = false;

    forms.forEach((div, index) => {
        const nameInput = div.querySelector(".p-name");
        const tcInput = div.querySelector(".p-tc");
        const birthInput = div.querySelector(".p-birth");
        const phoneInput = div.querySelector(".p-phone");
        const emailInput = div.querySelector(".p-email");
        const passportInput = div.querySelector(".p-passport");
        const passExpInput = div.querySelector(".p-pass-exp");

        const name = nameInput.value.trim();
        const tcKimlik = tcInput.value.trim();
        const birthDate = birthInput.value;
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();
        const pasaportNo = passportInput.value.trim();
        const pasaportExpirationDate = passExpInput.value;

        // 1) Ad Soyad: zorunlu, min 2 karakter, sadece harf
        if (!name || name.length < 2 || !NAME_REGEX.test(name)) {
            markInvalid(nameInput);
            alert((index + 1) + ". yolcunun Ad Soyad alanı geçersiz. Sadece harf kullanınız ve en az 2 karakter giriniz.");
            error = true;
            return;
        } else {
            markValid(nameInput);
        }

        // 2) TC kimlik (zorunlu, 11 hane, sayı)
        if (!/^[0-9]{11}$/.test(tcKimlik)) {
            markInvalid(tcInput);
            alert((index + 1) + ". yolcunun TC Kimlik alanı geçersiz. 11 haneli sayı giriniz.");
            error = true;
            return;
        } else {
            markValid(tcInput);
        }

        // 3) Doğum tarihi (zorunlu, gelecekte olamaz)
        if (!isValidBirthDate(birthDate)) {
            markInvalid(birthInput);
            alert((index + 1) + ". yolcunun doğum tarihi geçersiz. Gelecekte bir tarih olamaz ve boş bırakılamaz.");
            error = true;
            return;
        } else {
            markValid(birthInput);
        }

        // 4) Pasaport No: S/E ile başlar, 9 karakter
        if (!PASSPORT_REGEX.test(pasaportNo)) {
            markInvalid(passportInput);
            alert((index + 1) + ". yolcunun Pasaport No alanı geçersiz. 'S' veya 'E' ile başlamalı ve toplam 9 karakter olmalı (ör: S12345678).");
            error = true;
            return;
        } else {
            markValid(passportInput);
        }

        // 5) Pasaport geçerlilik tarihi: zorunlu, bugünden büyük olmalı
        if (!isValidPassportExpiry(pasaportExpirationDate)) {
            markInvalid(passExpInput);
            alert((index + 1) + ". yolcunun pasaport geçerlilik tarihi geçersiz. Bugünden ileri bir tarih olmalıdır.");
            error = true;
            return;
        } else {
            markValid(passExpInput);
        }

        // 6) Telefon: 5 ile başlar, 10 hane
        if (!PHONE_REGEX.test(phone)) {
            markInvalid(phoneInput);
            alert((index + 1) + ". yolcunun telefon numarası geçersiz. 5 ile başlamalı ve toplam 10 hane olmalıdır (ör: 5XXXXXXXXX).");
            error = true;
            return;
        } else {
            markValid(phoneInput);
        }

        // 7) Email: geçerli format
        if (!EMAIL_REGEX.test(email)) {
            markInvalid(emailInput);
            alert((index + 1) + ". yolcunun e-posta adresi geçersiz.");
            error = true;
            return;
        } else {
            markValid(emailInput);
        }

        const p = {
            name,
            tcKimlik,
            birthDate,
            phone,
            email,
            pasaportNo,
            pasaportExpirationDate
        };

        passengers.push(p);
    });

    if (error) {
        return; // Hata varsa fetch'e hiç girme
    }

    const userId = parseInt(CURRENT_USER_ID, 10);

    if (!userId) {
        alert("Oturum bulunamadı, lütfen tekrar giriş yapın.");
        window.location.href = "login.html";
        return;
    }

    const data = {
        userId: userId,
        packageId: parseInt(pkgId),
        guestCount: parseInt(count),
        passengers: passengers
    };

    fetch(`${API_BASE}/reservations/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
        .then(async res => {
            if (!res.ok) throw new Error(await res.text());
            return res.json();
        })
        .then(d => {
            const currentTotalText = document
                .getElementById("total-amount")
                .innerText.replace(/\./g, "").replace(" TL", "");

            if (
                confirm(
                    "Rezervasyon oluşturuldu! Ödeme sayfasına yönlendiriliyorsunuz."
                )
            ) {
                window.location.href = `payment.html?resId=${d.reservationId}&amount=${currentTotalText}`;
            }
        })
        .catch(e => alert("Hata: " + e.message));
}
