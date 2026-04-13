# Reglas de Negocio — Mi Portafolio Inteligente

> **Hackaton 2026 · Comunidad de Desarrollo de Software · Protección**
> Derivadas del modelo de datos `V1__create_initial_schema.sql`

---

## Índice

1. [Usuarios y Autenticación](#1-usuarios-y-autenticación)
2. [Roles y Acceso Funcional](#2-roles-y-acceso-funcional)
3. [Perfiles de Inversión](#3-perfiles-de-inversión)
4. [Portafolios y Opciones de Inversión](#4-portafolios-y-opciones-de-inversión)
5. [Calibración de Perfil](#5-calibración-de-perfil)
6. [Simulación y Proyección](#6-simulación-y-proyección)
7. [Disclaimers](#7-disclaimers)
8. [Parámetros del Sistema](#8-parámetros-del-sistema)

---

## 1. Usuarios y Autenticación

**RN-USU-01** — El acceso al sistema es exclusivamente mediante OAuth2. Los únicos proveedores aceptados son `GOOGLE` y `MICROSOFT`. No existe registro con usuario y contraseña propios.

**RN-USU-02** — El correo electrónico de un usuario es único en el sistema. No pueden existir dos cuentas con el mismo correo.

**RN-USU-03** — El identificador del proveedor OAuth (`oauth_sub`) es único en el sistema. No puede haber dos usuarios con el mismo `sub`, independientemente del proveedor.

**RN-USU-04** — Al primer inicio de sesión, el usuario queda registrado sin perfil de inversión asignado (`id_perfil_inversion` nulo). El perfil se asigna únicamente al completar la encuesta de calibración.

**RN-USU-05** — Cada vez que el perfil de un usuario cambia, se debe actualizar `fecha_ultima_actualizacion_perfil_inversion`. Este campo es la referencia que usa el sistema para determinar si el usuario requiere una nueva calibración.

---

## 2. Roles y Acceso Funcional

**RN-ROL-01** — Un usuario puede tener uno o más roles asignados simultáneamente.

**RN-ROL-02** — Un rol agrupa un conjunto de opciones funcionales. El acceso a cualquier funcionalidad del sistema está determinado por los roles asignados al usuario.

**RN-ROL-03** — La misma opción funcional puede pertenecer a más de un rol.

---

## 3. Perfiles de Inversión

**RN-PER-01** — Existen tres perfiles de inversión: Conservador, Moderado y Agresivo. Cada perfil tiene una distribución porcentual sobre uno o más portafolios.

**RN-PER-02** — La suma de los porcentajes asignados a los portafolios de un perfil debe ser exactamente 100%. Esta validación es responsabilidad de la capa de aplicación.

**RN-PER-03** — Para cada combinación perfil-portafolio solo puede existir una fórmula de exposición. No se permiten duplicados.

**RN-PER-04** — La fórmula de exposición define los umbrales mínimo y máximo de exposición permitida de un perfil sobre un portafolio. Sirve como límite de alerta o control de riesgo.

---

## 4. Portafolios y Opciones de Inversión

**RN-POR-01** — Un portafolio está compuesto por una o más opciones de inversión. La relación es de muchos a muchos: una opción puede pertenecer a varios portafolios.

**RN-POR-02** — Cada portafolio tiene una rentabilidad calculada mínima y máxima. Estos valores representan el rango de rendimiento esperado del portafolio completo.

**RN-POR-03** — Cada opción de inversión tiene su propia rentabilidad mínima y máxima, independiente del portafolio al que pertenezca.

---

## 5. Calibración de Perfil

**RN-CAL-01** — Todo usuario nuevo debe completar una encuesta de calibración antes de poder realizar simulaciones. Sin perfil asignado no se habilita la simulación.

**RN-CAL-02** — Una encuesta puede ser iniciada por dos orígenes:
- `DEMANDA`: el usuario la solicita voluntariamente.
- `SISTEMA`: el sistema la dispara automáticamente al detectar que venció el plazo de vigencia del perfil actual.

**RN-CAL-03** — El sistema determina la necesidad de recalibración comparando `usuarios.fecha_ultima_actualizacion_perfil_inversion` con el intervalo definido en `parametros_bysone`. Si el tiempo transcurrido supera dicho parámetro, se crea una nueva encuesta con origen `SISTEMA`.

**RN-CAL-04** — Una encuesta tiene dos estados posibles: `PENDIENTE` (iniciada pero no terminada) y `COMPLETADA` (todas las preguntas respondidas y perfil calculado).

**RN-CAL-05** — Cada pregunta activa solo puede responderse una vez por encuesta. No se permite modificar una respuesta ya registrada dentro de la misma encuesta.

**RN-CAL-06** — Solo se presentan al usuario las preguntas marcadas como activas (`activa = TRUE`). Las preguntas inactivas se excluyen del cuestionario sin eliminarse del catálogo.

**RN-CAL-07** — El perfil resultante se determina por la suma de los puntajes de las opciones seleccionadas. Los rangos de puntaje por perfil son configurables a través de `parametros_bysone`.

**RN-CAL-08** — Al completar la encuesta se deben actualizar simultáneamente:
- `encuestas_calibracion.puntaje_total`
- `encuestas_calibracion.id_perfil_resultado`
- `encuestas_calibracion.estado` → `COMPLETADA`
- `usuarios.id_perfil_inversion`
- `usuarios.fecha_ultima_actualizacion_perfil_inversion`

**RN-CAL-09** — El historial de encuestas de un usuario se conserva. No se eliminan encuestas previas al asignar un nuevo perfil.

---

## 6. Simulación y Proyección

**RN-SIM-01** — Un usuario puede simular usando su perfil asignado o escogiendo cualquier otro perfil disponible. La simulación no modifica el perfil del usuario.

**RN-SIM-02** — Para acceder a la simulación, el usuario debe tener un perfil de inversión asignado (ver RN-CAL-01).

**RN-SIM-03** — La simulación se calcula en memoria y se muestra al usuario antes de decidir si la guarda. Si el usuario no guarda, no se persiste ningún dato en la base de datos.

**RN-SIM-04** — Solo si el usuario decide guardar la simulación se insertan registros en `simulaciones_bysone` y `detalle_proyeccion_simulacion`.

**RN-SIM-05** — La proyección genera una fila por período en `detalle_proyeccion_simulacion`, con tres escenarios: mínimo, máximo y esperado. El período corresponde a la unidad definida en `tipos_plazo` (DÍA, MES, TRIMESTRE, AÑO).

**RN-SIM-06** — Las tasas de rentabilidad aplicadas en el cálculo (`rentabilidad_minima_aplicada`, `rentabilidad_maxima_aplicada`) se guardan como snapshot en el detalle. Esto garantiza que la simulación sea reproducible aunque la configuración del portafolio cambie en el futuro.

**RN-SIM-07** — El nombre del perfil usado se guarda como snapshot (`nombre_perfil_simulado`) en la cabecera de la simulación, por la misma razón de reproducibilidad.

**RN-SIM-08** — No puede existir más de un registro para el mismo período dentro de una simulación.

**RN-SIM-09** — El campo `plazo_inversion` es un número entero cuya unidad está definida por `tipos_plazo`. El motor de cálculo convierte el plazo a días usando `factor_conversion_dias` para estandarizar los cálculos internos.

**RN-SIM-10** — Al guardar una simulación, se referencia el disclaimer vigente en ese momento. Un disclaimer se considera vigente si `activo = TRUE` y la fecha actual está dentro del rango `fecha_vigencia_desde` / `fecha_vigencia_hasta` (o si `fecha_vigencia_hasta` es nula).

---

## 7. Disclaimers

**RN-DIS-01** — Cada simulación guardada debe estar asociada al disclaimer vigente al momento de realizarla. Esto garantiza trazabilidad legal sobre qué texto vio el usuario.

**RN-DIS-02** — Un disclaimer es vigente si cumple simultáneamente: `activo = TRUE` y la fecha actual es mayor o igual a `fecha_vigencia_desde`. Si `fecha_vigencia_hasta` es nula, el disclaimer no tiene fecha de expiración.

**RN-DIS-03** — No se utilizan los `parametros_bysone` para almacenar textos legales. Los disclaimers tienen su propia tabla con soporte para texto extenso (`TEXT`) y gestión de vigencia.

**RN-DIS-04** — Los disclaimers no se eliminan; se desactivan (`activo = FALSE`) o se les asigna una `fecha_vigencia_hasta` para que dejen de aplicar. El historial de disclaimers se conserva.

---

## 8. Parámetros del Sistema

**RN-PAR-01** — `parametros_bysone` almacena exclusivamente configuración técnica y de negocio del sistema: intervalos de recalibración, umbrales de puntaje por perfil, tasas de referencia u otros valores configurables.

**RN-PAR-02** — Los textos legales (disclaimers) no se almacenan en `parametros_bysone`. Ver sección 7.

**RN-PAR-03** — Los valores de los parámetros son de tipo texto (`VARCHAR`). La capa de aplicación es responsable de parsear el valor según el tipo de dato esperado (entero, decimal, fecha, etc.).

---

> Documento generado el 2026-04-13. Debe actualizarse cada vez que el modelo de datos o los requisitos de negocio cambien.
