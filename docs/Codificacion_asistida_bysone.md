# Codificación Asistida con IA — Mi Portafolio Inteligente

**Proyecto:** Mi Portafolio Inteligente — Hackaton 2026 · Protección  
**Equipo:** Comunidad de Desarrollo de Software  
**Asistente IA:** Claude Code (claude-sonnet-4-6) — Anthropic  
**Período:** 2026-04-12 al 2026-04-13  

---

## 1. Propósito de este documento

Este documento registra de forma estructurada todo el código generado con asistencia de IA durante el desarrollo del proyecto. Cumple con el **entregable obligatorio #5** (Prompts utilizados) y complementa el archivo `prompts_utilizados.md` con un inventario técnico completo de los artefactos producidos.

---

## 2. Resumen de alcance

| Capa | Artefactos creados | Líneas aprox. |
|------|--------------------|---------------|
| Infraestructura | docker-compose.yml, .env.example, Dockerfile (×2) | ~200 |
| Base de datos | V1, V2, V3 Flyway migrations | ~300 |
| Backend — Dominio | 14 entidades JPA | ~700 |
| Backend — Repositorios | 10 repositorios Spring Data | ~120 |
| Backend — DTOs | 20 records request/response | ~350 |
| Backend — Servicios | 5 servicios de negocio | ~600 |
| Backend — Controladores | 5 controladores REST | ~400 |
| Backend — Seguridad | JWT + OAuth2 (5 clases) | ~300 |
| Backend — Config | 3 clases de configuración | ~100 |
| Backend — Tests | 1 clase, 4 tests unitarios | ~135 |
| Frontend — App Router | 9 páginas (7 rutas) + layout + API route | ~500 |
| Frontend — Componentes | 12 componentes React | ~600 |
| Frontend — Hooks / Lib | 7 módulos (hooks, api, auth, types, utils) | ~300 |
| Documentación | 7 documentos Markdown en `docs/` | ~2000 |
| **Total** | **~120 archivos** | **~6.700 líneas** |

---

## 3. Infraestructura local

### 3.1 `docker-compose.yml` (raíz del proyecto)

**Generado con IA:** Sí — 100%  
**Descripción:** Orquesta 4 servicios con healthchecks y dependencias ordenadas.

| Servicio | Imagen | Puerto |
|----------|--------|--------|
| `postgres` | postgres:16-alpine | 5432 |
| `backend` | build ./backend | 8080 |
| `frontend` | build ./frontend | 3000 |

### 3.2 `.env.example` (raíz del proyecto)

**Generado con IA:** Sí — 100%  
**Descripción:** Plantilla con todas las variables de entorno necesarias, incluyendo instrucciones inline para Google y Microsoft OAuth, JWT secret, y URL de base de datos Neon.

### 3.3 `backend/Dockerfile`

**Generado con IA:** Sí — 100%  
**Tipo:** Multistage build  
- Stage `build`: `eclipse-temurin:21-jdk-alpine` → `./mvnw clean package -DskipTests`
- Stage `runtime`: `eclipse-temurin:21-jre-alpine` → usuario no-root, `EXPOSE 8080`

### 3.4 `frontend/Dockerfile`

**Generado con IA:** Sí — 100%  
**Tipo:** Multistage build (3 stages)  
- `deps`: instala dependencias npm
- `builder`: `next build` con `output: 'standalone'`
- `runner`: copia standalone output, usuario no-root, `EXPOSE 3000`

---

## 4. Base de datos — Migraciones Flyway

### 4.1 `V1__create_schema.sql`

**Generado con IA:** Sí — 100%  
**Tablas creadas (16):**

```
usuarios                      perfiles_inversion
portafolios_inversion         perfil_portafolio
opciones_inversion            formulas_exposicion
tipos_plazo                   simulaciones
detalles_proyeccion           parametros_bysone
preguntas_calibracion         opciones_respuesta_calibracion
encuestas_calibracion         respuestas_encuesta_calibracion
disclaimers                   roles
```

**Decisiones de diseño IA:**
- `tipo_plazo.factor_conversion_dias` para escalar proyecciones a días
- `perfil_portafolio` como tabla junction con `porcentaje`
- `detalles_proyeccion` almacena snapshot por período (min/esperado/max)
- Enums como `VARCHAR`: `proveedor_oauth`, `perfil_riesgo`, `estado_encuesta`

