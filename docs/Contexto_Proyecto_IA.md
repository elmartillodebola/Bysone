# Contexto del Proyecto para IA — Mi Portafolio Inteligente

> Este archivo es una copia de la memoria del asistente IA (Claude Code) sobre el proyecto.
> Permite retomar cualquier sesión de trabajo con contexto completo sin repetir explicaciones.
> **Última actualización:** 2026-04-13

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
| Bonus | Cola de mensajes real (RabbitMQ) | +10 |
| Bonus | POO avanzada | +5 |
| Bonus | Object storage | +5 |

> Todos los bonus están incluidos en el diseño desde el inicio. No recortar sin consultar al equipo.

---

## 2. Stack Tecnológico

| Capa | Tecnología | Proveedor |
|------|-----------|-----------|
| Frontend | Next.js 14+ (React, App Router, TypeScript) | Fly.io — PaaS |
| Backend | Java 21 + Spring Boot 3 + Spring WebFlux | Fly.io — Contenedor |
| Base de datos | PostgreSQL 16 + R2DBC (reactivo) | Neon — DBaaS |
| Cola de mensajes | RabbitMQ | Fly.io — Contenedor |
| Autenticación | OAuth2 (Google Workspace + Microsoft 365) | — |
| Migraciones BD | Flyway (V1 schema + V2 semilla) | — |
| CI/CD | GitHub Actions → Fly.io deploy | GitHub |
| Asistentes IA | GitHub Copilot + Claude Code | — |

**Librerías frontend clave:** Tailwind CSS · shadcn/ui · NextAuth.js v5 · Axios · TanStack Query · React Hook Form + Zod · Recharts

**Librerías backend clave:** Spring WebFlux · R2DBC · Spring Security OAuth2 · Spring AMQP · SpringDoc OpenAPI · JUnit 5 + StepVerifier

---

## 3. Arquitectura del Sistema

### Flujo de comunicación

```
Next.js (3000) ──HTTPS──▶ Spring Boot WebFlux API (8080)
                                    │
                     ┌──────────────┼──────────────┐
                     │              │              │
              Neon PostgreSQL   RabbitMQ       Swagger UI
              (R2DBC reactivo)  (broker)       (/swagger-ui.html)
                                    │
                            Notification Worker
                                    │
                               SMTP / Email
```

### CI/CD

```
push develop → GitHub Actions → build + test → deploy staging  (Fly.io)
push main    → GitHub Actions → build + test → deploy producción (Fly.io)
```

### Estructura hexagonal del backend

```
backend/src/main/java/com/proteccion/portafolio/
├── domain/
│   ├── model/           ← Entidades puras (sin Spring)
│   │   ├── usuario/
│   │   ├── perfil/
│   │   ├── simulacion/
│   │   └── calibracion/
│   ├── port/
│   │   ├── in/          ← Interfaces casos de uso
│   │   └── out/         ← Interfaces repos y servicios externos
│   └── service/         ← Motor simulación, scoring calibración
├── application/api/
│   ├── controller/      ← Controllers WebFlux (Mono/Flux)
│   ├── request/
│   └── response/
└── infrastructure/
    ├── persistence/     ← Adaptadores R2DBC
    ├── messaging/       ← Producer/Consumer RabbitMQ
    ├── oauth/           ← Config Spring Security OAuth2
    └── config/          ← Beans, Flyway, SpringDoc
```

> Regla estricta: el dominio no puede importar nada de Spring ni de infrastructure. Los controllers solo orquestan, nunca contienen lógica de negocio.

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
│   ├── ui/              ← shadcn/ui base
│   ├── calibracion/     ← PreguntaCard, BarraProgreso, ResultadoPerfil
│   ├── simulacion/      ← FormularioSimulacion, GraficaProyeccion, HistorialItem
│   ├── perfil/          ← PerfilCard, PortafolioBreakdown
│   └── shared/          ← Header, Navbar, Spinner, ErrorBoundary
├── hooks/
│   ├── useSimulacion.ts ← Estado pre-guardado en memoria (hasta confirmar guardar)
│   └── useCalibracion.ts← Avance del wizard
└── lib/
    ├── api.ts           ← Axios + interceptores JWT
    ├── auth.ts          ← NextAuth (Google + Microsoft)
    ├── types.ts         ← Tipos TypeScript compartidos
    └── queryClient.ts   ← TanStack Query config
