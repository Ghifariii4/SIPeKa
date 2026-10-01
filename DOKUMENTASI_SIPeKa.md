# Dokumentasi Lengkap Sistem & Arsitektur SIPeKa
**Sistem Informasi Pelayanan Kantin SMKN 8 Jakarta**

> **Versi Dokumentasi**: 2.0 (Edisi Lengkap: Kasir, Pembeli, Penitip, Admin, Real-time Sync & Anti-Fraud)  
> **Target Pengembang**: Tim Developer, Guru Pembina, Administrator, dan AI Coding Agents.

---

## 📌 1. Ringkasan Eksekutif & Tujuan Proyek

**SIPeKa** (*Sistem Informasi Pelayanan Kantin*) adalah aplikasi Android native modern yang dirancang khusus untuk mendigitalkan seluruh ekosistem transaksi, manajemen laci kas, monitoring dagangan titipan siswa/guru, dan pemesanan makanan di SMKN 8 Jakarta.

Aplikasi ini mengintegrasikan **4 peran pengguna** dalam satu ekosistem terpadu:
1. **Siswa (Pembeli)**: Memilih makanan dari katalog kantin, melakukan transaksi pre-order, memperoleh voucher tiket QR Code instan, melacak riwayat pesanan pribadi, dan mengedit informasi profil mandiri.
2. **Kasir (Petugas POS)**: Membuka shift dengan modal awal (Clock-In), melayani transaksi tunai di tempat, memonitor antrean pre-order siswa secara real-time, memverifikasi voucher QR, menyerahkan makanan dengan proteksi anti-fraud, dan menutup shift (Clock-Out) dengan rekonsiliasi kas.
3. **Penitip (Mitra Dagangan Siswa/Guru)**: Mengunggah produk titipan, memantau sisa stok dan produk terjual, mengecek akumulasi bagi hasil bersih, serta mengelola profil toko titipan secara mandiri.
4. **Admin (Guru Pembina / Manajemen Sekolah)**: Mengawasi rekapan kas masuk, memvalidasi dan menyetujui shift kasir, mencairkan bagi hasil titipan, serta mengelola akun seluruh pengguna sekolah.

---

## 🏗️ 2. Arsitektur Teknis & Stack Teknologi

| Komponen | Spesifikasi / Library | Peran dalam Sistem |
| :--- | :--- | :--- |
| **Platform** | Android Native (API Level 24+ s.d 34) | Kompatibel dengan mayoritas smartphone siswa & tablet kasir |
| **Bahasa** | Kotlin (JDK 11) | Null safety, Coroutines, Flow |
| **UI Toolkit** | Jetpack Compose + Material 3 | Declarative UI, animasi fluid, design token kustom |
| **Arsitektur UI** | MVVM (Model-View-ViewModel) | Pemisahan logika bisnis dari UI, StateFlow reaktif |
| **Penyimpanan Lokal** | DataStore Preferences | Penyimpanan JWT, profil peran, riwayat lokal, dan shared pre-orders |
| **Networking** | Retrofit2 + OkHttp3 + Gson | REST API Client dengan Auth Header Interceptor otomatis |
| **Image Loading** | Coil Compose (`io.coil-kt:coil-compose`) | Cache gambar async memory & disk dari backend |
| **Barcode / QR** | ZXing Core (`com.google.zxing:core`) | Generator bitmap QR Code resolusi tinggi untuk tiket pre-order |
| **Navigasi** | Jetpack Navigation Compose | Single Activity NavHost dengan Role-Based Access Control |

---

## 📁 3. Struktur Direktori Proyek

