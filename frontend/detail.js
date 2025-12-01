const urlParams = new URLSearchParams(window.location.search);
const tourId = urlParams.get('id');
const API_BASE = "http://localhost:8080/api";

let currentBasePrice = 0;

// Sayfa Yüklenince Çalışacak Kodlar
document.addEventListener("DOMContentLoaded", () => {
    if(!tourId) {
        document.getElementById('loading').innerText = "Geçersiz ID! Lütfen listeye dönüp tekrar seçiniz.";
    } else {
        initPage();
    }
});

// Sayfa Açılış Verilerini Çekme
async function initPage() {
    try {
        // 1. TUR BİLGİLERİNİ ÇEK
        const tourRes = await fetch(`${API_BASE}/tours/${tourId}`);
        if(!tourRes.ok) throw new Error("Tur bulunamadı");
        const tour = await tourRes.json();

        // HTML'e Doldur (Yeni ID'lere göre)
        document.getElementById('t-name').innerText = tour.packageName;
        document.getElementById('t-duration').innerText = tour.duration;
        document.getElementById('t-rating').innerText = tour.avg_rating || "Yeni";
        document.getElementById('t-desc').innerText = tour.description;
        document.getElementById('t-cap').innerText = tour.capacity || "Belirsiz"; // Kapasiteyi de ekledik

        // ... (Destinasyon ve Paket çekme kısımları AYNI kalsın) ...

        // Yükleme ekranını kapat, ANA KONTEYNERİ aç
        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-container').style.display = 'block'; 

        // Bilgileri HTML'e Doldur
        document.getElementById('t-name').innerText = tour.packageName;
        document.getElementById('t-duration').innerText = tour.duration;
        document.getElementById('t-rating').innerText = tour.avg_rating || "Yeni";
        document.getElementById('t-desc').innerText = tour.description;

        // Destinasyonları Virgülle Yaz
        if(tour.destinations && tour.destinations.length > 0) {
            const sehirler = tour.destinations.map(d => d.destinationCity).join(", ");
            document.getElementById('t-dest').innerText = sehirler;
        } else {
            document.getElementById('t-dest').innerText = "Belirtilmemiş";
        }

        // 2. PAKETLERİ (TARİHLERİ) ÇEK
        const pkgRes = await fetch(`${API_BASE}/tour-packages/by-tour/${tourId}`);
        const packages = await pkgRes.json();
        const selectBox = document.getElementById("packageSelect");

        if (packages.length === 0) {
            selectBox.style.display = "none";
            document.getElementById("no-package-msg").style.display = "block";
        } else {
            selectBox.innerHTML = ""; 
            packages.forEach(pkg => {
                const opt = document.createElement("option");
                opt.value = pkg.packageId;
                opt.setAttribute("data-price", pkg.basePrice);
                opt.text = `${pkg.startDate} - ${pkg.endDate} (${pkg.basePrice} TL)`;
                selectBox.appendChild(opt);
            });
            
            // Otomatik olarak ilkini seç ve fiyatı getir
            selectBox.selectedIndex = 0;
            paketDegisti();
        }

        // Yükleme ekranını kapat, içeriği aç
        document.getElementById('loading').style.display = 'none';
        document.getElementById('main-content').style.display = 'flex';
        
        // İlk açılışta 1 kişilik formu oluştur
        yolcuFormlariniOlustur();

        

    } catch (err) {
        console.error(err);
        document.getElementById('loading').innerHTML = "<h3 style='color:red'>Hata oluştu! Backend çalışıyor mu?</h3>";
    }
}

// --- Paket (Tarih) Değişince ---
function paketDegisti() {
    const select = document.getElementById("packageSelect");
    const selectedOption = select.options[select.selectedIndex];
    
    if (selectedOption) {
        currentBasePrice = parseFloat(selectedOption.getAttribute("data-price")) || 0;
        document.getElementById("display-price").innerText = currentBasePrice + " TL";
        fiyatiHesapla();
    }
}

// --- Toplam Fiyat Hesapla ---
function fiyatiHesapla() {
    const kisiSayisi = parseInt(document.getElementById("guest-count").value) || 1;
    const toplam = kisiSayisi * currentBasePrice;
    document.getElementById("total-amount").innerText = toplam.toLocaleString();
}

