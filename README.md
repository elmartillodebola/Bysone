# Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Repositorio: https://github.com/elmartillodebola/Bysone

---

## El reto

Protección propuso a su comunidad de desarrollo el siguiente reto:

> *Diseñar y construir una herramienta que le permita a un usuario explorar, simular y comparar distintas estrategias de inversión para su pensión voluntaria, de acuerdo con su perfil de riesgo, y recibir notificaciones asíncronas con los resultados.*

El sistema debe determinar el perfil de inversión del usuario mediante un cuestionario de calibración, mostrar proyecciones de rentabilidad por horizonte de tiempo, y permitir al administrador gestionar todos los parámetros del negocio sin necesidad de redespliegue.

---

## La solución — Mi Portafolio Inteligente

Aplicación web fullstack donde el usuario:

1. **Se autentica** con su cuenta Google (OAuth2).
2. **Calibra su perfil** respondiendo un cuestionario que lo clasifica como Conservador, Moderado o Agresivo.
3. **Simula inversiones** con montos, plazos y frecuencias de aporte, visualizando proyecciones de rentabilidad mínima, media y máxima.
4. **Recibe notificaciones** asíncronas por correo cuando el sistema procesa su simulación.
5. **Gestiona su perfil** editando sus datos personales y revisando su estado de calibración.

El módulo de **Configuración** (solo ADMIN) permite ajustar en caliente parámetros del sistema, perfiles de inversión, portafolios, cuestionario de calibración, disclaimers y más, sin tocar código ni redesplegar.

---

## Equipo — Bysone

El nombre del equipo es **Bysone**, conformado por integrantes de la Comunidad de Desarrollo de Software de Protección.

| Integrante | Rol en el equipo |
|---|---|
| Sofia Sanchez | Integrante |
| Byron Rivas | Integrante |
| Nelson Acevedo | Integrante |

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Frontend | Next.js 14 (App Router) · TypeScript · Tailwind CSS · shadcn/ui · React Query |
| Backend | Java 21 · Spring Boot 3 · Spring MVC · Spring Security · JPA / Hibernate |
| Base de datos | PostgreSQL 16 (Neon — DBaaS) · Flyway (migraciones) |
| Autenticación | OAuth2 Google · JWT (Bearer token) |
| CI/CD | GitHub Actions |
| Despliegue | Fly.io (frontend + backend) · Neon (BD) |

---

## Inicio rápido (local)

### 1. Clonar el repositorio

```bash
git clone https://github.com/elmartillodebola/Bysone.git
cd Bysone
```

### 2. Levantar la base de datos (Docker)

```bash
docker compose up -d postgres
```

La BD queda disponible en `localhost:5433`. Flyway aplica las migraciones automáticamente al iniciar el backend.

### 3. Configurar variables de entorno del backend

El backend toma la configuración del perfil `local` (`application-local.yaml`). Las variables mínimas son la URL de BD y las credenciales OAuth2 de Google.

### 4. Iniciar el backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 5. Configurar variables de entorno del frontend

Crear el archivo `frontend/.env.local` con:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=<secreto-local>
AUTH_TRUST_HOST=true
GOOGLE_CLIENT_ID=<tu-client-id>
GOOGLE_CLIENT_SECRET=<tu-client-secret>
```

### 6. Iniciar el frontend

```bash
cd frontend
npm install
npm run dev
```

| Servicio | URL local |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### 7. Ejecutar tests

```bash
# Backend — todos los tests
cd backend && ./mvnw test

# Backend — suite específica
cd backend && ./mvnw test -Dtest=AdminPerfilServiceTest