```text
d:/AndroidStudioProjects/SIPeKa/
├── app/
│   ├── src/main/java/com/smkn8jkt/sipeka/
│   │   ├── MainActivity.kt                     # Activity Utama Android
│   │   ├── MainViewModel.kt                    # Helper debugger & inspeksi API
│   │   │
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── AuthModels.kt               # DTO Login, Register, UserData, Role
│   │   │   │   └── PosModels.kt                # DTO Produk, Transaksi, Shift, OrderData, Bagi Hasil
│   │   │   └── remote/
│   │   │       ├── ApiClient.kt                # Instance Retrofit, Logging, Header Interceptor
│   │   │       ├── ApiService.kt               # Kontrak 18+ endpoint REST API backend
│   │   │       └── TokenManager.kt             # DataStore Preferences (Auth, Profil, Shared Orders)
│   │   │
│   │   ├── navigation/
│   │   │   └── AppNavigation.kt                # NavHost, rute layar, otentikasi role guard
│   │   │
│   │   ├── ui/
│   │   │   ├── components/
│   │   │   │   └── SipekaComponents.kt         # Tombol, TextField, Badge, Komponen Reusable
│   │   │   │
│   │   │   ├── screens/
│   │   │   │   ├── admin/                      # Modul Guru Pembina & Admin Sekolah
│   │   │   │   │   ├── AdminDashboardScreen.kt
│   │   │   │   │   └── AdminViewModel.kt
│   │   │   │   ├── auth/                       # Modul Autentikasi Masuk & Daftar
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   └── AuthViewModel.kt
│   │   │   │   ├── pembeli/                    # Modul Siswa / Pembeli Pre-Order
│   │   │   │   │   ├── PembeliHomeScreen.kt    # Beranda, Keranjang, Pesanan Saya, Profil Mandiri
│   │   │   │   │   └── PembeliViewModel.kt     # Logika pemesanan, validasi stok, QR ticket
│   │   │   │   ├── penitip/                    # Modul Siswa / Mitra Penitip Makanan
│   │   │   │   │   ├── PenitipDashboardScreen.kt # Monitoring penjualan, bagi hasil, profil toko
│   │   │   │   │   └── PenitipViewModel.kt
│   │   │   │   ├── pos/                        # Modul Kasir POS Kantin
│   │   │   │   │   ├── HomeScreen.kt           # POS Catalog, Antrean Pre-Order, Scan QR, Shift
│   │   │   │   │   ├── CheckoutBottomSheet.kt  # Modal kembalian tunai & bayar cepat
│   │   │   │   │   ├── RiwayatScreen.kt        # Rekap penjualan kasir, rincian nota & verifikasi
│   │   │   │   │   ├── ProfilKasirScreen.kt    # Profil kasir, edit NIP/nama, jam sistem, Clock-Out
│   │   │   │   │   ├── PosViewModel.kt         # Logika transaksi, shift, dan verifikasi pre-order
│   │   │   │   │   └── SipekaProductCard.kt    # Komponen kartu produk POS
│   │   │   │   └── splash/
│   │   │   │       └── SplashScreen.kt         # Pemeriksaan sesi & routing otomatis
│   │   │   │
│   │   │   └── theme/                          # Design System Tokens
│   │   │       ├── Color.kt                    # Palet Mocha Warm Canvas & Vibrant Orange
│   │   │       ├── Theme.kt                    # Material 3 Theme setup
│   │   │       └── Type.kt                     # Tipografi teks
│   │   │
│   │   └── util/
│   │       └── QrCodeUtil.kt                   # ZXing generator QR Code Bitmap
│   │
│   └── build.gradle.kts                        # Dependensi modul aplikasi
│
├── .agents/
│   └── rules/
│       └── sipeka-architecture.md              # Aturan & konteks resmi untuk AI Coding Agent
├── AGENTS.md                                   # Quick reference panduan AI Agent
├── DOKUMENTASI_SIPeKa.md                       # Dokumentasi teknis komprehensif ini
└── .gitignore                                  # Aturan keamanan repo (anti kebocoran rahasia)
```

---

## 👥 4. Alur Kerja & Spesifikasi Setiap Peran Pengguna

### 1. 🎓 Siswa (Pembeli / Pre-Order)
- **Katalog & Navigasi Beranda**:
  - Filter kategori cepat (*Semua, Makanan, Minuman, Snack*) dan bilah pencarian real-time.
  - Tampilan visual sisa stok: stok normal, stok tipis, dan badge stok habis.
