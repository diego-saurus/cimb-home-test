# Call Monitoring

A call monitoring web application built with **Spring Boot 4** (backend) and **Vue 3 + Nuxt UI** (frontend), backed by **PostgreSQL 16**.

This README is a tutorial. It walks you through running the application on your machine, end to end. Two paths are provided:

- **Path A — Docker Compose (recommended):** the fastest way. All three services start with one command.
- **Path B — Local development (IntelliJ IDEA + pnpm):** run the backend and frontend directly on your machine for hot reload and IDE tooling.

By the end you will have the application running at **http://localhost:8080**.

---

## 1. Prerequisites

| Tool                  | Version | Required for     | Notes                                                                                                |
| --------------------- | ------- | ---------------- | ---------------------------------------------------------------------------------------------------- |
| Docker Desktop        | 24+     | Path A           | Includes `docker compose`. Verify with `docker --version`.                                           |
| JDK                   | 17      | Path B (backend) | Temurin or any JDK 17 distribution. Backend Dockerfile also uses 17.                                |
| IntelliJ IDEA         | 2024+   | Path B (backend) | Community or Ultimate. Open the `backend/` folder as a Maven project.                                |
| Node.js               | 22      | Path B (frontend) | Required by the frontend Dockerfile. `node -v` should print `v22.x`.                                 |
| pnpm                  | 9+      | Path B (frontend) | Install with `npm install -g pnpm` or `corepack enable && corepack prepare pnpm@latest --activate`.   |
| curl                  | any     | Smoke test       | Used to verify backend and frontend endpoints.                                                       |

> If you only want to see the app run, install Docker Desktop and follow Path A. Skip the rest.

---

## 2. Path A — Run with Docker Compose

This path starts three containers: PostgreSQL, the Spring Boot backend, and the Nginx-served frontend. The frontend proxies `/api/*` to the backend, so only port `8080` is exposed on your host.

### Step 1 — Prepare the environment file

Copy the example file and pick a Postgres password. Docker Compose refuses to start if `POSTGRES_USER` or `POSTGRES_PASSWORD` is empty.

```bash
cp .env.example .env
```

Open `.env` and replace the placeholder:

```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=replace-with-a-strong-password
POSTGRES_DB=cimb
```

### Step 2 — Build and start the stack

From the project root:

```bash
docker compose up --build -d
```

The first build downloads base images and compiles both the backend (Maven) and the frontend (Vite). It typically takes 3–8 minutes depending on your connection. Subsequent starts are instant because the images are cached.

Watch the logs while everything comes up:

```bash
docker compose logs -f backend
```

Wait until you see a line like `Started CallMonitoringApplication in X.XXX seconds`. Press `Ctrl+C` to detach from the log stream — the containers keep running.

### Step 3 — Check container health

```bash
docker compose ps
```

You should see three services, all with status `healthy` (or `Up` for `backend` while it is still finishing its startup window):

```
NAME          IMAGE                            STATUS                    PORTS
cm-backend    call-monitoring/backend:local    Up (healthy)              8080/tcp
cm-frontend   call-monitoring/frontend:local   Up (healthy)              0.0.0.0:8080->8080/tcp
cm-postgres   postgres:16-alpine               Up (healthy)              5432/tcp
```

### Step 4 — Open the application

Open your browser at:

- **http://localhost:8080** — the Vue frontend (served by Nginx)

The frontend calls the backend through the `/api/*` proxy, so there is no CORS prompt and no second port to remember.

### Step 5 — Verify the backend directly (optional)

The backend container also listens on port 8080, but only on the internal Docker network. To smoke-test it, exec into the backend container or use a one-off curl from another container on the same network:

```bash
docker compose exec backend wget -qO- http://127.0.0.1:8080/actuator/health
```

If you need to reach the backend from your host without going through the frontend proxy, add a published port to the `backend` service in `docker-compose.yml` (see "How to expose the backend directly" below).

### Step 6 — Stop the stack

```bash
docker compose down            # stop and remove containers, keep the Postgres volume
docker compose down -v         # also delete the Postgres data volume (fresh start)
```

---

## 3. Path B — Local development (IntelliJ IDEA + pnpm)

Use this path when you want hot reload, full debugger access, or you intend to contribute code.

### Step 1 — Start PostgreSQL

The easiest way is to reuse the Postgres container from the Compose file:

```bash
docker compose up -d postgres
```

This starts only the `postgres` service on its internal network. Your local backend will connect to it on `localhost:5432`.

Verify:

```bash
docker compose ps
```

`cm-postgres` should be `healthy`.

### Step 2 — Run the backend in IntelliJ IDEA

1. Open IntelliJ IDEA. Choose **File → Open…** and select the `backend/` directory (not the project root). IntelliJ detects it as a Maven project and starts importing.
2. Wait for the Maven sync to finish. The first sync downloads dependencies — it can take a few minutes.
3. Open `src/main/java/.../CallMonitoringApplication.java` (or the main `@SpringBootApplication` class).
4. Make sure the **Project SDK** is set to **JDK 17**. IntelliJ prompts you if it is missing.
5. Create a **Run Configuration**:
   - Click **Add Configuration…** → **Application**.
   - **Main class:** the `@SpringBootApplication` class.
   - **Environment variables:** add the values from `backend/.env.example`, for example:
     - `DB_URL=jdbc:postgresql://localhost:5432/cimb`
     - `DB_USERNAME=postgres`
     - `DB_PASSWORD=<the value from your root .env>`
     - `CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000`
   - **Active profiles:** `local`.
