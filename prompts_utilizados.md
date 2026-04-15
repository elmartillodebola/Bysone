# Prompts Utilizados con IA — Mi Portafolio Inteligente
**Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**

> Este documento registra los prompts clave usados con IA durante el desarrollo del proyecto,
> organizados por etapa. Para cada prompt se indica qué se pidió, qué se obtuvo y qué se ajustó.

---

## Etapa 1 · Entendimiento del problema

### Prompt 1.1 — Contexto inicial del proyecto
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Quiero construir primero la documentación y diseño de una aplicación que estamos trabajando en equipo dentro de una hackaton de la comunidad de desarrollo de software de nuestra empresa Protección."

**Qué se obtuvo:**
- El asistente solicitó contexto adicional: condiciones del problema, stack tecnológico, tamaño del equipo, nube objetivo y base de datos preferida.
- Se estableció un flujo colaborativo para construir la documentación por etapas.

**Qué se ajustó:**
- Se proporcionó el enunciado completo del reto (niveles Iniciando → Mentor) para que el asistente pudiera orientar las decisiones técnicas hacia el nivel máximo.

---

### Prompt 1.2 — Análisis del reto y niveles
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> Enunciado completo del reto: *"Mi Portafolio Inteligente"* con los 4 niveles (Iniciando, Junior, Senior, Mentor) y los requisitos técnicos (Auth JWT/OAuth, POO, almacenamiento nube, Docker, cola de notificaciones).

**Qué se obtuvo:**
- Análisis de la hoja de ruta técnica para alcanzar el nivel Mentor.
- Identificación de los componentes críticos: auth, simulación con POO, persistencia en nube, contenedor Docker, cola de mensajes asíncrona.
- Preguntas orientadoras para definir el stack tecnológico.

**Qué se ajustó:**
- Se añadieron los entregables obligatorios (7 items) y los criterios de evaluación (90 pts base + 30 pts bonus = 120 pts máx) para priorizar el trabajo.

---

### Prompt 1.3 — Entregables y criterios de evaluación
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> Imagen con los 7 entregables obligatorios: repositorio Git, app desplegada, tests unitarios, diseño previo, prompts utilizados, demo en vivo y documentación.
> Imagen con los criterios de evaluación: criterios base (90 pts) y bonus (30 pts).

**Qué se obtuvo:**
- Tabla de priorización de criterios ordenada por impacto en puntaje.
- Identificación de los 4 bonus alcanzables: tests unitarios (+10), cola real (+10), POO avanzada (+5), object storage (+5).
- Estrategia para maximizar los 120 pts posibles.

**Qué se ajustó:**
- El equipo decidió que todos los bonus deben estar incluidos en el diseño inicial, no como añadidos posteriores.

---

### Prompt 1.4 — Estructura del documento de prompts
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Quiero que los diferentes prompts que vamos usando los vayas dejando en un MD aparte, segmentados por etapa (entendimiento, diseño, etc.)."

**Qué se obtuvo:**
- Creación de este documento con estructura por etapas.
- Plantilla reutilizable para registrar cada prompt con: qué se pidió, qué se obtuvo, qué se ajustó.

**Qué se ajustó:**
- N/A — primera versión del documento.

---

## Etapa 2 · Diseño y arquitectura

### Prompt 2.1 — Modelo de datos y DDL Flyway
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**

> "La persistencia la manejaremos en Neon, previo a llegar a neon, tenemos el diseño del modelo de datos creado por el equipo, quiero pasarte las estructuras de las bases de datos, como veras solo estoy definiendo los nombres de las entidades, los nombres de los campos, que llave primaria y foranea existe, las relaciones entre las entidades, cuando termine de pasarte dicha información, crea el DDL respectivo y que podamos usar dentro del proyecto en flyway, comienzo con esta primera entidad: Usuarios con los campos: [imagen]"

Luego de forma iterativa, entidad por entidad con capturas de pantalla:

> "Entidad Perfiles_Inversion, con los siguientes campos: [imagen]"

> "Entidad 'Portafolios_Inversion' con los campos: [imagen]"

> "Entidad Opciones_Inversion, con los campos: [imagen]"

> "Entidad: portafolio_inversion_x_opciones_inversion con los campos: [imagen]"

> "Entidad: Perfiles_inversion_x_Portafolios_inversion con los campos: [imagen]"

> "Entidad: Formulas_exposicion, con los campos: [imagen]"

> "Entidad Roles_bysone con los campos: [imagen]"

> "Entidad: opciones_funcionales_bysone: con los campos: [imagen]"

> "Entidad: Roles_x_Opcion_funcional, con los campos: [imagen]"

> "Entidad: Parametros_Bysone con los campos: [imagen]"

> "Entidad: Simulaciones_Bysone, con los campos: [imagen]"

> "estas son todas las tablas que quiero que consideres, es necesario colocarle el tipo de dato y longitud, cual es la llave primaria y foranea, tal como te pase el diseño"

**Qué se obtuvo:**
- DDL completo en `backend/src/main/resources/db/migration/V1__create_initial_schema.sql`
- 12 tablas creadas en orden respetando dependencias de FK: roles, acceso funcional, parámetros, portafolios, opciones de inversión, perfiles, usuarios y simulaciones
- Tipos de datos asignados: `BIGSERIAL` para PKs, `VARCHAR` con longitudes por dominio, `DECIMAL(5,2)` para porcentajes y rentabilidades, `DECIMAL(18,2)` para valores monetarios, `BOOLEAN` para flags, `TIMESTAMP` para fechas

**Qué se ajustó:**
- Se añadió `id_simulacion BIGSERIAL` como PK de `simulaciones_bysone` al no estar explícita en el diseño
- Se identificaron tres puntos pendientes de validación con el equipo:
  1. `perfiles_inversion` tiene FKs directas a portafolios (`porcentaje_1/2/3`) Y existe la tabla `perfiles_inversion_x_portafolios_inversion` — posible redundancia
  2. `usuarios` no tiene relación con `roles_bysone` — posible tabla `usuarios_x_rol` faltante

---

### Prompt 2.2 — Ajuste en registro de prompts utilizados
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "en los prompts utilizados no estás dejando explícitamente los prompts que estamos usando para interactuar contigo, puedes ajustar, acude a la memoria si es necesaria por usar los prompts y que aparezcan en el md"

**Qué se obtuvo:**
- Corrección del prompt 2.1 para incluir los mensajes literales enviados al asistente en lugar de un resumen
- Ajuste de la regla en memoria: "Qué se pidió" debe citar los prompts textuales, no parafrasearlos

**Qué se ajustó:**
- N/A — corrección aplicada en caliente sobre el prompt 2.1

### Prompt 2.3 — Revisión y normalización del modelo de datos
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "quiero que revisemos el modelo de datos y aseguremos que las relaciones estan bien definidas y este bien normalizado el modelo, identifica las tablas que no tienen relacion y estan aisladas"

> "si procede con los ajustes propuestos"

**Qué se obtuvo:**
- Análisis completo del modelo con 4 hallazgos:
  1. Cluster `roles_bysone` / `opciones_funcionales_bysone` aislado de `usuarios` — faltaba `usuarios_x_rol`
  2. Violación 1NF en `perfiles_inversion` por grupos repetidos (`porcentaje_1/2/3`)
  3. Redundancia: `perfiles_inversion_x_portafolios_inversion` duplicaba la relación sin aportar `porcentaje`
  4. Inconsistencia de nombre: `perfil_inversion` → debía ser `id_perfil_inversion` en `simulaciones_bysone`
- DDL actualizado en `V1__create_initial_schema.sql` con los 4 fixes aplicados

**Qué se ajustó:**
- [FIX-A] `perfiles_inversion` simplificada: eliminados `porcentaje_1/2/3` e `id_portafolio_inversion_porcentaje_1/2/3`
- [FIX-B] `perfiles_inversion_x_portafolios_inversion` ahora incluye `porcentaje DECIMAL(5,2)` — única fuente de verdad para la distribución del perfil
- [FIX-C] Creada `usuarios_x_rol` para conectar usuarios con el sistema de roles
- [FIX-D] Renombrado `perfil_inversion` → `id_perfil_inversion` en `simulaciones_bysone`

---

### Prompt 2.4 — Entidades de calibración de perfil de inversión
**Fecha:** 2026-04-12
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "tenemos un grupo de entidades que nos hace falta establecer e incluir si fuera el caso, corresponde a las entidades con las cuales un nuevo usuario debe hacer una encuesta de calibración apoyado en las preguntas de calibracion para definir que tipo de perfil tiene: moderado, agresivo o conservador, al terminar la fecha de dicha calibración se guarda en la tabla de usuarios y esta se puede solicitar por demanda por parte del usuario o porque el sistema al detectar si paso el tiempo que indique el parámetro respecto se repita la encuesta de nuevo. antes de hacer ajuste indícame cuales serían y como se relacionan entre si"

> "la entidad preguntas_calibracion ya esta en el modelo, la ajustaría agregando cambiado el campo pregunta por texto_pregunta, agregando el orden y si esta activa o no. proceder con la entidad 2, 3 y 4 propuestas"

**Qué se obtuvo:**
- Análisis previo de las 4 entidades necesarias y sus relaciones antes de tocar el DDL
- DDL actualizado en `V1__create_initial_schema.sql` con la sección CALIBRACIÓN DE PERFIL:
  - `preguntas_calibracion` — ajustada: `texto_pregunta`, `orden`, `activa`
  - `opciones_respuesta_calibracion` — opciones por pregunta con `puntaje` y `orden`
  - `encuestas_calibracion` — instancia por usuario con `origen` (DEMANDA|SISTEMA), `estado` y `id_perfil_resultado`
  - `respuestas_encuesta_calibracion` — respuesta por pregunta dentro de una encuesta; constraint UNIQUE por `(id_encuesta, id_pregunta)` para evitar doble respuesta

**Qué se ajustó:**
- `id_perfil_resultado` en `encuestas_calibracion` es nullable (la encuesta puede estar PENDIENTE antes de completarse)
- CHECK constraints en `origen` y `estado` para garantizar integridad sin tabla catálogo adicional
- UNIQUE en `(id_encuesta, id_pregunta)` en `respuestas_encuesta_calibracion` para garantizar una sola respuesta por pregunta por encuesta