- **Keranjang Belanja & Validasi Stok (Anti-Jahil #1)**:
  - Pembeli **tidak dapat** memesan melebihi stok fisik yang tersedia.
  - Tombol tambah otomatis terkunci saat stok habis untuk mencegah pesanan fiktif/minus.
- **Voucher Pre-Order & QR Code**:
  - Saat checkout, sistem meng-generate kode unik `QR-PREORDER-XXXX` dan ID transaksi.
  - Tiket QR Code di-render secara grafis menggunakan library ZXing `QrCodeUtil`.
  - Data pesanan mencantumkan: Nama Siswa, Kelas, NISN, daftar menu pesanan, dan total harga.
- **Riwayat Pesanan Terisolasi (Anti-Intip Privasi)**:
  - Tab Pesanan Saya hanya menampilkan transaksi milik akun siswa yang sedang login.
  - Tiket QR yang sudah selesai/diambil diberi penanda hijau `SELESAI / DIAMBIL`.
- **Edit Profil Akun Mandiri**:
  - Siswa dapat mengubah Nama Lengkap, Kelas, NISN, dan Password akun secara mandiri.

---

### 2. 🏪 Kasir (Petugas POS Kantin)
- **Manajemen Shift (Clock-In & Clock-Out)**:
  - Sebelum melayani transaksi, kasir **wajib** melakukan Clock-In dengan memasukkan modal awal laci.
  - Jika shift belum dibuka, sistem menonaktifkan transaksi POS dan melarang penyerahan makanan pre-order.
- **Transaksi Tunai Cepat & Kalkulasi Kembalian**:
  - Preset nominal uang cepat (`Uang Pas`, `10rb`, `20rb`, `50rb`, `100rb`).
  - Kalkulasi kembalian otomatis dengan validasi uang kurang.
- **Antrean Pre-Order Siswa Masuk Real-Time**:
  - Badge angka oranye dan banner notifikasi muncul seketika di layar Beranda Kasir jika ada pesanan siswa yang belum diambil.
  - Kasir dapat menekan tombol antrean untuk melihat daftar siswa yang menunggu.
- **Modal Verifikasi Pre-Order & Anti-Fraud**:
  - Kasir dapat mencari pesanan lewat scan/ketik kode QR ataupun memilih dari daftar antrean siswa.
  - Menampilkan identitas lengkap pemesan (**Nama Siswa, Kelas, NISN**) agar kasir dapat mencocokkan secara lisan saat siswa datang ke meja kasir.
  - Tombol aksi `Verifikasi & Serahkan Makanan` langsung mengunci status pesanan menjadi `COMPLETED`.
  - **Proteksi Pengambilan Ganda (Anti-Jahil #2)**: Jika ada pihak yang mencoba mengklaim kembali tiket QR yang sudah diserahkan, sistem menampilkan peringatan keamanan keras:
    > *"⛔ PERINGATAN KEAMANAN: Pesanan sudah pernah diambil sebelumnya! Jangan serahkan makanan ganda."*
- **Edit Profil Kasir**:
  - Kasir dapat mengubah Nama Petugas, NIP, dan Password akun pada tab Profil.

---

### 3. 🍱 Penitip (Mitra Dagangan Siswa / Guru)
- **Monitoring Penjualan Real-Time**:
  - Menampilkan ringkasan sisa stok makanan, jumlah item terjual, dan harga jual per porsi.
- **Kalkulasi Bagi Hasil Otomatis**:
  - Rumus bagi hasil: `(Harga Jual - Rp 1.000 Kas PKK) × Jumlah Terjual`.
  - Kartu pendapatan belum dicairkan (*unpaid earnings*) dengan visual gradien espresso.
- **Manajemen Produk Titipan**:
  - Form penambahan produk dengan unggah foto, harga jual, deskripsi, dan stok awal.
  - Tombol hapus produk titipan dengan konfirmasi keamanan.
- **Edit Profil Toko & Kontak Mandiri**:
  - Penitip dapat mengubah Nama Penanggung Jawab, Nama Usaha/Brand Kantin, NIP/Nomor WhatsApp, dan Password baru.

---

### 4. 👨‍🏫 Admin (Guru Pembina / Manajemen Sekolah)
- **Dashboard Finansial**:
  - Menampilkan total kas laba sekolah dari margin Rp 1.000 per transaksi.
- **Validasi Shift & Setoran Kasir**:
  - Guru Pembina memverifikasi selisih fisik kas akhir kasir sebelum shift ditutup resmi.
- **Pencairan Saldo Penitip**:
  - Daftar permohonan penarikan dana dengan tombol persetujuan transfer/tunai.
- **Manajemen Akun Pengguna**:
  - Menyetujui pendaftaran akun baru, mengubah hak akses (role), mereset kata sandi, dan menonaktifkan akun yang melanggar.

---

## ⚡ 5. Sistem Koneksi Antar Peran Real-Time (Shared State Bus)

SIPeKa menghubungkan pembeli dan kasir menggunakan arsitektur **Shared Reactive Event Bus** yang terintegrasi di `TokenManager.kt`:

```mermaid
sequenceDiagram
    autonumber
    actor Siswa as Siswa (Pembeli)
    participant TM as TokenManager (DataStore)
    actor Kasir as Petugas Kasir
    actor Penitip as Mitra Penitip

    Siswa->>Siswa: Pilih Menu & Cek Stok
    Siswa->>TM: Checkout Pre-Order -> saveSharedOrder(OrderData)
    Note over TM: OrderData tersimpan di SHARED_ORDERS_KEY<br/>Status: PENDING
    TM-->>Kasir: sharedOrdersFlow memancarkan data pesanan baru
    Note over Kasir: Banner Antrean Muncul & Badge Oranye Bertambah
    Siswa->>Kasir: Datang ke konter & tunjukkan QR Code / Sebut Nama & Kelas
    Kasir->>Kasir: Cocokkan Identitas Fisik (Nama, Kelas, Menu)
    Kasir->>TM: completeSharedOrder(orderId, namaKasir)
    alt Pesanan Belum Pernah Diambil
        Note over TM: Status diubah menjadi COMPLETED<br/>Dicatat completed_at & kasir_name
        TM-->>Siswa: sharedOrdersFlow update -> Tiket berubah jadi "SELESAI"
        TM-->>Kasir: Verifikasi sukses! Makanan diserahkan.
        Kasir->>Penitip: Stok berkurang & penjualan tercatat
    else Pesanan SUDAH Pernah Diambil
        TM-->>Kasir: ⛔ BLOKIR! Peringatan Keamanan Pengambilan Ganda
    end
```

---

## 🛡️ 6. Matriks Keamanan & Anti-Fraud (Anti-Jahil)

| Potensi Tindakan Jahil / Fraud | Lapisan Pertahanan SIPeKa | Dampak Sistem |
| :--- | :--- | :--- |
| **Siswa memesan barang yang stoknya sudah habis** | Validasi stok ketat di `PembeliViewModel.checkoutPreOrder()` dan tombol dinonaktifkan di UI. | Checkout ditolak sebelum pesanan dibuat. |
| **Siswa mengambil makanan dua kali dengan screenshot QR** | `TokenManager.completeSharedOrder()` mengecek status `isCompleted`. Sekali diserahkan, token langsung dikunci permanen. | Layar kasir menampilkan peringatan merah keras: *"⛔ PERINGATAN KEAMANAN: Pesanan SUDAH PERNAH DIAMBIL sebelumnya!"*. |
| **Orang lain mengaku-ngaku sebagai pemesan** | Modal kasir menampilkan **Nama Lengkap, Kelas, dan NISN** siswa. Kasir dapat mencocokkan identitas siswa secara verbal sebelum serah-terima. | Mencegah salah serah atau klaim palsu oleh siswa lain. |
| **Penyerahan makanan di luar jam operasional kantin** | `PosViewModel.verifyAndCompletePreOrder()` mewajibkan kasir memiliki shift aktif (`isShiftOpen == true`). | Verifikasi ditolak: *"⛔ Shift kasir belum dibuka! Harap Clock-In terlebih dahulu."* |
| **Melihat atau mengklaim pesanan siswa lain** | `PembeliViewModel.fetchMyOrders()` menyaring daftar hanya untuk ID/NISN akun siswa yang sedang terautentikasi. | Siswa lain tidak dapat mengintip nota maupun kode QR milik orang lain. |

---

## 🔒 7. Keamanan Repositori Git & Pencegahan Kebocoran Data (Git-Leak Prevention)

Untuk memastikan kode aman dipublikasikan ke GitHub publik/privat tanpa membocorkan kredensial:

1. **File yang Dikecualikan (`.gitignore`)**:
   - `local.properties`: Menyembunyikan path SDK Android lokal pengguna.
   - `*.jks`, `*.keystore`, `*.key`, `*.pem`, `*.p12`: Mencegah kebocoran sertifikat signing rilis APK.
   - `.env`, `.env.*`, `secrets.properties`, `credentials.json`, `google-services.json`: Melindungi API key rahasia.
   - `.idea/`, `*.iml`, `.vscode/`: Mengabaikan konfigurasi spesifik mesin pengembang.
   - `build/`, `.gradle/`: Mengabaikan binari kompilasi sementara.
2. **Penyimpanan Token & Password**:
   - Token JWT dan password tidak pernah di-hardcode ke dalam file Java/Kotlin.
   - Seluruh data otentikasi disimpan aman di Android DataStore Preferences berbasis sandbox lokal aplikasi.

---

## 🎨 8. Design System & Token Warna (Warm Mocha Stitch Canvas)

Aplikasi mengadopsi bahasa visual modern bertema kopi hangat (*Warm Espresso & Mocha*) dengan aksen *Vibrant Orange*:

```kotlin
val BgDarkEspresso   = Color(0xFF2B1810) // Header utama & App Bar
val BtnDarkChocolate = Color(0xFF451A0D) // Tombol primer & teks kontras tinggi
val BtnMocha         = Color(0xFF8C5338) // Aksen brand & sub-elemen
val BgWarmTan        = Color(0xFFE8D7C8) // Tag, chip aktif, border lembut
val CardCreamWhite   = Color(0xFFFFFDF9) // Latar belakang kartu kontainer
val BgLightCanvas    = Color(0xFFF7F2EC) // Latar belakang layar penuh
val SegmentBg        = Color(0xFFEFE8DF) // Kontainer toggle tab
val BorderStitch     = Color(0xFFE4D8CA) // Garis batas halus 1dp
val GreenSuccess     = Color(0xFF2E7D32) // Status sukses & lunas
val VibrantOrange    = Color(0xFFF95721) // Aksen utama Pre-Order Siswa
```

---

## 🚀 9. Panduan Membangun & Menjalankan Proyek (Build Guide)

### Prasyarat
- Android Studio Ladybug / Meerkat (atau versi terbaru).
- JDK 11 atau JDK 17 terkonfigurasi pada `JAVA_HOME`.
- Koneksi internet untuk mengunduh dependensi Gradle pada build awal.

### Menjalankan via Terminal
```powershell
# 1. Bersihkan cache build lama
.\gradlew.bat clean

# 2. Kompilasi Kotlin Debug
.\gradlew.bat :app:compileDebugKotlin

# 3. Rakit APK Debug
.\gradlew.bat :app:assembleDebug

# 4. Instal ke Device/Emulator yang aktif
.\gradlew.bat :app:installDebug
```

---

## 🔒 10. Panduan Aman Melakukan Push ke Git / GitHub (Zero-Leak Git Workflow)

Untuk memastikan kode aman dipublikasikan ke GitHub publik maupun privat tanpa risiko kebocoran:

### 📋 Checklist Sebelum Push (Pre-Push Verification)
1. **Periksa File Sensitif**: Pastikan file berikut tidak muncul di daftar `git status`:
   - `local.properties` (berisi direktori SDK komputer pengguna)
   - `*.jks`, `*.keystore` (kunci penandatanganan aplikasi rilis)
   - `.env`, `secrets.properties` (kredensial backend rahasia)
   - Direktori `build/`, `.gradle/`, `.idea/workspace.xml`
2. **Uji Validasi `.gitignore`**:
   Jalankan:
   ```powershell
   git status --ignored -s
   ```
   Pastikan file rahasia bertanda `!!` (ignored), BUKAN `??` (untracked) atau `M` (modified).
3. **Periksa String Rahasia Hardcoded**:
   Pastikan tidak ada token JWT statis, password pengguna, atau API key yang tertulis secara langsung di dalam kode Kotlin.

### 🚀 Perintah Git Rekomendasi
```powershell
# 1. Tinjau perubahan yang siap di-stage
git status

# 2. Tambahkan semua perubahan kode dan dokumentasi
git add .

# 3. Buat commit dengan pesan terstruktur
git commit -m "feat: complete multi-role integration, real-time shared pre-order bus, self-profile editing, and anti-fraud security"

# 4. Push ke repositori remote utama
git push origin master
```

---

## 🔄 11. Siklus Hidup Tiket QR & Verifikasi Pre-Order (QR Lifecycle)

```text
[SISWA: Checkout Pre-Order]
       │
       ▼
 [STATUS: PENDING] ──────────────────────────────────────────────┐
       │                                                         │
       ├─ Tiket tersimpan di DataStore (SHARED_ORDERS_KEY)       │
       ├─ Tiket muncul di tab "Pesanan Saya" (QR & Status Kuning)│ (Real-Time Flow)
       └─ Terpancar ke antrean Kasir POS                         │
                                                                 │
                                                                 ▼
                                                  [KASIR: Notifikasi Antrean]
                                                         │
                                                         ├─ Badge counter oranye bertambah
                                                         └─ Banner antrean siswa tampil di layar
                                                                 │
                                                                 ▼
[SISWA: Datang ke Meja Kasir] ──────────────────────────── [KASIR: Verifikasi Fisik]
- Siswa menunjukkan QR Code                               - Kasir mencocokkan Nama, Kelas, & NISN
- Siswa menyebutkan nama / kelas                          - Kasir menekan "Verifikasi & Serahkan"
                                                                 │
                                                                 ▼
                                                    [SISTEM: Anti-Fraud Check]
                                                      Apakah sudah pernah diambil?
                                                      ├── YA ──> ⛔ BLOKIR! Tolak ganda
                                                      └── TIDAK ─> ✅ PROSES
                                                                     │
                                                                     ▼
                                                             [STATUS: COMPLETED]
                                                                     │
                                                                     ├─ Kasir: Pesanan selesai
                                                                     ├─ Stok Penitip: Berkurang
                                                                     └─ Siswa: Tiket berubah hijau
```

---

## 🌐 12. Daftar Endpoint REST API yang Terhubung

| HTTP Method | Endpoint | Fungsi |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Otentikasi pengguna & penerbitan token JWT |
| `POST` | `/api/auth/register` | Pendaftaran akun baru (Siswa / Penitip) |
| `PUT` | `/api/users/{id}` | Pembaruan profil mandiri (Nama, NISN/NIP, Kelas, Password) |
| `GET` | `/api/products` | Pengambilan daftar katalog produk dan stok kantin |
| `POST` | `/api/products` | Penambahan produk titipan baru oleh Mitra Penitip |
| `DELETE` | `/api/products/{id}` | Penonaktifan / penghapusan produk titipan |
| `GET` | `/api/orders` | Pengambilan seluruh riwayat transaksi |
| `POST` | `/api/orders` | Pembuatan transaksi pesanan / pre-order baru |
| `POST` | `/api/orders/{id}/complete` | Penyelesaian penyerahan makanan pre-order oleh kasir |
| `GET` | `/api/shifts/active` | Pengecekan status shift kasir yang sedang aktif |
| `POST` | `/api/shifts/clock-in` | Pembukaan shift kasir dengan input modal awal laci |
| `POST` | `/api/shifts/clock-out` | Penutupan shift kasir dengan rekonsiliasi kas fisik |

---

*Dokumentasi ini disusun dan dipelihara secara otomatis untuk menjamin integritas arsitektur SIPeKa SMKN 8 Jakarta.*