# Frontend
cd frontend && npm test
```

---

## Estructura del repositorio

```
Bysone/
├── backend/                        # Spring Boot — API REST
│   ├── src/main/java/com/bysone/
│   │   ├── controller/             # Endpoints REST (admin + usuario)
│   │   ├── service/                # Lógica de negocio
│   │   ├── domain/                 # Entidades JPA
│   │   ├── repository/             # Spring Data JPA
│   │   ├── dto/                    # Request / Response records
│   │   └── security/               # JWT, OAuth2, filtros
│   └── src/main/resources/
│       └── db/migration/           # Migraciones Flyway (V1, V2)
├── frontend/                       # Next.js 14 — App Router
│   └── src/
│       ├── app/(dashboard)/        # Páginas protegidas por sesión
│       │   ├── admin/              # Módulo Configuración (solo ADMIN)
│       │   ├── calibracion/        # Cuestionario de calibración
│       │   ├── perfil/             # Mi Perfil
│       │   └── simulacion/         # Simulador de inversión
│       ├── components/             # Componentes reutilizables
│       ├── hooks/                  # React Query hooks
│       └── lib/                    # Cliente API, tipos, utilidades
├── docs/                           # Documentación del proyecto
└── prompts_utilizados.md           # Registro de prompts IA por etapa
```

---

## Funcionalidades implementadas

### Usuario autenticado

| Funcionalidad | Ruta |
|---|---|
| Inicio de sesión (Google OAuth2) | `/auth/signin` |
| Mi Perfil — datos personales y estado de calibración | `/perfil` |
| Cuestionario de calibración de perfil | `/calibracion` |
| Simulación de inversión con proyecciones | `/simulacion` |
| Historial de simulaciones | `/simulacion/historial` |

### Administrador (rol ADMIN)

| Sección | Ruta | Operaciones |
|---|---|---|
| Usuarios registrados | `/admin/usuarios` | Consulta (solo lectura) |
| Perfiles de inversión | `/admin/perfiles` | CRUD + composición de portafolios + fórmulas de exposición |
| Parámetros del sistema | `/admin/parametros` | Editar valores (timeouts, intervalos, umbrales) |
| Opciones de inversión | `/admin/opciones-inversion` | CRUD |
| Portafolios de inversión | `/admin/portafolios` | CRUD + asignación de opciones |
| Cuestionario de calibración | `/admin/preguntas` | CRUD preguntas + opciones de respuesta |
| Disclaimers legales | `/admin/disclaimers` | CRUD con gestión de vigencia |
| Tipos de plazo | `/admin/tipos-plazo` | CRUD catálogo de unidades de tiempo |
| Roles × Opciones funcionales | `/admin/roles-opciones` | Matriz de asignación de permisos por rol |
| Roles | `/admin/roles` | CRUD catálogo de roles |
| Opciones funcionales | `/admin/opciones-funcionales` | CRUD catálogo de permisos del sistema |

---

## Documentación

### Diseño y arquitectura

| Documento | Descripción |
|---|---|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Stack, servicios en nube, modelo de dominio, decisiones de arquitectura |
| [docs/Plan_Desarrollo_Bysone.md](docs/Plan_Desarrollo_Bysone.md) | Plan de desarrollo por fases, roles del equipo, stack técnico detallado |
| [docs/API_Contracts.md](docs/API_Contracts.md) | Contratos de API — fuente de verdad entre frontend y backend |
| [docs/Lineamientos_frontend.md](docs/Lineamientos_frontend.md) | Principios de diseño visual, componentes y navegación del frontend |

### Reglas y criterios

| Documento | Descripción |
|---|---|
| [docs/Reglas_Negocio.md](docs/Reglas_Negocio.md) | Reglas de negocio organizadas por dominio (RN-PER, RN-USU, RN-CAL…) |
| [docs/Criterios_Aceptacion.md](docs/Criterios_Aceptacion.md) | Criterios de aceptación por entidad con responsabilidad BD vs. aplicación |

### Manuales de usuario

| Documento | Descripción |
|---|---|
| [docs/Manual_Usuario.md](docs/Manual_Usuario.md) | Guía para el usuario final: ingreso, calibración de perfil, simulador de inversión e historial |
| [docs/Manual_Administrador.md](docs/Manual_Administrador.md) | Guía para el administrador: configuración del cuestionario, perfiles, portafolios, parámetros y lógica de cálculo del perfil de inversión |

### Contexto y codificación asistida

| Documento | Descripción |
|---|---|
| [docs/Contexto_Proyecto_IA.md](docs/Contexto_Proyecto_IA.md) | Contexto completo del proyecto para retomar sesiones de IA sin perder estado |
| [docs/Codificacion_asistida_bysone.md](docs/Codificacion_asistida_bysone.md) | Lineamientos y metodología de codificación asistida con IA en el proyecto |
| [prompts_utilizados.md](prompts_utilizados.md) | Registro completo de prompts usados con Claude Code, organizados por etapa (3.1 → 3.21) |

### Base de datos

| Archivo | Descripción |
|---|---|
| [backend/src/main/resources/db/migration/V1__create_initial_schema.sql](backend/src/main/resources/db/migration/V1__create_initial_schema.sql) | DDL completo — 16 tablas con constraints, FKs e índices |
| [backend/src/main/resources/db/migration/V2__datos_semilla.sql](backend/src/main/resources/db/migration/V2__datos_semilla.sql) | Datos semilla — registros de ejemplo por entidad, orden FK garantizado |

---

## Modelo de datos (resumen)

El esquema tiene 16 tablas organizadas en 5 dominios:

| Dominio | Tablas principales |
|---|---|
| Acceso y roles | `roles_bysone`, `opciones_funcionales_bysone`, `roles_x_opcion_funcional` |
| Inversión | `perfiles_inversion`, `portafolios_inversion`, `opciones_inversion`, tablas de composición |
| Usuarios | `usuarios`, `usuarios_x_rol` |
| Calibración | `preguntas_calibracion`, `opciones_respuesta_calibracion`, `encuestas_calibracion` |
| Simulación | `simulaciones_bysone`, `detalle_proyeccion_simulacion`, `tipos_plazo`, `disclaimers_bysone` |

---

> Proyecto desarrollado para la **Hackaton 2026** de la Comunidad de Desarrollo de Software de **Protección S.A.**
> Equipo **Bysone** — Sofia Sanchez · Byron Rivas · Nelson Acevedo