---

### Prompt 2.5 — Modelo de proyección de simulación de inversión
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Tenemos otra funcionalidad relacionada con proyección, en las cuales creemos que es posible darle el siguiente manejo: ya tenemos la entidad simulaciones_bysone, revisemos y ajustemos inclusive con nuevas entidades de ser el caso, la idea es que un usuario al escoger el perfil de riesgo o usar el que tiene asignado simule con esos datos como se comportaria la inversion con la configuracion de cada perfil de riesgos y su portafolio asociado, si la simulación es al final guardada por el usuario se almacena sino no se almacena, como luce la entidad"

> "si procede"

**Qué se obtuvo:**
- Análisis de la entidad `simulaciones_bysone` actual: solo tenía inputs, sin outputs ni snapshot del perfil
- Identificación de 3 problemas: sin proyección almacenada, perfil mutable en el tiempo, `id_parametro` ambiguo
- Propuesta patrón maestro-detalle aprobada por el equipo
- DDL actualizado con:
  - `simulaciones_bysone` ajustada: campo `nombre_perfil_simulado VARCHAR(100)` como snapshot del perfil al momento de simular
  - `detalle_proyeccion_simulacion` nueva: una fila por período (año), con `valor_proyectado_minimo`, `valor_proyectado_maximo`, `valor_proyectado_esperado` y snapshot de tasas aplicadas (`rentabilidad_minima_aplicada`, `rentabilidad_maxima_aplicada`)
  - UNIQUE `(id_simulacion, periodo)` para garantizar un solo registro por año por simulación

**Qué se ajustó:**
- Solo se inserta en BD si el usuario decide guardar — no hay flag "guardada", la ausencia de registro implica descarte
- Las tasas de rentabilidad se guardan como snapshot en el detalle para que la simulación sea reproducible aunque el portafolio cambie después

---

### Prompt 2.6 — Revisión completa del esquema y ajustes finales
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "pasemos a revisar el esquema completo antes de continuar"

> "procede con todos los ajustes, en el caso de plazo_inversion, considerar una entidad nueva y sus relaciones con la simulación con id_plazo, allí colocaremos si son dias, mes, trimestre, año, agrega los campos respectivos con el resto de la información de la entidad, para el caso del id_parametro es debido a que los disclaimer que tendrán las simulaciones serán un parámetro, dar consejo al respecto si es un buen manejo o lo manejamos de otra manera"

**Qué se obtuvo:**
- Revisión completa del esquema: mapa de clusters, 5 hallazgos identificados
- Consejo sobre disclaimers: se recomendó tabla independiente `disclaimers_bysone` en lugar de usar `parametros_bysone`, argumentando necesidad de TEXT, ciclo de vida con vigencia y separación de responsabilidades
- DDL actualizado con 6 cambios aplicados:
  1. `formulas_exposicion`: UNIQUE `(id_perfil_inversion, id_portafolio_inversion)`
  2. `usuarios`: campos `proveedor_oauth VARCHAR(20)` y `oauth_sub VARCHAR(255) UNIQUE` para mapeo OAuth2, con CHECK `('GOOGLE', 'MICROSOFT')`
  3. `encuestas_calibracion`: campo `puntaje_total INTEGER` para evitar recalcular al consultar historial
  4. Nueva tabla `tipos_plazo`: catálogo con `nombre_plazo`, `descripcion`, `factor_conversion_dias` para normalizar cualquier plazo a días en el motor de cálculo
  5. Nueva tabla `disclaimers_bysone`: `titulo`, `contenido TEXT`, `activo`, `fecha_vigencia_desde`, `fecha_vigencia_hasta` para versionar textos legales
  6. `simulaciones_bysone`: reemplazado `id_parametro` por `id_disclaimer` (FK → `disclaimers_bysone`) y agregado `id_tipo_plazo` (FK → `tipos_plazo`)

**Qué se ajustó:**
- `id_disclaimer` es nullable en `simulaciones_bysone` (puede no requerir disclaimer en todos los casos)
- `puntaje_total` es nullable en `encuestas_calibracion` (se llena al completar, no al crear)
- Se mantiene `parametros_bysone` para configuración técnica del sistema (intervalos, tasas, umbrales)

---

### Prompt 2.7 — Generación de reglas de negocio desde el modelo de datos
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "a partir del modelo de datos ajustado quiero que crees un MD con las reglas de negocio, llamémoslo Reglas_Negocio.md"

**Qué se obtuvo:**
- Archivo `docs/Reglas_Negocio.md` con 30 reglas de negocio organizadas en 8 secciones:
  1. Usuarios y Autenticación (5 reglas)
  2. Roles y Acceso Funcional (3 reglas)
  3. Perfiles de Inversión (4 reglas)
  4. Portafolios y Opciones de Inversión (3 reglas)
  5. Calibración de Perfil (9 reglas)
  6. Simulación y Proyección (10 reglas)
  7. Disclaimers (4 reglas)
  8. Parámetros del Sistema (3 reglas)
- Reglas derivadas de constraints del DDL (UNIQUE, CHECK, NOT NULL, FK) y flujos definidos en conversaciones anteriores

**Qué se ajustó:**
- N/A — primera versión del documento

---

### Prompt 2.8 — Criterios de aceptación del modelo de datos
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "antes de los datos semilla, quiero crear un md adicional pero con Criterios_aceptacion.md en los cuales en orden de cada entidad y en general del modelo de datos tengamos los criterios como por ejemplo: cada Id debe ser mayor que cero e incremental, las fechas a insertar no pueden ser diferentes a las del dia en curso, etc."

**Qué se obtuvo:**
- Archivo `docs/Criterios_Aceptacion.md` con 68 criterios organizados en 10 secciones, una por dominio del modelo
- Cada criterio indica si la validación es responsabilidad de la base de datos `[BD]`, la aplicación `[APP]` o ambas `[BD/APP]`
- Criterios generales (CA-GEN) aplicables a todas las tablas
- Criterios específicos por entidad derivados de constraints del DDL y reglas de negocio

**Qué se ajustó:**
- N/A — primera versión del documento

---

### Prompt 2.9 — Datos semilla (V2 Flyway)
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Crea los datos semilla y crea un archivo V2 de estos datos semilla, asegúrate de que puedan ser creados en el orden tal que no hayan problemas con la consistencia de la BD, genera datos hasta máximo 5 instancias por entidad"

**Qué se obtuvo:**
- Archivo `backend/src/main/resources/db/migration/V2__datos_semilla.sql`
- 20 bloques de INSERT en orden estricto de dependencias FK
- Máximo 5 filas por entidad (algunas con menos según su naturaleza)
- Actualización de las 16 secuencias BIGSERIAL al final para evitar conflictos de PK en inserciones futuras
- Valores de proyección calculados matemáticamente desde las rentabilidades del modelo:
  - Moderado (Ana): min ponderada 4.80%, max ponderada 9.60%
  - Conservador (Carlos): min 3.00%, max 6.00%
- Coherencia de puntajes de calibración con los parámetros del sistema (umbral conservador ≤ 2, moderado 3–5)

**Qué se ajustó:**
- Detalle de proyección de Carlos limitado a los primeros 5 períodos totales (2 de Ana + 3 de Carlos) para respetar el límite de 5 por entidad
- Parámetros de umbral de puntaje ajustados a escala del cuestionario semilla (max 2 preguntas × max puntaje 3 = 6 puntos posibles)

---

### Prompt 2.10 — Plan de desarrollo y actualización de README
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "veo buen detalle para el back, no quedó faltando detalle del front? solo contexta no hagas ajustes al plan aun"

> "ajusta el plan y documéntalo en un doc llamado Plan_Desarrollo_Bysone.md ajusta el readme como corresponde para todos los md creados."

**Qué se obtuvo:**
- Contextualización de las brechas del frontend: faltaban estructura de carpetas, pantallas concretas, librerías (Recharts, shadcn/ui, React Query, React Hook Form + Zod), gestión de estado pre-guardado, flujo de pantallas de simulación, variables de entorno por ambiente y componentes a testear
- Archivo `docs/Plan_Desarrollo_Bysone.md` con plan completo de 4 fases:
  - Fase 0 (infra base): esqueleto hexagonal backend, estructura Next.js, docker-compose
  - Fase 1 (backend core): dominio + casos de uso + adaptadores R2DBC / RabbitMQ / OAuth2
  - Fase 2 (features): 5 módulos detallados (Auth, Calibración, Perfiles, Simulación, Notificaciones) con endpoints API y pantallas UI por módulo
  - Fase 3 (integración y pruebas): tabla de tests por capa y herramienta
  - Fase 4 (despliegue): variables por ambiente, comandos Fly.io, pipelines, smoke tests
- `README.md` actualizado con tabla de documentación organizada en 3 secciones: Diseño y arquitectura, Base de datos, Guías operativas

**Qué se ajustó:**
- Frontend amplió: estructura de carpetas con rutas App Router, 7 pantallas explícitas, hooks personalizados `useSimulacion` y `useCalibracion`, flujo de dos pasos en simulación (calcular sin persistir → confirmar para guardar)
- Swagger UI añadido al README como endpoint local

---

### Prompt 2.11 — Guardar memoria del proyecto en el repositorio
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "guarda la memoria del proyecto donde vamos"

> "crea una copia de la memoria en un archivo dentro del proyecto"

**Qué se obtuvo:**
- Memoria interna del asistente actualizada: `project_progress.md`, `project_architecture.md` y nuevo `project_datamodel.md`
- Archivo `docs/Contexto_Proyecto_IA.md` creado en el repositorio con copia consolidada de toda la memoria: visión general, stack, arquitectura (backend hexagonal + frontend 7 pantallas), modelo de datos (16 tablas + decisiones), estado de avance, próximo paso y convenciones de trabajo con IA
- README actualizado con referencia al nuevo archivo

**Qué se ajustó:**
- N/A

---

### Prompt 2.12 — Contratos de API entre frontend y backend
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "Actua como Arquitecto del proyecto y genera los contratos de API entre front y back para la correcta interaccion, ten en cuenta el manejo de errores. revisa los archivos .md con las definiciones actuales y añade los prompt de nuestro chat en prompts_utilizados.md"

