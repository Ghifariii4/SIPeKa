dl# Dokumentasi Teknis Aplikasi SIPeKa
**Sistem Informasi Pelayanan Kantin SMKN 8 Jakarta**

> **Catatan**: Dokumentasi ini mencakup arsitektur sistem, struktur proyek, fitur berbasis peran (Kasir, Admin Guru Pembina, dan Penitip Dagangan), panduan alur kerja, serta referensi API REST backend Node.js.

---

## 📌 Ringkasan Proyek

**SIPeKa** (Sistem Informasi Pelayanan Kantin) adalah aplikasi Android native yang dibangun dengan Jetpack Compose Material 3 untuk digitalisasi operasional kantin sekolah SMKN 8 Jakarta. Aplikasi ini memfasilitasi transaksi POS kasir, manajemen shift & laci kas, validasi setoran & bagi hasil oleh Guru Pembina, monitoring barang titipan siswa, serta pemrosesan pesanan pre-order berbasis QR Code.

### 🛠️ Teknologi & Library Utama

| Komponen | Teknologi / Library | Keterangan |
| :--- | :--- | :--- |
| **Bahasa Pemrograman** | Kotlin | JDK 11, Kotlin Compose Compiler |
| **UI Toolkit** | Jetpack Compose (Material 3) | Declarative UI, Modifiers, Animations |
| **Arsitektur UI** | MVVM (Model-View-ViewModel) | StateFlow, Shared ViewModel Scoping |
| **Networking** | Retrofit2 + Gson Converter | REST API, OkHttp3 Logging Interceptor |
| **Penyimpanan Lokal** | DataStore Preferences | JWT Token, Role, Modal Awal, Tracker ID |
| **Image Loading** | Coil Compose (`io.coil-kt:coil-compose`) | SubcomposeAsyncImage, Memory Caching |
| **Navigasi** | Navigation Compose | Single Activity NavHost (`AppNavigation.kt`) |

---

## 📁 Struktur Paket Proyek

Arsitektur kode diatur menggunakan pola **Package by Layer & Feature**:

