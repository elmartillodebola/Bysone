# Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Repositorio: https://github.com/elmartillodebola/Bysone

Mini-aplicación web donde un usuario registrado puede simular y comparar distintos perfiles de inversión
(conservador, moderado, agresivo) para su pensión voluntaria, con visualización de proyecciones y
notificaciones asíncronas por correo.

---

## Inicio rápido (local)

### 1. Clonar el repositorio

```bash
git clone https://github.com/elmartillodebola/Bysone.git
cd Bysone
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con tus credenciales OAuth2 y cadena de conexión Neon
```

### 3. Levantar todos los servicios

```bash
docker compose up --build
```

| Servicio | URL local |
|----------|-----------|
| Frontend (Next.js) | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| RabbitMQ Management | http://localhost:15672 |

### 4. Ejecutar tests

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npm test
```

---

## Documentación

### Diseño y arquitectura

| Documento | Descripción |
|-----------|-------------|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Stack, servicios en nube, modelo de dominio, decisiones de arquitectura |
| [docs/Plan_Desarrollo_Bysone.md](docs/Plan_Desarrollo_Bysone.md) | Plan de desarrollo por fases, roles del equipo, stack técnico detallado |
| [docs/Reglas_Negocio.md](docs/Reglas_Negocio.md) | 30 reglas de negocio organizadas por dominio, derivadas del modelo de datos |
| [docs/Criterios_Aceptacion.md](docs/Criterios_Aceptacion.md) | 68 criterios de aceptación por entidad, con responsabilidad BD vs aplicación |
| [docs/API_Contracts.md](docs/API_Contracts.md) | Contratos de API (11 endpoints, 5 módulos) — fuente de verdad front ↔ back |
| [docs/Contexto_Proyecto_IA.md](docs/Contexto_Proyecto_IA.md) | Contexto completo del proyecto para retomar sesiones con IA sin perder estado |

### Base de datos

| Archivo | Descripción |
|---------|-------------|
| [backend/src/main/resources/db/migration/V1__create_initial_schema.sql](backend/src/main/resources/db/migration/V1__create_initial_schema.sql) | DDL completo — 16 tablas con constraints, FKs e índices |
| [backend/src/main/resources/db/migration/V2__datos_semilla.sql](backend/src/main/resources/db/migration/V2__datos_semilla.sql) | Datos semilla — hasta 5 registros por entidad, orden FK garantizado |

### Guías operativas

| Documento | Descripción |
|-----------|-------------|
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | Guía de desarrollo local, convenciones, flujo de ramas |
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | Configuración Fly.io, secretos, pipeline CI/CD |

### Registro de IA

| Documento | Descripción |
|-----------|-------------|
| [prompts_utilizados.md](prompts_utilizados.md) | Prompts usados con IA durante el desarrollo, organizados por etapa |

---

## Equipo

| Nombre | Rol |
|--------|-----|
| _(por completar)_ | _(por completar)_ |

---

> Proyecto desarrollado en la **Hackaton 2026** de la Comunidad de Desarrollo de Software de Protección.