**Qué se obtuvo:**
- Archivo `docs/API_Contracts.md` con contratos completos para 11 endpoints organizados en 5 módulos:
  1. **Auth** (1 endpoint): `GET /api/v1/usuarios/me` — perfil del usuario con flag `requiereRecalibracion`
  2. **Calibración** (4 endpoints): listar preguntas, crear encuesta, registrar respuesta, completar encuesta
  3. **Perfiles y Portafolios** (1 endpoint): listar perfiles con distribución porcentual, portafolios, opciones y fórmulas de exposición
  4. **Simulación** (4 endpoints): calcular proyección sin persistir, guardar simulación, listar simulaciones paginadas, detalle de simulación
  5. **Disclaimers** (1 endpoint): obtener disclaimer vigente
- Convenciones globales: base URL, content-type, autenticación JWT, nomenclatura camelCase, timestamps ISO-8601, precisión decimal
- Envelope estándar de error con estructura consistente: `timestamp`, `status`, `error`, `code`, `message`, `path`, `details[]`
- Catálogo de 17 códigos de error (6 generales + 11 de dominio) con HTTP status y módulo
- Para cada endpoint: método HTTP, path, autenticación, request body/params con tipos y validaciones, response con JSON de ejemplo completo, tabla de campos, y tabla de errores posibles con códigos específicos
- Ejemplos de error detallados para los escenarios más complejos (409, 422, 403)
- Matriz resumen de errores por endpoint
- Tabla resumen de los 11 endpoints
- Decisión arquitectónica: el endpoint `POST /simulaciones` recalcula server-side la proyección (no confía en datos del frontend) para prevenir manipulación

**Qué se ajustó:**
- Se añadió endpoint `GET /api/v1/simulaciones/{id}` (no estaba en el plan original pero es necesario para que el historial pueda mostrar el detalle de una simulación guardada)
- Se incluyó paginación estándar de Spring (`page`, `size`, `sort`) en el listado de simulaciones
- Se incluyó objeto `resumen` en la respuesta de cálculo/guardado con ganancia esperada y rendimiento porcentual total
- Se actualizó `prompts_utilizados.md` con este prompt
- Se actualizó `README.md` con referencia al nuevo documento

---

### Prompt 2.13 — Ajuste contrato error 409 en creación de encuesta
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "Veo conflictivo el contrato de POST /api/v1/calibracion/encuestas/{idEncuesta}/completar dado que este comando sería consumido por el front end en caso de error 409 en POST /api/v1/calibracion/encuestas (cuando comenzo una encuesta) y el esperaria que el error retorne el id de respuesta para que el front sepa cual encuesta completar"

**Qué se obtuvo:**
- Actualización del contrato de error `SURVEY_ALREADY_PENDING` (409) en `POST /api/v1/calibracion/encuestas` para incluir un objeto `encuestaPendiente` con:
  - `id`: PK de la encuesta pendiente (necesario para redirigir al wizard)
  - `fechaRealizacion`: cuándo se creó
  - `origen`: si fue por DEMANDA o SISTEMA
  - `preguntasRespondidas`: progreso actual
  - `preguntasTotales`: total de preguntas activas
- Nota explícita para el frontend sobre cómo usar estos datos para retomar el wizard
- Actualización de la descripción del código de error en el catálogo (sección 3) indicando que incluye `encuestaPendiente` con id y progreso

**Qué se ajustó:**
- El campo `encuestaPendiente` se añade como extensión del envelope estándar de error (campos adicionales junto a `timestamp`, `status`, `error`, `code`, `message`, `path`, `details`)
- Se incluyen `preguntasRespondidas` y `preguntasTotales` para que el frontend pueda mostrar la barra de progreso sin una llamada adicional al backend

---

### Prompt 2.14 — Enriquecer error 409 con preguntas pendientes para retomar wizard
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "el error 409 en POST /api/v1/calibracion/encuestas debe responder con el idEncuesta y las preguntas que faltan por diligenciar para retomar con POST /api/v1/calibracion/encuestas/{idEncuesta}/respuestas y solo está entregando: [JSON con encuestaPendiente sin preguntasPendientes]"

**Qué se obtuvo:**
- Enriquecimiento del objeto `encuestaPendiente` en el error 409 `SURVEY_ALREADY_PENDING` con el array `preguntasPendientes[]`:
  - Cada pregunta pendiente incluye: `id`, `textoPregunta`, `orden` y `opciones[]`
  - Cada opción incluye: `id`, `textoOpcion`, `orden` — **sin `puntaje`** para no exponer la lógica de scoring
- Tabla detallada de campos de `encuestaPendiente` con tipos y descripciones
- 4 notas prácticas para el frontend: cómo usar el `id` como path param, cómo iterar `preguntasPendientes`, cómo renderizar barra de progreso, y por qué no se expone `puntaje`
- Actualización de la descripción de `SURVEY_ALREADY_PENDING` en el catálogo de errores (sección 3)

**Qué se ajustó:**
- Las opciones de respuesta dentro de `preguntasPendientes` **omiten el campo `puntaje`** a diferencia de `GET /calibracion/preguntas` — decisión de seguridad para no exponer la lógica de scoring durante reanudación
- El frontend puede retomar el wizard en una sola llamada: el 409 ya trae todo lo necesario sin requerir `GET /calibracion/preguntas` adicional

---

### Prompt 2.15 — Añadir rentabilidad ponderada a nivel de perfil en GET /perfiles
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "En el contrato, seccion 6.1. GET /api/v1/perfiles — Listar perfiles de inversión hace falta que el contrato entregue rentabilidadMinima, Maxima y media a nivel de cada portafolio"

**Qué se obtuvo:**
- Actualización del contrato `GET /api/v1/perfiles` con 3 campos nuevos a nivel de perfil:
  - `rentabilidadMinima`: ponderada (∑ portafolio.rentabilidadMinima × porcentaje / 100)
  - `rentabilidadMedia`: promedio de mínima y máxima
  - `rentabilidadMaxima`: ponderada (∑ portafolio.rentabilidadMaxima × porcentaje / 100)
- Valores de ejemplo alineados con datos semilla: Conservador (3.00–5.00), Moderado (4.80–9.60), Agresivo (6.00–12.00)
- Tabla de campos actualizada con fórmulas de cálculo

**Qué se ajustó:**
- Campos calculados server-side por el backend — el frontend los consume directamente para mostrar en `PerfilCard` sin recalcular

---

### Prompt 2.16 — Revisión cruzada API Contracts vs Criterios de Aceptación
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "ya se revisaron los API_contracts y la lectura es satisfactoria, revisa si cumple los criterios de aceptacion y si hace falta agregar alguno, notificame sin modificar ningun archivo"

**Qué se obtuvo:**
- Análisis cruzado entre `API_Contracts.md`, `Criterios_Aceptacion.md` y `Reglas_Negocio.md`
- 10 hallazgos identificados: 2 criterios desactualizados, 1 decisión pendiente, 7 criterios nuevos sugeridos
- Tabla resumen con tipo de acción, archivo afectado y descripción para cada hallazgo
- No se modificó ningún archivo

**Qué se ajustó:**
- N/A — solo análisis, cambios pendientes de aprobación del equipo

---

### Prompt 2.17 — Aplicación de observaciones a Criterios y API Contracts
**Fecha:** 2026-04-13
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "3. el criterio se cumple al hacer POST o PATCH por el admin, no al obtener la respuesta en el GET
> 4. la calcula el backend automaticamente
> 5. agrega el criterio sugerido
> 6. agrega criterio sugerido
> 7. agrega criterio
> 8. agregar criterio
> 9. agrega criterio
> 10. dejalo con camel case en ambos casos
> aplica los cambios de acuerdo a las observaciones numeradas"

**Qué se obtuvo:**
- **Hallazgo 3 (CA-ORC-05):** Sin cambios — el equipo confirma que el criterio de mínimo 2 opciones por pregunta se valida al momento de crear/editar preguntas (POST/PATCH admin), no al listar en GET
- **Hallazgo 4 (fechaVencimiento):** Actualización de `API_Contracts.md`:
  - Añadido campo `fechaVencimiento` en response de `POST /calibracion/encuestas` y `POST /encuestas/{id}/completar`
  - Documentado como calculado automáticamente por el backend (`fechaRealizacion + INTERVALO_RECALIBRACION_DIAS`), nullable si el parámetro no está configurado
- **Hallazgo 5:** Nuevo criterio `CA-ENC-09` en `Criterios_Aceptacion.md` — la publicación del evento RabbitMQ no bloquea la transacción de completar encuesta
- **Hallazgo 6:** Nuevo criterio `CA-USU-11` — fórmula de cálculo de `requiereRecalibracion` (`fechaUltimaActualizacionPerfilInversion` nula o diferencia con fecha actual > `INTERVALO_RECALIBRACION_DIAS`)
- **Hallazgo 7:** Nuevo criterio `CA-PER-03` — fórmula de rentabilidad ponderada del perfil (∑ portafolio.rentabilidad × porcentaje / 100)
- **Hallazgo 8:** Nuevo criterio `CA-SIM-09` — aislamiento de simulaciones por usuario (solo propias, 403 si ajena)
- **Hallazgo 9:** Nuevo criterio `CA-ENC-10` — aislamiento de encuestas por usuario (solo propias, 403 si ajena)
- **Hallazgo 10:** Nota en convenciones de `API_Contracts.md` — campos de fecha/timestamp generados server-side, `camelCase` en JSON / `snake_case` en BD

**Qué se ajustó:**
- `Criterios_Aceptacion.md`: 5 criterios nuevos (CA-ENC-09, CA-ENC-10, CA-USU-11, CA-PER-03, CA-SIM-09)
- `API_Contracts.md`: `fechaVencimiento` añadido a responses de encuestas + nota de convenciones para timestamps

---

## Etapa 3 · Implementación

---

### Prompt 3.1 — Validación de entorno y confirmación de stack
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "si necesito automatizar el flujo de ci/cd necesitare oauth para github?"

> "si retiralo pues solo usaremos google y microsoft"

> "si actualiza memoria por si nos toca reiniciar en algun momento y tener este punto de partida"

