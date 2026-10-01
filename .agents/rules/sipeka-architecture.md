---
description: Aturan arsitektur, keamanan anti-fraud, dan tata kelola kode SIPeKa SMKN 8 Jakarta untuk AI Coding Assistant.
globs: **/*.kt, **/*.kts, **/*.xml
---

# Aturan Arsitektur & Keamanan Kode SIPeKa

Setiap AI Agent yang beroperasi pada repositori ini harus mematuhi panduan dan batasan arsitektur berikut:

## 1. Arsitektur Multi-Peran (4 Peran Pengguna)
- Sistem memiliki 4 peran: `pembeli` (Siswa), `kasir` (Petugas POS), `penitip` (Mitra Dagangan), dan `admin` (Guru Pembina).
- Setiap ViewModel memiliki scope data terisolasi. Jangan pernah mencampur StateFlow antar layar tanpa melalui `TokenManager` atau ViewModelFactory yang sesuai di `AppNavigation.kt`.
- Data riwayat pesanan siswa pada `PembeliViewModel` harus selalu difilter ketat hanya untuk akun siswa yang sedang aktif.

## 2. Jembatan Data Real-time (Reactive Shared State)
- Antrean pre-order siswa yang dipesan dari aplikasi siswa disinkronkan ke antrean kasir melalui `TokenManager.sharedOrdersFlow`.
- Kasir mendengarkan `sharedOrdersFlow` secara real-time dan menampilkan badge notifikasi serta banner antrean.
- Saat kasir menyelesaikan penyerahan pesanan, status harus diperbarui ke `COMPLETED` dan dipancarkan kembali sehingga tiket QR di smartphone siswa langsung berubah menjadi `SELESAI / DIAMBIL`.

## 3. Logika Keamanan & Anti-Fraud (Anti-Jahil)
- **Anti-Double-Redemption**: Tiket QR dan nomor pesanan yang sudah berstatus `COMPLETED` tidak boleh dapat diserahkan untuk kedua kalinya. Sistem harus menampilkan peringatan keamanan keras jika dicoba klaim ulang.
- **Clock-In Enforcement**: Kasir tidak boleh diizinkan menyerahkan makanan jika shift kasir belum dibuka (`isShiftOpen == false`).
- **Verifikasi Identitas Lisan**: Kasir harus selalu diperlihatkan Nama Siswa, Kelas, dan NISN untuk mencocokkan identitas siswa di depan konter.
- **Validasi Stok**: Pembeli tidak boleh dapat memesan item yang stoknya habis (`<= 0`) atau memilih kuantitas melebihi stok yang ada.

## 4. Hak Edit Profil Mandiri
- Seluruh peran pengguna berhak mengedit data akun mereka sendiri melalui UI khusus di masing-masing modul:
  - Pembeli: Nama, Kelas, NISN, Password baru.
  - Kasir: Nama, NIP, Password baru.
  - Penitip: Nama Pemilik, Nama Toko/Brand, NIP/Kontak, Password baru.
- Seluruh pembaruan profil harus disimpan secara persisten di DataStore Preferences (`TokenManager`) dan dikirim ke backend via `apiService.updateUser(...)`.

## 5. Standar UI Jetpack Compose & Desain
- Gunakan token warna tema resmi dari `com.smkn8jkt.sipeka.ui.theme` (`BgDarkEspresso`, `BtnDarkChocolate`, `BtnMocha`, `CardCreamWhite`, `BgWarmTan`, `SegmentBg`, `BorderStitch`, `VibrantOrange`).
- Hindari warna mentah seperti `Color.Red`, `Color.Green`, `Color.Blue`. Gunakan `RedError`, `GreenSuccess`, dan `AmberWarning`.
- Pertahankan sudut membulat konsisten: kartu (16-18dp), tombol (10-12dp), badge (4-6dp), chip/avatar (lingkaran / CircleShape).

## 6. Keamanan Repositori Git (Zero-Leak Policy)
- Jangan pernah meng-unignore `local.properties`, keystore penandatanganan (`*.jks`, `*.keystore`), file `.env`, atau kredensial rahasia.
- Jangan menulis token autentikasi atau password secara hardcode di dalam kode sumber.
