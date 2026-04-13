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
| Tópicos y Colas | Fly.io | Servicio contenerizado (broker) |
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

> _Por definir — se completará con la información del equipo._

---

## Decisiones de arquitectura

> _Por definir — se completará a medida que avance el diseño._