> "si vamos a las fase A, te voy adejar trabajando automaticamente"

**Qué se obtuvo:**
- Confirmación de que GitHub OAuth (proveedor de login) es distinto de GitHub Actions (CI/CD): el CI/CD usa `GITHUB_TOKEN` automático + `FLY_API_TOKEN` como secret, no OAuth de usuario
- Eliminación de `GITHUB` como proveedor OAuth en `docs/API_Contracts.md` (líneas 44 y 158)
- Actualización de memoria del proyecto con estado de avance, decisiones de OAuth, entorno verificado (Node 20 + npm + Next.js CLI) y orden de fases acordado
- Inicio de Fase A: generación de `docker-compose.yml`, `.env.example`, `backend/Dockerfile`, `frontend/Dockerfile`
- Corrección de `pom.xml` (Java 25 → 21) y `application.yaml` (GitHub removido, config AMQP y SMTP agregada)
- Creación de `V3__rename_roles.sql` (solo rename de roles) eliminando la migración anterior que incluía GitHub OAuth

**Qué se ajustó:**
- N/A — los archivos ya existentes del equipo (pom.xml con Spring MVC + JPA) resultaron correctos; el error estaba en los docs de arquitectura generados en sesión anterior que decían WebFlux/R2DBC

---

### Prompt 3.2 — Corrección de stack: Spring MVC + JPA (no reactivo)
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "interrumpi porque recuerdo que los API_contracts plantean que no se hara el trabajo reactivo sino lineal, confirma y ajusta para que ya no sea reactivo sino acorde a lo definido en los API_contracts"

**Qué se obtuvo:**
- Confirmación de que los API_Contracts definen `ResponseEntity<T>` estándar sin Mono/Flux — el enfoque correcto es Spring MVC + JPA (síncrono/lineal)
- Tabla comparativa reactivo vs. lineal con conclusión de que el pom.xml del equipo ya estaba correcto
- Correcciones aplicadas:
  1. `pom.xml`: Java 21, AMQP agregado, Validation agregado, dependencias reorganizadas con comentarios por capa
  2. `application.yaml`: GitHub OAuth eliminado, puerto corregido a 8080, open-in-view=false
  3. `V3__rename_roles.sql`: nueva migración sin GitHub, solo rename de roles
  4. `docs/Plan_Desarrollo_Bysone.md`: todas las referencias a WebFlux/R2DBC/StepVerifier/WebTestClient corregidas a MVC/JPA/MockMvc
- Memoria actualizada: nueva regla permanente `feedback_no_reactivo.md` y stack actualizado en `project_stack.md`

**Qué se ajustó:**
- Los docs de arquitectura de sesiones anteriores (`Contexto_Proyecto_IA.md`, `Plan_Desarrollo_Bysone.md`) decían WebFlux por error — corregido a Spring MVC

---

### Prompt 3.3 — Creación del proyecto Next.js (Fase A frontend)
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "si por favor continua, recuerda porfa actualziar en el archivo de prompts_utilizados los prompts literales que coloque de aca en adelante."

**Qué se obtuvo:**
- Proyecto Next.js 14 completo en `frontend/` con toda la estructura definida en la arquitectura:
  - `package.json` con todas las dependencias: Next.js 14, NextAuth v5, TanStack Query, React Hook Form + Zod, Recharts, shadcn/ui base (Radix UI + CVA), Axios, Jest + Testing Library
  - `next.config.ts` con `output: 'standalone'` para Docker
  - `tsconfig.json` con alias `@/*` → `src/*`
  - `tailwind.config.ts` con variables CSS shadcn/ui
  - `postcss.config.js`, `jest.config.ts`, `jest.setup.ts`
  - `src/lib/types.ts` — todos los tipos TypeScript alineados con los API Contracts
  - `src/lib/api.ts` — Axios con interceptores JWT y manejo de 401
  - `src/lib/auth.ts` — NextAuth v5 con Google + Microsoft (sin GitHub)
  - `src/lib/queryClient.ts` — TanStack Query config
  - `src/lib/utils.ts` — `cn()`, `formatCurrency()`, `formatPercent()`
  - `src/hooks/useSimulacion.ts` — estado pre-guardado en memoria (calcular → confirmar → guardar)
  - `src/hooks/useCalibracion.ts` — avance del wizard paso a paso con manejo de encuesta pendiente (409)
  - 7 páginas completas: login, dashboard, calibracion, calibracion/resultado, simulacion, simulacion/historial, perfil
  - Componentes: `Providers`, `Header`, `Navbar`, `Spinner`, `Button`, `PreguntaCard`, `BarraProgreso`, `FormularioSimulacion`, `GraficaProyeccion`, `HistorialItem`, `PerfilCard`, `PortafolioBreakdown`
  - Guard de sesión en `(dashboard)/layout.tsx` con redirección a `/login`
  - Handler NextAuth en `app/api/auth/[...nextauth]/route.ts`

**Qué se ajustó:**
- N/A — primera implementación completa del frontend

---

## Etapa 4 · Pruebas y calidad

> *Los prompts de esta etapa se irán registrando a medida que avancemos.*

---

## Etapa 5 · Despliegue y entrega

> *Los prompts de esta etapa se irán registrando a medida que avancemos.*

---

## Reflexión general

> *Se completará al finalizar el proyecto con los aprendizajes sobre el uso de IA como herramienta de desarrollo.*


## Prompts para implementacion 

> creemos que ya tenemos todo lo neesario para que por medio la documetnacion permitir que crees sin supervision todo el proyecto, recomendaria que consdieraramos crear los compoentes de infraestrucura en local via contenedores, pasaramos al backen y luego al frontend. si ves qiue no peudes estar si supeorvisin en la primera parte relacionada con la infraestrucrtura puedo acompañar esa parte, primero diseña el pla a seguir antes de comenzar a trabajar---si actualiza memoria por si nos toca reiniciar en algun momento y tener este punto dep artida------iinterrumpi porque recuerdo que los API_contracts planteanque no se hara el trabajo reactivo sino lineal, confirma y ajusta paraue ya nos ea reactivo sino acordo a lo definido en los API_contracts
---

### Prompt 3.3 — Implementación completa del backend (Fase B)
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "adelante vamos al siguiente paso, antes de ello considera al final del trabajo que crearemos un md general con el desarrollo que ejecutaste, podríamos llamarlos Codificacion_asistida_bysone.md, continua con el siguiente paso"

**Qué se obtuvo:**
- Capa de dominio (entities JPA): `Usuario`, `PerfilInversion`, `PortafolioInversion`, `TipoPlazo`, `Simulacion`, `DetalleProyeccionSimulacion`, `PerfilPortafolio`, `FormulaExposicion`, `PreguntaCalibracion`, `OpcionRespuestaCalibracion`, `EncuestaCalibracion`, `RespuestaEncuestaCalibracion`, `OpcionInversion`, `Disclaimer`
- Repositorios Spring Data JPA con métodos de búsqueda derivados
- DTOs request/response para todos los flujos: auth, usuario, calibración, simulación, perfiles
- Servicios: `UsuarioService`, `CalibracionService`, `SimulacionService`, `PerfilService`
- Controladores REST: `AuthController`, `UsuarioController`, `CalibracionController`, `SimulacionController`, `PerfilController`
- Infraestructura: `SecurityConfig`, `JwtTokenProvider`, `JwtAuthFilter`, `RabbitMqConfig`, `GlobalExceptionHandler`, `OpenApiConfig`
- Mensajería RabbitMQ: `NotificacionProducer`, `NotificacionConsumer`
- Tests unitarios: `SimulacionServiceTest` (4 tests, JUnit 5 + Mockito)

**Qué se ajustó:**
- Se reescribió `SimulacionService` completamente tras detectar métodos `toResumen()` duplicados (×14 variantes) generados en la primera pasada
- Se corrigió la entidad `Usuario` añadiendo relación `@ManyToOne` a `PerfilInversion` que faltaba
- Se añadió `countByActivaTrue()` a `PreguntaCalibracionRepository` que `CalibracionService` necesitaba
- Se excluyó `BackendApplicationTests` del test run unitario con `@ActiveProfiles("test")` ya que requiere infraestructura real

---

### Prompt 3.4 — Tests unitarios SimulacionService
**Fecha:** 2026-04-13
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> (Continuación automática tras BUILD SUCCESS de compilación)

**Qué se obtuvo:**
- `SimulacionServiceTest.java` con 4 escenarios: proyección a N períodos, valor final > inicial, orden min ≤ esperado ≤ max, resumen con ganancia positiva
- BUILD SUCCESS: `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0`

**Qué se ajustó:**
- El test de `contextLoads` del `BackendApplicationTests` se anotó con `@ActiveProfiles("test")` para que no falle en entornos sin BD

---

---

### Prompt 3.5 — Fase C: CI/CD y configuración de despliegue
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Vamos a la siguiente Fase"

**Qué se obtuvo:**
- `.github/workflows/ci.yml` — pipeline CI: build + test backend (JUnit) y type-check + build frontend (Next.js) en paralelo
- `.github/workflows/deploy-backend.yml` — CD: deploy a Fly.io en push a `main` (paths: `backend/**`)
- `.github/workflows/deploy-frontend.yml` — CD: deploy a Fly.io en push a `main` (paths: `frontend/**`)
- `backend/fly.toml` — config Fly.io: app `bysone-backend`, `shared-cpu-1x`, health check `GET /actuator/health`
- `frontend/fly.toml` — config Fly.io: app `bysone-frontend`, `shared-cpu-1x`, health check `GET /`
- `spring-boot-starter-actuator` añadido a `pom.xml` con endpoint `/actuator/health` expuesto
- `docs/Contexto_Proyecto_IA.md` actualizado: stack corregido (MVC no reactivo), estado de avance, guía de primer deploy

**Qué se ajustó:**
- N/A — primera implementación de CI/CD

---

### Prompt 3.6 — Consolidación de migraciones y corrección de flujo de calibración
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "quisiera que las opciones_respuesta_calibracion, quedara en el mismo de datos semilla, para no llenarnos de archivos, lo mismo revisa el v3_rename_roles y considera ponerlo en el v1 o en datos semilla según su objetivo así solo tendrías dos archivos por ahora para el tema de la bd"