6. Click **Run**. The backend listens on `http://localhost:8080`.

> Tip: you can also run from the terminal inside `backend/`:
>
> ```bash
> SPRING_PROFILES_ACTIVE=local \
> DB_URL=jdbc:postgresql://localhost:5432/cimb \
> DB_USERNAME=postgres \
> DB_PASSWORD=replace-with-a-strong-password \
> CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000 \
> ./mvnw spring-boot:run
> ```

### Step 3 — Run the frontend with pnpm

Open a second terminal.

```bash
cd frontend
cp .env.example .env
```

Edit `frontend/.env` so the frontend can reach the backend:

```dotenv
VITE_SERVER_URL=http://localhost:8080
```

Then install dependencies and start Vite:

```bash
pnpm install
pnpm dev
```

Vite serves the frontend at **http://localhost:5173**. The backend's CORS config (set via `CORS_ALLOWED_ORIGINS`) already allows that origin.

### Step 4 — Verify

- Open **http://localhost:5173** — the Vue app loads and calls the backend on `:8080`.
- Optional backend smoke test: `curl http://localhost:8080/actuator/health`.

---

## 4. Application URLs

| Mode                      | URL                          | Notes                                                                 |
| ------------------------- | ---------------------------- | --------------------------------------------------------------------- |
| Docker Compose            | http://localhost:8080        | Frontend + `/api/*` reverse-proxied to backend by Nginx.              |
| Local development (Vue)   | http://localhost:5173        | Vite dev server. Backend reached directly on `:8080`.                 |
| Local development (API)   | http://localhost:8080        | Spring Boot.                                                           |
| Postgres                  | localhost:5432               | Database `cimb`, user `postgres`, password from `.env`.                |

---

## 5. Common operations

### View logs

```bash
docker compose logs -f                  # all services
docker compose logs -f backend         # only the backend
docker compose logs -f frontend        # only the frontend
docker compose logs -f postgres        # only Postgres
```

### Reset the database

```bash
docker compose down -v                 # delete the Postgres volume
docker compose up -d postgres          # start a fresh Postgres
```

Flyway will re-run the migrations in `backend/src/main/resources/db/migration/` on the next backend start.

### Rebuild a single service after changing code

```bash
docker compose build backend           # rebuild the backend image
docker compose up -d backend           # recreate the container with the new image
```

### Run tests

Backend (Maven):

```bash
cd backend
./mvnw test
```

Frontend (Vitest):

```bash
cd frontend
pnpm test
```

---

## 6. Troubleshooting

### `POSTGRES_USER is required` / `POSTGRES_PASSWORD is required` when starting Compose

The `.env` file is missing or has empty values. Copy `.env.example` to `.env` and fill in real values:

```bash
cp .env.example .env
```

### Backend fails to start: `FATAL: password authentication failed for user "postgres"`

The `POSTGRES_PASSWORD` in your root `.env` does not match the one Postgres was initialized with. If you changed the password after the first start, Postgres still uses the old one. Reset the volume:

```bash
docker compose down -v
docker compose up --build -d
```

### Frontend loads but data is empty / `Network Error`

In local development, confirm `frontend/.env` has `VITE_SERVER_URL=http://localhost:8080`. Vite only reads `.env` at startup — restart `pnpm dev` after editing it.

In Docker, the frontend always talks to the backend through the Nginx proxy at `/api`. The proxy resolves `backend` as a Docker DNS name, so it only works inside the Compose network — which is the default.

### Port 8080 already in use

Another application on your machine is bound to port 8080. Either stop it, or change the published port in `docker-compose.yml`:

```yaml
  frontend:
    ports:
      - target: 8080
        published: 9090        # <— change to any free port
        protocol: tcp
        mode: host
```

Then visit `http://localhost:9090` instead.

### How to expose the backend directly

By default the backend is only reachable on the internal Docker network. To publish it on the host, edit `docker-compose.yml` and add:

```yaml
  backend:
    ports:
      - "8080:8080"
```

Restart with `docker compose up -d backend`. Be aware that this also relaxes the network isolation — only do it on a trusted machine.

### CORS error in the browser console (local dev mode)

The backend's `CORS_ALLOWED_ORIGINS` environment variable must include the origin you opened the frontend on. Default Vite origin is `http://localhost:5173`. Set:

```bash
export CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

before starting the backend, and restart it.

---

## 7. Project layout

```
.
├── backend/                  Spring Boot 4 (Java 17)
│   ├── src/main/resources/   application.yaml, application-local.yml, application-prod.yml
│   ├── src/main/resources/db/migration/   Flyway migrations
│   └── Dockerfile            Multi-stage build (Temurin 17)
├── frontend/                 Vue 3 + Nuxt UI + Vite
│   ├── src/                  Application source
│   ├── nginx.conf            Reverse proxy: /api → backend:8080
│   └── Dockerfile            Multi-stage build (Node 22 → nginx-unprivileged)
├── docker-compose.yml        Postgres + backend + frontend
├── .env.example              Template for DB credentials
└── README.md                 You are here
```
