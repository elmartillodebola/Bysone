# Contexto del Proyecto para IA — Mi Portafolio Inteligente

> Este archivo es una copia de la memoria del asistente IA (Claude Code) sobre el proyecto.
> Permite retomar cualquier sesión de trabajo con contexto completo sin repetir explicaciones.
> **Última actualización:** 2026-04-14

---

## 1. Visión General

**Proyecto:** Mi Portafolio Inteligente
**Evento:** Hackaton 2026 · Comunidad de Desarrollo de Software · Protección
**Repositorio:** https://github.com/elmartillodebola/Bysone

**Qué es:** Mini-aplicación web donde un usuario registrado puede simular y comparar distintos perfiles de inversión (conservador, moderado, agresivo) para su pensión voluntaria, con visualización de proyecciones y notificaciones asíncronas por correo.

**Puntuación objetivo: 120 / 120**

| Tipo | Criterio | Pts |
|------|----------|-----|
| Base | Funcionalidad completa, POO, auth OAuth2, Docker, despliegue en nube | 90 |
| Bonus | Tests unitarios | +10 |
| Bonus | POO avanzada | +5 |
| Bonus | Object storage | +5 |

> Todos los bonus están incluidos en el diseño desde el inicio. No recortar sin consultar al equipo.

---

## 2. Stack Tecnológico

> ⚠️ **IMPORTANTE:** el backend usa **Spring MVC (síncrono/lineal) + JPA/Hibernate**, NO WebFlux ni R2DBC.
> Los contratos de API usan `ResponseEntity<T>` — esto es Spring MVC estándar.

| Capa | Tecnología | Proveedor |
|------|-----------|-----------|
| Frontend | Next.js 14 (React, App Router, TypeScript) | Fly.io — PaaS |
| Backend | Java 21 + Spring Boot 3 + Spring MVC (síncrono) | Fly.io — Contenedor |
| Base de datos | PostgreSQL 16 + JPA/Hibernate (JDBC) | Neon — DBaaS |
| Autenticación | OAuth2 (Google Workspace + Microsoft 365) — NO GitHub | — |
| Migraciones BD | Flyway (V1 schema + V2 semilla + V3 roles) | — |
| CI/CD | GitHub Actions → Fly.io deploy | GitHub |
| Asistentes IA | Claude Code (claude-sonnet-4-6) | — |

**Librerías frontend clave:** Tailwind CSS · shadcn/ui · NextAuth.js v5 · Axios · TanStack Query · React Hook Form + Zod · Recharts

**Librerías backend clave:** Spring MVC · JPA/Hibernate · Spring Security OAuth2 · SpringDoc OpenAPI · JUnit 5 + Mockito · Spring Actuator

---

## 3. Arquitectura del Sistema

### Flujo de comunicación

```
Next.js (3000) ──HTTPS──▶ Spring Boot MVC API (8080)
                                    │
                     ┌──────────────┼──────────────┐
                     │              │              │
              Neon PostgreSQL       Swagger UI
              (JDBC / JPA)          (/swagger-ui.html)
```

### CI/CD

```
push develop → GitHub Actions (ci.yml)  → build + test
push main    → GitHub Actions (ci.yml)  → build + test
                            ↓
              deploy-backend.yml → flyctl deploy (bysone-backend)
              deploy-frontend.yml → flyctl deploy (bysone-frontend)
```

### Estructura real del backend (implementada)

```
backend/src/main/java/com/bysone/backend/
├── domain/               ← Entidades JPA (@Entity)
├── repository/           ← Spring Data JPA interfaces
├── dto/
│   ├── request/          ← Records de entrada
│   └── response/         ← Records de salida
├── service/              ← Lógica de negocio
├── controller/           ← @RestController Spring MVC
├── security/             ← JWT + OAuth2 handlers
├── config/               ← SecurityConfig, OpenApiConfig, GlobalExceptionHandler
```

### Estructura del frontend (Next.js 14 App Router)

```
frontend/src/
├── app/
│   ├── (auth)/login/page.tsx              ← Login OAuth2 (Google + Microsoft)
│   ├── (dashboard)/
│   │   ├── layout.tsx                     ← Guard de sesión
│   │   ├── page.tsx                       ← Dashboard home
│   │   ├── calibracion/page.tsx           ← Wizard encuesta paso a paso
│   │   ├── calibracion/resultado/page.tsx ← Perfil asignado tras calibración
│   │   ├── simulacion/page.tsx            ← Formulario + gráfica proyección
│   │   ├── simulacion/historial/page.tsx  ← Simulaciones guardadas
│   │   └── perfil/page.tsx                ← Detalle perfil y portafolios
│   └── api/auth/[...nextauth]/route.ts    ← Handler NextAuth
├── components/
│   ├── ui/Button.tsx
│   ├── calibracion/  ← PreguntaCard, BarraProgreso
│   ├── simulacion/   ← FormularioSimulacion, GraficaProyeccion, HistorialItem
│   ├── perfil/       ← PerfilCard, PortafolioBreakdown
│   └── shared/       ← Header, Navbar, Spinner, Providers
├── hooks/
│   ├── useSimulacion.ts
│   └── useCalibracion.ts
└── lib/
    ├── api.ts           ← Axios + interceptores JWT
    ├── auth.ts          ← NextAuth v5 (Google + Microsoft)
    ├── types.ts         ← Tipos TypeScript compartidos
    ├── utils.ts         ← formatCurrency, formatPercent
    └── queryClient.ts   ← TanStack Query config
```

