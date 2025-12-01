const API_BASE = "http://localhost:8080/api";
const grid = document.getElementById("results-grid");
const title = document.getElementById("page-title");

// TÜM FİLTRELERİN MERKEZİ STATE'İ:
const filters = {
    country: "",
    city: "",
    minPrice: null,
    maxPrice: null,
    minDur: null,
    maxDur: null,
    guests: null,
    sort: null // 'priceAsc' veya 'priceDesc'
};

// // Sayfa açılınca hepsini getir
// document.addEventListener("DOMContentLoaded", () => {
//     tumTurlariGetir(); // Mevcut fonksiyonun
//     ulkeleriDoldur();  // Yeni fonksiyon
//     yolcuFormlariniOlustur();
// });

// ---------------------------------------------
// 1. ANA FONKSİYON: Tüm Turları Getir
// ---------------------------------------------

// Sayfa açılınca Ülkeleri Doldur
document.addEventListener("DOMContentLoaded", () => {
    tumTurlariGetir(); // Mevcut fonksiyonun
    ulkeleriDoldur();  // Yeni fonksiyon
});

// --- YENİ EKLENEN FONKSİYONLAR ---

// 1. Ülkeleri Dropdown'a Doldur
function ulkeleriDoldur() {
    fetch(`${API_BASE}/destinations/countries`)
        .then(res => res.json())
        .then(countries => {
            const select = document.getElementById("filterCountry");
            countries.forEach(country => {
                const opt = document.createElement("option");
                opt.value = country;
                opt.text = country;
                select.appendChild(opt);
            });
        });
}

// 2. Ülke Seçilince Şehirleri Getir
function ulkeFiltresiSecildi() {
    const country = document.getElementById("filterCountry").value;
    const citySelect = document.getElementById("filterCity");
    
    citySelect.innerHTML = '<option value="">Tümü</option>'; // Temizle
    citySelect.disabled = true; // Kilitle

    if (!country) return; // Boş seçildiyse dur

    // Şehirleri Çek
    fetch(`${API_BASE}/destinations/cities/${country}`)
        .then(res => res.json())
        .then(cities => {
            cities.forEach(city => {
                const opt = document.createElement("option");
                // Şehir adını value olarak kullanıyoruz çünkü search-city endpointi isim bekliyor
                opt.value = city.destinationCity; 
                opt.text = city.destinationCity;
                citySelect.appendChild(opt);
            });
            citySelect.disabled = false; // Kilidi aç
        });
}

// 3. "Ara" Butonuna Basınca Çalışan Zeka
function destinasyonFiltrele() {
    const country = document.getElementById("filterCountry").value;
    const city = document.getElementById("filterCity").value;

    filters.country = country || "";
    filters.city    = city || "";

    applyFilters();

    // title.innerText = "Arama Sonuçları";
    // grid.innerHTML = "⏳ Aranıyor...";

    // let url = "";

    // if (city) {
    //     // Eğer Şehir seçildiyse -> Şehir Arama Endpoint'ine git
    //     // Adres: /api/tours/search-city/{city}
    //     url = `${API_BASE}/tours/search-city/${city}`;
    //     title.innerText = `🏙️ "${city}" Turları`;
    // } else if (country) {
    //     // Eğer sadece Ülke seçildiyse -> Ülke Arama Endpoint'ine git (YENİ YAPTIĞIMIZ)
    //     // Adres: /api/tours/by-country/{country}
    //     url = `${API_BASE}/tours/by-country/${country}`;
    //     title.innerText = `🌍 "${country}" Turları`;
    // } else {
    //     // Hiçbiri seçilmediyse -> Hepsini getir
    //     tumTurlariGetir();
    //     return;
    // }

    // // İsteği At ve Listele
    // fetch(url)
    //     .then(res => res.json())
    //     .then(data => renderCards(data, "tour"))
    //     .catch(err => showError(err));
}