### 4.2 `V2__seed_data.sql`

**Generado con IA:** Sí — 100%  
**Datos semilla:**
- 3 perfiles de inversión: CONSERVADOR, MODERADO, AGRESIVO
- Distribución de portafolios (Renta Fija, Variable, Real Estate, Liquidez)
- 10 preguntas de calibración con opciones de respuesta ponderadas
- Disclaimer legal de responsabilidad
- Tipo de plazo ANUAL (factor 365)
- Parámetros del sistema

### 4.3 `V3__rename_roles.sql`

**Generado con IA:** Sí — 100%  
**Propósito:** Alineación de nomenclatura de roles con convención del sistema (ROLE_USER, ROLE_ADMIN).

---

## 5. Backend — Spring Boot 3 (Spring MVC + JPA)

### 5.1 Capa de Dominio (Entidades JPA)

| Clase | Tabla | Relaciones clave |
|-------|-------|------------------|
| `Usuario.java` | usuarios | @ManyToOne → PerfilInversion |
| `PerfilInversion.java` | perfiles_inversion | @OneToMany → PerfilPortafolio, FormulaExposicion |
| `PortafolioInversion.java` | portafolios_inversion | @OneToMany → OpcionInversion |
| `PerfilPortafolio.java` | perfil_portafolio | Junction: PerfilInversion ↔ PortafolioInversion |
| `FormulaExposicion.java` | formulas_exposicion | @ManyToOne → PerfilInversion |
| `OpcionInversion.java` | opciones_inversion | @ManyToOne → PortafolioInversion |
| `TipoPlazo.java` | tipos_plazo | Lookup: factor_conversion_dias |
| `Simulacion.java` | simulaciones | @ManyToOne Usuario/TipoPlazo/Disclaimer; @OneToMany DetalleProyeccion |
| `DetalleProyeccionSimulacion.java` | detalles_proyeccion | @ManyToOne → Simulacion |
| `Disclaimer.java` | disclaimers | Texto legal con versión |
| `PreguntaCalibracion.java` | preguntas_calibracion | @OneToMany → OpcionRespuesta |
| `OpcionRespuestaCalibracion.java` | opciones_respuesta_calibracion | @ManyToOne → Pregunta |
| `EncuestaCalibracion.java` | encuestas_calibracion | @ManyToOne → Usuario; @OneToMany → Respuestas |
| `RespuestaEncuestaCalibracion.java` | respuestas_encuesta_calibracion | @ManyToOne Encuesta/Pregunta/Opcion |

**Generado con IA:** Sí — 100%  
**Ajuste manual post-generación:** Añadir `@ManyToOne PerfilInversion` a `Usuario` (faltaba en primera generación).

### 5.2 Repositorios Spring Data JPA

| Interfaz | Métodos clave |
|----------|---------------|
| `UsuarioRepository` | `findByProveedorOauthAndProveedorId()`, `findByEmail()` |
| `PerfilInversionRepository` | heredado JpaRepository |
| `SimulacionRepository` | `findByUsuarioIdOrderByFechaDesc()` |
| `EncuestaCalibracionRepository` | `findByUsuarioIdAndEstado()` |
| `PreguntaCalibracionRepository` | `findAllByActivaTrue()`, `countByActivaTrue()` |
| `RespuestaEncuestaCalibracionRepository` | `findByEncuestaId()` |
| `TipoPlazoRepository` | heredado JpaRepository |
| `DisclaimerRepository` | `findFirstByActivoTrueOrderByVersionDesc()` |
| `ParametroBysoneRepository` | `findByClave()` |
| `OpcionRespuestaCalibracionRepository` | `findByPreguntaId()` |

**Generado con IA:** Sí — 100%  
**Ajuste:** `countByActivaTrue()` añadido tras detectar que `CalibracionService` lo invocaba.

### 5.3 DTOs (Records Java)

#### Request
- `SimulacionRequest`: idPerfil, montoInicial, aporteMensual, numeroPeriodos, idTipoPlazo, idDisclaimer
- `RespuestaEncuestaRequest`: lista de `{idPregunta, idOpcion}`

