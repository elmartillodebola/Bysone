# Contratos de API — Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Contrato entre Frontend (Next.js 14) y Backend (Spring Boot 3).
> Este documento es la fuente de verdad para que P2 (frontend) construya mocks y P1 (backend) implemente los endpoints.
>
> **Última actualización:** 2026-04-13

---

## Índice

1. [Convenciones Globales](#1-convenciones-globales)
2. [Envelope Estándar de Error](#2-envelope-estándar-de-error)
3. [Catálogo de Códigos de Error](#3-catálogo-de-códigos-de-error)
4. [Módulo Auth](#4-módulo-auth)
5. [Módulo Calibración](#5-módulo-calibración)
6. [Módulo Perfiles y Portafolios](#6-módulo-perfiles-y-portafolios)
7. [Módulo Simulación](#7-módulo-simulación)
8. [Módulo Disclaimers](#8-módulo-disclaimers)
9. [Resumen de Endpoints](#9-resumen-de-endpoints)

---

## 1. Convenciones Globales

| Concepto | Valor |
|----------|-------|
| **Base URL** | `/api/v1` |
| **Content-Type** | `application/json` |
| **Autenticación** | `Authorization: Bearer <JWT>` en todos los endpoints (excepto OAuth2 login flow) |
| **Timestamps** | ISO-8601 con zona horaria: `2026-04-13T14:30:00Z` |
| **Decimales monetarios** | 2 decimales — mapeo `DECIMAL(18,2)` del modelo |
| **Decimales porcentuales** | 2 decimales — mapeo `DECIMAL(5,2)` del modelo |
| **Nomenclatura JSON** | `camelCase` (el backend mapea desde `snake_case` de BD) |
| **IDs** | `Long` — `BIGSERIAL` siempre mayor que 0 |
| **Paginación** | Query params `page` (0-based), `size` (default 20), `sort` (e.g., `fechaSimulacion,desc`) |
| **Campos de fecha/timestamp** | Generados por el servidor — no se aceptan del cliente. En JSON usan `camelCase` (`fechaRealizacion`, `fechaSimulacion`, `fechaRegistro`); en BD usan `snake_case` (`fecha_realizacion`, `fecha_simulacion`, `fecha_registro`) |

### Enums del sistema (valores exactos del modelo de datos)

| Enum | Valores válidos |
|------|----------------|
| Proveedor OAuth | `GOOGLE`, `MICROSOFT` |
| Origen encuesta | `DEMANDA`, `SISTEMA` |
| Estado encuesta | `PENDIENTE`, `COMPLETADA` |
| Tipo de plazo | `DÍA`, `MES`, `TRIMESTRE`, `AÑO` |

---

## 2. Envelope Estándar de Error

Todas las respuestas de error del backend siguen esta estructura:

```json
{
  "timestamp": "2026-04-13T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Descripción legible del error",
  "path": "/api/v1/simulaciones/calcular",
  "details": [
    {
      "field": "valorInversionInicial",
      "message": "Debe ser mayor que 0"
    }
  ]
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `timestamp` | `string` (ISO-8601) | Fecha y hora del error |
| `status` | `integer` | Código HTTP |
| `error` | `string` | Nombre estándar HTTP (`Bad Request`, `Unauthorized`, etc.) |
| `code` | `string` | Código de error de aplicación (ver catálogo) |
| `message` | `string` | Descripción en español para mostrar al usuario |
| `path` | `string` | URI del endpoint que generó el error |
| `details` | `array` | (Opcional) Lista de errores de validación por campo |
| `details[].field` | `string` | Nombre del campo con error |
| `details[].message` | `string` | Descripción del error del campo |

---

## 3. Catálogo de Códigos de Error

### Errores generales

| Código | HTTP | Descripción |
|--------|------|-------------|
| `VALIDATION_ERROR` | 400 | Uno o más campos no cumplen las validaciones |
| `UNAUTHORIZED` | 401 | Token JWT ausente, expirado o inválido |
| `FORBIDDEN` | 403 | El usuario no tiene permisos para esta acción |
| `RESOURCE_NOT_FOUND` | 404 | El recurso solicitado no existe |
| `CONFLICT` | 409 | Conflicto de estado (duplicado, estado inválido) |
| `UNPROCESSABLE_ENTITY` | 422 | La solicitud es válida sintácticamente pero viola una regla de negocio |
| `INTERNAL_ERROR` | 500 | Error interno del servidor |

### Errores de dominio

| Código | HTTP | Módulo | Descripción |
|--------|------|--------|-------------|
| `NO_PROFILE_ASSIGNED` | 403 | Simulación | El usuario no tiene perfil de inversión asignado (ver RN-CAL-01) |
| `SURVEY_ALREADY_PENDING` | 409 | Calibración | Ya existe una encuesta `PENDIENTE`. Incluye `encuestaPendiente` con `id`, progreso y `preguntasPendientes[]` con opciones para retomar el wizard (ver CA-ENC-08) |
| `SURVEY_ALREADY_COMPLETED` | 409 | Calibración | La encuesta ya fue completada, no se pueden agregar respuestas (ver CA-REC-04) |
| `QUESTION_ALREADY_ANSWERED` | 409 | Calibración | La pregunta ya fue respondida en esta encuesta (ver CA-REC-01) |
| `OPTION_MISMATCH` | 422 | Calibración | La opción de respuesta no pertenece a la pregunta indicada (ver CA-REC-03) |
| `QUESTION_INACTIVE` | 422 | Calibración | La pregunta no está activa (ver CA-REC-02) |
| `UNANSWERED_QUESTIONS` | 422 | Calibración | No se han respondido todas las preguntas activas (ver RN-CAL-08) |
| `SURVEY_NOT_OWNED` | 403 | Calibración | La encuesta no pertenece al usuario autenticado |
| `DISCLAIMER_NOT_FOUND` | 404 | Simulación | No hay disclaimer vigente o el indicado no existe |
| `DISCLAIMER_NOT_ACTIVE` | 422 | Simulación | El disclaimer referenciado no está vigente (ver CA-SIM-06) |
| `PROFILE_NOT_FOUND` | 404 | Simulación | El perfil de inversión indicado no existe |

---

## 4. Módulo Auth

### 4.1. `GET /api/v1/usuarios/me` — Obtener perfil del usuario autenticado

> **Ref:** RN-USU-01 a RN-USU-05, RN-CAL-03

Devuelve los datos del usuario autenticado, su perfil de inversión asignado (si tiene) y un flag indicando si requiere recalibración.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request

Sin body ni query params. El usuario se identifica por el JWT.

#### Response `200 OK`

```json
{
  "id": 1,
  "nombreCompleto": "Ana García López",
  "correo": "ana.garcia@proteccion.com",
  "celular": "3001234567",
  "proveedorOauth": "GOOGLE",
  "fechaRegistro": "2026-04-01T10:00:00Z",
  "roles": ["USER", "ADMIN"],
  "perfilInversion": {
    "id": 2,
    "nombre": "Moderado",
    "fechaUltimaActualizacion": "2026-04-01T10:30:00Z"
  },
  "requiereRecalibracion": false
}
```

| Campo | Tipo | Nullable | Descripción |
|-------|------|----------|-------------|
| `id` | `long` | No | PK del usuario |
| `nombreCompleto` | `string` | No | Nombre completo (max 200 chars) |
| `correo` | `string` | No | Email único del usuario |
| `celular` | `string` | Sí | Celular (solo dígitos, max 20) |
| `proveedorOauth` | `string` | No | `GOOGLE` \| `MICROSOFT` |
| `fechaRegistro` | `string` | No | ISO-8601 |
| `roles` | `string[]` | No | Lista de nombres de roles (`USER`, `ADMIN`, `MAINTAINER`) |
| `perfilInversion` | `object` | Sí | `null` si no ha completado calibración |
| `perfilInversion.id` | `long` | No | PK del perfil |
| `perfilInversion.nombre` | `string` | No | Nombre del perfil (`Conservador`, `Moderado`, `Agresivo`) |
| `perfilInversion.fechaUltimaActualizacion` | `string` | No | ISO-8601 de la última calibración |
| `requiereRecalibracion` | `boolean` | No | `true` si venció el plazo definido en parámetro `INTERVALO_RECALIBRACION_DIAS` |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |

---

## 5. Módulo Calibración

### 5.1. `GET /api/v1/calibracion/preguntas` — Listar preguntas activas

> **Ref:** RN-CAL-06, CA-PRG-01 a CA-PRG-04, CA-ORC-01 a CA-ORC-05

Devuelve todas las preguntas activas con sus opciones de respuesta, ordenadas por el campo `orden`.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request

Sin body ni query params.

#### Response `200 OK`

```json
[
  {
    "id": 1,
    "textoPregunta": "¿Cómo reaccionaría si su inversión pierde un 10% en un mes?",
    "orden": 1,
    "opciones": [
      {
        "id": 1,
        "textoOpcion": "Retiraría toda mi inversión inmediatamente",
        "orden": 1,
        "puntaje": 1
      },
      {
        "id": 2,
        "textoOpcion": "Esperaría a que se recupere sin hacer cambios",
        "orden": 2,
        "puntaje": 2
      },
      {
        "id": 3,
        "textoOpcion": "Aprovecharía para invertir más a precio bajo",
        "orden": 3,
        "puntaje": 3
      }
    ]
  },
  {
    "id": 2,
    "textoPregunta": "¿Cuál es su horizonte de inversión?",
    "orden": 2,
    "opciones": [
      {
        "id": 4,
        "textoOpcion": "Menos de 1 año",
        "orden": 1,
        "puntaje": 1
      },
      {
        "id": 5,
        "textoOpcion": "Entre 1 y 5 años",
        "orden": 2,
        "puntaje": 2
      },
      {
        "id": 6,
        "textoOpcion": "Más de 5 años",
        "orden": 3,
        "puntaje": 3
      }
    ]
  }
]
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `[].id` | `long` | PK de la pregunta |
| `[].textoPregunta` | `string` | Texto de la pregunta (max 500 chars) |
| `[].orden` | `integer` | Orden de presentación |
| `[].opciones` | `array` | Opciones de respuesta ordenadas |
| `[].opciones[].id` | `long` | PK de la opción |
| `[].opciones[].textoOpcion` | `string` | Texto de la opción (max 300 chars) |
| `[].opciones[].orden` | `integer` | Orden de presentación dentro de la pregunta |
| `[].opciones[].puntaje` | `integer` | Puntaje de la opción (≥ 0) |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |

---

### 5.2. `POST /api/v1/calibracion/encuestas` — Crear encuesta de calibración

> **Ref:** RN-CAL-02, RN-CAL-04, CA-ENC-01 a CA-ENC-08

Crea una nueva encuesta de calibración para el usuario autenticado. Valida que no exista otra encuesta en estado `PENDIENTE`.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request Body

```json
{
  "origen": "DEMANDA"
}
```

| Campo | Tipo | Requerido | Validaciones |
|-------|------|-----------|-------------|
| `origen` | `string` | Sí | `DEMANDA` \| `SISTEMA` |

#### Response `201 Created`

```json
{
  "id": 5,
  "idUsuario": 1,
  "fechaRealizacion": "2026-04-13T14:30:00Z",
  "fechaVencimiento": "2026-10-10T14:30:00Z",
  "origen": "DEMANDA",
  "estado": "PENDIENTE",
  "puntajeTotal": null,
  "perfilResultado": null
}
```

| Campo | Tipo | Nullable | Descripción |
|-------|------|----------|-------------|
| `id` | `long` | No | PK de la encuesta |
| `idUsuario` | `long` | No | PK del usuario |
| `fechaRealizacion` | `string` | No | ISO-8601 (generada por el servidor) |
| `fechaVencimiento` | `string` | Sí | ISO-8601, calculada automáticamente por el backend sumando el parámetro `INTERVALO_RECALIBRACION_DIAS` a `fechaRealizacion`. `null` si el parámetro no está configurado |
| `origen` | `string` | No | `DEMANDA` \| `SISTEMA` |
| `estado` | `string` | No | Siempre `PENDIENTE` al crear |
| `puntajeTotal` | `integer` | Sí | `null` hasta completar |
| `perfilResultado` | `object` | Sí | `null` hasta completar |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 400 | `VALIDATION_ERROR` | `origen` ausente o valor inválido |
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 409 | `SURVEY_ALREADY_PENDING` | Ya existe una encuesta `PENDIENTE` para este usuario |

**Ejemplo de error 409:**

```json
{
  "timestamp": "2026-04-13T14:30:00Z",
  "status": 409,
  "error": "Conflict",
  "code": "SURVEY_ALREADY_PENDING",
  "message": "Ya existe una encuesta de calibración pendiente. Complete o descarte la encuesta actual antes de crear una nueva.",
  "path": "/api/v1/calibracion/encuestas",
  "details": [],
  "encuestaPendiente": {
    "id": 3,
    "fechaRealizacion": "2026-04-13T10:00:00Z",
    "origen": "DEMANDA",
    "preguntasRespondidas": 1,
    "preguntasTotales": 2,
    "preguntasPendientes": [
      {
        "id": 2,
        "textoPregunta": "¿Cuál es su horizonte de inversión?",
        "orden": 2,
        "opciones": [
          {
            "id": 4,
            "textoOpcion": "Menos de 1 año",
            "orden": 1
          },
          {
            "id": 5,
            "textoOpcion": "Entre 1 y 5 años",
            "orden": 2
          },
          {
            "id": 6,
            "textoOpcion": "Más de 5 años",
            "orden": 3
          }
        ]
      }
    ]
  }
}
```

| Campo `encuestaPendiente` | Tipo | Descripción |
|---------------------------|------|-------------|
| `id` | `long` | PK de la encuesta pendiente — usar como `{idEncuesta}` en `/respuestas` y `/completar` |
| `fechaRealizacion` | `string` | ISO-8601, cuándo se creó la encuesta |
| `origen` | `string` | `DEMANDA` \| `SISTEMA` |
| `preguntasRespondidas` | `integer` | Cantidad de preguntas ya respondidas (para barra de progreso) |
| `preguntasTotales` | `integer` | Total de preguntas activas |
| `preguntasPendientes` | `array` | Preguntas activas **sin responder**, ordenadas por `orden`. Misma estructura que `GET /calibracion/preguntas` pero sin `puntaje` en opciones (no exponer puntajes al reanudar) |
| `preguntasPendientes[].id` | `long` | PK de la pregunta — usar como `idPregunta` en el body de `/respuestas` |
| `preguntasPendientes[].textoPregunta` | `string` | Texto de la pregunta |
| `preguntasPendientes[].orden` | `integer` | Orden de presentación |
| `preguntasPendientes[].opciones` | `array` | Opciones de respuesta disponibles |
| `preguntasPendientes[].opciones[].id` | `long` | PK de la opción — usar como `idOpcionRespuesta` en el body de `/respuestas` |
| `preguntasPendientes[].opciones[].textoOpcion` | `string` | Texto de la opción |
| `preguntasPendientes[].opciones[].orden` | `integer` | Orden de presentación |

> **Nota para el frontend:**
> 1. Usar `encuestaPendiente.id` como path param en `POST /encuestas/{id}/respuestas`.
> 2. Iterar `preguntasPendientes` ordenadas por `orden` para retomar el wizard exactamente donde quedó.
> 3. Los campos `preguntasRespondidas` y `preguntasTotales` permiten renderizar la barra de progreso sin cálculos adicionales.
> 4. Las opciones **no incluyen `puntaje`** para no exponer la lógica de scoring al usuario durante el flujo de reanudación.

---

### 5.3. `POST /api/v1/calibracion/encuestas/{idEncuesta}/respuestas` — Registrar respuesta

> **Ref:** RN-CAL-05, CA-REC-01 a CA-REC-04

Registra la respuesta de una pregunta dentro de una encuesta. Solo una respuesta por pregunta por encuesta.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Path Parameters

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `idEncuesta` | `long` | PK de la encuesta |

#### Request Body

```json
{
  "idPregunta": 1,
  "idOpcionRespuesta": 3
}
```

| Campo | Tipo | Requerido | Validaciones |
|-------|------|-----------|-------------|
| `idPregunta` | `long` | Sí | Debe existir y estar activa |
| `idOpcionRespuesta` | `long` | Sí | Debe pertenecer a la pregunta indicada |

#### Response `201 Created`

```json
{
  "id": 10,
  "idEncuesta": 5,
  "idPregunta": 1,
  "idOpcionRespuesta": 3,
  "textoPregunta": "¿Cómo reaccionaría si su inversión pierde un 10% en un mes?",
  "textoOpcionSeleccionada": "Aprovecharía para invertir más a precio bajo",
  "puntaje": 3
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `long` | PK de la respuesta |
| `idEncuesta` | `long` | PK de la encuesta |
| `idPregunta` | `long` | PK de la pregunta |
| `idOpcionRespuesta` | `long` | PK de la opción seleccionada |
| `textoPregunta` | `string` | Texto de la pregunta (para confirmación visual) |
| `textoOpcionSeleccionada` | `string` | Texto de la opción elegida |
| `puntaje` | `integer` | Puntaje de la opción seleccionada |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 400 | `VALIDATION_ERROR` | Campos requeridos ausentes o con tipo incorrecto |
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 403 | `SURVEY_NOT_OWNED` | La encuesta no pertenece al usuario autenticado |
| 404 | `RESOURCE_NOT_FOUND` | La encuesta, pregunta u opción no existe |
| 409 | `QUESTION_ALREADY_ANSWERED` | La pregunta ya fue respondida en esta encuesta |
| 409 | `SURVEY_ALREADY_COMPLETED` | La encuesta ya está en estado `COMPLETADA` |
| 422 | `OPTION_MISMATCH` | La opción de respuesta no pertenece a la pregunta indicada |
| 422 | `QUESTION_INACTIVE` | La pregunta no está activa (`activa = FALSE`) |

**Ejemplo de error 409 (pregunta ya respondida):**

```json
{
  "timestamp": "2026-04-13T14:35:00Z",
  "status": 409,
  "error": "Conflict",
  "code": "QUESTION_ALREADY_ANSWERED",
  "message": "La pregunta 1 ya fue respondida en esta encuesta. No se permite modificar respuestas.",
  "path": "/api/v1/calibracion/encuestas/5/respuestas",
  "details": [
    {
      "field": "idPregunta",
      "message": "Ya existe una respuesta registrada para esta pregunta en la encuesta 5"
    }
  ]
}
```

**Ejemplo de error 422 (opción no pertenece a la pregunta):**

```json
{
  "timestamp": "2026-04-13T14:35:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "code": "OPTION_MISMATCH",
  "message": "La opción de respuesta 6 no pertenece a la pregunta 1.",
  "path": "/api/v1/calibracion/encuestas/5/respuestas",
  "details": [
    {
      "field": "idOpcionRespuesta",
      "message": "La opción seleccionada pertenece a la pregunta 2, no a la pregunta 1"
    }
  ]
}
```

---

### 5.4. `POST /api/v1/calibracion/encuestas/{idEncuesta}/completar` — Completar encuesta y asignar perfil

> **Ref:** RN-CAL-07, RN-CAL-08, CA-ENC-05 a CA-ENC-07

Calcula el puntaje total, determina el perfil de inversión según los parámetros del sistema, actualiza la encuesta y el perfil del usuario. Publica evento RabbitMQ para notificación por email.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Path Parameters

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `idEncuesta` | `long` | PK de la encuesta |

#### Request Body

Sin body. Toda la información se obtiene de las respuestas ya registradas.

#### Response `200 OK`

```json
{
  "id": 5,
  "idUsuario": 1,
  "fechaRealizacion": "2026-04-13T14:30:00Z",
  "fechaVencimiento": "2026-10-10T14:30:00Z",
  "origen": "DEMANDA",
  "estado": "COMPLETADA",
  "puntajeTotal": 5,
  "perfilResultado": {
    "id": 2,
    "nombre": "Moderado"
  },
  "resumen": {
    "totalPreguntas": 2,
    "totalRespondidas": 2,
    "puntajeMaximoPosible": 6
  }
}
```

| Campo | Tipo | Nullable | Descripción |
|-------|------|----------|-------------|
| `id` | `long` | No | PK de la encuesta |
| `idUsuario` | `long` | No | PK del usuario |
| `fechaRealizacion` | `string` | No | ISO-8601 (generada por el servidor) |
| `fechaVencimiento` | `string` | Sí | ISO-8601, calculada automáticamente por el backend. `null` si el parámetro no está configurado |
| `origen` | `string` | No | `DEMANDA` \| `SISTEMA` |
| `estado` | `string` | No | `COMPLETADA` tras procesar |
| `puntajeTotal` | `integer` | No | Suma de puntajes de las opciones seleccionadas |
| `perfilResultado` | `object` | No | Perfil asignado |
| `perfilResultado.id` | `long` | No | PK del perfil |
| `perfilResultado.nombre` | `string` | No | `Conservador` \| `Moderado` \| `Agresivo` |
| `resumen.totalPreguntas` | `integer` | No | Total de preguntas activas |
| `resumen.totalRespondidas` | `integer` | No | Preguntas respondidas en esta encuesta |
| `resumen.puntajeMaximoPosible` | `integer` | No | Puntaje máximo alcanzable |

**Lógica de asignación de perfil** (configurable vía `parametros_bysone`):

| Parámetro | Valor semilla | Rango de puntaje | Perfil asignado |
|-----------|---------------|-------------------|-----------------|
| `PUNTAJE_PERFIL_CONSERVADOR_MAX` | 2 | 0 – 2 | Conservador |
| `PUNTAJE_PERFIL_MODERADO_MAX` | 4 | 3 – 4 | Moderado |
| (todo lo demás) | — | 5+ | Agresivo |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 403 | `SURVEY_NOT_OWNED` | La encuesta no pertenece al usuario autenticado |
| 404 | `RESOURCE_NOT_FOUND` | La encuesta no existe |
| 409 | `SURVEY_ALREADY_COMPLETED` | La encuesta ya fue completada previamente |
| 422 | `UNANSWERED_QUESTIONS` | No se han respondido todas las preguntas activas |

**Ejemplo de error 422 (preguntas sin responder):**

```json
{
  "timestamp": "2026-04-13T14:40:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "code": "UNANSWERED_QUESTIONS",
  "message": "Faltan 1 pregunta(s) por responder antes de completar la encuesta.",
  "path": "/api/v1/calibracion/encuestas/5/completar",
  "details": [
    {
      "field": "preguntas",
      "message": "Las preguntas con id [2] no han sido respondidas"
    }
  ]
}
```

---

## 6. Módulo Perfiles y Portafolios

### 6.1. `GET /api/v1/perfiles` — Listar perfiles de inversión

> **Ref:** RN-PER-01 a RN-PER-04, RN-POR-01 a RN-POR-03

Devuelve los perfiles de inversión con su distribución porcentual de portafolios, opciones de inversión y fórmulas de exposición.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request

Sin body ni query params.

#### Response `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Conservador",
    "rentabilidadMinima": 3.00,
    "rentabilidadMedia": 4.00,
    "rentabilidadMaxima": 5.00,
    "portafolios": [
      {
        "id": 1,
        "nombre": "Renta Fija",
        "descripcion": "Portafolio de instrumentos de renta fija con bajo riesgo",
        "porcentaje": 70.00,
        "rentabilidadMinima": 3.00,
        "rentabilidadMaxima": 5.00,
        "formulaExposicion": {
          "id": 1,
          "umbralPorcentajeMin": 60.00,
          "umbralPorcentajeMax": 80.00
        },
        "opcionesInversion": [
          {
            "id": 1,
            "nombre": "Bonos del Tesoro",
            "descripcion": "Deuda soberana a largo plazo",
            "rentabilidadMinima": 3.50,
            "rentabilidadMaxima": 5.50
          },
          {
            "id": 2,
            "nombre": "CDTs",
            "descripcion": "Certificados de depósito a término",
            "rentabilidadMinima": 4.00,
            "rentabilidadMaxima": 6.00
          }
        ]
      },
      {
        "id": 2,
        "nombre": "Renta Variable",
        "descripcion": "Portafolio de acciones y derivados con mayor riesgo",
        "porcentaje": 30.00,
        "rentabilidadMinima": 5.00,
        "rentabilidadMaxima": 12.00,
        "formulaExposicion": {
          "id": 2,
          "umbralPorcentajeMin": 20.00,
          "umbralPorcentajeMax": 40.00
        },
        "opcionesInversion": [
          {
            "id": 3,
            "nombre": "Acciones Locales",
            "descripcion": "Acciones en bolsa colombiana",
            "rentabilidadMinima": 6.00,
            "rentabilidadMaxima": 15.00
          }
        ]
      }
    ]
  },
  {
    "id": 2,
    "nombre": "Moderado",
    "rentabilidadMinima": 4.80,
    "rentabilidadMedia": 7.20,
    "rentabilidadMaxima": 9.60,
    "portafolios": [ "..." ]
  },
  {
    "id": 3,
    "nombre": "Agresivo",
    "rentabilidadMinima": 6.00,
    "rentabilidadMedia": 9.00,
    "rentabilidadMaxima": 12.00,
    "portafolios": [ "..." ]
  }
]
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `[].id` | `long` | PK del perfil |
| `[].nombre` | `string` | Nombre del perfil (max 100) |
| `[].rentabilidadMinima` | `decimal` | Rentabilidad ponderada mínima del perfil (∑ portafolio.rentabilidadMinima × portafolio.porcentaje / 100) |
| `[].rentabilidadMedia` | `decimal` | Promedio de `rentabilidadMinima` y `rentabilidadMaxima` del perfil |
| `[].rentabilidadMaxima` | `decimal` | Rentabilidad ponderada máxima del perfil (∑ portafolio.rentabilidadMaxima × portafolio.porcentaje / 100) |
| `[].portafolios` | `array` | Portafolios con su distribución |
| `[].portafolios[].id` | `long` | PK del portafolio |
| `[].portafolios[].nombre` | `string` | Nombre del portafolio (max 150) |
| `[].portafolios[].descripcion` | `string` | Descripción (nullable, max 500) |
| `[].portafolios[].porcentaje` | `decimal` | % de distribución del perfil sobre este portafolio (suma = 100.00) |
| `[].portafolios[].rentabilidadMinima` | `decimal` | Rentabilidad calculada mínima del portafolio |
| `[].portafolios[].rentabilidadMaxima` | `decimal` | Rentabilidad calculada máxima del portafolio |
| `[].portafolios[].formulaExposicion` | `object` | Umbrales de exposición (nullable si no tiene fórmula) |
| `[].portafolios[].formulaExposicion.id` | `long` | PK de la fórmula |
| `[].portafolios[].formulaExposicion.umbralPorcentajeMin` | `decimal` | Umbral mínimo de exposición (0–100) |
| `[].portafolios[].formulaExposicion.umbralPorcentajeMax` | `decimal` | Umbral máximo de exposición (0–100) |
| `[].portafolios[].opcionesInversion` | `array` | Opciones de inversión del portafolio |
| `[].portafolios[].opcionesInversion[].id` | `long` | PK de la opción |
| `[].portafolios[].opcionesInversion[].nombre` | `string` | Nombre de la opción (max 150) |
| `[].portafolios[].opcionesInversion[].descripcion` | `string` | Descripción (nullable, max 500) |
| `[].portafolios[].opcionesInversion[].rentabilidadMinima` | `decimal` | Rentabilidad mínima de la opción |
| `[].portafolios[].opcionesInversion[].rentabilidadMaxima` | `decimal` | Rentabilidad máxima de la opción |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |

---

## 7. Módulo Simulación

### 7.1. `POST /api/v1/simulaciones/calcular` — Calcular proyección (sin persistir)

> **Ref:** RN-SIM-01 a RN-SIM-03, RN-SIM-05, RN-SIM-09, CA-SIM-01 a CA-SIM-07

Calcula la proyección de inversión en memoria y devuelve los resultados. **No persiste nada en base de datos.** El frontend muestra la gráfica y el usuario decide si guarda.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request Body

```json
{
  "idPerfilInversion": 2,
  "valorInversionInicial": 10000000.00,
  "valorInversionPeriodica": 500000.00,
  "plazoInversion": 5,
  "idTipoPlazo": 4,
  "reinviertePlazo": true
}
```

| Campo | Tipo | Requerido | Validaciones |
|-------|------|-----------|-------------|
| `idPerfilInversion` | `long` | Sí | Debe existir en `perfiles_inversion` |
| `valorInversionInicial` | `decimal` | Sí | > 0 (CA-SIM-01) |
| `valorInversionPeriodica` | `decimal` | No | ≥ 0 si se envía (CA-SIM-02). Default: 0 |
| `plazoInversion` | `integer` | Sí | > 0 (CA-SIM-03) |
| `idTipoPlazo` | `long` | Sí | Debe existir en `tipos_plazo` |
| `reinviertePlazo` | `boolean` | Sí | Flag de reinversión |

#### Response `200 OK`

```json
{
  "perfilSimulado": {
    "id": 2,
    "nombre": "Moderado"
  },
  "inputs": {
    "valorInversionInicial": 10000000.00,
    "valorInversionPeriodica": 500000.00,
    "plazoInversion": 5,
    "tipoPlazo": {
      "id": 4,
      "nombre": "AÑO",
      "factorConversionDias": 365
    },
    "reinviertePlazo": true
  },
  "proyeccion": [
    {
      "periodo": 1,
      "valorProyectadoMinimo": 10480000.00,
      "valorProyectadoMaximo": 10960000.00,
      "valorProyectadoEsperado": 10720000.00,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    },
    {
      "periodo": 2,
      "valorProyectadoMinimo": 11003040.00,
      "valorProyectadoMaximo": 12071360.00,
      "valorProyectadoEsperado": 11522560.00,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    },
    {
      "periodo": 3,
      "valorProyectadoMinimo": 11571185.92,
      "valorProyectadoMaximo": 13262210.56,
      "valorProyectadoEsperado": 12394238.72,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    },
    {
      "periodo": 4,
      "valorProyectadoMinimo": 12186602.84,
      "valorProyectadoMaximo": 14543422.77,
      "valorProyectadoEsperado": 13333200.80,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    },
    {
      "periodo": 5,
      "valorProyectadoMinimo": 12851559.78,
      "valorProyectadoMaximo": 15947871.36,
      "valorProyectadoEsperado": 14342343.26,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    }
  ],
  "resumen": {
    "valorFinalMinimo": 12851559.78,
    "valorFinalMaximo": 15947871.36,
    "valorFinalEsperado": 14342343.26,
    "gananciaEsperada": 4342343.26,
    "rendimientoEsperadoPorcentaje": 43.42
  }
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `perfilSimulado` | `object` | Perfil utilizado para la simulación |
| `inputs` | `object` | Eco de los parámetros de entrada |
| `inputs.tipoPlazo` | `object` | Detalle del tipo de plazo |
| `proyeccion` | `array` | Detalle por período |
| `proyeccion[].periodo` | `integer` | Número de período (1 a `plazoInversion`) |
| `proyeccion[].valorProyectadoMinimo` | `decimal` | Escenario pesimista |
| `proyeccion[].valorProyectadoMaximo` | `decimal` | Escenario optimista |
| `proyeccion[].valorProyectadoEsperado` | `decimal` | Escenario esperado (promedio ponderado) |
| `proyeccion[].rentabilidadMinimaAplicada` | `decimal` | Tasa mín aplicada (snapshot) |
| `proyeccion[].rentabilidadMaximaAplicada` | `decimal` | Tasa máx aplicada (snapshot) |
| `resumen` | `object` | Resumen del último período |
| `resumen.valorFinalMinimo` | `decimal` | Valor final mínimo |
| `resumen.valorFinalMaximo` | `decimal` | Valor final máximo |
| `resumen.valorFinalEsperado` | `decimal` | Valor final esperado |
| `resumen.gananciaEsperada` | `decimal` | Ganancia esperada (`valorFinalEsperado - valorInversionInicial`) |
| `resumen.rendimientoEsperadoPorcentaje` | `decimal` | Rendimiento total en % |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 400 | `VALIDATION_ERROR` | Campos con formato incorrecto o ausentes |
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 403 | `NO_PROFILE_ASSIGNED` | El usuario no tiene perfil de inversión asignado (RN-CAL-01) |
| 404 | `PROFILE_NOT_FOUND` | El `idPerfilInversion` no existe |
| 404 | `RESOURCE_NOT_FOUND` | El `idTipoPlazo` no existe |

**Ejemplo de error 403 (sin perfil asignado):**

```json
{
  "timestamp": "2026-04-13T15:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "code": "NO_PROFILE_ASSIGNED",
  "message": "Debe completar la encuesta de calibración antes de realizar simulaciones.",
  "path": "/api/v1/simulaciones/calcular",
  "details": []
}
```

**Ejemplo de error 400 (validación):**

```json
{
  "timestamp": "2026-04-13T15:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Los datos de la simulación contienen errores.",
  "path": "/api/v1/simulaciones/calcular",
  "details": [
    {
      "field": "valorInversionInicial",
      "message": "Debe ser mayor que 0"
    },
    {
      "field": "plazoInversion",
      "message": "Debe ser mayor que 0"
    }
  ]
}
```

---

### 7.2. `POST /api/v1/simulaciones` — Guardar simulación

> **Ref:** RN-SIM-04, RN-SIM-06, RN-SIM-07, RN-SIM-10, CA-SIM-05 a CA-SIM-08, CA-DET-01 a CA-DET-08

Persiste la simulación y su detalle de proyección en una transacción atómica. El backend **recalcula** la proyección server-side para prevenir manipulación; el frontend solo envía los inputs originales.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request Body

```json
{
  "idPerfilInversion": 2,
  "valorInversionInicial": 10000000.00,
  "valorInversionPeriodica": 500000.00,
  "plazoInversion": 5,
  "idTipoPlazo": 4,
  "reinviertePlazo": true,
  "idDisclaimer": 1
}
```

| Campo | Tipo | Requerido | Validaciones |
|-------|------|-----------|-------------|
| `idPerfilInversion` | `long` | Sí | Debe existir en `perfiles_inversion` |
| `valorInversionInicial` | `decimal` | Sí | > 0 |
| `valorInversionPeriodica` | `decimal` | No | ≥ 0 si se envía. Default: 0 |
| `plazoInversion` | `integer` | Sí | > 0 |
| `idTipoPlazo` | `long` | Sí | Debe existir en `tipos_plazo` |
| `reinviertePlazo` | `boolean` | Sí | Flag de reinversión |
| `idDisclaimer` | `long` | No | Si se envía, debe estar vigente y activo (CA-SIM-06) |

#### Response `201 Created`

```json
{
  "id": 3,
  "idUsuario": 1,
  "fechaSimulacion": "2026-04-13T15:10:00Z",
  "perfilSimulado": {
    "id": 2,
    "nombre": "Moderado"
  },
  "inputs": {
    "valorInversionInicial": 10000000.00,
    "valorInversionPeriodica": 500000.00,
    "plazoInversion": 5,
    "tipoPlazo": {
      "id": 4,
      "nombre": "AÑO",
      "factorConversionDias": 365
    },
    "reinviertePlazo": true
  },
  "disclaimer": {
    "id": 1,
    "titulo": "Disclaimer inversiones 2026"
  },
  "proyeccion": [
    {
      "periodo": 1,
      "valorProyectadoMinimo": 10480000.00,
      "valorProyectadoMaximo": 10960000.00,
      "valorProyectadoEsperado": 10720000.00,
      "rentabilidadMinimaAplicada": 4.80,
      "rentabilidadMaximaAplicada": 9.60
    }
  ],
  "resumen": {
    "valorFinalMinimo": 12851559.78,
    "valorFinalMaximo": 15947871.36,
    "valorFinalEsperado": 14342343.26,
    "gananciaEsperada": 4342343.26,
    "rendimientoEsperadoPorcentaje": 43.42
  }
}
```

> La estructura de response es idéntica a `/calcular` con los campos adicionales: `id`, `idUsuario`, `fechaSimulacion`, `disclaimer`.

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 400 | `VALIDATION_ERROR` | Campos con formato incorrecto o ausentes |
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 403 | `NO_PROFILE_ASSIGNED` | El usuario no tiene perfil de inversión asignado |
| 404 | `PROFILE_NOT_FOUND` | El `idPerfilInversion` no existe |
| 404 | `RESOURCE_NOT_FOUND` | El `idTipoPlazo` no existe |
| 404 | `DISCLAIMER_NOT_FOUND` | El disclaimer indicado no existe |
| 422 | `DISCLAIMER_NOT_ACTIVE` | El disclaimer existe pero no está vigente |

---

### 7.3. `GET /api/v1/simulaciones` — Listar simulaciones del usuario

> **Ref:** RN-SIM-04

Devuelve la lista paginada de simulaciones guardadas del usuario autenticado, ordenadas por fecha descendente.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Query Parameters

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | `integer` | `0` | Número de página (0-based) |
| `size` | `integer` | `20` | Elementos por página |
| `sort` | `string` | `fechaSimulacion,desc` | Campo y dirección de ordenamiento |

#### Response `200 OK`

```json
{
  "content": [
    {
      "id": 3,
      "fechaSimulacion": "2026-04-13T15:10:00Z",
      "nombrePerfilSimulado": "Moderado",
      "valorInversionInicial": 10000000.00,
      "plazoInversion": 5,
      "tipoPlazo": "AÑO",
      "reinviertePlazo": true,
      "valorFinalEsperado": 14342343.26,
      "gananciaEsperada": 4342343.26
    },
    {
      "id": 1,
      "fechaSimulacion": "2026-04-10T09:00:00Z",
      "nombrePerfilSimulado": "Moderado",
      "valorInversionInicial": 5000000.00,
      "plazoInversion": 3,
      "tipoPlazo": "AÑO",
      "reinviertePlazo": false,
      "valorFinalEsperado": 5860000.00,
      "gananciaEsperada": 860000.00
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `content` | `array` | Lista de simulaciones (resumen sin detalle de proyección) |
| `content[].id` | `long` | PK de la simulación |
| `content[].fechaSimulacion` | `string` | ISO-8601 |
| `content[].nombrePerfilSimulado` | `string` | Snapshot del nombre del perfil |
| `content[].valorInversionInicial` | `decimal` | Monto inicial |
| `content[].plazoInversion` | `integer` | Plazo |
| `content[].tipoPlazo` | `string` | Nombre del tipo de plazo |
| `content[].reinviertePlazo` | `boolean` | Flag de reinversión |
| `content[].valorFinalEsperado` | `decimal` | Valor esperado del último período |
| `content[].gananciaEsperada` | `decimal` | Ganancia esperada total |
| `page` | `object` | Metadata de paginación |
| `page.number` | `integer` | Página actual (0-based) |
| `page.size` | `integer` | Tamaño de página |
| `page.totalElements` | `long` | Total de registros |
| `page.totalPages` | `integer` | Total de páginas |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |

---

### 7.4. `GET /api/v1/simulaciones/{idSimulacion}` — Detalle de una simulación

> **Ref:** RN-SIM-06

Devuelve el detalle completo de una simulación guardada, incluyendo todos los períodos de proyección.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Path Parameters

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `idSimulacion` | `long` | PK de la simulación |

#### Response `200 OK`

Misma estructura que `POST /api/v1/simulaciones` response (sección 7.2), incluyendo `proyeccion[]` completa y `resumen`.

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 403 | `FORBIDDEN` | La simulación no pertenece al usuario autenticado |
| 404 | `RESOURCE_NOT_FOUND` | La simulación no existe |

---

## 8. Módulo Disclaimers

### 8.1. `GET /api/v1/disclaimers/vigente` — Obtener disclaimer vigente

> **Ref:** RN-DIS-01 a RN-DIS-04, CA-DIS-01 a CA-DIS-06

Devuelve el disclaimer activo y vigente al momento de la consulta. El frontend lo muestra antes de que el usuario confirme guardar una simulación.

**Autenticación:** `Bearer JWT` (obligatorio)

#### Request

Sin body ni query params.

#### Response `200 OK`

```json
{
  "id": 1,
  "titulo": "Disclaimer inversiones 2026",
  "contenido": "Las rentabilidades mostradas son proyecciones basadas en datos históricos y no constituyen garantía de rendimiento futuro. Las inversiones están sujetas a riesgos de mercado y el capital invertido puede disminuir. Protección S.A. no se hace responsable por las decisiones de inversión tomadas con base en estas simulaciones. Consulte a su asesor financiero antes de invertir.",
  "activo": true,
  "fechaVigenciaDesde": "2026-01-01T00:00:00Z",
  "fechaVigenciaHasta": null
}
```

| Campo | Tipo | Nullable | Descripción |
|-------|------|----------|-------------|
| `id` | `long` | No | PK del disclaimer |
| `titulo` | `string` | No | Título (max 200 chars) |
| `contenido` | `string` | No | Texto legal completo |
| `activo` | `boolean` | No | Siempre `true` en esta respuesta |
| `fechaVigenciaDesde` | `string` | No | ISO-8601, inicio de vigencia |
| `fechaVigenciaHasta` | `string` | Sí | ISO-8601, fin de vigencia. `null` = sin expiración |

#### Errores posibles

| HTTP | Código | Cuándo |
|------|--------|--------|
| 401 | `UNAUTHORIZED` | Token ausente, expirado o inválido |
| 404 | `DISCLAIMER_NOT_FOUND` | No hay disclaimer activo y vigente en este momento |

---

## 9. Resumen de Endpoints

| # | Método | Endpoint | Módulo | Descripción |
|---|--------|----------|--------|-------------|
| 1 | `GET` | `/api/v1/usuarios/me` | Auth | Perfil del usuario autenticado |
| 2 | `GET` | `/api/v1/calibracion/preguntas` | Calibración | Preguntas activas con opciones |
| 3 | `POST` | `/api/v1/calibracion/encuestas` | Calibración | Crear encuesta de calibración |
| 4 | `POST` | `/api/v1/calibracion/encuestas/{id}/respuestas` | Calibración | Registrar respuesta |
| 5 | `POST` | `/api/v1/calibracion/encuestas/{id}/completar` | Calibración | Completar encuesta y asignar perfil |
| 6 | `GET` | `/api/v1/perfiles` | Perfiles | Listar perfiles con portafolios |
| 7 | `POST` | `/api/v1/simulaciones/calcular` | Simulación | Calcular proyección (sin persistir) |
| 8 | `POST` | `/api/v1/simulaciones` | Simulación | Guardar simulación |
| 9 | `GET` | `/api/v1/simulaciones` | Simulación | Listar simulaciones del usuario |
| 10 | `GET` | `/api/v1/simulaciones/{id}` | Simulación | Detalle de una simulación |
| 11 | `GET` | `/api/v1/disclaimers/vigente` | Disclaimers | Disclaimer vigente |

### Matriz de errores por endpoint

| Endpoint | 400 | 401 | 403 | 404 | 409 | 422 |
|----------|-----|-----|-----|-----|-----|-----|
| `GET /usuarios/me` | — | ✓ | — | — | — | — |
| `GET /calibracion/preguntas` | — | ✓ | — | — | — | — |
| `POST /calibracion/encuestas` | ✓ | ✓ | — | — | ✓ | — |
| `POST /encuestas/{id}/respuestas` | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| `POST /encuestas/{id}/completar` | — | ✓ | ✓ | ✓ | ✓ | ✓ |
| `GET /perfiles` | — | ✓ | — | — | — | — |
| `POST /simulaciones/calcular` | ✓ | ✓ | ✓ | ✓ | — | — |
| `POST /simulaciones` | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| `GET /simulaciones` | — | ✓ | — | — | — | — |
| `GET /simulaciones/{id}` | — | ✓ | ✓ | ✓ | — | — |
| `GET /disclaimers/vigente` | — | ✓ | — | ✓ | — | — |

---

> Documento generado el 2026-04-13. Debe actualizarse cada vez que se agreguen, modifiquen o eliminen endpoints.