> "procede con los pasos directamente, reiniciando el proyecto y verificando paso a paso que todo lo del front lo tenemos implementado según las especificaciones que usamos"

> "no me esta funcionando al parecer memorizo elementos de mi sesion, le di cerrar sesion y no me funciona y no me carga las preguntas, podrías reiniciar todo para que podamos iniciar desde cero y continuar con las pruebas y ajustes"

> "algo hicimos mal entre versiones la calibración funcionaba perfecto en la primera corrida, lo único que nos faltaban eran opciones de respuesta a las preguntas, las adicionaste, corremos de nuevo el proyecto, le cambiaste los puertos, devolvimos los puertos y usamos los iniciales, se agregó código de error si las opciones para cada pregunta no están disponibles, pero ya la simulación no funciona — revisala a fondo pues es una funcionalidad sencilla pedir la respuesta a una pregunta y pasar a la siguiente pero al cargar y contestar una pregunta no pasa a la siguiente sino que vuelve y aparece el error que se adicionó"

**Qué se obtuvo:**
- `V3__rename_roles.sql` y `V4__opciones_calibracion.sql` eliminados; todo consolidado en `V2__datos_semilla.sql`
- `V2` ahora incluye: roles corregidos (ADMIN/MAINTAINER/USER), las 15 opciones de respuesta (3 × 5 preguntas), y el parámetro `TIMEOUT_SESION_INACTIVIDAD_MINUTOS = '5'`
- `application-local.yaml` creado para desarrollo local sin necesidad de exportar variables de entorno
- Corrección en `JwtAuthFilter`: try/catch alrededor de `extractUsername` para devolver 403 en lugar de 500 en tokens inválidos
- `frontend/.env.local` creado con `NEXT_PUBLIC_API_URL=http://localhost:8080`
- `api.ts` actualizado: manejo de 403 igual que 401 (limpiar token + redirigir a /login)
- `auth/callback/page.tsx` reescrito con `<Suspense>` para cumplir requisito de Next.js con `useSearchParams`
- `useCalibracion.ts` corregido: al recibir 409 en `iniciar()` lee `preguntasRespondidas` y posiciona `pasoActual`; al recibir 409 en `responder()` avanza al siguiente paso en lugar de mostrar error

**Qué se ajustó:**
- Los puertos se fijaron definitivamente: backend 8080, frontend 3000; procesos conflictivos se deben matar antes de iniciar
- `Header.tsx`: campo corregido de `correoUsuario` a `correo` alineando con el DTO `UsuarioMeResponse`
- `RespuestaEncuestaCalibracionRepository`: añadido `countByEncuestaCalibracionId(Long)` con retorno `int`
- `CalibracionService.EncuestaPendienteException`: ahora incluye `preguntasRespondidas` calculado vía el repositorio
- `CalibracionController`: el body del 409 incluye `preguntasRespondidas` en el objeto `encuestaPendiente`

---

### Prompt 3.7 — BR-CAL-001, BR-SES-001, useInactividad y tests
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "convierte esta solución en regla de negocio, de paso quiero que incluyas reglas de negocio relacionadas con el inicio de sesión que indican que la sesión debe vencer cada 5 minutos sin actividad, dejando ese tiempo en los parámetros para ser configurado, obligando al inicio de sesión nuevamente, por otro lado quiero que comenzar a implementar ese control de tiempo para la sesión y que implementemos el botón de cierre de sesión, antes de continuar con las demás pruebas, inclúyelo también en los tests"

**Qué se obtuvo:**
- `docs/Reglas_Negocio.md` actualizado con dos reglas nuevas:
  - **BR-CAL-001**: encuesta retomable — el 409 en creación incluye `preguntasRespondidas`; el frontend retoma desde ese paso sin tocar respuestas previas
  - **BR-SES-001**: timeout de inactividad configurable via `parametros_bysone`; el frontend consulta `/api/v1/config/sesion` y activa un temporizador que se reinicia con cualquier evento del usuario
- `ConfiguracionService.java` — lee `TIMEOUT_SESION_INACTIVIDAD_MINUTOS` de BD, fallback 5 min
- `ConfiguracionController.java` — expone `GET /api/v1/config/sesion` sin autenticación
- `SecurityConfig.java` — `/api/v1/config/**` añadido a `permitAll()`
- `frontend/src/hooks/useInactividad.ts` — hook que consulta el endpoint, activa timer en eventos de usuario, al vencer limpia token y redirige a `/login`, limpieza total al desmontar
- `AuthGuard.tsx` — integra `useInactividad()` como única línea adicional
- `Header.tsx` — botón "Cerrar sesión" ya correcto (limpia `bysone_token` + `router.replace('/login')`)
- Tests backend: `ConfiguracionServiceTest.java` (3 casos) + `CalibracionReglaNegocioTest.java` (3 casos) — **6/6 pasando**
- Test frontend: `useInactividad.test.ts` (4 casos con fake timers y mocks de router/api) — **4/4 pasando**
- `ts-node` instalado como devDependency en el frontend (requerido por `jest.config.ts`)

**Qué se ajustó:**
- N/A — primera implementación completa de la funcionalidad de sesión

---

### Prompt 3.8 — Roles, permisos, menú de Configuración y gestión de parámetros
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "antes de continuar quiero entender como manejaremos el tema de roles y permisos según la opción funcional y el rol, por lo que estimamos, solo tendremos un menú que será el de configuraciones de Bysone, que será de acceso solo para el rol administrador, las demás funciones las deben acceder todos los tipos de usuarios administradores y no administradores"

> "incluye en el tema de administración nuevos parámetros y ajustes de parámetros, y procede con tu propuesta complementada con la nuestra"

**Qué se obtuvo:**
- `V2__datos_semilla.sql` actualizado: nueva opción funcional `GESTIONAR_PARAMETROS` (id=6) y asignación completa de roles:
  - ADMIN: las 6 opciones (incluyendo `GESTIONAR_PARAMETROS`)
  - MAINTAINER: perfiles, portafolios, simulación, historial
  - USER: simulación, historial
- `OAuth2AuthSuccessHandler.java`: JWT ahora incluye claim `roles: ["ADMIN"]` para que el frontend controle visibilidad del menú
- `AdminParametrosController.java`: CRUD completo en `/api/v1/admin/parametros` — listar, obtener por ID, buscar por nombre, crear (con normalización de nombre), actualizar valor. Protegido con `@PreAuthorize("hasRole('ADMIN')")`
- `useCurrentUser.ts`: hook frontend que expone `usuario`, `isLoading` y `esAdmin` (lee `/usuarios/me`)
- `Navbar.tsx`: muestra el ítem "Configuración" solo cuando `esAdmin = true`
- `/admin/page.tsx`: página de administración con tabla editable de parámetros y formulario para agregar nuevos. Redirige al inicio si el usuario no es ADMIN
- `docs/Reglas_Negocio.md`: nuevas reglas BR-ROL-001 a BR-ROL-004 y BR-PAR-001 a BR-PAR-003

**Qué se ajustó:**
- El Navbar mejoró su lógica de active-link: usa `pathname.startsWith(href)` con excepción para `/` para que "Configuración" también se marque activo en subrutas `/admin/*`
- Los parámetros no se pueden eliminar desde la interfaz (solo crear y editar) para preservar el historial de configuración del sistema

---

### Prompt 3.9 — Documentación de reglas de negocio de roles y parámetros
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Estaremos haciendo varios cambios creo que no es necesario reiniciar el backend y el proyecto hasta que acumulemos los cambios que queremos probar, por favor crea estas nuevas reglas de negocio en el archivo de reglas de negocio md"

**Qué se obtuvo:**
- `docs/Reglas_Negocio.md` actualizado con 7 nuevas reglas en sus secciones correspondientes:
  - **Sección 2 — Roles:** BR-ROL-001 (tabla roles × opciones), BR-ROL-002 (funciones abiertas vs. exclusivas ADMIN), BR-ROL-003 (roles en JWT y `/me`), BR-ROL-004 (protección doble frontend + backend 403)
  - **Sección 8 — Parámetros:** BR-PAR-001 (solo ADMIN gestiona, cambios en caliente), BR-PAR-002 (normalización de nombre, unicidad), BR-PAR-003 (parámetros no se eliminan por interfaz)
- Fecha del documento actualizada a 2026-04-14

**Qué se ajustó:**
- N/A — solo documentación

---

## Etapa 3.9.1 — PASOS 1-10: Implementación completa de CRUDs Admin

---

### Prompt 3.9.1 — PASO 1: Configuración de Roles × Opciones Funcionales
**Fecha:** 2026-04-14
**Herramienta:** GitHub Copilot (claude-sonnet-4-20250514)

**Qué se pidió:**
> "agregemos la funcionalidad de los demas Cruds en el menu de configuracion y que son requeridos segun el modelo de datos ejemplo: opciones de inversion, portafolios, etc. incluye las validaciones de campos que cumplan con los criterios de aceptotación e incluyamos los test, agrega este en los prompts, si resultan nuevas reglas de negocio las incluimos o neuvos criterios tambien, de esta forma dejemsop todos los cruds listos. Lista el trabajo define plan y vamos paso a paso para uqe sea controlado"

Luego: `"coo recomiendas iniciemos genera aca la lista de lso pasos y muestrame el plan y te doy el go despues"`

Finalmente: `"vamos adelante"`

**Qué se obtuvo:**
- Plan de 10 pasos (PLAN_IMPLEMENTACION_CRUDs_ADMIN.md) con estructura jerárquica:
  - **Fase 1 (Fundación):** Roles×Opciones → Encuesta Calibración → Perfiles Básicos (15 tests)
  - **Fase 2 (Inversiones):** Portafolio×Opción → Perfil×Portafolio → Fórmulas (12 tests)
  - **Fase 3 (Información):** Disclaimers (4 tests)
  - **Fase 4 (Interfaz):** Frontend, Documentación, Integración (3 tests)