```
com.smkn8jkt.sipeka
├── MainActivity.kt                  # Entry Point Utama Activity
├── MainViewModel.kt                 # ViewModel Debugger API
│
├── data/
│   ├── model/                      # DTOs & Data Classes
│   │   ├── AuthModels.kt           # LoginRequest, RegisterRequest, LoginResponse
│   │   └── PosModels.kt            # ProductResponse, OrderData, ShiftData, PayoutResponse, dll.
│   │
│   └── remote/                     # Jaringan HTTP & Storage
│       ├── ApiClient.kt            # Client Retrofit & Auth Interceptor
│       ├── ApiService.kt           # Interface 18 Endpoint REST API
│       └── TokenManager.kt         # Penyimpanan Persisten DataStore Preferences
│
├── navigation/                     # Navigasi & Rute Layar
│   └── AppNavigation.kt            # NavHost, Auto-Login Routing, Access Control
│
└── ui/
    ├── components/                 # Komponen Reusable (Button, TextField, Dropdown)
    │   └── SipekaComponents.kt
    │
    ├── screens/                    # Layar & ViewModel Berdasarkan Fitur
    │   ├── admin/                  # Modul Admin Guru Pembina
    │   │   ├── AdminDashboardScreen.kt
    │   │   └── AdminViewModel.kt
    │   │
    │   ├── auth/                   # Modul Autentikasi
    │   │   ├── LoginScreen.kt
    │   │   ├── RegisterScreen.kt
    │   │   └── AuthViewModel.kt
    │   │
    │   ├── penitip/                # Modul Penitip Dagangan Siswa
    │   │   ├── PenitipDashboardScreen.kt
    │   │   └── PenitipViewModel.kt
    │   │
    │   ├── pos/                    # Modul POS Kasir Kantin
    │   │   ├── HomeScreen.kt
    │   │   ├── PosViewModel.kt
    │   │   ├── CheckoutBottomSheet.kt
    │   │   ├── RiwayatScreen.kt
    │   │   ├── ProfilKasirScreen.kt
    │   │   └── SipekaProductCard.kt
    │   │
    │   └── splash/                 # Modul Awal Aplikasi
    │       └── SplashScreen.kt
    │
    └── theme/                      # Sistem Desain Material 3
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 👥 Fitur & Modul Berdasarkan Peran

### 1. 🏪 Modul Kasir (POS Canteen Cashier)

- **Buka Shift & Modal Awal**:
  - Input nominal modal awal laci kasir saat pertama kali masuk.
  - Data modal awal disimpan secara persisten di DataStore (`startingCashFlow`) sehingga tetap konsisten meskipun aplikasi di-restart.
- **Katalog & Keranjang Belanja**:
  - Grid produk 2 kolom dengan gambar real-time dari server via Coil `ProductImage`.
  - Indikator stok cerdas: Stok Normal, Stok Tipis (`<= 5 pcs`), dan Stok Habis (`Habis` + tombol `+` nonaktif).
  - Chip filter kategori instan (*Semua Menu, Makanan, Minuman, Snack*) & bilah pencarian produk.
  - Bar keranjang melayang (*Floating Persistent Cart Bar*) yang menampilkan total Rupiah dan jumlah item.
- **Pembayaran & Kalkulasi Kembalian (`CheckoutBottomSheet`)**:
  - Input nominal uang diterima dari pembeli.
  - Tombol preset nominal cepat: `Uang Pas`, `10rb`, `20rb`, `50rb`, dan `100rb`.
  - Kalkulasi **Jumlah Kembalian** real-time (`Uang Diterima - Total Bayar`). Peringatan otomatis jika uang kurang.
  - Pop-up Bukti Transaksi Berhasil dengan perincian pembayaran.
- **Verifikasi Pre-Order QR Code**:
  - Aksi cepat *Scan QR Pre-Order*.
  - Pop-up detail pesanan siswa untuk **pemeriksaan kecocokan fisik barang** sebelum menyerahkan ke pembeli.
- **Riwayat Transaksi Terpisah (`RiwayatScreen`)**:
  - Pengelompokan riwayat berdasarkan **Shift** dan **Hari/Tanggal Kasir**.
  - Filter chip cepat: `Shift Aktif`, `Hari Ini`, dan `Semua`.
  - Klik kartu transaksi untuk membuka **Detail Struk Transaksi** (`OrderDetailDialog`) yang merinci nama produk, harga satuan, qty, dan subtotal.
- **Profil Kasir & Jam Sistem (`ProfilKasirScreen`)**:
  - Kartu Jam Real-Time Sistem (`LiveClockCard`).
  - Rekapitulasi shift: *Mulai Shift*, *Modal Awal*, *Total Penjualan Shift*, dan *Total Estimasi Kas Laci*.
  - Dialog konfirmasi Tutup Shift dengan instruksi penyerahan uang fisik ke Guru Pembina.

---

### 2. 👨‍🏫 Modul Admin Keuangan (Guru Pembina)

- **Tab 1: Keuangan (`AdminFinanceTab`)**:
  - **Laba Bersih Hari Ini**: Card Gradient Hijau-Emas yang menampilkan akumulasi margin sekolah secara real-time.
  - **Validasi Setoran Kasir**: Daftar shift kasir yang sudah ditutup beserta nominal setoran laci.
  - **Bagi Hasil Penitip**: Daftar akumulasi hak bagi hasil siswa/penitip.
  - **Aksi Massal (Batch Processing)**: Fitur checkbox *"Pilih Semua"* untuk melakukan **Validasi Massal** atau **Pencairan Massal** dalam sekali klik.
- **Tab 2: Penjualan (`AdminTransactionsTab`)**:
  - Ringkasan Total Omzet Penjualan Kantin.
  - Bilah pencarian ID Transaksi.
  - Daftar seluruh riwayat transaksi penjualan kasir terpisah per tanggal/shift.
  - Klik kartu untuk melihat rincian item produk (`OrderDetailDialog`).
- **Tab 3: Pengguna (`AdminUsersTab`)**:
  - **Persetujuan Akun (ACC)**: Antrean pendaftaran akun baru siswa/penitip. Tombol **"ACC"** menyetujui akun sehingga user baru dapat login.
  - **Daftar Seluruh Pengguna**: List seluruh user beserta lencana peran (*Admin, Kasir, Penitip, Siswa*).
  - **Edit Data User (`EditUserDialog`)**: Klik user untuk mengubah Nama, NIP/NISN, atau Role.
  - **Tambah Petugas Kasir Baru**: Modal dialog pendaftaran kasir internal.

---

### 3. 🍱 Modul Penitip Dagangan Siswa

- **Card Pendapatan Belum Dicairkan**:
  - Menampilkan estimasi hak bagi hasil yang belum dicairkan dengan rumus: `(Harga Jual - Rp 1.000) × Terjual`.
- **Status Barang & Indikator Sales**:
  - List produk titipan milik penitip dengan gambar asli via Coil.
  - Indikator badge hijau **"Terjual: X pcs"** dan badge oranye/merah **"Sisa Stok: Y pcs"**.
  - Tombol tempat sampah merah untuk **Hapus Produk Titipan** (dilengkapi dialog konfirmasi).
- **Tambah Produk + Upload Foto (`AddProductDialog`)**:
  - Floating Action Button (+) di kanan bawah.
  - Form pendaftaran produk: **Photo Picker** (`PickVisualMedia`), preview foto (`AsyncImage`), Nama Produk, Harga Jual, Deskripsi, dan Stok Awal.
  - Mengirimkan request Multipart form-data `POST /api/v1/products`.

---

### 4. 🔐 Modul Autentikasi & Keamanan

- **Login & Register**:
  - Autentikasi NISN/NIP dan Password.
  - **Antrean ACC Pendaftaran**: Pendaftaran akun siswa/penitip baru akan diblokir saat login hingga Admin Guru Pembina menekan tombol **"ACC"**.
- **Sesi Auto-Login Persisten**:
  - `SplashScreen` mengecek token JWT di DataStore `TokenManager`.
  - Jika token valid dan ada, aplikasi otomatis masuk ke Dashboard sesuai Role tanpa perlu login ulang.
  - Tombol **Logout** secara aman menghapus token JWT dan mengembalikan pengguna ke layar Login.

---

## 🌐 Referensi API REST Backend Node.js

**Base URL**: `http://47.129.118.194/api/v1/`  
**Media Uploads URL**: `http://47.129.118.194/uploads/`

