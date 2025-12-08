const API_URL = "http://localhost:8080/api/tours";

// Sayfa açılınca otomatik çalışsın diye en alta fonksiyonu çağıracağız
function turlariGetir() {
    const listeKutusu = document.getElementById("tur-listesi");
    
    if (!listeKutusu) {
        console.error("HATA: HTML sayfasında id='tur-listesi' olan div bulunamadı!");
        return;
    }

    listeKutusu.innerHTML = "⏳ Turlar yükleniyor...";

    fetch(API_URL)
        .then(response => response.json())
        .then(data => {
            listeKutusu.innerHTML = "";

            if (data.length === 0) {
                listeKutusu.innerHTML = "<p>⚠️ Kayıtlı tur yok.</p>";
                return;
            }

            data.forEach(tur => {
                // HTML Kodu 
                const turKarti = `
                    <div class="tour-card" style="border:1px solid #ccc; padding:15px; margin:10px; border-radius:8px; background:#fff;">
                        <h2>✈️ ${tur.packageName}</h2>
                        <p><strong>Tip:</strong> ${tur.tourType}</p>
                        <p><strong>Fiyat:</strong> ${tur.bir_kisilik_oda} TL</p>
                        <p style="color:#555;">${tur.description}</p>
                        
                        <a href="detail.html?id=${tur.tourId}" style="
                            display: inline-block;
                            margin-top: 10px;
                            padding: 10px 20px;
                            background-color: #007bff;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            font-weight: bold;">
                            🔍 İncele
                        </a>
                    </div>
                `;
                listeKutusu.innerHTML += turKarti;
            });
        })
        .catch(error => {
            console.error("Hata:", error);
            listeKutusu.innerHTML = `<p style="color:red">Bağlantı Hatası! Backend çalışıyor mu?</p>`;
        });
}

// Sayfa açıldığında fonksiyonu otomatik başlat
turlariGetir();