- **PASO 1 Ejecución** — Configuración de Roles × Opciones Funcionales:
  - **Entidades JPA:**
    - `OpcionFuncional`: id, nombreOpcionFuncional
    - `RolesXOpcionFuncional`: relación M2M con claves compuestas (idRol, idOpcion)
    - `RolesXOpcionFuncionalId`: clase para clave compuesta (@IdClass)
  - **Repositorios:**
    - `OpcionFuncionalRepository`: findByNombreOpcionFuncional()
    - `RolesXOpcionFuncionalRepository`: findByIdRol(), findByIdOpcion()
  - **DTOs:**
    - `OpcionFuncionalRequest`: validación @NotBlank, @Size(max=150)
    - `OpcionFuncionalResponse`: id, nombreOpcionFuncional
    - `AsignarOpcionARolRequest`: idRol, idOpcion con @NotNull
    - `RolOpcionResponse`: respuesta de asignación con nombres
  - **Servicio:** `RolesOpcionesService`
    - `asignarOpcionARole(idRol, idOpcion)`: validación CA-RXO-01 (no duplicar)
    - `desasignarOpcionDelRol(idRol, idOpcion)`
    - `obtenerOpcionesDelRol(idRol)`
    - `obtenerTodosLosRoles()`, `obtenerTodasLasOpciones()`
  - **Controlador:** `AdminRolesOpcionesController` (@PreAuthorize("hasRole('ADMIN')"))
    - `GET /api/v1/admin/roles-opciones/roles` — listar todos los roles
    - `GET /api/v1/admin/roles-opciones/opciones` — listar opciones funcionales
    - `GET /api/v1/admin/roles-opciones/rol/{idRol}` — obtener opciones de un rol
    - `POST /api/v1/admin/roles-opciones` — asignar opción a rol
    - `DELETE /api/v1/admin/roles-opciones/{idRol}/{idOpcion}` — desasignar
  - **Tests:** `RolesOpcionesServiceTest` (4 tests, @ExtendWith(MockitoExtension.class))
    - Test 1: `testAsignarOpcionARole_Success` — CA-RXO-02 (IDs existen)
    - Test 2: `testAsignarOpcionARole_Duplicada` — CA-RXO-01 (validar no duplicar)
    - Test 3: `testDesasignarOpcionDelRol_Success` — desasignación exitosa
    - Test 4: `testObtenerOpcionesDelRol_Success` — obtener lista de opciones
  - **Resultado:** `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0` ✓ BUILD SUCCESS ✓

**Qué se ajustó:**
- N/A — primera implementación de PASO 1

---

### Prompt 3.10 — CRUDs admin: opciones de inversión, portafolios, preguntas calibración y disclaimers
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "Agregemos la funcionalidad de los demás Cruds en el menú de configuración y que son requeridos según el modelo de datos ejemplo: opciones de inversión, portafolios, etc. incluye las validaciones de campos que cumplan con los criterios de aceptación e incluyamos los test, agrega este en los prompts, si resultan nuevas reglas de negocio las incluimos o nuevos criterios también, de esta forma dejemos todos los cruds listos."

> "Entiendo que debe suministrarse un usuario inicial como administrador en las semillas incluyamos como mínimo el usuario martillodebola@gmail.com con ese rol. Considera que aun no estamos configurando a qué tiene acceso el rol de admin para cuando terminemos de montar toda la funcionalidad nos encargaremos de ello, así que por el momento todos los menús están disponibles para todos los usuarios"

**Qué se obtuvo:**
- **Repositorios nuevos:**
  - `OpcionInversionRepository.java`: `existsByNombreOpcionAndIdOpcionNot()`, `existsByNombreOpcion()`
  - `PortafolioInversionRepository.java`: `existsByNombrePortafolioAndIdPortafolioNot()`, `existsByOpcionInversionId()`
- **Controllers admin (todos `@PreAuthorize("hasRole('ADMIN')")`)**:
  - `AdminOpcionInversionController.java` — `/api/v1/admin/opciones-inversion`: CRUD completo, validación unicidad de nombre, rentabilidades (mín ≥ 0, máx > 0, mín ≤ máx), eliminación protegida si la opción está en un portafolio
  - `AdminPortafolioController.java` — `/api/v1/admin/portafolios`: CRUD completo + `PUT /portafolios/{id}/opciones` para asignar/reemplazar opciones de inversión, eliminación protegida si portafolio asignado a perfil, validación de rentabilidades
  - `AdminPreguntaController.java` — `/api/v1/admin/preguntas-calibracion`: CRUD de preguntas (con `PATCH /{id}/activa`) + CRUD anidado de opciones de respuesta (`/preguntas/{id}/opciones`), validación mínimo 2 opciones por pregunta antes de activar, eliminación de opciones protegida si hay respuestas de encuesta referenciadas
  - `AdminDisclaimerController.java` — `/api/v1/admin/disclaimers`: CRUD completo + `PATCH /{id}/activo`, no se eliminan (RN-DIS-04), validación de fechas (inicio < fin)
- **V2__datos_semilla.sql actualizado**:
  - Nueva opción funcional 6: `GESTIONAR_PARAMETROS`
  - ADMIN: opciones 1–6 (todas); MAINTAINER: 2,3,4,5; USER: 4,5
  - Usuario semilla: `martillodebola@gmail.com` con roles ADMIN + USER (`oauth_sub = 'pending-google-martillodebola'` — se vincula al primer login Google por email fallback en `CustomOidcUserService`)
  - Secuencias actualizadas a valor máximo semilla
- **CustomOidcUserService.java actualizado**: doble búsqueda oauth_sub → email → crear nuevo; al encontrar por email actualiza `oauth_sub` automáticamente
- **Tests backend — 19/19 pasando**:
  - `AdminOpcionInversionControllerTest.java` (5 tests): listar, crear, crear nombre duplicado, actualizar, eliminar con restricción
  - `AdminPortafolioControllerTest.java` (5 tests): listar, crear, actualizar, asignar opciones, eliminar con restricción
  - `AdminPreguntaControllerTest.java` (5 tests): listar, crear pregunta, toggle activa, crear opción, eliminar opción con restricción
  - `AdminDisclaimerControllerTest.java` (4 tests): listar, crear, actualizar, toggle activo
- **Páginas frontend admin (React Query + useMutation)**:
  - `/admin/page.tsx` — hub con tarjetas de navegación a cada sección
  - `/admin/parametros/page.tsx` — tabla editable de parámetros del sistema, formulario de nuevo parámetro
  - `/admin/opciones-inversion/page.tsx` — tabla con CRUD, validaciones de rentabilidad en frontend
  - `/admin/portafolios/page.tsx` — tabla con CRUD + modal de asignación de opciones de inversión (checkboxes)
  - `/admin/preguntas/page.tsx` — lista expandible: cada pregunta despliega sus opciones de respuesta con sub-CRUD inline
  - `/admin/disclaimers/page.tsx` — lista de disclaimers con vigencia, formulario de creación/edición con datetime-local

**Qué se ajustó:**
- `AdminPreguntaControllerTest` — test CA-ORC-05 requería `pregunta.setId(1L)` explícito antes del mock para que el verify por ID no fallara
- Import innecesario de `PortafolioInversionRepository` en `AdminOpcionInversionController` eliminado
- Navbar: todos los menús visibles para todos los usuarios mientras se completa implementación; TODO comments con referencia a BR-ROL-002 para activar restricción ADMIN cuando corresponda

---

### Prompt 3.11 — Funcionalidad completa de "Mi Perfil": datos usuario, estado calibración y edición
**Fecha:** 2026-04-15
**Herramienta:** Claude Code (claude-sonnet-4-6)
**Rama:** feature/perfil-usuario

**Qué se pidió:**
> "adicioneemos la funcionalid del 'Mi Perfil' de tal forma que se muestra la informacion del perfil del usuario de la sesion, su estado si ya hizo la calibracion, sino la ha hecho que indique que la haga y si ya la hizo que muestre sus datos, generemos tambien la opcion de editar los valores, crea los test y ajusta los criterios de aceptacion y las reglas de negocio segun el caso, ademas adiona este prompts y los demas en prompts utilizados"

**Qué se obtuvo:**
- **Backend — nuevos endpoints en `UsuarioController`**:
  - `PUT /api/v1/usuarios/me` — edita nombre y celular del usuario autenticado; correo y proveedor OAuth son inmutables
  - `GET /api/v1/usuarios/me/calibracion` — retorna la última encuesta COMPLETADA (HTTP 200) o HTTP 204 si no existe
- **Backend — cambios de soporte**:
  - `ActualizarUsuarioRequest.java` — DTO con validaciones `@NotBlank`, `@Size`, `@Pattern` para nombre y celular
  - `UltimaEncuestaResponse.java` — nuevo DTO con id, fechaRealizacion, estado, puntajeTotal, perfilAsignado
  - `UsuarioMeResponse.java` — enriquecido con campo `fechaUltimaActualizacionPerfil`
  - `EncuestaCalibracionRepository` — nuevo método `findTopByUsuarioAndEstadoOrderByFechaRealizacionDesc`
  - `UsuarioService` — métodos `actualizarDatos()` y `getUltimaEncuestaCompletada()`
- **Test backend — `UsuarioServiceTest.java`** (7 casos):
  - Sin perfil → `requiereRecalibracion = true`
  - Con perfil reciente → `requiereRecalibracion = false`
  - Perfil con intervalo no vencido → no requiere recalibrar
  - Actualiza nombre y celular correctamente
  - Celular vacío/en blanco se guarda como null (RN-USU-07)
  - Correo y proveedor no se modifican (RN-USU-07)
  - Sin encuesta completada → Optional vacío
  - Con encuesta completada → retorna datos correctos
- **Frontend — nuevos componentes**:
  - `DatosUsuarioCard.tsx` — muestra datos personales con edición inline (nombre y celular)
  - `EstadoCalibracionCard.tsx` — tres estados visuales: "Sin calibrar", "Al día", "Recalibración requerida", con CTA contextual
- **Frontend — nuevo hook `useUsuario.ts`**:
  - `useUsuarioMe()` — query para `/usuarios/me`
  - `useActualizarUsuario()` — mutation `PUT /usuarios/me` con invalidación de caché
  - `useUltimaCalibracion()` — query para `/usuarios/me/calibracion`, maneja HTTP 204 → null