| Endpoint | Metode | Deskripsi | Payload / Query Parameter |
| :--- | :--- | :--- | :--- |
| `auth/login` | `POST` | Authentikasi login user | `{ nisn_nip, password }` |
| `auth/register` | `POST` | Pendaftaran akun baru | `{ nisn_nip, name, password, role }` |
| `products` | `GET` | Mengambil katalog produk | `?q=search_query` |
| `products/{id}` | `GET` | Mengambil detail produk | Path `id` |
| `products` | `POST` | Upload produk baru (Multipart) | Form-data: `image`, `name`, `price`, `description`, `stock`, `school_margin` |
| `products/{id}` | `DELETE` | Hapus produk dari database | Path `id`, Query: `?force=true&cascade=true&hard=true` |
| `shifts/clock-in` | `POST` | Buka shift kasir & modal awal | `{ starting_cash }` |
| `shifts/current` | `GET` | Cek status shift aktif kasir | - |
| `shifts/clock-out` | `POST` | Tutup shift kasir | - |
| `pos/transaction` | `POST` | Transaksi POS kasir | `{ items: [{ product_id, quantity }] }` |
| `pos/scan/{qr_code}` | `PUT` | Pemrosesan QR pre-order | Path `qr_code` |
| `orders` | `GET` | Riwayat transaksi penjualan | - |
| `orders/{id}` | `GET` | Detail rincian struk transaksi | Path `id` |
| `admin/finance/profit` | `GET` | Laba bersih sekolah real-time | - |
| `admin/finance/shifts` | `GET` | Antrean setoran shift kasir | - |
| `admin/finance/shifts/{id}/validate` | `PUT` | Validasi setoran shift kasir | Path `id` |
| `admin/finance/payouts` | `GET` | Antrean bagi hasil penitip | - |
| `admin/finance/payouts/{penitip_id}`| `PUT` | Pencairan bagi hasil penitip | Path `penitip_id` |
| `admin/users` | `GET` | Daftar seluruh pengguna | - |
| `admin/users/internal` | `POST` | Pendaftaran petugas kasir baru | `{ nisn_nip, name, password, role: "kasir" }` |
| `admin/users/{id}/approve` | `PUT` | ACC pendaftaran akun user | Path `id` |
| `admin/users/{id}` | `PUT` | Edit data pengguna | Path `id`, Body: `UserData` |

---

## 🔒 Catatan Pelacakan Persisten DataStore

Aplikasi menggunakan `TokenManager` (`DataStore Preferences`) untuk melacak status berikut secara lokal agar tidak hilang saat re-login atau restart aplikasi:

1. `TOKEN_KEY` & `ROLE_KEY`: Token JWT dan peran user.
2. `STARTING_CASH_KEY`: Nominal modal awal shift kasir aktif.
3. `VALIDATED_SHIFTS_KEY`: Set ID shift yang telah divalidasi oleh Admin.
4. `PROCESSED_PAYOUTS_KEY`: Set ID penitip yang telah dicairkan oleh Admin.
5. `APPROVED_USERS_KEY`: Set ID user yang telah di-ACC oleh Admin.

---

## 🚀 Panduan Build & Running

1. Buka proyek **SIPeKa** di Android Studio 2026.1.4 / Ladybug+.
2. Pastikan koneksi internet aktif untuk mendownload dependensi Gradle (`Coil`, `Retrofit`, `DataStore`).
3. Jalankan perintah kompilasi:
   ```bash
   ./gradlew :app:assembleDebug
   ```
4. Jalankan aplikasi pada Emulator atau Perangkat Android Fisik (minSdk 24 / Android 7.0+).

---
*Dokumentasi disajikan untuk proyek SIPeKa SMKN 8 Jakarta.*
