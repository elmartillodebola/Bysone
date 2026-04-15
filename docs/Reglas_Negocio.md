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

**BR-SES-002** — Al cerrar sesión, el backend fuerza a Google a mostrar el selector de cuenta en el siguiente inicio de sesión (`prompt=select_account`). Esto impide que el usuario vuelva a entrar automáticamente con la sesión OAuth cacheada en el navegador, garantizando que siempre haya una acción consciente de autenticación.

**BR-SES-001** — La sesión de un usuario expira automáticamente tras un período de inactividad configurable. El tiempo de inactividad se almacena en `parametros_bysone` bajo la clave `TIMEOUT_SESION_INACTIVIDAD_MINUTOS` (valor por defecto: 5 minutos). El frontend consulta este valor en `/api/v1/config/sesion` (endpoint público) al iniciar y activa un temporizador que se reinicia ante cualquier evento del usuario (clic, teclado, scroll). Al cumplirse el tiempo sin actividad, el token JWT se elimina del almacenamiento local y el usuario es redirigido a `/login`. Un administrador puede cambiar el valor directamente en la base de datos sin necesidad de redespliegue.

**RN-USU-01** — El acceso al sistema es exclusivamente mediante OAuth2. Los únicos proveedores aceptados son `GOOGLE` y `MICROSOFT`. No existe registro con usuario y contraseña propios.

**RN-USU-02** — El correo electrónico de un usuario es único en el sistema. No pueden existir dos cuentas con el mismo correo.

**RN-USU-03** — El identificador del proveedor OAuth (`oauth_sub`) es único en el sistema. No puede haber dos usuarios con el mismo `sub`, independientemente del proveedor.

**RN-USU-04** — Al primer inicio de sesión, el usuario queda registrado sin perfil de inversión asignado (`id_perfil_inversion` nulo). El perfil se asigna únicamente al completar la encuesta de calibración.

**RN-USU-05** — Cada vez que el perfil de un usuario cambia, se debe actualizar `fecha_ultima_actualizacion_perfil_inversion`. Este campo es la referencia que usa el sistema para determinar si el usuario requiere una nueva calibración.

**RN-USU-06** — Un usuario autenticado puede editar únicamente su nombre completo y su número de celular a través de `PUT /api/v1/usuarios/me`. El correo electrónico y el proveedor OAuth provienen del Identity Provider y no son editables por el usuario.

**RN-USU-07** — El celular es un campo opcional. Si el usuario lo deja en blanco durante la edición, se guarda como `NULL` en base de datos. El nombre completo es obligatorio y no puede quedar vacío.

---

## 2. Roles y Acceso Funcional

**RN-ROL-01** — Un usuario puede tener uno o más roles asignados simultáneamente.

**RN-ROL-02** — Un rol agrupa un conjunto de opciones funcionales. El acceso a cualquier funcionalidad del sistema está determinado por los roles asignados al usuario.

**RN-ROL-03** — La misma opción funcional puede pertenecer a más de un rol.

**BR-ROL-001** — El sistema define tres roles con la siguiente distribución de opciones funcionales:

| Rol | Opciones funcionales |
|---|---|
| `ADMIN` | GESTIONAR_USUARIOS, GESTIONAR_PERFILES, GESTIONAR_PORTAFOLIOS, REALIZAR_SIMULACION, VER_HISTORIAL_SIMULACIONES, **GESTIONAR_PARAMETROS** |
| `MAINTAINER` | GESTIONAR_PERFILES, GESTIONAR_PORTAFOLIOS, REALIZAR_SIMULACION, VER_HISTORIAL_SIMULACIONES |
| `USER` | REALIZAR_SIMULACION, VER_HISTORIAL_SIMULACIONES |

**BR-ROL-002** — Las funcionalidades de uso final (calibración, simulación, historial, perfil propio) son accesibles para cualquier usuario autenticado independientemente de su rol. La única funcionalidad exclusiva del rol `ADMIN` es el menú de **Configuración Bysone** (`GESTIONAR_PARAMETROS`).

