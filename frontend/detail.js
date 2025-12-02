const urlParams = new URLSearchParams(window.location.search);
const tourId = urlParams.get('id');
const API_BASE = "http://localhost:8080/api";

const CURRENT_USER_ID = localStorage.getItem("userId");

if (!CURRENT_USER_ID) {
    alert("Rezervasyon yapmak için önce giriş yapmalısınız.");
    window.location.href = "login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    if(!tourId) {
        document.getElementById('loading').innerText = "Geçersiz ID!";
    } else {
        initPage();
    }
});

async function initPage() {
    try {
        // 1. TUR BİLGİLERİ
        const tourRes = await fetch(`${API_BASE}/tours/${tourId}`);
        const tour = await tourRes.json();

        document.getElementById('t-name').innerText = tour.packageName;
        document.getElementById('t-duration').innerText = tour.duration;
        document.getElementById('t-rating').innerText = tour.avg_rating || "Yeni";
        document.getElementById('t-desc').innerText = tour.description;

        if(tour.destinations && tour.destinations.length > 0) {
            const sehirler = tour.destinations.map(d => d.destinationCity).join(", ");
            document.getElementById('t-dest').innerText = sehirler;
        }

        // 2. PAKETLERİ ÇEK VE LİSTELE (KART OLARAK)
        const pkgRes = await fetch(`${API_BASE}/tour-packages/by-tour/${tourId}`);
        const packages = await pkgRes.json();
        const listContainer = document.getElementById("package-list-container");

        listContainer.innerHTML = ""; // Temizle

        if (packages.length === 0) {
            listContainer.innerHTML = "<p style='color:red;'>⚠️ Bu tur için şu an açık tarih bulunmamaktadır.</p>";
        } else {
            packages.forEach(pkg => {
                const stock = pkg.availableSeats || 0;
                const isFull = stock < 1;
                
                // Paket Kartı HTML'i (GÜNCELLENMİŞ KISIM BURASI)
                const card = document.createElement("div");
                card.className = "package-card";
                card.style.flexDirection = "column"; // İçerik alt alta gelsin diye
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
                                <button class="btn-info-pkg" onclick="paketDetayGoster(this)" style="background:#17a2b8; color:white; border:none; padding:8px 15px; border-radius:5px; cursor:pointer; margin-right:5px;">
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
                                ${pkg.hotelPackages && pkg.hotelPackages.length > 0 ? 
                                  pkg.hotelPackages.map(hp => `<li>${hp.hotel.hotelName} (${hp.hotel.hotelRate} Yıldız)</li>`).join('') 
                                  : '<li>Otel bilgisi girilmedi.</li>'}
                            </ul>
                        </div>

                        <div style="margin-top:10px;">
                            <strong>✈️ Ulaşım:</strong>
                            <ul style="margin:5px 0; padding-left:20px;">
                                ${pkg.flightPackages && pkg.flightPackages.length > 0 ? 
                                  pkg.flightPackages.map(fp => `<li>${fp.flight.firma} (${fp.flight.kalkisKonumu} ➝ ${fp.flight.varisKonumu})</li>`).join('') 
                                  : '<li>Uçuş bilgisi girilmedi.</li>'}
                            </ul>
                        </div>
                    </div>
                `;
                listContainer.appendChild(card);
            });
        }

        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-container').style.display = 'block';

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerText = "Hata oluştu!";
    }
}

// --- REZERVASYON EKRANINA GEÇİŞ ---
function rezervasyonaBasla(pkgId, price, dateStr) {
    // Seçilen paketin bilgilerini sakla
    document.getElementById("selectedPackageId").value = pkgId;
    document.getElementById("selectedPackagePrice").value = price;
    
    // Başlığı güncelle
    document.getElementById("res-title").innerText = `Rezervasyon: ${dateStr} Tarihli Paket`;

    // Ekranı değiştir
    document.getElementById("tour-showcase").style.display = "none";
    document.getElementById("reservation-panel").style.display = "block";
    
    // Formu hazırla
    yolcuFormlariniOlustur();
    fiyatiHesapla();
    
    // Sayfanın en altına (formun olduğu yere) kaydır
    document.getElementById("reservation-panel").scrollIntoView({behavior: "smooth"});
}

function detayaDon() {
    document.getElementById("reservation-panel").style.display = "none";
    document.getElementById("tour-showcase").style.display = "block";
}

// --- FİYAT HESAPLA ---
function fiyatiHesapla() {
    const kisiSayisi = parseInt(document.getElementById("guest-count").value) || 1;
    const birimFiyat = parseFloat(document.getElementById("selectedPackagePrice").value) || 0;
    
    const toplam = kisiSayisi * birimFiyat;
    document.getElementById("total-amount").innerText = toplam.toLocaleString();
}

// --- DİNAMİK YOLCU FORMLARI ---
// --- Dinamik Yolcu Formlarını Oluştur (ETİKETLİ VERSİYON) ---
function yolcuFormlariniOlustur() {
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const container = document.getElementById("passenger-forms-container");
    
    container.innerHTML = ""; 

    for (let i = 1; i <= count; i++) {
        // Silme butonu (1. kişi hariç)
        const deleteButton = (i > 1) 
            ? `<button class="btn-remove-passenger" onclick="yolcuSil(this)" type="button" title="Bu yolcuyu çıkar">✕</button>` 
            : '';

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
                        <input type="text" class="p-passport" placeholder="Pasaport No">
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
    fiyatiHesapla();
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
        if(numSpan) numSpan.innerText = index + 1;
    });
}
// --- PAKET DETAYLARINI AÇ/KAPA ---
function paketDetayGoster(btn) {
    // Butonun bulunduğu kartın içindeki detay kutusunu bul
    // (Buton -> div -> div(üst) -> div(kart) -> detay kutusu)
    // Daha güvenli yöntem: Butonun en yakın 'package-card' ebeveynini bul, oradan detay kutusunu seç.
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
// --- REZERVASYONU KAYDET ---
function rezervasyonuTamamla() {
    const pkgId = document.getElementById("selectedPackageId").value;
    const count = document.getElementById("guest-count").value;
    const forms = document.querySelectorAll("#passenger-forms-container > div");
    const passengers = [];
    let error = false;

    forms.forEach(div => {
        const p = {
            name: div.querySelector(".p-name").value,
            tcKimlik: div.querySelector(".p-tc").value,
            birthDate: div.querySelector(".p-birth").value,
            phone: div.querySelector(".p-phone").value,
            email: div.querySelector(".p-email").value,
            pasaportNo: div.querySelector(".p-passport").value,
            pasaportExpirationDate: div.querySelector(".p-pass-exp").value
        };
        
        if(Object.values(p).some(val => !val)) error = true;
        passengers.push(p);
    });

    if(error) { alert("Lütfen tüm alanları doldurun!"); return; }

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
        if(!res.ok) throw new Error(await res.text());
        return res.json();
    })
    .then(d => {
            // ÖNCEKİ HALİ: alert("Başarılı"); reload();
            
            // YENİ HALİ: Ödeme Sayfasına Git
            // Backend'den dönen rezervasyonun ID'sini ve Tutarını alıyoruz
            // Not: Backend Reservation objesi dönmeli. Eğer dönüyorsa d.totalPrice vardır.
            // Eğer d.totalPrice yoksa, biz sayfadaki hesaplanmış tutarı kullanabiliriz.
            
            // Sayfadaki toplam tutarı al (TL yazısını temizle)
            const currentTotalText = document.getElementById("total-amount").innerText.replace(/\./g,'').replace(' TL',''); 
            
            if (confirm("Rezervasyon oluşturuldu! Ödeme sayfasına yönlendiriliyorsunuz.")) {
                window.location.href = `payment.html?resId=${d.reservationId}&amount=${currentTotalText}`;
            }
        })
    .catch(e => alert("Hata: " + e.message));
}