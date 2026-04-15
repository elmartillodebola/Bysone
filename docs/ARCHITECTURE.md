# Arquitectura — Mi Portafolio Inteligente

## Índice
- [Servicios en nube](#servicios-en-nube)
- [Stack tecnológico](#stack-tecnológico)
- [Modelo de dominio](#modelo-de-dominio)
- [Decisiones de arquitectura](#decisiones-de-arquitectura)

---

## Servicios en nube

La aplicación adopta un modelo **multinube** con proveedores especializados por capa:

| Capa | Proveedor | Tipo / Modelo |
|------|-----------|---------------|
| Versionamiento | GitHub | SCM (Git) |
| CI/CD | GitHub Actions | CI/CD gestionado |
| Persistencia | Neon | PostgreSQL — DBaaS |
| Cómputo Frontend | Fly.io | PaaS (compute gestionado) |
| Cómputo Backend | Fly.io | Contenedor (PaaS) |
| Gestión de parámetros | Fly.io | Variables de entorno (PaaS config) |
| Dominio / Acceso público | Fly.io | URL pública HTTPS |

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Autenticación | OAuth2 — Gmail y Microsoft |
| Frontend | Next.js |
| Backend | Java con Spring Boot |
| Asistentes IA | GitHub Copilot · Claude |

---

## Modelo de dominio

| Dominio | Entidades | Endpoints API |
|---------|-----------|---------------|
| Auth / Usuarios | `usuarios`, `roles_bysone`, `usuarios_x_rol` | `GET /api/v1/usuarios/me` |
| Calibración | `preguntas_calibracion`, `opciones_respuesta_calibracion`, `encuestas_calibracion`, `respuestas_encuesta_calibracion` | 4 endpoints en `/api/v1/calibracion/*` |
| Perfiles / Portafolios | `perfiles_inversion`, `portafolios_inversion`, `opciones_inversion`, `formulas_exposicion` + tablas puente | `GET /api/v1/perfiles` |
| Simulación | `simulaciones_bysone`, `detalle_proyeccion_simulacion`, `tipos_plazo` | 4 endpoints en `/api/v1/simulaciones/*` |
| Disclaimers | `disclaimers_bysone` | `GET /api/v1/disclaimers/vigente` |

> Detalle completo en [API_Contracts.md](API_Contracts.md) — 11 endpoints, 17 códigos de error, envelope estándar.

---

## Decisiones de arquitectura

| # | Decisión | Justificación |
|---|----------|---------------|
| ADR-01 | Envelope estándar de error con `code` de dominio | Permite al frontend mapear mensajes de error sin parsear texto libre |
| ADR-02 | Cálculo server-side en `POST /simulaciones` (recalcula, no confía en frontend) | Previene manipulación de proyecciones — el frontend solo envía inputs |
| ADR-03 | Snapshot de perfil y tasas en simulación guardada | Reproducibilidad: la simulación es inmutable aunque la configuración del perfil cambie |
| ADR-04 | Paginación estándar Spring (`page`, `size`, `sort`) en listados | Consistencia con el ecosistema Spring Boot; soporte de ordenamiento dinámico |
| ADR-05 | Disclaimer como entidad independiente (no en `parametros_bysone`) | Requiere TEXT, ciclo de vida con vigencia, y trazabilidad legal separada |
| ADR-06 | `camelCase` en JSON, `snake_case` en BD | Convención idiomática para Java/TypeScript en frontend y PostgreSQL en BD |
