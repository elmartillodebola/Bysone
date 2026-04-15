# Manual del Administrador — Mi Portafolio Inteligente

> Este manual describe cómo un usuario con rol **ADMIN** puede configurar y operar el sistema sin tocar código ni redesplegar. Todos los cambios aplican de inmediato.

---

## Acceso al módulo de configuración

1. Ingresa con tu cuenta Google en `/auth/signin`.
2. El sistema verifica que tu usuario tenga el rol `ADMIN`.
3. Navega a `/admin` para ver el hub de Configuración.

Desde el hub accedes a cada sección usando las tarjetas. Cada sección tiene un botón **Volver a Configuración** para regresar al hub.

---

## Secciones del hub

### Usuarios registrados `/admin/usuarios`

Consulta de solo lectura. Muestra todos los usuarios que han iniciado sesión al menos una vez:

- Nombre y correo Google
- Perfil de inversión asignado (si ya calibró)
- Estado de calibración (PENDIENTE / COMPLETADA / VENCIDA)
- Roles asignados

No se pueden crear ni eliminar usuarios desde aquí; el alta ocurre automáticamente en el primer inicio de sesión OAuth2.

---

### Perfiles de inversión `/admin/perfiles`

Los perfiles (Conservador, Moderado, Agresivo) definen qué portafolios puede usar cada usuario según su resultado de calibración.

**Qué puedes hacer:**

- **Crear / editar / eliminar** perfiles. No puedes eliminar un perfil que tenga usuarios asignados.
- **Composición de portafolios:** para cada perfil defines qué portafolios lo integran y en qué porcentaje. La suma de porcentajes debe ser exactamente 100 %.
- **Fórmulas de exposición:** para cada par perfil–portafolio puedes definir un rango admisible (`umbral_min` % – `umbral_max` %). Si existe una fórmula, el sistema rechaza composiciones fuera del rango al guardar.

**Ejemplo de configuración semilla:**

| Perfil | Portafolio | Porcentaje | Rango fórmula |
|--------|-----------|------------|---------------|
| Conservador | Renta Fija | 100 % | 80 %–100 % |
| Moderado | Renta Fija | 40 % | 30 %–50 % |
| Moderado | Renta Variable Moderada | 60 % | 50 %–70 % |
| Agresivo | Renta Variable Moderada | 30 % | 20 %–40 % |
| Agresivo | Renta Variable Alta | 70 % | 60 %–80 % |

---

### Parámetros del sistema `/admin/parametros`

Controles globales del sistema. Cada parámetro tiene un nombre fijo (clave) y un valor editable.

| Parámetro | Descripción | Valor semilla |
|-----------|-------------|---------------|
| `INTERVALO_RECALIBRACION_DIAS` | Días de vigencia de una calibración antes de que venza | 180 |
| `TIMEOUT_SESION_MINUTOS` | Minutos de inactividad antes de expirar la sesión | 30 |
| `PUNTAJE_PERFIL_CONSERVADOR_MAX` | Puntaje máximo para clasificar como Conservador | 2 |
| `PUNTAJE_PERFIL_MODERADO_MIN` | Puntaje mínimo para clasificar como Moderado | 3 |
| `PUNTAJE_PERFIL_MODERADO_MAX` | Puntaje máximo para clasificar como Moderado | 5 |
| `PUNTAJE_PERFIL_AGRESIVO_MIN` | Puntaje mínimo para clasificar como Agresivo | 6 |

> Los parámetros de puntaje son referencia documental del diseño. El cálculo actual usa los tercios automáticos descritos en la sección de calibración más abajo.

---

### Opciones de inversión `/admin/opciones-inversion`

Instrumentos financieros individuales (CDT, acciones, bonos, etc.) con sus rentabilidades.

Cada opción tiene:
- **Nombre** (ej. "CDT Bancolombia")
- **Rentabilidad mínima, esperada y máxima** (porcentajes anuales)
- **Descripción** opcional

Las opciones se agrupan en portafolios. Una opción puede pertenecer a varios portafolios.

---

### Portafolios de inversión `/admin/portafolios`

Un portafolio es un conjunto de opciones de inversión. Los portafolios se asignan a perfiles en la sección **Perfiles de inversión**.

Cada portafolio tiene:
- **Nombre** (ej. "Renta Variable Alta")
- **Descripción** opcional
- **Opciones de inversión** que lo componen (asignación desde la misma pantalla)

No puedes eliminar un portafolio que esté asignado a un perfil.

---

### Cuestionario de calibración `/admin/preguntas`

Aquí configuras las preguntas que determinan el perfil de inversión del usuario. Lee con atención la sección **Cómo funciona el cálculo del perfil** más abajo antes de modificar el cuestionario.

Cada pregunta tiene:
- **Texto** de la pregunta
- **Orden** de aparición
- **Estado** activo/inactivo (solo las preguntas activas aparecen en el cuestionario)
- **Opciones de respuesta**, cada una con: texto, orden y **puntaje** (entero)

