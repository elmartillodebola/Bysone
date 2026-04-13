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

> *Los prompts de esta etapa se irán registrando a medida que avancemos.*

---

## Etapa 4 · Pruebas y calidad

> *Los prompts de esta etapa se irán registrando a medida que avancemos.*

---

## Etapa 5 · Despliegue y entrega

> *Los prompts de esta etapa se irán registrando a medida que avancemos.*

---

## Reflexión general

> *Se completará al finalizar el proyecto con los aprendizajes sobre el uso de IA como herramienta de desarrollo.*
