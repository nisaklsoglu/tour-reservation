const API_BASE = "http://localhost:8080/api";
const container = document.getElementById('reservations-container');
// Test user id (incelemeler/rezervasyonlar için sabit, login gelince değişecek)
const USER_ID = 1;

function loadReservations() {
    container.innerHTML = '⏳ Yükleniyor...';
    fetch(`${API_BASE}/reservations/user/${USER_ID}`)
        .then(res => res.json())
        .then(list => {
            if (!list || list.length === 0) {
                container.innerHTML = '<p>Henüz rezervasyonunuz yok.</p>';
                return;
            }

            container.innerHTML = '';
            list.forEach(r => {
                const pkg = r.tourPackage || {};
                const tour = pkg.tour || {};
                const html = `
                    <div class="card">
                        <div style="display:flex; justify-content:space-between; align-items:center">
                            <div>
                                <div style="font-weight:700">${tour.packageName || 'Bilinmeyen Tur'}</div>
                                <div class="muted">Paket ID: ${pkg.packageId || '-'}</div>
                            </div>
                            <div style="text-align:right">
                                <div style="font-weight:600">Toplam: ${r.totalPrice ? r.totalPrice + ' TL' : '—'}</div>
                                <div class="muted">Durum: ${r.status}</div>
                            </div>
                        </div>
                        <div style="margin-top:10px">Rezervasyon Tarihi: ${r.reservationDate || '-'}</div>
                        <div style="margin-top:8px"> <a href="detail.html?id=${tour.tourId || (pkg.tourId||'')}" class="btn-detail">Tura Git</a> </div>
                    </div>
                `;
                container.innerHTML += html;
            });
        })
        .catch(err => {
            container.innerHTML = '<p>Hata: ' + err.message + '</p>';
        });
}

loadReservations();