**BR-ROL-003** — El rol del usuario se incluye como claim `roles` dentro del JWT emitido al autenticarse. El frontend lo utiliza para mostrar u ocultar el menú de Configuración sin necesidad de una llamada adicional al backend. El endpoint `/api/v1/usuarios/me` también devuelve los roles para uso en cualquier componente que lo requiera.

**BR-ROL-004** — Las rutas `/api/v1/admin/**` están protegidas exclusivamente para el rol `ADMIN`. Si un usuario sin ese rol intenta acceder directamente a una URL del panel de administración, el frontend lo redirige al inicio y el backend responde con HTTP 403.

---

## 3. Perfiles de Inversión

**RN-PER-01** — Existen tres perfiles de inversión base: Conservador, Moderado y Agresivo. El sistema permite crear nuevos perfiles adicionales desde la pantalla de Configuración sin necesidad de redespliegue.

**RN-PER-02** — La suma de los porcentajes asignados a los portafolios de un perfil debe ser exactamente 100%. Esta validación es responsabilidad de la capa de aplicación al actualizar la composición.

**RN-PER-03** — Para cada combinación perfil-portafolio solo puede existir una fórmula de exposición. No se permiten duplicados.

**RN-PER-04** — La fórmula de exposición define los umbrales mínimo y máximo de exposición permitida de un perfil sobre un portafolio. Sirve como límite de alerta o control de riesgo.

**RN-PER-05** — Un perfil no puede eliminarse si algún usuario lo tiene asignado como perfil de inversión activo. El sistema debe devolver un error descriptivo y no proceder con la eliminación.

**RN-PER-06** — El nombre del perfil es único en el sistema (sin distinguir mayúsculas/minúsculas). Intentar crear o renombrar un perfil con un nombre ya existente resulta en error HTTP 409.

**RN-PER-07** — La actualización de composición (portafolios + porcentajes) y la de fórmulas de exposición son operaciones de reemplazo total: se eliminan todas las filas previas y se insertan las nuevas en la misma transacción.

---

## 4. Portafolios y Opciones de Inversión

**RN-POR-01** — Un portafolio está compuesto por una o más opciones de inversión. La relación es de muchos a muchos: una opción puede pertenecer a varios portafolios.

**RN-POR-02** — Cada portafolio tiene una rentabilidad calculada mínima y máxima. Estos valores representan el rango de rendimiento esperado del portafolio completo.

**RN-POR-03** — Cada opción de inversión tiene su propia rentabilidad mínima y máxima, independiente del portafolio al que pertenezca.

**RN-POR-04** — El nombre de cada opción de inversión es único en el sistema. No pueden existir dos opciones con el mismo nombre (restricción `UNIQUE` en BD + validación en aplicación).

**BR-OPC-001** — Solo los usuarios con rol `ADMIN` pueden crear, editar y gestionar opciones de inversión. La rentabilidad mínima debe ser ≥ 0 y la rentabilidad máxima debe ser > 0. La rentabilidad mínima no puede superar a la máxima.

**BR-OPC-002** — Una opción de inversión no puede eliminarse si está asignada a uno o más portafolios. El sistema responde con HTTP 409 indicando la restricción. Para retirarla, primero debe desvincularse de todos los portafolios que la contienen.

**BR-OPC-003** — El nombre de cada portafolio es único en el sistema. La asignación de opciones de inversión a un portafolio es una operación de reemplazo completo: `PUT /admin/portafolios/{id}/opciones` sustituye todas las opciones actuales por las enviadas en la petición.

**BR-OPC-004** — Un portafolio no puede eliminarse si está asignado a uno o más perfiles de inversión. El sistema responde con HTTP 409. Para eliminarlo, primero debe desvincularse de todos los perfiles.

---

## 5. Calibración de Perfil

**BR-CAL-001** — Si un usuario recarga o vuelve a la pantalla de calibración mientras tiene una encuesta en estado `PENDIENTE`, el sistema detecta la encuesta existente (responde con HTTP 409 en el endpoint de inicio) y retoma el flujo desde la última pregunta no respondida. El frontend lee el campo `preguntasRespondidas` del cuerpo del 409 y posiciona el paso en esa posición. El historial de respuestas ya registradas no se toca.

