const API_BASE = "http://localhost:8080/api";
const grid = document.getElementById("results-grid");
const title = document.getElementById("page-title");

// GLOBAL FİLTRE DURUMU
let currentFilters = {
    city: "",
    country: "",
    minPrice: "",
    maxPrice: "",
    minDur: "",
    maxDur: "",
    guests: "",
    sortBy: "rating"
};

// Sayfa açılınca çalışır
document.addEventListener("DOMContentLoaded", () => {
    ulkeleriDoldur();
    verileriGetir(); // İlk açılışta hepsini getirir
});

// --- 1. FİLTRELERİ UYGULA BUTONU ---
function filtreleriUygula() {
    // HTML'den değerleri al
    currentFilters.country = document.getElementById("filterCountry").value;
    
    const citySelect = document.getElementById("filterCity");
    currentFilters.city = citySelect ? citySelect.value : "";
    if(currentFilters.city === "Tümü") currentFilters.city = "";

    currentFilters.minPrice = document.getElementById("minPrice").value;
    currentFilters.maxPrice = document.getElementById("maxPrice").value;
    currentFilters.minDur = document.getElementById("minDur").value;
    currentFilters.maxDur = document.getElementById("maxDur").value;
    currentFilters.guests = document.getElementById("filterGuests").value;
    currentFilters.sortBy = document.getElementById("sortOrder").value;

    // Verileri çek
    verileriGetir();
}

// --- 2. SIRALAMA DEĞİŞİNCE ---
function siralamayiDegistir() {
    console.log("Sıralama değişti, Backend'den yeni veri isteniyor...");
    currentFilters.sortBy = document.getElementById("sortOrder").value;
    
    // Backend sıralamayı yaptığı için tekrar istek atıyoruz
    verileriGetir();
}

// --- 3. BACKEND İSTEĞİ (TEK MERKEZ) ---
function verileriGetir() {
    title.innerText = "Sonuçlar Yükleniyor...";
    grid.innerHTML = "⏳ Lütfen bekleyin...";

    // Parametreleri hazırla
    const params = new URLSearchParams();
    if (currentFilters.country) params.append("country", currentFilters.country);
    if (currentFilters.city) params.append("city", currentFilters.city);
    if (currentFilters.minPrice) params.append("minPrice", currentFilters.minPrice);
    if (currentFilters.maxPrice) params.append("maxPrice", currentFilters.maxPrice);
    if (currentFilters.minDur) params.append("minDuration", currentFilters.minDur);
    if (currentFilters.maxDur) params.append("maxDuration", currentFilters.maxDur);
    if (currentFilters.guests) params.append("guests", currentFilters.guests);
    
    // ARTIK SORT PARAMETRESİNİ DE GÖNDERİYORUZ (Çünkü Backend yapıyor)
    if (currentFilters.sortBy) params.append("sortBy", currentFilters.sortBy);

    // Backend'deki "Search" Endpoint'ine git
    fetch(`${API_BASE}/tours/search?${params.toString()}`)
        .then(res => res.json())
        .then(data => {
            if(data.length > 0) {
                title.innerText = `${data.length} Tur Bulundu`;
            } else {
                title.innerText = "Sonuç Bulunamadı";
            }
            // Gelen veri zaten sıralı, direkt basıyoruz
            renderCards(data);
        })
        .catch(err => showError(err));
}

// --- 4. KARTLARI ÇİZ ---
function renderCards(data) {
    grid.innerHTML = "";

    if (!data || data.length === 0) {
        grid.innerHTML = "<p>⚠️ Kriterlere uygun sonuç bulunamadı.</p>";
        return;
    }

    data.forEach(dto => {
        // Backend'den 'TourWithPackagesDTO' geliyor
        const tour = dto.tour;
        const packages = dto.packages; // Backend bunu zaten sıraladı!

        let id = tour.tourId;
        let name = tour.packageName;
        let desc = tour.description;
        let cardContent = "";

        // Kartın içine paketleri listele
        if(packages && packages.length > 0) {
            cardContent = `<div style="margin:10px 0; padding:10px; background:#f8f9fa; border-radius:5px; font-size:13px; border:1px dashed #ccc;">
                               <strong style="color:#27ae60;">🔥 Uygun Fırsatlar:</strong>
                               <ul style="padding-left:20px; margin:5px 0; color:#555;">`;
            
            // İlk 3 tanesini gösterelim
            packages.slice(0, 3).forEach(p => {
                cardContent += `<li>📅 ${p.startDate}: <strong>${p.basePrice} TL</strong></li>`;
            });
            
            if(packages.length > 3) cardContent += `<li>...ve ${packages.length - 3} tarih daha</li>`;
            cardContent += `</ul></div>`;
        } else {
            cardContent = `<p style="color:#e67e22; font-weight:bold; margin-top:10px;">Tarih Seçiniz 📅</p>`;
        }

        const cardHTML = `
            <div class="card">
                <h3>${name}</h3>
                ${cardContent}
                <p style="color:#666; font-size:0.9em;">${desc ? desc.substring(0, 80) : ''}...</p>
                <a href="detail.html?id=${id}" class="btn-inspect">İncele</a>
            </div>
        `;
        grid.innerHTML += cardHTML;
    });
}

// --- YARDIMCI FONKSİYONLAR ---

function ulkeleriDoldur() {
    fetch(`${API_BASE}/destinations/countries`)
        .then(res => res.json())
        .then(countries => {
            const select = document.getElementById("filterCountry");
            select.innerHTML = '<option value="">Tümü</option>';
            
            const uniqueList = [...new Set(countries.filter(c=>c).map(c=>c.trim()))];
            
            uniqueList.forEach(country => {
                const opt = document.createElement("option");
                opt.value = country;
                opt.text = country;
                select.appendChild(opt);
            });
        })
        .catch(err => console.error(err));
}

function ulkeFiltresiSecildi() {
    const country = document.getElementById("filterCountry").value;
    const citySelect = document.getElementById("filterCity");
    
    citySelect.innerHTML = '<option value="">Tümü</option>';
    citySelect.disabled = true;

    if (!country || country === "Tümü") return;

    fetch(`${API_BASE}/destinations/cities/${country}`)
        .then(res => res.json())
        .then(cities => {
            cities.forEach(city => {
                const opt = document.createElement("option");
                opt.value = city.destinationCity;
                opt.text = city.destinationCity;
                citySelect.appendChild(opt);
            });
            citySelect.disabled = false;
        });
}

function showError(err) {
    console.error(err);
    grid.innerHTML = `<p style="color:red">Hata oluştu! Backend çalışıyor mu?</p>`;
}