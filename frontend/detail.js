/* ============================================================
   GLOBAL SABİTLER, REGEX’LER, LOGIN KONTROLÜ
============================================================ */

const urlParams = new URLSearchParams(window.location.search);
const tourId = urlParams.get('id');
const API_BASE = "http://localhost:8080/api";

const CURRENT_USER_ID = localStorage.getItem("userId");

const NAME_REGEX = /^[a-zA-ZÇçĞğİıÖöŞşÜü\s]+$/;
const PHONE_REGEX = /^5[0-9]{9}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSPORT_REGEX = /^[SE][0-9]{8}$/i;

let globalPackages = [];

// Login kontrol
if (!CURRENT_USER_ID) {
    alert("Rezervasyon yapmak için önce giriş yapmalısınız.");
    window.location.href = "login.html";
}

/* ============================================================
   TARİH KONTROL FONKSİYONLARI
============================================================ */

function isValidBirthDate(dateStr) {
    if (!dateStr) return false;
    const today = new Date(); today.setHours(0,0,0,0);
    const birth = new Date(dateStr); birth.setHours(0,0,0,0);
    return birth <= today;
}

function isValidPassportExpiry(dateStr) {
    if (!dateStr) return false;
    const today = new Date(); today.setHours(0,0,0,0);
    const exp = new Date(dateStr); exp.setHours(0,0,0,0);
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

/* ============================================================
   SAYFA YÜKLEME
============================================================ */

document.addEventListener("DOMContentLoaded", () => {
    if (!tourId) {
        document.getElementById('loading').innerText = "Geçersiz ID!";
    } else {
        initPage();
    }
});

/* ============================================================
   ANA YÜKLEME FONKSİYONU
============================================================ */

async function initPage() {
    try {
        /* ---------- TUR BİLGİLERİ ---------- */
        const tourRes = await fetch(`${API_BASE}/tours/${tourId}`);
        const tour = await tourRes.json();

        document.getElementById('t-name').innerText = tour.packageName;
        document.getElementById('t-duration').innerText = tour.duration;
        document.getElementById('t-rating').innerText = tour.avg_rating || "Yeni";
        document.getElementById('t-desc').innerText = tour.description;

        if (tour.destinations?.length) {
            document.getElementById('t-dest').innerText =
                tour.destinations.map(d => d.destinationCity).join(", ");
        }

        /* ---------- PAKETLERİ ÇEK ---------- */
        const pkgRes = await fetch(`${API_BASE}/tour-packages/by-tour/${tourId}`);
        globalPackages = await pkgRes.json();

        renderPackages(globalPackages);

        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-container').style.display = 'block';

        /* ---------- YORUMLARI ÇEK ---------- */
        await loadTourReviews();

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerText = "Hata oluştu!";
    }
}

/* ============================================================
   FİLTRELEME
============================================================ */

function filterPackages() {
    const hotelQuery = document.getElementById("searchHotel").value.toLocaleLowerCase('tr');
    const flightQuery = document.getElementById("searchFlight").value.toLocaleLowerCase('tr');

    const filtered = globalPackages.filter(pkg => {

        const hotels = pkg.hotelPackages || [];
        const flights = pkg.flightPackages || [];

        const matchesHotel =
            !hotelQuery ||
            hotels.some(h => h.hotel.hotelName.toLocaleLowerCase('tr').includes(hotelQuery));

        const matchesFlight =
            !flightQuery ||
            flights.some(f => f.flight.firma.toLocaleLowerCase('tr').includes(flightQuery));

        return matchesHotel && matchesFlight;
    });

    renderPackages(filtered);
}

function clearFilters() {
    document.getElementById("searchHotel").value = "";
    document.getElementById("searchFlight").value = "";
    renderPackages(globalPackages);
}

/* ============================================================
   PAKETLERİ EKRANA BASMA
============================================================ */

function renderPackages(packageList) {
    const container = document.getElementById("package-list-container");
    container.innerHTML = "";

    if (!packageList || packageList.length === 0) {
        container.innerHTML =
            "<p style='color:red; text-align:center;'>⚠️ Kriterlere uygun paket bulunamadı.</p>";
        return;
    }

    packageList.forEach(pkg => {
        const stock = pkg.availableSeats || 0;
        const isFull = stock < 1;

        const card = document.createElement("div");
        card.className = "package-card";

        card.innerHTML = `
            <div style="display:flex; justify-content:space-between;">
                <div>
                    <div class="pkg-date">📅 ${pkg.startDate} ➝ ${pkg.endDate}</div>
                    <div class="pkg-stock" style="color:${isFull ? 'red' : '#666'}">
                        ${isFull ? 'KONTENJAN DOLU' : `Kalan Kontenjan: ${stock} kişi`}
                    </div>
                </div>

                <div style="text-align:right;">
                    <div class="pkg-price">${pkg.basePrice} TL</div>
                    <button class="btn-info-pkg" onclick="paketDetayGoster(this)">ℹ️ Detaylar</button>

                    <button class="btn-select-pkg"
                        onclick="rezervasyonaBasla(${pkg.packageId}, ${pkg.basePrice}, '${pkg.startDate}')"
                        ${isFull ? 'disabled style="background:#ccc; cursor:not-allowed;"' : ''}>
                        ${isFull ? 'Dolu' : 'Seç & İlerle 👉'}
                    </button>
                </div>
            </div>

            <div class="pkg-details-box" style="display:none; margin-top:15px;">
                <h4>📦 Paket İçeriği</h4>

                <p><strong>Rehber:</strong> ${pkg.guide ? pkg.guide.guideName : 'Belirlenmedi'}</p>

                <strong>🏨 Oteller:</strong>
                <ul>
                    ${pkg.hotelPackages?.length
                        ? pkg.hotelPackages.map(h => `<li>${h.hotel.hotelName}</li>`).join("")
                        : "<li>Otel bilgisi yok.</li>"
                    }
                </ul>

                <strong>✈️ Uçuşlar:</strong>
                <ul>
                    ${pkg.flightPackages?.length
                        ? pkg.flightPackages.map(f => `<li>${f.flight.firma}</li>`).join("")
                        : "<li>Uçuş bilgisi yok.</li>"
                    }
                </ul>
            </div>
        `;

        container.appendChild(card);
    });
}

/* ============================================================
   TUR YORUMLARI
============================================================ */

async function loadTourReviews() {
    const container = document.getElementById("tour-reviews-container");

    container.innerHTML = `<p class="muted">Yorumlar yükleniyor...</p>`;

    try {
        const res = await fetch(`${API_BASE}/tours/${tourId}/reviews`);
        const text = await res.text();

        if (!res.ok) throw new Error(text);

        const list = JSON.parse(text);

        if (!Array.isArray(list) || list.length === 0) {
            container.innerHTML = `<p class="muted">Bu tur için henüz yorum yok.</p>`;
            return;
        }

        container.innerHTML = "";
        list.forEach(r => {
            container.innerHTML += `
                <div class="review-card">
                    <div class="review-header">
                        <span>${r.user?.email || "Anonim"}</span>
                        <span>⭐ ${r.rating}</span>
                    </div>
                    <p>${r.comment || ""}</p>
                    <small>${new Date(r.reviewDate).toLocaleString("tr-TR")}</small>
                </div>
            `;
        });

    } catch (err) {
        container.innerHTML = `<p style="color:red;">Yorumlar yüklenirken hata oluştu.</p>`;
    }
}

/* ============================================================
   REZERVASYON AKIŞI
============================================================ */

function rezervasyonaBasla(pkgId, price, dateStr) {

    document.getElementById("selectedPackageId").value = pkgId;
    document.getElementById("selectedPackagePrice").value = price;
    document.getElementById("res-title").innerText = `Rezervasyon: ${dateStr}`;

    document.getElementById("tour-showcase").style.display = "none";
    document.getElementById("reservation-panel").style.display = "block";

    yolcuFormlariniOlustur();
    fiyatiHesapla();

    document.getElementById("reservation-panel")
        .scrollIntoView({ behavior: "smooth" });
}

function detayaDon() {
    document.getElementById("reservation-panel").style.display = "none";
    document.getElementById("tour-showcase").style.display = "block";
}

function fiyatiHesapla() {
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const price = parseFloat(document.getElementById("selectedPackagePrice").value) || 0;
    document.getElementById("total-amount").innerText = (count * price).toLocaleString();
}

/* ============================================================
   YOLCU FORMU OLUŞTURMA
============================================================ */

function yolcuFormlariniOlustur() {
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const container = document.getElementById("passenger-forms-container");
    container.innerHTML = "";

    for (let i = 1; i <= count; i++) {

        const delBtn = i > 1
            ? `<button class="btn-remove-passenger" onclick="yolcuSil(this)">✕</button>`
            : "";

        container.innerHTML += `
            <div class="passenger-card">
                ${delBtn}
                <div class="passenger-header"><span class="p-num">${i}</span>. Yolcu Bilgileri</div>

                <div class="form-row">
                    <div class="form-col">
                        <small>Ad Soyad:</small>
                        <input type="text" class="p-name" placeholder="Ad Soyad">
                    </div>
                    <div class="form-col">
                        <small>TC Kimlik:</small>
                        <input type="text" class="p-tc" maxlength="11">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <small>Doğum Tarihi:</small>
                        <input type="date" class="p-birth">
                    </div>
                    <div class="form-col">
                        <small>Pasaport No:</small>
                        <input type="text" class="p-passport" placeholder="S12345678">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <small>Pasaport Geçerlilik:</small>
                        <input type="date" class="p-pass-exp">
                    </div>
                </div>

                <div class="form-row" style="border-top:1px dashed #ccc; padding-top:8px;">
                    <div class="form-col">
                        <input type="text" class="p-phone" placeholder="Telefon (5xx...)">
                    </div>
                    <div class="form-col">
                        <input type="email" class="p-email" placeholder="E-posta">
                    </div>
                </div>
            </div>
        `;
    }

    attachPassengerValidationListeners();
    fiyatiHesapla();
}

/* ============================================================
   YOLCU FORMU VALIDASYON (CANLI)
============================================================ */

function attachPassengerValidationListeners() {

    const cards = document.querySelectorAll(".passenger-card");

    cards.forEach(card => {

        const nameInput = card.querySelector(".p-name");
        const tcInput = card.querySelector(".p-tc");
        const phoneInput = card.querySelector(".p-phone");
        const emailInput = card.querySelector(".p-email");
        const passportInput = card.querySelector(".p-passport");
        const birthInput = card.querySelector(".p-birth");
        const passExpInput = card.querySelector(".p-pass-exp");

        nameInput?.addEventListener("input", () => {
            const v = nameInput.value.trim();
            if (!v) return nameInput.classList.remove("error","valid");
            NAME_REGEX.test(v) && v.length >= 2 ? markValid(nameInput) : markInvalid(nameInput);
        });

        tcInput?.addEventListener("input", () => {
            const v = tcInput.value.trim();
            if (!v) return tcInput.classList.remove("error","valid");
            /^[0-9]{11}$/.test(v) ? markValid(tcInput) : markInvalid(tcInput);
        });

        phoneInput?.addEventListener("input", () => {
            const v = phoneInput.value.trim();
            if (!v) return phoneInput.classList.remove("error","valid");
            PHONE_REGEX.test(v) ? markValid(phoneInput) : markInvalid(phoneInput);
        });

        emailInput?.addEventListener("input", () => {
            const v = emailInput.value.trim();
            if (!v) return emailInput.classList.remove("error","valid");
            EMAIL_REGEX.test(v) ? markValid(emailInput) : markInvalid(emailInput);
        });

        passportInput?.addEventListener("input", () => {
            const v = passportInput.value.trim();
            if (!v) return passportInput.classList.remove("error","valid");
            PASSPORT_REGEX.test(v) ? markValid(passportInput) : markInvalid(passportInput);
        });

        birthInput?.addEventListener("change", () => {
            const v = birthInput.value;
            if (!v) return birthInput.classList.remove("error","valid");
            isValidBirthDate(v) ? markValid(birthInput) : markInvalid(birthInput);
        });

        passExpInput?.addEventListener("change", () => {
            const v = passExpInput.value;
            if (!v) return passExpInput.classList.remove("error","valid");
            isValidPassportExpiry(v) ? markValid(passExpInput) : markInvalid(passExpInput);
        });

    });
}

/* ============================================================
   YOLCU SİLME
============================================================ */

function yolcuSil(btn) {
    btn.parentElement.remove();

    const guestInput = document.getElementById("guest-count");
    guestInput.value = Math.max(1, parseInt(guestInput.value) - 1);

    yenidenNumaralandir();
    fiyatiHesapla();
}

function yenidenNumaralandir() {
    const cards = document.querySelectorAll(".passenger-card");
    cards.forEach((c, index) => {
        c.querySelector(".p-num").innerText = index + 1;
    });
}

/* ============================================================
   DETAY GİZLE / GÖSTER
============================================================ */

function paketDetayGoster(btn) {
    const box = btn.closest(".package-card").querySelector(".pkg-details-box");
    const visible = box.style.display === "block";

    box.style.display = visible ? "none" : "block";
    btn.innerHTML = visible ? "ℹ️ Detaylar" : "🔼 Gizle";
}

/* ============================================================
   REZERVASYON OLUŞTURMA (POST)
============================================================ */

function rezervasyonuTamamla() {

    const pkgId = parseInt(document.getElementById("selectedPackageId").value);
    const count = parseInt(document.getElementById("guest-count").value);
    const forms = document.querySelectorAll(".passenger-card");

    let passengerList = [];
    let error = false;

    forms.forEach((div, index) => {

        const nameInput = div.querySelector(".p-name");
        const tcInput = div.querySelector(".p-tc");
        const birthInput = div.querySelector(".p-birth");
        const passportInput = div.querySelector(".p-passport");
        const passExpInput = div.querySelector(".p-pass-exp");
        const phoneInput = div.querySelector(".p-phone");
        const emailInput = div.querySelector(".p-email");

        const name = nameInput.value.trim();
        const tc = tcInput.value.trim();
        const birth = birthInput.value;
        const passport = passportInput.value.trim();
        const exp = passExpInput.value;
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();

        // Ad Soyad
        if (!(name && NAME_REGEX.test(name) && name.length >= 2)) {
            alert(`${index+1}. yolcunun Ad Soyad bilgisi geçersiz.`);
            markInvalid(nameInput);
            error = true; return;
        } else markValid(nameInput);

        // TC
        if (!/^[0-9]{11}$/.test(tc)) {
            alert(`${index+1}. yolcunun TC Kimlik bilgisi geçersiz.`);
            markInvalid(tcInput);
            error = true; return;
        } else markValid(tcInput);

        // Doğum tarihi
        if (!isValidBirthDate(birth)) {
            alert(`${index+1}. yolcunun doğum tarihi geçersiz.`);
            markInvalid(birthInput);
            error = true; return;
        } else markValid(birthInput);

        // Pasaport
        if (!PASSPORT_REGEX.test(passport)) {
            alert(`${index+1}. yolcunun pasaport numarası geçersiz.`);
            markInvalid(passportInput);
            error = true; return;
        } else markValid(passportInput);

        // Pasaport geçerlilik
        if (!isValidPassportExpiry(exp)) {
            alert(`${index+1}. yolcunun pasaport geçerlilik tarihi geçersiz.`);
            markInvalid(passExpInput);
            error = true; return;
        } else markValid(passExpInput);

        // Telefon
        if (!PHONE_REGEX.test(phone)) {
            alert(`${index+1}. yolcunun telefon numarası geçersiz.`);
            markInvalid(phoneInput);
            error = true; return;
        } else markValid(phoneInput);

        // Email
        if (!EMAIL_REGEX.test(email)) {
            alert(`${index+1}. yolcunun email adresi geçersiz.`);
            markInvalid(emailInput);
            error = true; return;
        } else markValid(emailInput);

        passengerList.push({
            name,
            tcKimlik: tc,
            birthDate: birth,
            phone,
            email,
            pasaportNo: passport,
            pasaportExpirationDate: exp
        });

    });

    if (error) return;

    const data = {
        userId: parseInt(CURRENT_USER_ID),
        packageId: pkgId,
        guestCount: count,
        passengers: passengerList
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
        .then(resp => {

            const totalAmount = document.getElementById("total-amount").innerText
                .replace(/\./g,"")
                .replace(",", "")
                .replace("TL","")
                .trim();

            if (confirm("Rezervasyon başarıyla oluşturuldu! Ödeme sayfasına geçilsin mi?")) {
                window.location.href =
                    `payment.html?resId=${resp.reservationId}&amount=${totalAmount}`;
            }
        })
        .catch(err => alert("Hata: " + err.message));
}