**RN-CAL-01** — Todo usuario nuevo debe completar una encuesta de calibración antes de poder realizar simulaciones. Sin perfil asignado no se habilita la simulación.

**RN-CAL-02** — Una encuesta puede ser iniciada por dos orígenes:
- `DEMANDA`: el usuario la solicita voluntariamente.
- `SISTEMA`: el sistema la dispara automáticamente al detectar que venció el plazo de vigencia del perfil actual.

**RN-CAL-03** — El sistema determina la necesidad de recalibración comparando `usuarios.fecha_ultima_actualizacion_perfil_inversion` con el intervalo definido en `parametros_bysone`. Si el tiempo transcurrido supera dicho parámetro, se crea una nueva encuesta con origen `SISTEMA`.

**RN-CAL-04** — Una encuesta tiene dos estados posibles: `PENDIENTE` (iniciada pero no terminada) y `COMPLETADA` (todas las preguntas respondidas y perfil calculado).

**RN-CAL-05** — Cada pregunta activa solo puede responderse una vez por encuesta. No se permite modificar una respuesta ya registrada dentro de la misma encuesta.

**RN-CAL-06** — Solo se presentan al usuario las preguntas marcadas como activas (`activa = TRUE`). Las preguntas inactivas se excluyen del cuestionario sin eliminarse del catálogo.

**BR-CAL-002** — Solo los usuarios con rol `ADMIN` pueden crear, editar y activar/desactivar preguntas de calibración y sus opciones de respuesta.

**BR-CAL-003** — Una pregunta solo puede marcarse como activa (`activa = TRUE`) si tiene al menos 2 opciones de respuesta asociadas. El sistema responde con HTTP 409 si se intenta activar una pregunta sin el mínimo de opciones. Criterio de aceptación: CA-ORC-05.

**BR-CAL-004** — Una opción de respuesta no puede eliminarse si existe al menos una `respuesta_encuesta_calibracion` que la referencia. El sistema responde con HTTP 409. Esto garantiza la integridad histórica de las encuestas completadas.

**BR-CAL-005** — Las opciones de respuesta no exponen el campo `puntaje` en los endpoints públicos de calibración para no revelar la lógica de scoring al usuario. El puntaje es gestionado únicamente desde el panel de administración.

**RN-CAL-07** — El perfil resultante se determina por la suma de los puntajes de las opciones seleccionadas. Los rangos de puntaje por perfil son configurables a través de `parametros_bysone`.

**RN-CAL-08** — Al completar la encuesta se deben actualizar simultáneamente:
- `encuestas_calibracion.puntaje_total`
- `encuestas_calibracion.id_perfil_resultado`
- `encuestas_calibracion.estado` → `COMPLETADA`
- `usuarios.id_perfil_inversion`
- `usuarios.fecha_ultima_actualizacion_perfil_inversion`

**RN-CAL-09** — El historial de encuestas de un usuario se conserva. No se eliminan encuestas previas al asignar un nuevo perfil.

---

## 6. Simulación y Proyección (Evaluar si se justifica persistir las simulaciones)

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

**RN-SIM-11** — Cuando un usuario realice la simulación con alguno de los perfiles disponibles, y ser presentado el resultado de la simulación, dar la opcion por medio de un boton de comparar dicha simualacion con otros perfiles de inversion.

**RN-SIM-12** — Cuando un usuario realice la simulación y escoja comparar su simualación con los demas perfiles, la grafica le permitira comparar entre maximos, entre minimos y entre medias de cada perfil unicamente. 
---

## 7. Disclaimers

**RN-DIS-01** — Cada simulación guardada debe estar asociada al disclaimer vigente al momento de realizarla. Esto garantiza trazabilidad legal sobre qué texto vio el usuario.

**RN-DIS-02** — Un disclaimer es vigente si cumple simultáneamente: `activo = TRUE` y la fecha actual es mayor o igual a `fecha_vigencia_desde`. Si `fecha_vigencia_hasta` es nula, el disclaimer no tiene fecha de expiración.

**RN-DIS-03** — No se utilizan los `parametros_bysone` para almacenar textos legales. Los disclaimers tienen su propia tabla con soporte para texto extenso (`TEXT`) y gestión de vigencia.