---

### Disclaimers legales `/admin/disclaimers`

Textos legales que el usuario debe aceptar antes de usar el simulador.

Cada disclaimer tiene:
- **Título** y **contenido** del texto legal
- **Fecha de inicio y fin de vigencia**

El sistema muestra siempre el disclaimer vigente en la fecha actual. Solo puede haber un disclaimer activo por período; si los rangos se solapan, el sistema rechaza el guardado.

---

### Tipos de plazo `/admin/tipos-plazo`

Catálogo de unidades de tiempo usadas en las simulaciones (Días, Meses, Trimestres, Años, etc.).

Cada tipo de plazo tiene:
- **Nombre** (ej. "Meses")
- **Factor de conversión a días** (ej. 30 para Meses)

No puedes eliminar un tipo de plazo que esté en uso en alguna simulación existente.

---

### Roles × Opciones funcionales `/admin/roles-opciones`

Matriz de control de acceso. Aquí defines qué opciones funcionales (permisos) tiene asignadas cada rol del sistema.

- Las filas son los roles (ADMIN, USER, MAINTAINER…).
- Las columnas son las opciones funcionales (GESTIONAR_USUARIOS, REALIZAR_SIMULACION…).
- Marca o desmarca el checkbox para asignar o quitar un permiso.

Los cambios aplican en la siguiente solicitud autenticada del usuario afectado.

---

### Roles `/admin/roles`

Catálogo de roles disponibles en el sistema. Puedes crear nuevos roles o renombrar existentes.

No puedes eliminar un rol que esté asignado a algún usuario.

---

### Opciones funcionales `/admin/opciones-funcionales`

Catálogo de permisos del sistema. Los nombres se normalizan automáticamente a `MAYUSCULAS_CON_GUIONES_BAJOS`.

No puedes eliminar una opción funcional que esté asignada a algún rol.

---

## Cómo funciona el cálculo del perfil de inversión

Esta sección explica la lógica detrás del cuestionario de calibración para que puedas configurarlo correctamente.

### Las preguntas y sus opciones de respuesta

El cuestionario tiene N preguntas activas. Para cada pregunta el usuario elige una opción. Lo que determina el perfil **no es la pregunta en sí, sino el `puntaje` de la opción elegida**.

Cada opción de respuesta tiene un `puntaje` entero. La convención es:

| Puntaje | Significado |
|---------|-------------|
| 1 | Opción conservadora (baja tolerancia al riesgo, horizonte corto) |
| 2 | Opción moderada |
| 3 | Opción agresiva (alta tolerancia, horizonte largo) |

> Puedes usar cualquier rango de enteros, pero mantenlo consistente entre todas las preguntas.

### La fórmula de cálculo

Cuando el usuario finaliza el cuestionario, el sistema suma los puntajes de todas las opciones elegidas:

```
puntaje_total = suma de puntaje(opción elegida en cada pregunta)
```

Con 5 preguntas y opciones de puntaje 1–3, el rango posible es de **5** (todo conservador) a **15** (todo agresivo).

### Asignación del perfil (regla de tercios)

El sistema divide el rango posible en tercios según el número de perfiles registrados:

```
rango_teórico = número_de_perfiles × 3
tercio        = rango_teórico / 3
```

Con 3 perfiles (Conservador, Moderado, Agresivo):

| Puntaje total | Perfil asignado |
|---------------|-----------------|
| ≤ 3 | CONSERVADOR |
| 4 – 6 | MODERADO |
| ≥ 7 | AGRESIVO |

### Qué sucede después de la asignación

1. El perfil queda registrado en el usuario.
2. La encuesta queda en estado `COMPLETADA` con el `puntaje_total`.
3. Se calcula la fecha de vencimiento: `fecha_realizacion + INTERVALO_RECALIBRACION_DIAS`.
4. Cuando vence, el usuario debe recalibrar. El perfil anterior se mantiene hasta que complete la nueva calibración.

### Guía práctica al configurar el cuestionario

- **Mantén el puntaje creciente:** 1 = más conservador, N = más agresivo en todas las preguntas. Si una pregunta tiene puntajes al revés, el total quedará distorsionado.
- **Todas las preguntas pesan igual.** No existe un campo `peso` por pregunta; el peso se controla implícitamente incluyendo más o menos preguntas sobre un tema.
- **Si agregas un cuarto perfil**, el rango teórico sube a 12 y los tercios se recalculan automáticamente. Asegúrate de que el nombre del nuevo perfil coincida exactamente (por ejemplo `CONSERVADOR_EXTREMO`) para que la búsqueda por nombre funcione.
- **Si cambias los puntajes de las opciones**, recalibra mentalmente los umbrales resultantes con la fórmula de tercios para verificar que la distribución sea la esperada.

---

> Manual generado para el equipo **Bysone** — Hackaton 2026 · Protección S.A.