// --- Dinamik Yolcu Formlarını Oluştur ---
// --- Dinamik Yolcu Formlarını Oluştur (GÜNCELLENMİŞ) ---
function yolcuFormlariniOlustur() {
    const count = parseInt(document.getElementById("guest-count").value) || 1;
    const container = document.getElementById("passenger-forms-container");
    
    // Mevcut form sayısını al (Veri kaybını önlemek için sadece eksikse ekle, fazlaysa sil mantığı daha iyidir ama şimdilik senin yapını koruyarak ilerliyoruz)
    // Basitlik adına: Eğer sayı artıyorsa ekle, azalıyorsa sonuncuyu sil yapısı da kurabiliriz.
    // Ancak senin "X ile silme" isteğin için en temiz yöntem şudur:
    
    container.innerHTML = ""; 

    for (let i = 1; i <= count; i++) {
        // 1. Yolcu için silme butonu YOK (i > 1 kontrolü)
        const deleteButton = (i > 1) 
            ? `<button class="btn-remove-passenger" onclick="yolcuSil(this)" title="Bu yolcuyu çıkar">✕</button>` 
            : '';

        const html = `
            <div class="passenger-card">
                ${deleteButton} <div class="passenger-header"><span class="p-num">${i}</span>. Yolcu Bilgileri</div>
                
                <div class="form-row">
                    <div class="form-col"><input type="text" class="p-name" placeholder="Ad Soyad (Zorunlu)"></div>
                    <div class="form-col"><input type="text" class="p-tc" placeholder="TC Kimlik (11 Hane)" maxlength="11"></div>
                </div>

                <div class="form-row">
                    <div class="form-col"><small>Doğum Tarihi:</small><input type="date" class="p-birth"></div>
                    <div class="form-col"><small>Pasaport No:</small><input type="text" class="p-passport" placeholder="Pasaport No"></div>
                </div>

                <div class="form-row">
                    <div class="form-col"><small>Pasaport Bitiş:</small><input type="date" class="p-pass-exp"></div>
                </div>

                <div style="margin-top:10px; border-top:1px dashed #ddd; padding-top:10px;">
                    <small>İletişim Bilgileri (Zorunlu):</small>
                    <div class="form-row">
                        <div class="form-col"><input type="text" class="p-phone" placeholder="Telefon (5xx...)"></div>
                        <div class="form-col"><input type="email" class="p-email" placeholder="E-posta Adresi"></div>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += html;
    }
    // Fiyatı güncelle
    fiyatiHesapla();
}

// --- YENİ: Yolcu Silme Fonksiyonu ---
function yolcuSil(btn) {
    // 1. Kartı HTML'den kaldır
    const card = btn.parentElement;
    card.remove();

    // 2. Input'taki sayıyı düşür
    const countInput = document.getElementById("guest-count");
    let currentCount = parseInt(countInput.value);
    countInput.value = currentCount - 1;

    // 3. Başlıkları Yeniden Numaralandır (1, 3, 4 -> 1, 2, 3 olsun diye)
    yenidenNumaralandir();

    // 4. Fiyatı Yeniden Hesapla
    fiyatiHesapla();
}

// --- YENİ: Numaraları Düzeltme Yardımcısı ---
function yenidenNumaralandir() {
    const cards = document.querySelectorAll(".passenger-card");
    cards.forEach((card, index) => {
        // index 0'dan başlar, o yüzden +1 ekliyoruz
        const numSpan = card.querySelector(".p-num");
        if(numSpan) {
            numSpan.innerText = index + 1;
        }
    });
}
// --- EKRAN GEÇİŞLERİ ---

function rezervasyonaGec() {
    // Vitrini gizle, Rezervasyonu aç
    document.getElementById("tour-showcase").style.display = "none";
    document.getElementById("reservation-panel").style.display = "block";
    
    // Formları hazırla (Eğer ilk kez açılıyorsa)
    yolcuFormlariniOlustur();
}

function detayaDon() {
    // Rezervasyonu gizle, Vitrini aç
    document.getElementById("reservation-panel").style.display = "none";
    document.getElementById("tour-showcase").style.display = "block";
}

// --- REZERVASYONU KAYDET (BACKEND'E GÖNDER) ---
function rezervasyonuTamamla() {
    const packageId = document.getElementById("packageSelect").value;
    const guestCount = parseInt(document.getElementById("guest-count").value);
    
    const passengers = [];
    const formDivs = document.querySelectorAll("#passenger-forms-container > div");

    let hata = false;

    // Formlardaki verileri topla
    formDivs.forEach((div, index) => {
        const name = div.querySelector(".p-name").value;
        const tc = div.querySelector(".p-tc").value;
        const birth = div.querySelector(".p-birth").value;
        const phone = div.querySelector(".p-phone").value;
        const email = div.querySelector(".p-email").value;
        const passport = div.querySelector(".p-passport").value;
        const passExp = div.querySelector(".p-pass-exp").value;

        // Basit Zorunluluk Kontrolü
        if(!name || !tc || !birth || !phone || !email || !passport || !passExp) {
            alert(`${index + 1}. Yolcunun tüm bilgilerini doldurunuz!`);
            hata = true;
            return;
        }

        passengers.push({
            name: name,
            tcKimlik: tc,
            birthDate: birth,
            phone: phone,
            email: email,
            pasaportNo: passport,
            pasaportExpirationDate: passExp
        });
    });

    if(hata) return; // Eksik varsa gönderme

    const data = {
        userId: 1, // Şimdilik test kullanıcısı (ID: 1)
        packageId: parseInt(packageId),
        guestCount: guestCount,
        passengers: passengers
    };

    // POST İsteği
    fetch(`${API_BASE}/reservations/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(async res => {
        if(!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText); // Backend'den gelen hatayı göster
        }
        return res.json();
    })
    .then(d => {
        alert("✅ Rezervasyon Başarılı! Rezervasyon ID: " + d.reservationId);
        window.location.reload(); // Sayfayı yenile
    })
    .catch(e => {
        console.error(e);
        alert("Hata oluştu: " + e.message);
    });

    
}