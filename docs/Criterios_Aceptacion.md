# Criterios de Aceptación — Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Basados en el modelo de datos `V1__create_initial_schema.sql` y las reglas de negocio definidas.
>
> **Responsabilidad de validación:**
> - `[BD]` — enforced por constraint en base de datos (CHECK, UNIQUE, NOT NULL, FK)
> - `[APP]` — responsabilidad de la capa de aplicación (Spring Boot)
> - `[BD/APP]` — enforced por BD pero también debe validarse en aplicación para dar mensajes claros al usuario

---

## Índice

1. [Criterios Generales](#1-criterios-generales)
2. [Roles y Acceso Funcional](#2-roles-y-acceso-funcional)
3. [Parámetros del Sistema](#3-parámetros-del-sistema)
4. [Portafolios y Opciones de Inversión](#4-portafolios-y-opciones-de-inversión)
5. [Perfiles de Inversión](#5-perfiles-de-inversión)
6. [Usuarios](#6-usuarios)
7. [Calibración de Perfil](#7-calibración-de-perfil)
8. [Tipos de Plazo](#8-tipos-de-plazo)
9. [Disclaimers](#9-disclaimers)
10. [Simulaciones y Proyecciones](#10-simulaciones-y-proyecciones)

---

## 1. Criterios Generales

Aplican a todas las tablas del modelo.

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-GEN-01 | Todo ID generado (PK tipo BIGSERIAL) debe ser mayor que cero e incremental. No se admiten valores negativos ni cero. | `[BD]` |
| CA-GEN-02 | Ningún campo definido como `NOT NULL` puede recibir un valor nulo o una cadena vacía `''`. | `[BD/APP]` |
| CA-GEN-03 | Toda clave foránea (FK) debe referenciar un registro existente en su tabla padre antes de la inserción. | `[BD]` |
| CA-GEN-04 | No se pueden insertar registros con claves primarias duplicadas. | `[BD]` |
| CA-GEN-05 | Las fechas de registro o realización deben corresponder a la fecha y hora actuales del servidor. No se admiten fechas futuras en campos de auditoría (`fecha_registro`, `fecha_realizacion`, `fecha_simulacion`). | `[APP]` |
| CA-GEN-06 | No se permiten eliminaciones físicas en tablas de catálogo. Los registros se desactivan mediante campos `activo` o de estado. | `[APP]` |
| CA-GEN-07 | Toda relación muchos a muchos (tablas `_x_`) no admite combinaciones de claves duplicadas. | `[BD]` |

---

## 2. Roles y Acceso Funcional

### roles_bysone

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-ROL-01 | `nombre_rol` no puede estar vacío ni superar 100 caracteres. | `[BD/APP]` |
| CA-ROL-02 | `descripcion_rol` es opcional; si se ingresa, no puede superar 300 caracteres. | `[APP]` |

### opciones_funcionales_bysone

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-OFC-01 | `nombre_opcion_funcional` no puede estar vacío ni superar 150 caracteres. | `[BD/APP]` |

### roles_x_opcion_funcional

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-RXO-01 | No se puede asignar la misma opción funcional a un rol más de una vez. | `[BD]` |
| CA-RXO-02 | Tanto `id_rol` como `id_opcion` deben referenciar registros existentes. | `[BD]` |

### usuarios_x_rol

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-UXR-01 | No se puede asignar el mismo rol a un usuario más de una vez. | `[BD]` |
| CA-UXR-02 | Al eliminar un usuario, deben eliminarse primero sus registros en esta tabla. | `[APP]` |

---

## 3. Parámetros del Sistema

### parametros_bysone

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-PAR-01 | `nombre_parametro` no puede estar vacío ni superar 150 caracteres. | `[BD/APP]` |
| CA-PAR-02 | `valor_parametro` no puede estar vacío. | `[BD/APP]` |
| CA-PAR-03 | No pueden existir dos parámetros con el mismo `nombre_parametro`. | `[APP]` |
| CA-PAR-04 | Esta tabla no debe usarse para almacenar textos legales o disclaimers. | `[APP]` |

---

## 4. Portafolios y Opciones de Inversión

### portafolios_inversion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-POR-01 | `nombre_portafolio_inversion` no puede estar vacío ni superar 150 caracteres. | `[BD/APP]` |
| CA-POR-02 | `rentabilidad_calculada_variable_maximo` debe ser mayor o igual a `rentabilidad_calculada_variable_minimo`. | `[APP]` |
| CA-POR-03 | Ambas rentabilidades deben ser mayores o iguales a 0. | `[APP]` |

### opciones_inversion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-OPC-01 | `nombre_opcion_inversion` no puede estar vacío ni superar 150 caracteres. | `[BD/APP]` |
| CA-OPC-02 | `rentabilidad_maxima` debe ser mayor o igual a `rentabilidad_minima`. | `[APP]` |
| CA-OPC-03 | Ambas rentabilidades deben ser mayores o iguales a 0. | `[APP]` |

### portafolio_inversion_x_opciones_inversion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-PXO-01 | No se puede asociar la misma opción a un portafolio más de una vez. | `[BD]` |

---

## 5. Perfiles de Inversión

### perfiles_inversion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-PER-01 | `nombre_perfil_inversion` no puede estar vacío ni superar 100 caracteres. | `[BD/APP]` |
| CA-PER-02 | `nombre_perfil_inversion` debe ser único en el sistema. | `[APP]` |
| CA-PER-03 | La `rentabilidadMinima` del perfil es la suma ponderada de `rentabilidadCalculadaVariableMinimo` de cada portafolio por su `porcentaje / 100`. La `rentabilidadMaxima` se calcula análogamente con `rentabilidadCalculadaVariableMaximo`. La `rentabilidadMedia` es `(rentabilidadMinima + rentabilidadMaxima) / 2`. Estos campos son calculados por el backend al servir `GET /api/v1/perfiles`. | `[APP]` |

### perfiles_inversion_x_portafolios_inversion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-PXP-01 | `porcentaje` debe ser mayor que 0 y menor o igual a 100. | `[APP]` |
| CA-PXP-02 | La suma de los `porcentaje` de todos los portafolios asociados a un perfil debe ser exactamente 100.00. | `[APP]` |
| CA-PXP-03 | No se puede asociar el mismo portafolio a un perfil más de una vez. | `[BD]` |

### formulas_exposicion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-FEX-01 | `umbral_porcentaje_max_1` debe ser mayor o igual a `umbral_porcentaje_min_1`. | `[APP]` |
| CA-FEX-02 | Ambos umbrales deben estar entre 0.00 y 100.00. | `[APP]` |
| CA-FEX-03 | Solo puede existir una fórmula de exposición por combinación perfil-portafolio. | `[BD]` |

---

## 6. Usuarios

### usuarios

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-USU-01 | `correo_usuario` debe tener formato de email válido (contiene `@` y dominio). | `[APP]` |
| CA-USU-02 | `correo_usuario` debe ser único en el sistema. | `[BD]` |
| CA-USU-03 | `proveedor_oauth` solo acepta los valores `'GOOGLE'` o `'MICROSOFT'`. | `[BD]` |
| CA-USU-04 | `oauth_sub` no puede estar vacío y debe ser único en el sistema. | `[BD/APP]` |
| CA-USU-05 | `nombre_completo_usuario` no puede estar vacío ni superar 200 caracteres. | `[BD/APP]` |
| CA-USU-06 | `celular_usuario`, si se ingresa, debe contener solo dígitos y no superar 20 caracteres. | `[APP]` |
| CA-USU-07 | `fecha_registro` debe corresponder a la fecha y hora actuales del servidor. No se admiten fechas futuras. | `[BD/APP]` |
| CA-USU-08 | `fecha_ultima_actualizacion_perfil_inversion`, cuando se establece, no puede ser anterior a `fecha_registro`. | `[APP]` |
| CA-USU-09 | Si `id_perfil_inversion` es nulo, `fecha_ultima_actualizacion_perfil_inversion` también debe ser nula. | `[APP]` |
| CA-USU-10 | Al asignar o cambiar el perfil de inversión, se debe actualizar simultáneamente `fecha_ultima_actualizacion_perfil_inversion` con la fecha y hora actuales. | `[APP]` |
| CA-USU-11 | `requiereRecalibracion` es `true` cuando `fechaUltimaActualizacionPerfilInversion` es nula o cuando la diferencia entre la fecha actual del servidor y dicho campo supera el valor del parámetro `INTERVALO_RECALIBRACION_DIAS`. | `[APP]` |

---

## 7. Calibración de Perfil

### preguntas_calibracion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-PRG-01 | `texto_pregunta` no puede estar vacío ni superar 500 caracteres. | `[BD/APP]` |
| CA-PRG-02 | `orden` debe ser mayor que 0. | `[APP]` |
| CA-PRG-03 | No pueden existir dos preguntas activas con el mismo `orden`. | `[APP]` |
| CA-PRG-04 | Las preguntas no se eliminan físicamente; se desactivan estableciendo `activa = FALSE`. | `[APP]` |

### opciones_respuesta_calibracion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-ORC-01 | `texto_opcion` no puede estar vacío ni superar 300 caracteres. | `[BD/APP]` |
| CA-ORC-02 | `puntaje` debe ser mayor o igual a 0. | `[APP]` |
| CA-ORC-03 | `orden` debe ser mayor que 0. | `[APP]` |
| CA-ORC-04 | No pueden existir dos opciones con el mismo `orden` dentro de la misma pregunta. | `[APP]` |
| CA-ORC-05 | Cada pregunta debe tener al menos dos opciones de respuesta para poder ser presentada. | `[APP]` |

### encuestas_calibracion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-ENC-01 | `origen` solo acepta los valores `'DEMANDA'` o `'SISTEMA'`. | `[BD]` |
| CA-ENC-02 | `estado` solo acepta los valores `'PENDIENTE'` o `'COMPLETADA'`. | `[BD]` |
| CA-ENC-03 | `fecha_realizacion` debe corresponder a la fecha y hora actuales del servidor. No se admiten fechas futuras. | `[BD/APP]` |
| CA-ENC-04 | `fecha_vencimiento`, cuando se establece, debe ser estrictamente mayor a `fecha_realizacion`. | `[APP]` |
| CA-ENC-05 | `puntaje_total` solo puede establecerse cuando `estado = 'COMPLETADA'`. Debe ser mayor o igual a 0. | `[APP]` |
| CA-ENC-06 | `id_perfil_resultado` solo puede establecerse cuando `estado = 'COMPLETADA'`. | `[APP]` |
| CA-ENC-07 | Al completar la encuesta, `puntaje_total`, `id_perfil_resultado` y `estado` deben actualizarse en la misma transacción. | `[APP]` |
| CA-ENC-08 | Un usuario no puede tener más de una encuesta en estado `'PENDIENTE'` al mismo tiempo. | `[APP]` |
| CA-ENC-09 | Si la publicación del evento de notificación (RabbitMQ) falla al completar la encuesta, la calibración debe completarse exitosamente. La notificación es eventual, no bloquea la transacción. | `[APP]` |
| CA-ENC-10 | Un usuario solo puede registrar respuestas y completar encuestas propias. El acceso a encuestas de otro usuario debe retornar error 403. | `[APP]` |

### respuestas_encuesta_calibracion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-REC-01 | No se puede registrar más de una respuesta por pregunta dentro de la misma encuesta. | `[BD]` |
| CA-REC-02 | Solo se pueden responder preguntas activas (`activa = TRUE`). | `[APP]` |
| CA-REC-03 | La opción de respuesta seleccionada debe pertenecer a la pregunta que se está respondiendo. | `[APP]` |
| CA-REC-04 | No se pueden agregar respuestas a una encuesta en estado `'COMPLETADA'`. | `[APP]` |

---

## 8. Tipos de Plazo

### tipos_plazo

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-TPL-01 | `nombre_plazo` no puede estar vacío, debe ser único y no superar 50 caracteres. | `[BD/APP]` |
| CA-TPL-02 | `factor_conversion_dias` debe ser mayor que 0. | `[BD]` |
| CA-TPL-03 | Los valores válidos de `nombre_plazo` son: `'DÍA'`, `'MES'`, `'TRIMESTRE'`, `'AÑO'`. | `[APP]` |

---

## 9. Disclaimers

### disclaimers_bysone

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-DIS-01 | `titulo` no puede estar vacío ni superar 200 caracteres. | `[BD/APP]` |
| CA-DIS-02 | `contenido` no puede estar vacío. | `[BD/APP]` |
| CA-DIS-03 | `fecha_vigencia_desde` debe corresponder a la fecha y hora actuales del servidor al momento de inserción. | `[APP]` |
| CA-DIS-04 | `fecha_vigencia_hasta`, cuando se establece, debe ser estrictamente mayor a `fecha_vigencia_desde`. | `[APP]` |
| CA-DIS-05 | No pueden coexistir dos disclaimers activos con rangos de vigencia solapados. | `[APP]` |
| CA-DIS-06 | Los disclaimers no se eliminan físicamente; se desactivan con `activo = FALSE` o estableciendo `fecha_vigencia_hasta`. | `[APP]` |

---

## 10. Simulaciones y Proyecciones

### simulaciones_bysone

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-SIM-01 | `valor_inversion_inicial` debe ser mayor que 0. | `[APP]` |
| CA-SIM-02 | `valor_inversion_periodica`, si se establece, debe ser mayor o igual a 0. | `[APP]` |
| CA-SIM-03 | `plazo_inversion` debe ser mayor que 0. | `[APP]` |
| CA-SIM-04 | `fecha_simulacion` debe corresponder a la fecha y hora actuales del servidor. No se admiten fechas futuras. | `[BD/APP]` |
| CA-SIM-05 | `nombre_perfil_simulado` no puede estar vacío. Debe coincidir con el nombre del perfil referenciado por `id_perfil_inversion` al momento de la simulación. | `[APP]` |
| CA-SIM-06 | El disclaimer referenciado (`id_disclaimer`), si se establece, debe estar activo y dentro de su rango de vigencia al momento de crear la simulación. | `[APP]` |
| CA-SIM-07 | El usuario que simula debe tener un perfil de inversión asignado (`id_perfil_inversion` en `usuarios` no nulo). | `[APP]` |
| CA-SIM-08 | Solo se inserta el registro si el usuario confirma explícitamente que desea guardar la simulación. | `[APP]` |
| CA-SIM-09 | Un usuario solo puede consultar y listar sus propias simulaciones. El acceso a simulaciones de otro usuario debe retornar error 403. | `[APP]` |

### detalle_proyeccion_simulacion

| ID | Criterio | Responsable |
|----|----------|-------------|
| CA-DET-01 | `periodo` debe ser mayor que 0. | `[APP]` |
| CA-DET-02 | Los períodos deben ser consecutivos desde 1 hasta el valor de `plazo_inversion` de la cabecera. | `[APP]` |
| CA-DET-03 | El número total de filas de detalle debe ser igual a `plazo_inversion` de la cabecera. | `[APP]` |
| CA-DET-04 | `valor_proyectado_minimo` ≤ `valor_proyectado_esperado` ≤ `valor_proyectado_maximo`. | `[APP]` |
| CA-DET-05 | Los tres valores proyectados deben ser mayores o iguales a 0. | `[APP]` |
| CA-DET-06 | `rentabilidad_maxima_aplicada` debe ser mayor o igual a `rentabilidad_minima_aplicada`. | `[APP]` |
| CA-DET-07 | No puede existir más de un registro para el mismo `periodo` dentro de la misma simulación. | `[BD]` |
| CA-DET-08 | Los registros de detalle y la cabecera deben insertarse en la misma transacción. Si falla algún detalle, se revierte toda la simulación. | `[APP]` |

---

> Documento generado el 2026-04-13. Debe revisarse y actualizarse cuando cambien el modelo de datos o las reglas de negocio.
