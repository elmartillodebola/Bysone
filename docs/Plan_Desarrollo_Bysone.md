# Plan de Desarrollo — Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Arquitectura: Hexagonal · Stack: Spring MVC + JPA + Next.js 14 · BD: PostgreSQL 16 (Neon)

---

## Índice

1. [Equipo y roles](#1-equipo-y-roles)
2. [Fase 0 — Infraestructura base](#2-fase-0--infraestructura-base--días-12)
3. [Fase 1 — Backend core](#3-fase-1--backend-core--días-36)
4. [Fase 2 — Features completas](#4-fase-2--features-completas--días-610)
5. [Fase 3 — Integración y pruebas](#5-fase-3--integración-y-pruebas--días-1012)
6. [Fase 4 — Despliegue](#6-fase-4--despliegue--días-1214)
7. [Stack técnico detallado](#7-stack-técnico-detallado)
8. [Dependencias críticas y riesgos](#8-dependencias-críticas-y-riesgos)

---

## 1. Equipo y roles

| Persona | Rol | Responsabilidad principal |
|---------|-----|--------------------------|
| **P1** | Backend — Dominio | Arquitectura hexagonal, lógica de negocio, motor de simulación, APIs REST |
| **P2** | Frontend | Next.js, pantallas, flujos UI, integración con API, gráficas |
| **P3** | DevOps + Backend Infra | Infraestructura, CI/CD, adaptadores JPA / RabbitMQ / OAuth2, soporte backend |

---

## 2. Fase 0 — Infraestructura base · Días 1–2

Trabajo en paralelo. Al finalizar: todos los servicios levantan localmente con `docker compose up`.

### P1 — Esqueleto hexagonal del backend

Crear la estructura de paquetes base en Spring Boot 3:

```
backend/src/main/java/com/proteccion/portafolio/
├── domain/
│   ├── model/           ← Entidades puras (sin anotaciones Spring)
│   │   ├── usuario/
│   │   ├── perfil/
│   │   ├── simulacion/
│   │   └── calibracion/
│   ├── port/
│   │   ├── in/          ← Interfaces de casos de uso (puertos de entrada)
│   │   └── out/         ← Interfaces de repositorios y servicios (puertos de salida)
│   └── service/         ← Implementaciones de dominio (sin dependencias de infra)
├── application/
│   └── api/
│       ├── controller/  ← Controllers Spring MVC (adaptadores de entrada REST)
│       ├── request/     ← DTOs de entrada
│       └── response/    ← DTOs de salida
└── infrastructure/
    ├── persistence/     ← Adaptadores JPA (implementan puertos out)
    ├── messaging/       ← Adaptadores RabbitMQ (producer + consumer)
    ├── oauth/           ← Configuración Spring Security OAuth2
    └── config/          ← Beans, configuración Spring, Flyway
```

### P2 — Esqueleto del frontend

Crear proyecto Next.js 14 con App Router y estructura por feature:

```
frontend/src/
├── app/
│   ├── (auth)/
│   │   └── login/
│   │       └── page.tsx          ← Pantalla de login OAuth2
│   ├── (dashboard)/
│   │   ├── layout.tsx            ← Layout protegido (verifica sesión)
│   │   ├── page.tsx              ← Dashboard / home del usuario
│   │   ├── calibracion/
│   │   │   ├── page.tsx          ← Wizard de encuesta
│   │   │   └── resultado/
│   │   │       └── page.tsx      ← Resultado y perfil asignado
│   │   ├── simulacion/
│   │   │   ├── page.tsx          ← Formulario + gráfica de proyección
│   │   │   └── historial/
│   │   │       └── page.tsx      ← Listado de simulaciones guardadas
│   │   └── perfil/
│   │       └── page.tsx          ← Detalle del perfil y portafolios
│   └── api/auth/[...nextauth]/
│       └── route.ts              ← Handler NextAuth.js
├── components/
│   ├── ui/                       ← Componentes base (shadcn/ui)
│   ├── calibracion/              ← PreguntaCard, BarraProgreso, ResultadoPerfil
│   ├── simulacion/               ← FormularioSimulacion, GraficaProyeccion, HistorialItem
│   ├── perfil/                   ← PerfilCard, PortafolioBreakdown
│   └── shared/                   ← Header, Navbar, Spinner, ErrorBoundary
├── hooks/
│   ├── useSimulacion.ts          ← Lógica de estado de simulación pre-guardado
│   └── useCalibracion.ts         ← Lógica de avance del wizard
└── lib/
    ├── api.ts                    ← Cliente HTTP (Axios + interceptores JWT)
    ├── auth.ts                   ← Configuración NextAuth (Google + Microsoft)
    ├── types.ts                  ← Tipos TypeScript compartidos
    └── queryClient.ts            ← Configuración React Query
```

### P3 — Infraestructura local y repositorio

- Crear `docker-compose.yml` con servicios: PostgreSQL, RabbitMQ, backend, frontend
- Configurar `.env.example` con todas las variables requeridas
- Crear estructura de ramas en GitHub: `main`, `develop`, `feature/*`
- Validar que Flyway aplica V1 y V2 sin errores al levantar el backend
- Configurar Swagger UI (`springdoc-openapi-webflux-ui`) — P2 lo usa como contrato de API

---

## 3. Fase 1 — Backend core · Días 3–6

### P1 — Dominio y casos de uso

**Modelos de dominio** (clases puras, sin dependencias de framework):
- `Usuario`, `PerfilInversion`, `Portafolio`, `OpcionInversion`
- `EncuestaCalibración`, `PreguntaCalibración`, `RespuestaCalibración`
- `Simulacion`, `DetalleProyeccion`

**Puertos de entrada** (interfaces en `domain/port/in/`):
- `ObtenerPerfilesUseCase` — listar perfiles con sus portafolios
- `CalibracionUseCase` — iniciar encuesta, registrar respuestas, completar y calcular perfil
- `SimulacionUseCase` — calcular proyección, guardar si el usuario confirma
- `GestionUsuarioUseCase` — obtener y actualizar datos del usuario

**Servicios de dominio** (implementan los puertos de entrada):
- Motor de simulación: cálculo de `valor_proyectado_min/max/esperado` por período
- Scoring de calibración: suma de puntajes → determinación de perfil según parámetros

**Tests unitarios de dominio** (JUnit 5 + Mockito + StepVerifier):
- Un test por cada método del motor de simulación
- Un test por cada regla de scoring de calibración
- Tests de flujo reactivo con `StepVerifier`

### P3 — Adaptadores de infraestructura

- Repositorios R2DBC para cada entidad (implementan puertos de salida)
- Configuración Spring Security WebFlux:
  - Filtro de validación JWT OAuth2 (`BearerTokenAuthenticationFilter`)
  - Extracción de `oauth_sub` y `proveedor_oauth` del token
  - Creación automática de usuario en primera sesión
- Producer RabbitMQ: publica evento `PerfilActualizadoEvent` al completar calibración
- Consumer RabbitMQ (notification worker): recibe evento → envía email SMTP

---

## 4. Fase 2 — Features completas · Días 6–10

Trabajo en paralelo. P1 expone endpoints; P2 integra desde UI; P3 apoya con tests y configuración.

---

### Módulo Auth

**P1 — API**
- `GET /api/v1/usuarios/me` → devuelve datos del usuario autenticado y su perfil asignado
- Middleware: si es primer login, crear registro en `usuarios`; si tiene perfil y venció calibración, incluir flag `requiere_recalibracion: true`

**P2 — UI**
- `/login`: página con botones "Continuar con Google" y "Continuar con Microsoft"
- Guard en `(dashboard)/layout.tsx`: redirige a `/login` si no hay sesión
- Hook `useSession()` de NextAuth para acceder al token en llamadas API
- Si `requiere_recalibracion: true` → redirigir automáticamente a `/calibracion`

---

### Módulo Calibración

**P1 — API**
- `GET /api/v1/calibracion/preguntas` → lista preguntas activas con sus opciones, ordenadas
- `POST /api/v1/calibracion/encuestas` → crea encuesta con `origen: DEMANDA | SISTEMA`
- `POST /api/v1/calibracion/encuestas/{id}/respuestas` → registra respuesta por pregunta
- `POST /api/v1/calibracion/encuestas/{id}/completar` → calcula puntaje, asigna perfil, actualiza usuario, publica evento RabbitMQ

**P2 — UI**
- Wizard paso a paso: una pregunta por pantalla con barra de progreso (`BarraProgreso`)
- `PreguntaCard`: muestra texto de pregunta y opciones como radio buttons
- Al responder la última pregunta → llamada a `/completar` → pantalla de resultado
- `ResultadoPerfil`: muestra el perfil asignado (Conservador / Moderado / Agresivo), descripción y distribución de portafolios
- Manejo de estado del wizard con `useCalibracion` (pregunta actual, respuestas acumuladas, loading)

**P3**
- Tests unitarios del adaptador R2DBC de calibración
- Validar que el evento RabbitMQ llega al worker y dispara el email

---

### Módulo Perfiles y Portafolios

**P1 — API**
- `GET /api/v1/perfiles` → lista los 3 perfiles con porcentajes y portafolios asociados

**P2 — UI**
- `/perfil`: muestra el perfil actual del usuario con `PortafolioBreakdown` (gráfica de torta o barras con distribución porcentual)
- `PerfilCard`: tarjeta con nombre del perfil, descripción y rango de rentabilidad esperada

---

### Módulo Simulación

**P1 — API**
- `POST /api/v1/simulaciones/calcular` → recibe inputs, devuelve proyección calculada **sin persistir** (`Mono<ProyeccionResponse>`)
- `POST /api/v1/simulaciones` → persiste la simulación + detalle en una transacción reactiva
- `GET /api/v1/simulaciones` → lista simulaciones guardadas del usuario autenticado

**P2 — UI**
- `/simulacion`: pantalla dividida en dos pasos:
  - **Paso 1 — Configurar:** selector de perfil (propio u otro), monto inicial, aporte periódico, plazo + tipo (día/mes/trimestre/año), flag reinversión
  - **Paso 2 — Resultado:** `GraficaProyeccion` (Recharts — 3 líneas: mínimo, esperado, máximo por período) + resumen de inputs + botón "Guardar simulación" / "Descartar"
- Flujo: formulario → llamada a `/calcular` → mostrar gráfica → si guarda → llamada a `POST /simulaciones`
- Estado pre-guardado manejado con `useSimulacion` (en memoria hasta confirmación)
- `/simulacion/historial`: lista de `HistorialItem` con fecha, perfil usado, valor inicial y proyección esperada final; enlace a detalle

**P3**
- Tests unitarios del motor de simulación (fórmula por período, con y sin reinversión)

---

### Módulo Notificaciones

**P3**
- Worker completo: consumer RabbitMQ → template de email HTML → envío SMTP
- Email al completar calibración: informa el perfil asignado y próxima fecha de recalibración

---

## 5. Fase 3 — Integración y pruebas · Días 10–12

**TODOS — Integración local**
- Levantar stack completo con `docker compose up --build`
- Recorrer flujo completo: login → calibración → simulación → guardar → historial → email

**Tests unitarios obligatorios (bonus +10 pts)**

| Capa | Componente | Herramienta |
|------|-----------|-------------|
| Backend — Dominio | Motor de simulación, scoring calibración | JUnit 5 + StepVerifier |
| Backend — Adapters | Repositorios R2DBC, consumer RabbitMQ | JUnit 5 + Mockito |
| Backend — API | Controllers WebFlux | WebTestClient |
| Frontend | `FormularioSimulacion`, `CalibrationWizard` | Jest + Testing Library |
| Frontend | `GraficaProyeccion`, `PerfilCard` | Jest (render + snapshot) |
| Frontend | `useSimulacion`, `useCalibracion` | Jest (hooks testing) |

**Revisión contra criterios de aceptación**
- Validar cada criterio de `docs/Criterios_Aceptacion.md` en el flujo real
- Corrección de bugs de integración

---

## 6. Fase 4 — Despliegue · Días 12–14

### P3 — Configuración Fly.io

```bash
# Crear apps
fly apps create bysone-backend
fly apps create bysone-frontend
fly apps create bysone-broker

# Configurar secretos (una sola vez por app)
fly secrets set --app bysone-backend \
  DATABASE_URL=... \
  RABBITMQ_URL=... \
  GOOGLE_CLIENT_ID=... \
  JWT_SECRET=...

fly secrets set --app bysone-frontend \
  NEXT_PUBLIC_API_URL=https://bysone-backend.fly.dev \
  NEXTAUTH_URL=https://bysone-frontend.fly.dev \
  NEXTAUTH_SECRET=... \
  GOOGLE_CLIENT_ID=... \
  MICROSOFT_CLIENT_ID=...
```

**Variables de entorno frontend por ambiente:**

| Variable | Local | Pruebas | Producción |
|----------|-------|---------|------------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | URL staging Fly.io | URL prod Fly.io |
| `NEXTAUTH_URL` | `http://localhost:3000` | URL staging Fly.io | URL prod Fly.io |
| `NEXTAUTH_SECRET` | valor local | secreto staging | secreto prod |

### Pipelines GitHub Actions

```
push develop → CI (build + test) → deploy staging (Fly.io)
push main    → CI (build + test) → deploy producción (Fly.io)
```

### Smoke tests en staging (TODOS)
- Login con Google y Microsoft
- Completar encuesta de calibración
- Realizar y guardar simulación
- Verificar recepción de email
- Validar historial de simulaciones

### Deploy a producción
- Merge `develop` → `main`
- Pipeline despliega automáticamente
- Validación final antes de demo en vivo

---

## 7. Stack técnico detallado

### Backend

| Componente | Tecnología |
|-----------|------------|
| Framework | Spring Boot 3 + Spring MVC (síncrono/lineal) |
| Base de datos | JPA + `spring-boot-starter-data-jpa` + PostgreSQL JDBC |
| Migraciones | Flyway (V1 schema + V2 semilla + V3 roles) |
| Autenticación | Spring Security + OAuth2 Client + JWT |
| Mensajería | Spring AMQP (RabbitMQ) |
| Documentación API | SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui`) |
| Tests | JUnit 5 + Mockito + MockMvc |

### Frontend

| Componente | Tecnología |
|-----------|------------|
| Framework | Next.js 14 (App Router) |
| Lenguaje | TypeScript |
| Estilos | Tailwind CSS |
| Componentes UI | shadcn/ui |
| Autenticación | NextAuth.js v5 |
| Cliente HTTP | Axios |
| Estado servidor | TanStack Query (React Query) |
| Estado formularios | React Hook Form + Zod |
| Gráficas | Recharts |
| Tests | Jest + Testing Library |

---

## 8. Dependencias críticas y riesgos

### Mapa de dependencias

```
Fase 0 ──────────────────────────────────────────────┐
  P3: docker-compose + ramas                         │
  P1: esqueleto hexagonal                            │
  P2: esqueleto Next.js + Swagger disponible ◄───────┤
         │                                           │
         ▼                                           ▼
    Fase 1                                      Fase 1
  P1: dominio + casos de uso          P3: adaptadores JPA + OAuth2
         │                                           │
         └──────────────┬────────────────────────────┘
                        ▼
                    Fase 2
              Features en paralelo
              (P1 API / P2 UI / P3 tests)
                        │
                        ▼
                    Fase 3
                Integración + tests
                        │
                        ▼
                    Fase 4
                  Despliegue
```

### Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|--------|---------|------------|
| P2 bloqueado esperando endpoints de P1 | Alto | P1 publica contrato OpenAPI al inicio de Fase 1; P2 trabaja con mocks de Axios hasta que los endpoints estén listos |
| Complejidad JPA con esquema multi-tabla | Bajo | JPA estándar con Hibernate, más maduro y documentado que R2DBC |
| OAuth2 con múltiples proveedores | Medio | Configurar y probar Google primero; Microsoft al validar el flujo base |
| Tiempo insuficiente para todos los bonus | Bajo | POO avanzada y cola real están en el diseño desde el inicio; tests y object storage son los que más tiempo toman — priorizar tests |