function tumTurlariGetir() {
    title.innerText = "Tüm Turlar";
    grid.innerHTML = "⏳ Yükleniyor...";
    
    fetch(`${API_BASE}/tours`)
        .then(res => res.json())
        .then(data => renderCards(data, "tour"))
        .catch(err => showError(err));
}

function yolcuFormlariniOlustur() {
        const count = document.getElementById("guest-count").value;
        const container = document.getElementById("passenger-forms-container");
        
        container.innerHTML = ""; // Önce temizle

        for (let i = 1; i <= count; i++) {
            const html = `
                <div style="background:#f9f9f9; padding:10px; border:1px solid #ddd; margin-bottom:10px; border-radius:5px;">
                    <strong>${i}. Yolcu Bilgileri</strong>
                    <input type="text" class="p-name" placeholder="Ad Soyad" style="width:100%; margin-top:5px;">
                    <input type="text" class="p-tc" placeholder="TC Kimlik / Pasaport" style="width:100%; margin-top:5px;">
                    <input type="date" class="p-birth" placeholder="Doğum Tarihi" style="width:100%; margin-top:5px;">
                    ${i === 1 ? '<small style="color:blue">İletişim bilgileri 1. yolcudan alınır.</small><input type="text" class="p-phone" placeholder="Telefon"><input type="email" class="p-email" placeholder="E-posta">' : ''}
                </div>
            `;
            container.innerHTML += html;
        }
        
        // Fiyatı da güncelle
        if(typeof fiyatiHesapla === "function") fiyatiHesapla();
    }
    