---

## 4. Modelo de Datos

**DDL:** `backend/src/main/resources/db/migration/V1__create_initial_schema.sql` — 16 tablas  
**Semilla:** `backend/src/main/resources/db/migration/V2__datos_semilla.sql`  
**Ajuste nomenclatura:** `backend/src/main/resources/db/migration/V3__rename_roles.sql`

### Dominios y tablas

| Dominio | Tablas |
|---------|--------|
| Acceso | `roles` |
| Parámetros | `parametros_bysone` |
| Inversión | `portafolios_inversion`, `opciones_inversion` |
| Perfiles | `perfiles_inversion`, `perfil_portafolio` (junction), `formulas_exposicion` |
| Usuarios | `usuarios` (con FK a `perfiles_inversion`) |
| Calibración | `preguntas_calibracion`, `opciones_respuesta_calibracion`, `encuestas_calibracion`, `respuestas_encuesta_calibracion` |
| Simulación | `tipos_plazo`, `disclaimers`, `simulaciones`, `detalles_proyeccion` |

---

## 5. Estado de Avance

**Fecha:** 2026-04-14

| Etapa | Estado |
|-------|--------|
| 1. Entendimiento del problema | ✅ Completada |
| 2. Diseño y arquitectura | ✅ Completada |
| 3. Implementación — Fase A (infra + frontend) | ✅ Completada |
| 3. Implementación — Fase B (backend) | ✅ Completada |
| 3. Implementación — Fase C (CI/CD + despliegue) | 🔄 En curso |
| 4. Pruebas y calidad | Pendiente |
| 5. Entrega final | Pendiente |

### Entregables de la hackaton

| Entregable | Estado |
|-----------|--------|
| Repositorio Git | ✅ Creado |
| App desplegada | Pendiente — config Fly.io lista, falta `fly apps create` |
| Tests unitarios | ✅ 4 tests SimulacionService (BUILD SUCCESS) |
| Diseño previo | ✅ Completado |
| Prompts utilizados con IA | ✅ 18+ prompts registrados (Etapas 1-3) |
| Demo en vivo | Pendiente |
| Documentación | ✅ 7 docs en `docs/` incluyendo `Codificacion_asistida_bysone.md` |

### Archivos clave generados (Fase B y C)

| Archivo | Descripción |
|---------|-------------|
| `docker-compose.yml` | postgres + backend + frontend |
| `.env.example` | Todas las variables con instrucciones |
| `backend/Dockerfile` | Multistage eclipse-temurin:21 |
| `frontend/Dockerfile` | Multistage node:20-alpine standalone |
| `backend/fly.toml` | Config Fly.io: shared-cpu-1x, health check /actuator/health |
| `frontend/fly.toml` | Config Fly.io: shared-cpu-1x, health check / |
| `.github/workflows/ci.yml` | Build+test backend y frontend en paralelo |
| `.github/workflows/deploy-backend.yml` | Deploy a Fly.io en push a main |
| `.github/workflows/deploy-frontend.yml` | Deploy a Fly.io en push a main |
| `docs/Codificacion_asistida_bysone.md` | Inventario completo de código IA (~120 archivos) |

---

## 6. Próximo Paso — Despliegue supervisado

```bash
# 1. Levantar infraestructura local (supervisado con el equipo)
docker compose up --build

# 2. Crear apps en Fly.io (una sola vez)
fly apps create bysone-backend
fly apps create bysone-frontend

# 3. Configurar secretos de producción
fly secrets set --app bysone-backend DATABASE_URL=... JWT_SECRET=... \
  GOOGLE_CLIENT_ID=... GOOGLE_CLIENT_SECRET=... \
  MICROSOFT_CLIENT_ID=... MICROSOFT_CLIENT_SECRET=... \
  FRONTEND_URL=https://bysone-frontend.fly.dev

fly secrets set --app bysone-frontend \
  NEXTAUTH_SECRET=... NEXTAUTH_URL=https://bysone-frontend.fly.dev \
  NEXT_PUBLIC_API_URL=https://bysone-backend.fly.dev \
  GOOGLE_CLIENT_ID=... GOOGLE_CLIENT_SECRET=... \
  MICROSOFT_CLIENT_ID=... MICROSOFT_CLIENT_SECRET=...

# 4. Primer deploy manual
fly deploy --app bysone-backend --remote-only
fly deploy --app bysone-frontend --remote-only
```

---

## 7. Convenciones de Trabajo con IA

**Registro de prompts:** Cada tarea relevante debe quedar registrada en `prompts_utilizados.md`.

**Regla de memoria:** Este archivo debe actualizarse al final de cada sesión de trabajo significativa.

**Stack no negociable:** Spring MVC + JPA (síncrono). Nunca WebFlux, R2DBC, Mono ni Flux.

**OAuth2:** Solo Google + Microsoft para login de usuarios. GitHub es solo para CI/CD (GITHUB_TOKEN).

---

> Generado con Claude Code (claude-sonnet-4-6) · Hackaton 2026 · Protección
