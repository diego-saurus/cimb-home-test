# Call Monitoring

Aplikasi web call monitoring yang dibangun dengan **Spring Boot 4** (backend) dan **Vue 3 + Nuxt UI** (frontend), dengan database **PostgreSQL 16**.

Dua jalur instalasi tersedia:

- **Jalur A (Docker Compose, direkomendasikan):** cara tercepat. Ketiga service hidup dengan satu command.
- **Jalur B (IntelliJ IDEA + pnpm):** jalankan backend dan frontend langsung di mesin lokal untuk hot reload dan tooling IDE.

Aplikasi berjalan di **http://localhost:8080**.

---

## 1. Tools Yang Dibutuhkan

| Tool                  | Versi   | Untuk          | Catatan                                                                                              |
| --------------------- | ------- | -------------- | ---------------------------------------------------------------------------------------------------- |
| Docker Desktop        | 24+     | Jalur A        | Sudah termasuk `docker compose`. Cek dengan `docker --version`.                                      |
| JDK                   | 17      | Jalur B (BE)   | -                              |
| IntelliJ IDEA         | 2024+   | Jalur B (BE)   | Community atau Ultimate. Buka folder `backend/` sebagai proyek Maven.                                |
| Node.js               | 22      | Jalur B (FE)   | Dibutuhkan oleh Dockerfile frontend. `node -v` harus menghasilkan `v22.x`.                              |
| pnpm                  | 9+      | Jalur B (FE)   | Instal dengan `npm install -g pnpm` atau cek cara instalasi disini `https://pnpm.io/installation`. |

> Kalau tujuannya cuma mau lihat aplikasinya jalan, instal Docker Desktop dan ikut Jalur A. Lewati sisanya.

---

## 2. Jalur A: Jalankan dengan Docker Compose

Jalur ini menjalankan tiga container: PostgreSQL, backend Spring Boot, dan frontend yang disajikan Nginx. Frontend proxy `/api/*` ke backend, jadi cuma port `8080` yang di expose.

### Step 1: Siapkan file environment

Salin file contoh lalu pilih password Postgres. Docker Compose menolak start kalau `POSTGRES_USER` atau `POSTGRES_PASSWORD` kosong.

```bash
cp .env.example .env
```

Buka `.env` dan ganti placeholder-nya:

```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=replace-with-a-strong-password
POSTGRES_DB=cimb
```

### Step 2: Build dan start stack

Dari root project:

```bash
docker compose up --build -d

# Hapus perintah --build ketika project sudah pernah dijalankan
```

Build pertama mengunduh base image dan mengompilasi backend (Maven) sekaligus frontend (Vite). Butuh sekitar 3 sampai 8 menit, tergantung koneksi. Start berikutnya instan karena image sudah ter-cache.

Tunggu sampai muncul baris seperti `Started CallMonitoringApplication in X.XXX seconds`.

### Step 3: Cek kesehatan container (opsional)

```bash
docker compose ps
```

Akan terlihat tiga service, semuanya ber-status `healthy` (atau `Up` untuk `backend` yang masih menyelesaikan startup):

```
NAME          IMAGE                            STATUS                    PORTS
cm-backend    call-monitoring/backend:local    Up (healthy)              8080/tcp
cm-frontend   call-monitoring/frontend:local   Up (healthy)              0.0.0.0:8080->8080/tcp
cm-postgres   postgres:16-alpine               Up (healthy)              5432/tcp
```

### Step 4: Buka aplikasi

Buka browser ke:

- **http://localhost:8080**, frontend Vue (disajikan Nginx)

Frontend memanggil backend lewat proxy `/api/*`, jadi tidak ada prompt CORS dan tidak ada port kedua yang harus diingat.

### Step 5: Hentikan stack

```bash
docker compose down            # hentikan dan hapus container, volume Postgres tetap
docker compose down -v         # sekalian hapus volume Postgres (mulai dari nol)
```

---

## 3. Jalur B: Development lokal (IntelliJ IDEA + pnpm)

Pakai jalur ini kalau butuh hot reload, debugger penuh, atau berniat kontribusi kode.

### Step 1: Jalankan PostgreSQL

Cara termudah adalah pakai ulang container Postgres dari Compose:

```bash
docker compose up -d postgres
```

Perintah ini cuma menjalankan service `postgres` di jaringan internalnya. Backend lokal akan terkoneksi ke `localhost:5432`.

Verifikasi:

```bash
docker compose ps
```

`cm-postgres` harusnya berstatus `healthy`.

### Step 2: Jalankan backend di IntelliJ IDEA

1. Buka IntelliJ IDEA. Pilih **File → Open…** lalu arahkan ke direktori `backend/` (bukan root project). IntelliJ mendeteksinya sebagai proyek Maven dan mulai import.
2. Tunggu sinkronisasi Maven selesai. Sinkronisasi pertama mengunduh dependency dan butuh beberapa menit.
3. Buka `src/main/java/.../CallMonitoringApplication.java` (atau kelas utama `@SpringBootApplication`).
4. Pastikan **Project SDK** diset ke **JDK 17**. IntelliJ akan meminta kalau belum ada.
5. Buat **Run Configuration**:
   - Klik **Add Configuration…** → **Application**.
   - **Main class:** kelas `@SpringBootApplication`.
   - **Environment variables:** isi nilai dari `backend/.env.example`, misalnya:
     - `DB_URL=jdbc:postgresql://localhost:5432/cimb`
     - `DB_USERNAME=postgres`
     - `DB_PASSWORD=<nilai dari .env di root>`
     - `CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000`
   - **Active profiles:** `local`.
6. Klik **Run**. Backend listen di `http://localhost:8080`.

> Tips: bisa juga dijalankan dari terminal di dalam `backend/`:
>
> ```bash
> SPRING_PROFILES_ACTIVE=local \
> DB_URL=jdbc:postgresql://localhost:5432/cimb \
> DB_USERNAME=postgres \
> DB_PASSWORD=replace-with-a-strong-password \
> CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000 \
> ./mvnw spring-boot:run
> ```

### Step 3: Jalankan frontend dengan pnpm

Buka terminal kedua.

```bash
cd frontend
cp .env.example .env
```

Edit `frontend/.env` supaya frontend bisa menghubungi backend:

```dotenv
VITE_SERVER_URL=http://localhost:8080
```

 Lalu instal dependency dan jalankan Vite:

```bash
pnpm install
pnpm dev
```

Vite menjalankan frontend di **http://localhost:5173**. Konfigurasi CORS backend (di-set lewat `CORS_ALLOWED_ORIGINS`) sudah mengizinkan origin ini.

### Step 4: Verifikasi

- Buka **http://localhost:5173**, app Vue akan load dan memanggil backend di `:8080`.

---

## 4. URL aplikasi

| Mode                      | URL                          | Catatan                                                                  |
| ------------------------- | ---------------------------- | ------------------------------------------------------------------------ |
| Docker Compose            | http://localhost:8080        | Frontend + `/api/*` di-reverse-proxy ke backend oleh Nginx.              |
| Development lokal (Vue)   | http://localhost:5173        | Vite dev server. Backend dipanggil langsung di `:8080`.                  |
| Development lokal (API)   | http://localhost:8080        | -                                                              |
| Postgres                  | localhost:5432               | Database `cimb`, user `postgres`, password dari `.env`.                  |

---