// Backend'e Gönderme Fonksiyonu
function rezervasyonuTamamla() {
    const packageId = document.getElementById("packageSelect").value;
    const guestCount = document.getElementById("guest-count").value;
        
    // Formlardaki verileri topla
    const passengers = [];
    const formDivs = document.querySelectorAll("#passenger-forms-container > div"); // Her kutuyu al

    formDivs.forEach((div, index) => {
        const p = {
            name: div.querySelector(".p-name").value,
            tcKimlik: div.querySelector(".p-tc").value,
            birthDate: div.querySelector(".p-birth").value,
            // Sadece 1. yolcuda iletişim bilgisi var, diğerlerinde boş olabilir veya kopyalanabilir
            phone: index === 0 ? div.querySelector(".p-phone").value : null, 
            email: index === 0 ? div.querySelector(".p-email").value : null
        };
        passengers.push(p);
    });

    // JSON Hazırla
    const requestData = {
        userId: 1, // Şimdilik test için sabit, login olunca değişecek
        packageId: parseInt(packageId),
        guestCount: parseInt(guestCount),
        passengers: passengers // Listeyi gönderiyoruz
    };

    console.log("Giden Veri:", requestData); // Kontrol için

    fetch(`${API_BASE}/reservations/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData)
    })
    .then(res => {
        if(!res.ok) throw new Error("Rezervasyon başarısız!");
        return res.json();
    })
    .then(data => {
        alert("✅ Rezervasyon ve Yolcular Kaydedildi! ID: " + data.reservationId);
    })
    .catch(err => alert("Hata: " + err.message));
}

// ---------------------------------------------
// 2. ŞEHİR ARAMA (TourController)
// ---------------------------------------------
function sehirAra() {
    const city = document.getElementById("cityInput").value;
    if(!city) { alert("Lütfen şehir yazın!"); return; }

    title.innerText = `🏙️ "${city}" İçin Sonuçlar`;
    grid.innerHTML = "⏳ Aranıyor...";

    fetch(`${API_BASE}/tours/search-city/${city}`)
        .then(res => res.json())
        .then(data => renderCards(data, "tour"))
        .catch(err => showError(err));
}

// ---------------------------------------------
// 3. FİYAT ARALIĞI (TourPackageController)
// ---------------------------------------------
function fiyataGoreGetir() {
    const min = document.getElementById("minPrice").value || 0;
    const max = document.getElementById("maxPrice").value || 999999;

    filters.minPrice = min !== "" ? Number(min) : null;
    filters.maxPrice = max !== "" ? Number(max) : null;

    applyFilters();

    // title.innerText = `💰 ${min} - ${max} TL Arası Paketler`;
    // grid.innerHTML = "⏳ Filtreleniyor...";

    // // Backend endpoint: /api/tour-packages/by-price-range?min=X&max=Y
    // fetch(`${API_BASE}/tour-packages/by-price-range?min=${min}&max=${max}`)
    //     .then(res => res.json())
    //     .then(data => renderCards(data, "package")) // DİKKAT: Burada tip "package"
    //     .catch(err => showError(err));
}

// ---------------------------------------------
// 5. SADECE SÜREYE GÖRE ARA
// ---------------------------------------------
// ---------------------------------------------
// 5. SADECE SÜREYE GÖRE ARA (ARALIKLI)
// ---------------------------------------------
function sureyeGoreAra() {
    // Kutulardan değerleri al (Boşsa varsayılan değer ata)
    let min = document.getElementById("minDur").value;
    let max = document.getElementById("maxDur").value;

    // Eğer ikisi de boşsa uyarı ver
    if (!min && !max) {
        alert("Lütfen en az bir değer giriniz!");
        return;
    }

    // Boş bırakılanları mantıklı değerlerle doldur
    if (!min) min = 0;
    if (!max) max = 100;

    filters.minDur = min !== "" ? Number(min) : null;
    filters.maxDur = max !== "" ? Number(max) : null;

    applyFilters();

    // title.innerText = `⏳ ${min} - ${max} Günlük Turlar`;
    // grid.innerHTML = "⏳ Aranıyor...";

    // // YENİ ADRES: /api/tours/by-duration?min=...&max=...
    // fetch(`${API_BASE}/tours/by-duration?min=${min}&max=${max}`)
    //     .then(res => res.json())
    //     .then(data => renderCards(data, "tour"))
    //     .catch(err => showError(err));
}

// ---------------------------------------------
// 6. SADECE KİŞİ SAYISINA GÖRE ARA
// ---------------------------------------------
// ---------------------------------------------
// 6. KALAN KOLTUĞA GÖRE ARA (GÜNCELLENMİŞ)
// ---------------------------------------------
function kapasiteyeGoreAra() {
    const guests = document.getElementById("inputGuests").value;

    if (!guests) {
        alert("Lütfen kişi sayısı giriniz!");
        return;
    }

    filters.guests = Number(guests);

    applyFilters();

    // title.innerText = `👥 En Az ${guests} Kişilik Yeri Olan Paketler`;
    // grid.innerHTML = "⏳ Kontenjanlar kontrol ediliyor...";

    // // DİKKAT: Artık 'tours' değil 'tour-packages' endpointine gidiyoruz
    // fetch(`${API_BASE}/tour-packages/by-availability?seats=${guests}`)
    //     .then(res => res.json())
    //     .then(data => {
    //         // DİKKAT 2: Gelen veri 'paket' olduğu için renderCards'a "package" tipini gönderiyoruz
    //         renderCards(data, "package"); 
    //     })
    //     .catch(err => showError(err));
}

// ---------------------------------------------
// 4. SIRALAMA (TourPackageController)
// ---------------------------------------------
function fiyatSirala(yon) {
    filters.sort = yon === 'asc' ? 'priceAsc' : 'priceDesc';
    applyFilters();

    // const endpoint = yon === 'asc' ? 'order-by-price-asc' : 'order-by-price-desc';
    
    // title.innerText = yon === 'asc' ? "Fiyat: Düşükten Yükseğe" : "Fiyat: Yüksekten Düşüğe";
    // grid.innerHTML = "⏳ Sıralanıyor...";

    // fetch(`${API_BASE}/tour-packages/${endpoint}`)
    //     .then(res => res.json())
    //     .then(data => renderCards(data, "package"))
    //     .catch(err => showError(err));
}

// ---------------------------------------------
// TÜM FİLTRELERİ SIRALAMA
// ---------------------------------------------
function applyFilters() {
    title.innerText = "Filtrelenmiş Sonuçlar";
    grid.innerHTML = "⏳ Filtreleniyor...";

    const params = new URLSearchParams();

    if (filters.country) params.set("country", filters.country);
    if (filters.city) params.set("city", filters.city);
    if (filters.minPrice != null) params.set("minPrice", filters.minPrice);
    if (filters.maxPrice != null) params.set("maxPrice", filters.maxPrice);
    if (filters.minDur != null) params.set("minDur", filters.minDur);
    if (filters.maxDur != null) params.set("maxDur", filters.maxDur);
    if (filters.guests != null) params.set("guests", filters.guests);
    if (filters.sort) params.set("sort", filters.sort);

    fetch(`${API_BASE}/tours/search?${params.toString()}`)
        .then(res => res.json())
        .then(data => renderCards(data, "tour"))
        .catch(err => showError(err));
}


// ---------------------------------------------
// ORTAK KART ÇİZME FONKSİYONU
// ---------------------------------------------
function renderCards(data, type) {
    grid.innerHTML = "";

    // Eğer veri yoksa veya boşsa uyarı ver
    if (!data || data.length === 0) {
        grid.innerHTML = "<p>⚠️ Kriterlere uygun sonuç bulunamadı.</p>";
        return;
    }

    data.forEach(item => {
        let name, price, desc, id;

        // Backend'den gelen veri "Tour" mu yoksa "TourPackage" mi?
        if (type === "tour") {
            id    = item.tourId;
            name  = item.packageName;
            price = item.basePrice;
            desc  = item.description || `📅 ${item.startDate} - ${item.endDate}`;
        }
        else {
            // --- PACKAGE İSE ---
            id = item.packageId; 
            
            // Paketin içindeki tur ismini al (Yoksa varsayılan yaz)
            name = item.tour ? item.tour.packageName : "Özel Tur Paketi";
            
            // 🔴 HATA BURADAYDI! DÜZELTİLDİ:
            // Backend 'basePrice' gönderiyor, biz 'price' arıyorduk.
            price = item.basePrice; 
            
            desc = `📅 Tarih: ${item.startDate} - ${item.endDate}`;
        }

        // Fiyatı formatla (undefined yazmasın)
        const priceText = price ? `${price} TL` : "Fiyat Bilgisi Yok";

        // Detay linki için ID ayarı:
        // Eğer paketse, detay sayfasına paketin bağlı olduğu Turun ID'sini gönderelim
        const detailId = (type === 'tour') ? id : (item.tour ? item.tour.tourId : 1);

        const cardHTML = `
            <div class="card">
                <h3>${name}</h3>
                <p class="price">${priceText}</p>
                <p style="color:#666; font-size:0.9em;">${desc ? desc.substring(0, 100) : ''}...</p>
                
                <a href="detail.html?id=${detailId}" 
                   style="display:inline-block; margin-top:10px; padding:8px 15px; background:#007bff; color:white; text-decoration:none; border-radius:4px;">
                   İncele
                </a>
            </div>
        `;
        grid.innerHTML += cardHTML;
    });
}

function showError(err) {
    console.error(err);
    grid.innerHTML = `<p style="color:red">Hata oluştu! Backend çalışıyor mu?</p>`;
}

function filtreleriTemizle() {
    // filters objesini sıfırla
    filters.country = "";
    filters.city    = "";
    filters.minPrice = null;
    filters.maxPrice = null;
    filters.minDur   = null;
    filters.maxDur   = null;
    filters.guests   = null;
    filters.sort     = null;

    // inputları da sıfırla
    document.getElementById("filterCountry").value = "";
    const citySelect = document.getElementById("filterCity");
    citySelect.innerHTML = '<option value="">Önce Ülke Seç...</option>';
    citySelect.disabled = true;

    document.getElementById("minPrice").value = "";
    document.getElementById("maxPrice").value = "";
    document.getElementById("minDur").value   = "";
    document.getElementById("maxDur").value   = "";
    document.getElementById("inputGuests").value = "";

    // tekrar tüm turları getir
    tumTurlariGetir();
}