- **Frontend — refactorización `perfil/page.tsx`**: 3 secciones: datos personales, estado calibración, perfil de inversión
- **Test frontend — `DatosUsuarioCard.test.tsx`** (5 casos): lectura, celular no registrado, modo edición, nombre vacío, cancelar
- **Test frontend — `EstadoCalibracionCard.test.tsx`** (4 casos): sin calibrar, al día, vencido, nombre perfil
- **Docs actualizados**:
  - `Reglas_Negocio.md` — RN-USU-06 (campos editables) y RN-USU-07 (celular opcional, nombre obligatorio)
  - `Criterios_Aceptacion.md` — CA-USU-12 a CA-USU-16 (edición, validaciones, endpoint calibración, estados visuales)

**Qué se ajustó:**
- Import innecesario `HttpStatus` / `ResponseStatusException` eliminado de `UsuarioService` por warning del IDE
- Tests frontend usan `jest.mock('@/hooks/useUsuario')` para aislar la mutación sin necesidad de QueryClientProvider

### Prompt 3.12 — Consulta de usuarios en menú Configuración (solo lectura)
**Fecha:** 2026-04-14
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "crea la funcionalidad en el menu configuracion para consultar los usuarios, no permitas la opción de crear o modificar sería solo de consulta"

**Qué se obtuvo:**
- **Backend — `UsuarioAdminResponse.java`**: nuevo DTO record con los mismos campos que `UsuarioMeResponse` (id, nombreCompleto, correo, celular, proveedorOauth, fechaRegistro, fechaUltimaActualizacionPerfil, roles, perfilInversion, requiereRecalibracion)
- **Backend — `AdminUsuariosController.java`**: controlador `@PreAuthorize("hasRole('ADMIN')")` con dos endpoints de solo lectura:
  - `GET /api/v1/admin/usuarios` — lista todos los usuarios con cálculo de `requiereRecalibracion`
  - `GET /api/v1/admin/usuarios/{id}` — detalle individual
- **Frontend — `admin/usuarios/page.tsx`**: tabla con búsqueda (nombre, correo o perfil), columnas: nombre, correo, proveedor (badge de color), perfil asignado (badge), roles, estado calibración. Modal "Ver más" con detalle completo; sin botones de crear/editar/eliminar en ninguna parte
- **Frontend — `admin/page.tsx`**: nueva tarjeta "Usuarios registrados" como primera opción en el hub de Configuración

**Qué se ajustó:**
- Import innecesario `ParametroBysone` removido de `AdminUsuariosController`
- La lógica de cálculo de `requiereRecalibracion` se replica en el controller inline (no se extrae a servicio) para mantener la consulta de solo lectura sin dependencia adicional

**Archivos afectados:**
- `backend/src/main/java/com/bysone/backend/dto/response/UsuarioAdminResponse.java` (nuevo)
- `backend/src/main/java/com/bysone/backend/controller/AdminUsuariosController.java` (nuevo)
- `frontend/src/app/(dashboard)/admin/usuarios/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)

### Prompt 3.13 — CRUD y configuración de perfiles de inversión
**Fecha:** 2026-04-15
**Herramienta:** Claude Code (claude-sonnet-4-6)

**Qué se pidió:**
> "ahora adiciona en configuracion la funcionalidad del crud y configuracion de los perfiles de inversion, considerar que los perfiles se pueden agregar nuevos perfiles a los existentes y que se puedan configurar sus composiciones"

**Qué se obtuvo:**
- **Backend — repositorios nuevos**:
  - `PerfilPortafolioRepository.java` — con `deleteByPerfilId` (JPQL `@Modifying`)
  - `FormulaExposicionRepository.java` — con `deleteByPerfilId` (JPQL `@Modifying`)
- **Backend — repositorios extendidos**:
  - `PerfilInversionRepository` — añadido `findWithAllById` (fetch join de portafolios + fórmulas) y `existsByNombrePerfilIgnoreCase`
  - `UsuarioRepository` — añadido `existsByPerfilInversionId` para verificar usuarios asignados antes de eliminar
- **Backend — `AdminPerfilService.java`** (nuevo): lógica de negocio completa
  - `listar()`, `obtener(id)`
  - `crear(nombre)` — valida nombre único, crea perfil vacío
  - `renombrar(id, nombre)` — valida unicidad excluyendo el propio perfil (case-insensitive)
  - `eliminar(id)` — bloquea si hay usuarios asignados; elimina fórmulas y composición antes del perfil
  - `actualizarComposicion(id, items)` — valida suma = 100%, reemplaza todo en transacción
  - `actualizarFormulas(id, items)` — valida umbralMin ≤ umbralMax, reemplaza todo en transacción
- **Backend — `AdminPerfilController.java`** (nuevo): `@PreAuthorize("hasRole('ADMIN')")`
  - `GET /api/v1/admin/perfiles`
  - `GET /api/v1/admin/perfiles/{id}`
  - `POST /api/v1/admin/perfiles` → HTTP 201
  - `PUT /api/v1/admin/perfiles/{id}`
  - `DELETE /api/v1/admin/perfiles/{id}` → HTTP 204
  - `PUT /api/v1/admin/perfiles/{id}/composicion`
  - `PUT /api/v1/admin/perfiles/{id}/formulas`
- **Test backend — `AdminPerfilServiceTest.java`** (10 casos):
  - crear nombre único → crea correctamente
  - crear nombre duplicado → CONFLICT 409
  - crear nombre en blanco → BAD_REQUEST 400
  - renombrar mismo nombre (case-insensitive) → no lanza excepción
  - renombrar perfil inexistente → NOT_FOUND 404
  - eliminar sin usuarios → elimina correctamente (fórmulas + composición + perfil)
  - eliminar con usuarios asignados → CONFLICT 409
  - eliminar perfil inexistente → NOT_FOUND 404
  - actualizarComposicion suma 100 → guarda correctamente
  - actualizarComposicion no suma 100 → UNPROCESSABLE_ENTITY 422
  - actualizarComposicion portafolio inexistente → UNPROCESSABLE_ENTITY 422
- **Frontend — `admin/perfiles/page.tsx`** (nuevo):
  - Tabla: nombre, rentabilidades ponderadas (min/media/max), nº portafolios asignados
  - Botón "Nuevo perfil" → modal crear (solo nombre)
  - Por fila: Editar (renombra), Composición (panel modal con checkboxes + % por portafolio + indicador suma), Fórmulas (panel modal con checkboxes + umbral min/max), Eliminar
  - Indicador visual de suma total en panel de composición (verde si = 100%, ámbar si no)
- **Frontend — `admin/page.tsx`**: tarjeta "Perfiles de inversión" añadida al hub de Configuración

**Qué se ajustó:**
- `PerfilInversion` ya tenía `cascade = CascadeType.ALL` en colecciones, pero sin `orphanRemoval`, por lo que la estrategia de reemplazo usa repositorios directos con `deleteByPerfilId` + save de nuevas entidades, más predecible en transacciones con JPA
- La validación de nombre único en renombrar excluye el nombre actual del propio perfil para permitir guardar sin cambios o con cambio de case

**Archivos afectados:**
- `backend/.../repository/PerfilPortafolioRepository.java` (nuevo)
- `backend/.../repository/FormulaExposicionRepository.java` (nuevo)
- `backend/.../repository/PerfilInversionRepository.java` (modificado)
- `backend/.../repository/UsuarioRepository.java` (modificado)
- `backend/.../service/AdminPerfilService.java` (nuevo)
- `backend/.../controller/AdminPerfilController.java` (nuevo)
- `backend/.../test/.../service/AdminPerfilServiceTest.java` (nuevo)
- `frontend/src/app/(dashboard)/admin/perfiles/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)
- `docs/Criterios_Aceptacion.md` — CA-PER-04 a CA-PER-09 añadidos
- `docs/Reglas_Negocio.md` — RN-PER-01 actualizado; RN-PER-05, RN-PER-06, RN-PER-07 añadidos

---

### Prompt 3.14 — CRUD de Tipos de Plazo (catálogo unidades de tiempo para simulaciones)

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: segun los Pendientes de Configuración y UX (lista completa actualizada), recuerda en cada punto adicionar los test, ajustar donde sea necesario reglas de negocio, criterios de aceptacion y prompts utilizados. [P-01: Tipos de plazo]"

**Objetivo:** Implementar CRUD completo del catálogo `tipos_plazo` desde el menú de Configuración Bysone, con bloqueo de eliminación si hay simulaciones asociadas.

**Qué se implementó:**
- **Backend — `TipoPlazoRepository`**: añadidos `existsByNombrePlazoIgnoreCase` y `existsByNombrePlazoIgnoreCaseAndIdNot` para validación de unicidad
- **Backend — `SimulacionRepository`**: añadido `existsByTipoPlazoId` para bloquear eliminaciones en uso
- **Backend — `AdminTipoPlazoController`** (nuevo): CRUD completo `/api/v1/admin/tipos-plazo` con `@PreAuthorize("hasRole('ADMIN')")`. Reglas: nombre único case-insensitive (409), factor ≥ 1, bloqueo eliminación si en uso (409)
- **Tests — `AdminTipoPlazoControllerTest`** (nuevo): 8 tests con Mockito — crear válido, nombre duplicado, 404 actualizar inexistente, actualizar correcto, nombre colisión en actualización, 404 eliminar inexistente, bloqueo eliminación con simulaciones, eliminar correcto
- **Frontend — `admin/tipos-plazo/page.tsx`** (nuevo): tabla + formulario inline; validación frontend; botón Cancelar que regresa a `/admin`
- **Frontend — `admin/page.tsx`**: tarjeta "Tipos de plazo" añadida al hub de Configuración