**RN-DIS-04** — Los disclaimers no se eliminan; se desactivan (`activo = FALSE`) o se les asigna una `fecha_vigencia_hasta` para que dejen de aplicar. El historial de disclaimers se conserva.

**BR-DIS-001** — Solo los usuarios con rol `ADMIN` pueden crear, editar y activar/desactivar disclaimers a través del menú de Configuración Bysone.

**BR-DIS-002** — Si se suministra `fecha_vigencia_hasta`, esta debe ser estrictamente posterior a `fecha_vigencia_desde`. El sistema valida esta restricción y responde con HTTP 400 si no se cumple.

**BR-DIS-003** — El endpoint público `GET /api/v1/disclaimers/vigente` devuelve el disclaimer activo cuya `fecha_vigencia_desde` sea la más reciente y no haya vencido. Es consumido por el flujo de simulación antes de confirmar el guardado.

---

## 8. Parámetros del Sistema

**RN-PAR-01** — `parametros_bysone` almacena exclusivamente configuración técnica y de negocio del sistema: intervalos de recalibración, umbrales de puntaje por perfil, tasas de referencia u otros valores configurables.

**RN-PAR-02** — Los textos legales (disclaimers) no se almacenan en `parametros_bysone`. Ver sección 7.

**RN-PAR-03** — Los valores de los parámetros son de tipo texto (`VARCHAR`). La capa de aplicación es responsable de parsear el valor según el tipo de dato esperado (entero, decimal, fecha, etc.).

**BR-PAR-001** — Solo los usuarios con rol `ADMIN` pueden crear, consultar y modificar parámetros del sistema a través del menú de Configuración Bysone. Los cambios aplican de inmediato sin necesidad de redespliegue.

**BR-PAR-002** — Al crear un nuevo parámetro, el nombre se normaliza automáticamente a `MAYÚSCULAS_CON_GUIONES_BAJOS`. No pueden existir dos parámetros con el mismo nombre (restricción `UNIQUE` en BD).

**BR-PAR-003** — Los parámetros no se eliminan. Si un parámetro deja de aplicar, su valor puede actualizarse o dejarse en desuso; la eliminación requeriría intervención directa en la base de datos por un DBA. Esto preserva el historial de configuración del sistema.

---

## 9. Tipos de Plazo

**RN-TPL-01** — El catálogo `tipos_plazo` define las unidades de tiempo disponibles para las simulaciones de inversión (ej.: Días, Meses, Trimestres, Años). Solo los usuarios con rol `ADMIN` pueden gestionarlo desde el menú de Configuración Bysone.

**RN-TPL-02** — El nombre de un tipo de plazo es único en el sistema sin distinguir mayúsculas/minúsculas. Crear o actualizar un tipo de plazo con un nombre ya existente resulta en error HTTP 409.

**RN-TPL-03** — El campo `factor_conversion_dias` es obligatorio y debe ser al menos 1. Representa cuántos días equivale una unidad de ese tipo de plazo. El motor de simulación usa este factor para homogenizar los cálculos internos.

**RN-TPL-04** — Un tipo de plazo no puede eliminarse si existe al menos una simulación persistida que lo referencia. El sistema devuelve HTTP 409 con mensaje descriptivo para evitar inconsistencias históricas.

---

## 10. Roles

**RN-ROL-01** — El catálogo de roles define los niveles de acceso del sistema (ej.: ADMIN, USER, MAINTAINER). Solo los usuarios con rol `ADMIN` pueden gestionar este catálogo desde el menú de Configuración Bysone.

**RN-ROL-02** — El nombre del rol es único en el sistema (sin distinguir mayúsculas/minúsculas) y se normaliza automáticamente a MAYÚSCULAS. Crear o renombrar un rol con nombre duplicado resulta en error HTTP 409.

**RN-ROL-03** — Un rol no puede eliminarse si está asignado a uno o más usuarios. El sistema devuelve HTTP 409 con mensaje descriptivo.

---

> Documento actualizado el 2026-04-15. Incluye reglas de tipos de plazo (sección 9) y roles (sección 10). Debe actualizarse cada vez que el modelo de datos o los requisitos de negocio cambien.
