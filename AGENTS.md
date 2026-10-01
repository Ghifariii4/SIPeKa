# 🤖 AGENTS.md — Panduan & Aturan AI Agentic SIPeKa

> Dokumen ini adalah acuan resmi bagi seluruh **AI Coding Assistant & Autonomous Agents** (seperti Antigravity, Claude, Cursor, Windsurf) saat melakukan pengembangan, pemeliharaan, maupun penambahan fitur pada proyek **SIPeKa** (*Sistem Informasi Pelayanan Kantin SMKN 8 Jakarta*).

---

## 🧭 1. Konteks Proyek & Domain Bisnis

SIPeKa adalah aplikasi Android Native berbasis **Jetpack Compose + Material 3** yang melayani ekosistem kantin sekolah SMKN 8 Jakarta dengan 4 peran pengguna:
1. **Siswa (Pembeli)**: Memesan menu secara pre-order, memperoleh voucher QR Code, memantau riwayat pesanan pribadi, dan mengedit informasi profil siswa mandiri.
2. **Kasir (POS)**: Mengelola shift & modal awal, memproses kasir tunai, menerima antrean pesanan siswa secara real-time, memverifikasi voucher QR, dan menyerahkan makanan dengan proteksi anti-fraud.
3. **Penitip (Mitra Siswa/Guru)**: Memantau sisa stok dan produk terjual, mengecek bagi hasil bersih `(Harga Jual - Rp 1.000) × Terjual`, menambah produk titipan, dan mengelola profil toko titipan.
4. **Admin (Guru Pembina)**: Memvalidasi setoran kas shift kasir, menyetujui pencairan saldo penitip, dan mengelola seluruh akun pengguna sekolah.

---

## ⚡ 2. Aturan Emas Pengembangan (Golden Rules for AI Agents)

Setiap agen yang memodifikasi basis kode ini **WAJIB MEMATUHI ATURAN BERIKUT TANPA KECUALI**:

### 🛡️ ATURAN 1: Pertahankan Seluruh Logika Keamanan & Anti-Fraud (Anti-Jahil)
- **Cegah Pengambilan Ganda (Double Redemption)**: Jangan pernah mengubah atau menghapus pengecekan `isCompleted` di `TokenManager.completeSharedOrder()` atau `PosViewModel.verifyAndCompletePreOrder()`. Tiket QR yang sudah diserahkan harus selalu diblokir jika dicoba klaim ulang.
- **Pengecekan Shift Kasir**: Kasir **tidak boleh** diizinkan menyerahkan makanan jika shift belum dibuka (`isShiftOpen == false`).
- **Validasi Stok Pre-Order**: Pembeli **tidak boleh** dapat memesan produk yang stoknya habis (`stock <= 0`) atau melebihi jumlah stok fisik yang tersedia.
- **Verifikasi Identitas Fisik**: Layar kasir harus selalu menampilkan **Nama Siswa, Kelas, dan NISN** untuk dicocokkan secara lisan oleh petugas kasir.

### 👤 ATURAN 2: Pertahankan Hak Akses Edit Profil Mandiri untuk Semua Peran
- Setiap user (Pembeli, Kasir, Penitip) memiliki hak untuk mengedit profil mereka sendiri:
  - **Siswa/Pembeli**: Nama Lengkap, Kelas, NISN, Password baru.
  - **Kasir**: Nama Petugas, NIP, Password baru.
  - **Penitip**: Nama Pemilik, Nama Toko/Brand Titipan, NIP/Kontak, Password baru.
- Perubahan harus selalu tersimpan secara persisten ke `TokenManager` dan dikirim ke endpoint backend `apiService.updateUser(...)` jika ID pengguna tersedia.

### 🔄 ATURAN 3: Komunikasi Real-time Antar Peran Menggunakan Event Bus Terpusat
- Komunikasi antara pembeli (yang memesan makanan) dan kasir (yang melayani penyerahan) dilakukan secara reaktif melalui `TokenManager.sharedOrdersFlow`.
- Jangan membuat sistem penyimpanan terpisah yang memutus sinkronisasi reaktif antara `PembeliViewModel` dan `PosViewModel`.

### 🎨 ATURAN 4: Konsistensi Desain Visual & Token Warna (Warm Mocha Stitch Canvas)
- Jangan gunakan warna mentah sembarangan (hindari `Color.Red`, `Color.Green`, `Color.Blue` standar browser).
- Selalu gunakan token tema terkurasi di `com.smkn8jkt.sipeka.ui.theme`:
  - `BgDarkEspresso` (`#2B1810`), `BtnDarkChocolate` (`#451A0D`), `BtnMocha` (`#8C5338`), `BgWarmTan` (`#E8D7C8`), `CardCreamWhite` (`#FFFDF9`), `BorderStitch` (`#E4D8CA`), `VibrantOrange` (`#F95721`).

### 🔒 ATURAN 5: Keamanan Repositori Git (Zero Leak Tolerance)
- Jangan pernah melakukan commit file kredensial sensitif: `local.properties`, `*.jks`, `*.keystore`, `.env`, atau secret files.
- Jangan meletakkan token rahasia, API secret, atau sandi pribadi secara *hardcoded* dalam kode Kotlin.
- Pastikan `.gitignore` tetap melindungi file-file kunci penandatanganan dan konfigurasi lokal Android SDK.

---

## 🗂️ 3. Peta Navigasi & File Inti Proyek

| Kebutuhan / Modul | File Utama yang Terkait |
| :--- | :--- |
| **Navigasi & Routing** | `com/smkn8jkt/sipeka/navigation/AppNavigation.kt` |
| **Penyimpanan Persisten & Shared Bus** | `com/smkn8jkt/sipeka/data/remote/TokenManager.kt` |
| **Model Data & DTO** | `com/smkn8jkt/sipeka/data/model/PosModels.kt` |
| **Layar Siswa / Pembeli** | `com/smkn8jkt/sipeka/ui/screens/pembeli/PembeliHomeScreen.kt` & `PembeliViewModel.kt` |
| **Layar Kasir & Antrean QR** | `com/smkn8jkt/sipeka/ui/screens/pos/HomeScreen.kt` & `PosViewModel.kt` |
| **Layar Riwayat & Nota Kasir** | `com/smkn8jkt/sipeka/ui/screens/pos/RiwayatScreen.kt` |
| **Layar Profil Kasir** | `com/smkn8jkt/sipeka/ui/screens/pos/ProfilKasirScreen.kt` |
| **Layar Mitra Penitip** | `com/smkn8jkt/sipeka/ui/screens/penitip/PenitipDashboardScreen.kt` & `PenitipViewModel.kt` |
| **Layar Guru Pembina / Admin** | `com/smkn8jkt/sipeka/ui/screens/admin/AdminDashboardScreen.kt` & `AdminViewModel.kt` |
| **QR Code Bitmap Utility** | `com/smkn8jkt/sipeka/util/QrCodeUtil.kt` |

---

## 🛠️ 4. Panduan Verifikasi Kompilasi Bagi AI Agent

Sebelum menyelesaikan tugas atau memberikan ringkasan kepada pengguna, AI Agent **wajib** memvalidasi bahwa tidak ada *syntax error* atau *broken imports*:

```powershell
# Jalankan validasi kompilasi Kotlin
.\gradlew.bat :app:compileDebugKotlin --no-daemon
```

Jika terjadi galat kompilasi, perbaiki segera sebelum menyerahkan hasil ke pengguna.