**Archivos afectados:**
- `backend/.../repository/TipoPlazoRepository.java` (modificado)
- `backend/.../repository/SimulacionRepository.java` (modificado)
- `backend/.../controller/AdminTipoPlazoController.java` (nuevo)
- `backend/.../test/.../controller/AdminTipoPlazoControllerTest.java` (nuevo)
- `frontend/src/app/(dashboard)/admin/tipos-plazo/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)
- `docs/Criterios_Aceptacion.md` — CA-TPL-01 a CA-TPL-05 añadidos
- `docs/Reglas_Negocio.md` — RN-TPL-01 a RN-TPL-04 añadidos (sección 9)

---

### Prompt 3.15 — Pantalla Roles × Opciones funcionales (frontend)

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-02: Roles × Opciones]"

**Objetivo:** Crear la pantalla frontend que consume el `AdminRolesOpcionesController` ya existente para gestionar visualmente qué opciones funcionales tiene asignadas cada rol.

**Qué se implementó:**
- **Frontend — `admin/roles-opciones/page.tsx`** (nuevo): matriz roles × opciones con checkboxes; al marcar/desmarcar llama a POST/DELETE del backend. Carga asignaciones actuales de cada rol al montar. Botón Cancelar regresa a `/admin`
- **Frontend — `admin/page.tsx`**: tarjeta "Roles × Opciones funcionales" añadida al hub de Configuración
- **Tests — `src/__tests__/rolesOpciones.test.ts`** (nuevo): 5 tests Jest que validan la lógica del Set de asignaciones (clave "idRol-idOpcion", sin colisiones, múltiples opciones por rol)

**Archivos afectados:**
- `frontend/src/app/(dashboard)/admin/roles-opciones/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)
- `frontend/src/__tests__/rolesOpciones.test.ts` (nuevo)

---

### Prompt 3.16 — Validación de fórmulas de exposición al guardar composición (soft-block)

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-03: Validación fórmulas de exposición al guardar composición]"

**Objetivo:** Implementar la validación soft-block en el backend: al guardar la composición de un perfil, si existe una fórmula de exposición para un par perfil-portafolio, el porcentaje asignado debe estar dentro del rango [umbralMin, umbralMax]. Solo bloquea si la fórmula existe; si no hay fórmula, no aplica restricción.

**Qué se implementó:**
- **Backend — `FormulaExposicionRepository`**: añadido `findByPerfilIdAndPortafolioId` para consultar la fórmula de un par específico
- **Backend — `AdminPerfilService.actualizarComposicion`**: validación previa al replace-all que itera los ítems y verifica contra la fórmula existente (si la hay). Responde HTTP 422 con mensaje descriptivo que incluye el rango permitido
- **Tests — `AdminPerfilServiceTest`**: 4 nuevos tests de fórmulas de exposición (15 tests en total): porcentaje dentro del umbral, porcentaje bajo el umbral mínimo, porcentaje sobre el umbral máximo, sin fórmula definida no aplica restricción
- **Frontend — `perfiles/page.tsx`**: sin cambios estructurales — el `onError` de `mutComposicion` ya propaga el mensaje 422 del backend al estado `error` visible en el modal

**Archivos afectados:**
- `backend/.../repository/FormulaExposicionRepository.java` (modificado)
- `backend/.../service/AdminPerfilService.java` (modificado)
- `backend/.../test/.../service/AdminPerfilServiceTest.java` (modificado — 4 tests añadidos)
- `docs/Reglas_Negocio.md` — RN-PER-04 ya existente cubre la regla; sin cambios adicionales

---

### Prompt 3.17 — Botón Cancelar en todas las secciones de Configuración

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-04: Botón Cancelar en todas las páginas admin]"

**Objetivo:** Agregar un botón "Cancelar" en cada sección del menú de Configuración que, al pulsarse, navega de regreso a `/admin`.

**Qué se implementó:**
- `useRouter` de `next/navigation` importado en todas las páginas admin que no lo tenían
- Botón "Cancelar" (variant outline) en el header de cada página, alineado a la derecha mediante `flex items-center justify-between`
- Páginas modificadas: `preguntas`, `opciones-inversion`, `portafolios`, `disclaimers`, `parametros`, `usuarios`, `perfiles` (las páginas `tipos-plazo` y `roles-opciones` ya lo incluían desde su creación)

**Archivos afectados:**
- `frontend/src/app/(dashboard)/admin/preguntas/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/opciones-inversion/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/portafolios/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/disclaimers/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/parametros/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/usuarios/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/perfiles/page.tsx` (modificado)

---

### Prompt 3.18 — Revisión UX captura de datos en Configuración

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-05: Revisión UX captura de datos]"

**Objetivo:** Revisar todas las páginas del menú Configuración y corregir inconsistencias UX: validaciones frontend, mensajes de error, campos obligatorios marcados y experiencia consistente entre secciones.

**Qué se implementó:**
- **Parámetros** — validación frontend que bloquea guardar si `valorEdit` está vacío; mensaje de error visible cerca de la tabla
- **Portafolios** — `disabled` en botón Eliminar mientras la mutación está en curso
- **Disclaimers** — error movido al interior del formulario (antes aparecía al tope de la página); `required` en inputs obligatorios; `placeholder` descriptivo en Título y Contenido; etiqueta "opcional" en italic para fecha de fin
- **TypeScript** — verificado sin errores de compilación en todas las páginas modificadas

**Archivos afectados:**
- `frontend/src/app/(dashboard)/admin/parametros/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/portafolios/page.tsx` (modificado)
- `frontend/src/app/(dashboard)/admin/disclaimers/page.tsx` (modificado)

---

### Prompt 3.19 — Verificación gráfica de proyección en Simulaciones

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-06: Gráfica de proyección en Simulaciones]"

**Resultado de la revisión:** La gráfica de proyección ya estaba implementada en sesiones anteriores.

**Estado de la funcionalidad:**
- `frontend/src/components/simulacion/GraficaProyeccion.tsx` — componente completo con Recharts (AreaChart): 3 curvas (Mínimo/Esperado/Máximo), eje X por período, eje Y formateado como moneda, tarjeta de resumen con `gananciaEsperada` y `rendimientoPorcentualTotal`
- `frontend/src/app/(dashboard)/simulacion/page.tsx` — importa y usa `GraficaProyeccion` correctamente, condicionado a que `resultado` no sea null
- `frontend/src/hooks/useSimulacion.ts` — hook que gestiona estado del resultado en memoria hasta confirmación
- `frontend/src/lib/types.ts` — tipos `PeriodoProyeccion` y `ResumenSimulacion` definidos correctamente

**Acción tomada:** Ningún cambio de código necesario. P-06 marcado como completado.

---

### Prompt 3.20 — CRUD de Roles

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-07: CRUD de Roles]"

**Objetivo:** Implementar el CRUD completo del catálogo de roles del sistema con bloqueo de eliminación si hay usuarios asignados.

**Qué se implementó:**
- **Backend — `RoleRepository`**: añadidos `existsByNombreRolIgnoreCase` y `existsByNombreRolIgnoreCaseAndIdNot`
- **Backend — `UsuarioRepository`**: añadido `existsByRolesId` para verificar uso del rol
- **Backend — `AdminRolController`** (nuevo): CRUD completo `/api/v1/admin/roles` con `@PreAuthorize("hasRole('ADMIN')")`. Nombre normalizado a MAYÚSCULAS, único case-insensitive (409), bloqueo eliminación con usuarios (409)
- **Tests — `AdminRolControllerTest`** (nuevo): 8 tests — crear único, nombre duplicado 409, 404 actualizar inexistente, actualizar correcto, colisión nombre 409, 404 eliminar inexistente, bloqueo con usuarios 409, eliminar correcto
- **Frontend — `admin/roles/page.tsx`** (nuevo): tabla + formulario; nombre auto-MAYÚSCULAS; botón Cancelar; `disabled` en Eliminar mientras pending
- **Frontend — `admin/page.tsx`**: tarjeta "Roles" añadida al hub de Configuración
- **Docs** — RN-ROL-01 a RN-ROL-03 y CA-ROL-01 a CA-ROL-04 añadidos

**Archivos afectados:**
- `backend/.../repository/RoleRepository.java` (modificado)
- `backend/.../repository/UsuarioRepository.java` (modificado)
- `backend/.../controller/AdminRolController.java` (nuevo)
- `backend/.../test/.../controller/AdminRolControllerTest.java` (nuevo)
- `frontend/src/app/(dashboard)/admin/roles/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)
- `docs/Reglas_Negocio.md` — sección 10 (Roles) añadida
- `docs/Criterios_Aceptacion.md` — CA-ROL-01 a CA-ROL-04 añadidos

---

### Prompt 3.21 — CRUD de Opciones Funcionales

**Prompt literal enviado:**
> "quiero que vayas paso a paso en cada punto de forma autonoma: [...] [P-08: CRUD de Opciones funcionales]"

**Objetivo:** Implementar el CRUD completo del catálogo de opciones funcionales con bloqueo de eliminación si está asignada a roles.

**Qué se implementó:**
- **Backend — `OpcionFuncionalRepository`**: añadidos `existsByNombreOpcionFuncionalIgnoreCase` y `existsByNombreOpcionFuncionalIgnoreCaseAndIdNot`
- **Backend — `AdminOpcionFuncionalController`** (nuevo): CRUD `/api/v1/admin/opciones-funcionales` con `@PreAuthorize("hasRole('ADMIN')")`. Nombre auto-MAYÚSCULAS, único case-insensitive (409), bloqueo eliminación con roles asignados (409)
- **Tests — `AdminOpcionFuncionalControllerTest`** (nuevo): 8 tests — crear única, nombre duplicado 409, 404 actualizar inexistente, actualizar correcto, colisión 409, 404 eliminar inexistente, bloqueo con roles 409, eliminar correcto
- **Frontend — `admin/opciones-funcionales/page.tsx`** (nuevo): tabla + formulario; nombre auto-MAYÚSCULAS; botón Cancelar; `disabled` en Eliminar
- **Frontend — `admin/page.tsx`**: tarjeta "Opciones funcionales" añadida
- **Docs** — CA-OPF-01 a CA-OPF-04 añadidos

**Archivos afectados:**
- `backend/.../repository/OpcionFuncionalRepository.java` (modificado)
- `backend/.../controller/AdminOpcionFuncionalController.java` (nuevo)
- `backend/.../test/.../controller/AdminOpcionFuncionalControllerTest.java` (nuevo)
- `frontend/src/app/(dashboard)/admin/opciones-funcionales/page.tsx` (nuevo)
- `frontend/src/app/(dashboard)/admin/page.tsx` (modificado)
- `docs/Criterios_Aceptacion.md` — CA-OPF-01 a CA-OPF-04 añadidos