```

---

## 4. Modelo de Datos

**DDL:** `backend/src/main/resources/db/migration/V1__create_initial_schema.sql` — 16 tablas
**Semilla:** `backend/src/main/resources/db/migration/V2__datos_semilla.sql` — hasta 5 registros por entidad

### Dominios y tablas

| Dominio | Tablas |
|---------|--------|
| Roles y acceso | `roles_bysone`, `opciones_funcionales_bysone`, `roles_x_opcion_funcional` |
| Parámetros | `parametros_bysone` |
| Inversión | `portafolios_inversion`, `opciones_inversion`, `portafolio_inversion_x_opciones_inversion` |
| Perfiles | `perfiles_inversion`, `perfiles_inversion_x_portafolios_inversion`, `formulas_exposicion` |
| Usuarios | `usuarios`, `usuarios_x_rol` |
| Calibración | `preguntas_calibracion`, `opciones_respuesta_calibracion`, `encuestas_calibracion`, `respuestas_encuesta_calibracion` |
| Simulación | `tipos_plazo`, `disclaimers_bysone`, `simulaciones_bysone`, `detalle_proyeccion_simulacion` |

### Decisiones de diseño del modelo

| Decisión | Detalle |
|----------|---------|
| PKs | BIGSERIAL — auto-incremental, siempre > 0 |
| Perfiles normalizados | Sin grupos repetidos; porcentaje en tabla puente `perfiles_inversion_x_portafolios_inversion` |
| OAuth en usuarios | `oauth_sub VARCHAR(255) UNIQUE` + `proveedor_oauth` con CHECK ('GOOGLE', 'MICROSOFT') |
| Simulación maestro-detalle | Solo persiste si usuario confirma; snapshot de tasas en detalle para reproducibilidad |
| Disclaimers independientes | Tabla propia con `TEXT` y vigencia desde/hasta — no usar `parametros_bysone` para texto legal |
| Plazo con catálogo | `tipos_plazo` con `factor_conversion_dias` normaliza DÍA/MES/TRIMESTRE/AÑO para el motor |
| Calibración | `puntaje_total` persistido al completar; CHECK en `origen` ('DEMANDA','SISTEMA') y `estado` ('PENDIENTE','COMPLETADA') |
| Snapshot perfil | `nombre_perfil_simulado` en simulaciones — el perfil puede cambiar después |
| UNIQUE exposición | `formulas_exposicion` tiene UNIQUE en `(id_perfil_inversion, id_portafolio_inversion)` |

### Datos semilla incluidos

- 3 roles, 5 opciones funcionales, 5 parámetros de sistema
- 3 portafolios, 5 opciones de inversión, 3 perfiles (Conservador / Moderado / Agresivo)
- 4 tipos de plazo, 2 disclaimers (1 activo + 1 histórico)
- 3 usuarios: Ana (admin+usuario, perfil Moderado), Carlos (asesor+usuario, perfil Conservador), María (pendiente calibración)
- 2 simulaciones con proyección calculada matemáticamente por período

---

## 5. Estado de Avance

**Fecha:** 2026-04-13

| Etapa | Estado |
|-------|--------|
| 1. Entendimiento del problema | Completada (4 prompts) |
| 2. Diseño y arquitectura | Completada (10 prompts) |
| 3. Implementación | **Pendiente — siguiente paso** |
| 4. Pruebas y calidad | Pendiente |
| 5. Despliegue y entrega | Pendiente |

### Entregables de la hackaton

| Entregable | Estado |
|-----------|--------|
| Repositorio Git | Creado — github.com/elmartillodebola/Bysone |
| App desplegada | Pendiente |
| Tests unitarios | Pendiente |
| Diseño previo | Completado (modelo datos, reglas, criterios, plan) |
| Prompts utilizados con IA | En curso (14 prompts registrados) |
| Demo en vivo | Pendiente |
| Documentación | En curso (README + 4 docs en docs/) |

### Archivos existentes en el repositorio

| Archivo | Descripción |
|---------|-------------|
| README.md | Punto de entrada, inicio rápido, tabla de docs |
| docs/ARCHITECTURE.md | Stack y servicios en nube |
| docs/Plan_Desarrollo_Bysone.md | Plan 4 fases, roles, stack detallado, riesgos |
| docs/Reglas_Negocio.md | 30 reglas de negocio por dominio |
| docs/Criterios_Aceptacion.md | 68 criterios de aceptación (BD vs APP) |
| docs/Contexto_Proyecto_IA.md | Este archivo |
| backend/.../V1__create_initial_schema.sql | DDL 16 tablas |
| backend/.../V2__datos_semilla.sql | Datos semilla |
| prompts_utilizados.md | 14 prompts registrados |

### Archivos pendientes de crear

- `docs/DEVELOPMENT.md` — guía de desarrollo local y convenciones
- `docs/DEPLOYMENT.md` — configuración Fly.io y pipelines
- `docker-compose.yml` — entorno local completo
- `.env.example` — variables de entorno sin valores
- `frontend/` — proyecto Next.js
- `backend/` — proyecto Spring Boot (solo existe la carpeta de migraciones)
- `broker/` — configuración RabbitMQ
- `.github/workflows/` — pipelines CI/CD

---

## 6. Próximo Paso — Etapa 3: Implementación

Seguir el plan en `docs/Plan_Desarrollo_Bysone.md`, comenzando por la **Fase 0**:

1. **P3:** Crear `docker-compose.yml` y `.env.example`, configurar ramas en GitHub
2. **P1:** Crear proyecto Spring Boot 3 con estructura hexagonal y dependencias WebFlux + R2DBC
3. **P2:** Crear proyecto Next.js 14 con App Router, NextAuth.js y estructura de carpetas
4. Validar que `docker compose up` levanta todos los servicios y Flyway aplica V1 + V2

---

## 7. Convenciones de Trabajo con IA

**Registro de prompts:** Cada tarea relevante debe quedar registrada en `prompts_utilizados.md` con:
- El prompt literal (no parafraseado)
- Qué se obtuvo
- Qué se ajustó
- Numeración por etapa (3.x para Implementación)

**Regla de memoria:** Este archivo debe actualizarse al final de cada sesión de trabajo significativa para que cualquier integrante del equipo pueda retomar con la IA sin perder contexto.

---

> Generado con Claude Code (claude-sonnet-4-6) · Hackaton 2026 · Protección