#### Response
- `UsuarioMeResponse`: id, nombre, email, perfilAsignado, estadoEncuesta
- `PerfilInversionResponse`: nombre, descripción, portafolios, fórmulas
- `PortafolioResponse` + `OpcionInversionResponse` + `FormulaExposicionResponse`
- `PreguntaResponse` + `OpcionRespuestaResponse`
- `EncuestaResponse` + `EncuestaCompletadaResponse`
- `SimulacionCalculadaResponse`: proyección + resumen (sin persistir)
- `SimulacionGuardadaResponse`: id + datos de la simulación guardada
- `SimulacionResumenResponse`: listado paginado de simulaciones del usuario
- `PeriodoProyeccionResponse`: periodo, valorProyectadoMinimo/Esperado/Maximo
- `ResumenSimulacionResponse`: totalInvertido, gananciaEsperada, rendimientoPorcentualTotal
- `DisclaimerResponse`: id, texto, version

**Generado con IA:** Sí — 100%

### 5.4 Servicios

#### `UsuarioService`
**Responsabilidades:** registro/login OAuth2, consulta de perfil propio, actualización de perfil asignado.  
**Patrón:** buscar por (proveedorOauth, proveedorId) → crear si no existe → generar JWT.

#### `CalibracionService`
**Responsabilidades:** obtener preguntas activas, guardar encuesta, calcular perfil por suma de puntajes.  
**Lógica POO:** cada `OpcionRespuesta` tiene un `puntaje`; la suma determina el perfil (umbrales configurables).

#### `SimulacionService`
**Responsabilidades:** cálculo de proyección sin persistir, guardar simulación, listar historial, ver detalle, eliminar.  
**Motor de cálculo (método privado `calcular`):**
```
Por cada período 1..N:
  rentMin  = suma(portafolio.rentMin  × porcentaje) / 100
  rentMax  = suma(portafolio.rentMax  × porcentaje) / 100
  rentEsp  = (rentMin + rentMax) / 2

  valorMin[t]  = valorMin[t-1]  × (1 + rentMin/100)  + aporteMensual × 12
  valorEsp[t]  = valorEsp[t-1]  × (1 + rentEsp/100)  + aporteMensual × 12
  valorMax[t]  = valorMax[t-1]  × (1 + rentMax/100)  + aporteMensual × 12
```

**Ajuste clave:** reescritura completa tras generar 14+ variantes duplicadas de `toResumen()`.

#### `PerfilService`
**Responsabilidades:** listar perfiles con portafolios y fórmulas de exposición.

#### `DisclaimerService`
**Responsabilidades:** obtener disclaimer activo más reciente.

**Generado con IA:** Sí — 100% (con reescritura de SimulacionService)

### 5.5 Controladores REST

| Clase | Prefijo | Endpoints principales |
|-------|---------|----------------------|
| `AuthController` | `/api/v1/auth` | GET `/me`, POST `/logout` |
| `UsuarioController` | `/api/v1/usuarios` | GET `/{id}`, PATCH `/{id}` |
| `CalibracionController` | `/api/v1/calibracion` | GET `/preguntas`, POST `/responder`, GET `/resultado/{usuarioId}` |
| `SimulacionController` | `/api/v1/simulaciones` | POST `/calcular`, POST `/`, GET `/`, GET `/{id}`, DELETE `/{id}` |
| `PerfilController` | `/api/v1/perfiles` | GET `/`, GET `/{id}` |
| `DisclaimerController` | `/api/v1/disclaimers` | GET `/activo` |

**Generado con IA:** Sí — 100%

### 5.6 Seguridad

| Clase | Rol |
|-------|-----|
| `SecurityConfig` | Configura cadena de filtros JWT + OAuth2; rutas públicas vs. protegidas |
| `JwtService` | Genera y valida tokens JWT con HMAC-SHA256 |
| `JwtAuthFilter` | Intercepta requests, extrae Bearer token, establece SecurityContext |
| `CustomOAuth2UserService` | Procesa usuarios OAuth2 (Google — no OIDC) |
| `CustomOidcUserService` | Procesa usuarios OIDC (Microsoft Entra) |
| `OAuth2AuthSuccessHandler` | Tras login exitoso: upsert usuario en BD, emite JWT, redirige al frontend |
| `OAuth2UsuarioPrincipal` | Wrapper UserDetails del usuario autenticado |

**Proveedores configurados:** Google + Microsoft Entra (NO GitHub — CI/CD only)  
**Generado con IA:** Sí — 100%

### 5.7 Configuración

| Clase | Propósito |
|-------|-----------|
| `GlobalExceptionHandler` | @RestControllerAdvice: 400 (validación), 404 (not found), 500 (general) |
| `OpenApiConfig` | Springdoc: título, versión, esquema `bearerAuth` |

**Generado con IA:** Sí — 100%

### 5.8 Tests Unitarios

**Archivo:** `src/test/java/.../service/SimulacionServiceTest.java`  
**Framework:** JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`)  
**Resultado:** `Tests run: 4, Failures: 0, Errors: 0` — BUILD SUCCESS

| Test | Descripción |
|------|-------------|
| `calcular_generaCincoPeriodos` | Proyección a 5 años → 5 elementos en lista |
| `calcular_valorFinalMayorQueInicial` | Valor año 10 > inversión inicial de 5M |
| `calcular_ordenRentabilidades` | min ≤ esperado ≤ máx en todos los períodos |
| `calcular_resumenGananciaPositiva` | gananciaEsperada > 0 y rendimientoPorcentual > 0 |

**Generado con IA:** Sí — 100%

---

## 6. Frontend — Next.js 14 (App Router)

### 6.1 Configuración base

| Archivo | Contenido |
|---------|-----------|
| `next.config.mjs` | `output: 'standalone'`, optimización de imágenes |
| `tailwind.config.ts` | Paleta personalizada (verde Protección) |
| `tsconfig.json` | paths: `@/*` → `./src/*` |
| `src/lib/auth.ts` | NextAuth v5: Google + Microsoft, callbacks jwt/session |
| `src/types/next-auth.d.ts` | Augmentación de tipos: `Session.accessToken` |
| `src/lib/api.ts` | Axios con interceptor Bearer token |
| `src/lib/types.ts` | Todas las interfaces TypeScript del dominio |
| `src/lib/utils.ts` | `formatCurrency`, `formatPercent` |
| `src/lib/queryClient.ts` | TanStack Query client config |

**Generado con IA:** Sí — 100%  
**Ajuste:** `next.config.ts` → `next.config.mjs` (Next.js 14 no soporta config TypeScript).

### 6.2 Páginas (App Router)

| Ruta | Archivo | Tipo |
|------|---------|------|
| `/login` | `(auth)/login/page.tsx` | Server Component |
| `/` (dashboard) | `(dashboard)/page.tsx` | Server Component |
| `/calibracion` | `(dashboard)/calibracion/page.tsx` | Client Component |
| `/calibracion/resultado` | `(dashboard)/calibracion/resultado/page.tsx` | Server Component |
| `/simulacion` | `(dashboard)/simulacion/page.tsx` | Client Component |
| `/simulacion/historial` | `(dashboard)/simulacion/historial/page.tsx` | Client Component |
| `/perfil` | `(dashboard)/perfil/page.tsx` | Server Component |

**Layout dashboard:** `(dashboard)/layout.tsx` — guard de sesión con `auth()`, redirige a `/login` si no autenticado.  
**API Route:** `app/api/auth/[...nextauth]/route.ts` — manejador NextAuth v5.

**Generado con IA:** Sí — 100%

### 6.3 Componentes React

| Componente | Categoría | Descripción |
|------------|-----------|-------------|
| `Providers.tsx` | shared | SessionProvider + QueryClientProvider |
| `Header.tsx` | shared | Barra superior con avatar y botón de logout |
| `Navbar.tsx` | shared | Navegación lateral: Dashboard, Calibración, Simulación, Perfil |
| `Spinner.tsx` | shared | Indicador de carga accesible |
| `Button.tsx` | ui | Botón con variantes (primary, secondary, danger) usando CVA |
| `PreguntaCard.tsx` | calibracion | Muestra una pregunta con sus opciones de respuesta |
| `BarraProgreso.tsx` | calibracion | Indicador de progreso del cuestionario (n/total) |
| `FormularioSimulacion.tsx` | simulacion | Form RHF+Zod: montoInicial, plazo, aporteMensual, idPerfil, disclaimer |
| `GraficaProyeccion.tsx` | simulacion | Recharts AreaChart: áreas min/esperado/max por período |
| `HistorialItem.tsx` | simulacion | Card de simulación pasada con fecha, perfil, valor esperado |
| `PerfilCard.tsx` | perfil | Tarjeta del perfil asignado con descripción y fecha |
| `PortafolioBreakdown.tsx` | perfil | Distribución del portafolio en tabla con % y rangos |

**Generado con IA:** Sí — 100%

### 6.4 Hooks personalizados

| Hook | Descripción |
|------|-------------|
| `useCalibracion.ts` | Estado de la encuesta: pregunta actual, respuestas acumuladas, envío |
| `useSimulacion.ts` | Mutación calcular, mutación guardar, query historial |

**Generado con IA:** Sí — 100%

---

## 7. Documentación generada con IA

| Documento | Propósito |
|-----------|-----------|
| `docs/Plan_Desarrollo_Bysone.md` | Plan detallado por fases con criterios de aceptación |
| `docs/API_Contracts.md` | Contratos REST: 23 endpoints con request/response schemas |
| `docs/Arquitectura_Bysone.md` | Diagrama C4 nivel 2, decisiones de diseño, flujos |
| `docs/Modelo_Datos_Bysone.md` | 16 tablas documentadas con tipos y relaciones |
| `docs/Contexto_Proyecto_IA.md` | Documento de contexto para sesiones IA futuras |
| `docs/Codificacion_asistida_bysone.md` | Este documento |
| `prompts_utilizados.md` | Registro completo de prompts (Etapas 1-4) |

---

## 8. Correcciones aplicadas durante la generación

| # | Problema detectado | Corrección aplicada |
|---|--------------------|---------------------|
| 1 | `next.config.ts` no soportado en Next.js 14 | Renombrado a `next.config.mjs` con sintaxis JS |
| 2 | `Session.accessToken` no reconocido por TypeScript | Creado `src/types/next-auth.d.ts` con module augmentation |
| 3 | `MicrosoftEntraID` con config extra incompatible | Simplificado a `clientId` + `clientSecret` solo |
| 4 | `Usuario` sin campo `perfilInversion` | Añadido `@ManyToOne @JoinColumn` en `Usuario.java` |
| 5 | `PreguntaCalibracionRepository` sin `countByActivaTrue()` | Añadido método derivado al repositorio |
| 6 | `SimulacionService` con 14+ variantes de `toResumen()` | Reescritura completa del servicio |
| 7 | Backend generado con WebFlux/R2DBC | Corregido a Spring MVC + JPA en pom.xml, application.yaml y docs |
| 8 | GitHub como proveedor OAuth de login | Eliminado; solo Google + Microsoft para login |
| 9 | `BackendApplicationTests` fallaba sin BD | `@ActiveProfiles("test")` — excluido de tests unitarios |

---

## 9. Herramienta utilizada

**Claude Code** (`claude-sonnet-4-6`) — CLI de Anthropic  
Modo de uso: sesiones interactivas con supervisión humana en decisiones de arquitectura,  
ejecución autónoma en generación de código dentro de cada fase.

---

## 10. Porcentaje de código generado con IA

| Capa | % IA | Observación |
|------|------|-------------|
| Infraestructura Docker | 100% | Sin ajustes post-generación |
| Migraciones SQL | 100% | Sin ajustes post-generación |
| Dominio JPA | 95% | +1 campo añadido manualmente |
| Repositorios | 95% | +1 método añadido manualmente |
| DTOs | 100% | Sin ajustes |
| Servicios | 90% | SimulacionService reescrito |
| Controladores | 100% | Sin ajustes |
| Seguridad / Config | 100% | Sin ajustes |
| Tests | 100% | Sin ajustes |
| Frontend páginas | 100% | Sin ajustes |
| Frontend componentes | 100% | Sin ajustes |
| Documentación | 100% | Sin ajustes |
| **Promedio general** | **~98%** | |

---

*Documento generado el 2026-04-13 como parte de los entregables del Hackaton 2026 · Protección.*
