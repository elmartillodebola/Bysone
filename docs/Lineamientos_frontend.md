# Lineamientos de Frontend
Guía visual y de presentación para la implementación del Frontend.

Este documento define los principios de diseño, estilos visuales y
mecanismos de navegación que deben respetarse en el desarrollo del frontend.
Está basado en referencias visuales reales y sirve como contrato entre
diseño, frontend y asistentes de IA (Claude, Copilot, etc.).

---

## 1. Principios generales

- Diseño corporativo, sobrio y funcional
- Priorizar legibilidad sobre ornamentación
- El color comunica estado, no decoración
- Un solo foco de acción principal por pantalla
- Uso intensivo del espacio en blanco

---

## 2. Paleta de colores

### 2.1 Colores primarios

| Uso | Hex |
|---|---|
| Azul corporativo principal | `#003A8F` |
| Azul secundario / interacción | `#1677FF` |
| Amarillo marca (CTA) | `#E6E600` |

Notas:
- El amarillo se usa exclusivamente para acciones primarias
- El azul se usa para navegación y énfasis controlado

---

### 2.2 Colores de estado

| Estado | Hex |
|---|---|
| Óptimo / éxito | `#52C41A` |
| Advertencia | `#FFF4CC` |
| Riesgo / No óptimo | `#FF4D4F` |
| Información | `#E6F4FF` |

---

### 2.3 Escala de grises

| Uso | Hex |
|---|---|
| Fondo general | `#F5F7FA` |
| Fondo de tarjetas | `#FFFFFF` |
| Bordes / divisores | `#E5E7EB` |
| Texto principal | `#262626` |
| Texto secundario | `#8C8C8C` |
| Texto deshabilitado | `#BFBFBF` |

---

## 3. Tipografía

### 3.1 Familia tipográfica

Sans-serif corporativa.

Sugeridas:
- Inter
- Roboto
- Segoe UI

---

### 3.2 Jerarquía tipográfica

| Nivel | Uso |
|---|---|
| H1 | Título de vista / nombre de usuario |
| H2 | Secciones principales |
| Body | Contenido principal |
| Label | Filtros, indicadores, badges |
| Helper | Texto contextual o explicativo |

Notas:
- La jerarquía se logra con peso y espaciado, no solo con tamaño
- Evitar uso excesivo de mayúsculas

---

## 4. Iconografía

- Estilo outline (línea)
- Grosor uniforme
- Sin rellenos
- Monocromática

Uso:
- Menú lateral
- Estados informativos
- Acciones secundarias

Regla:
> Ningún ícono debe usarse sin texto asociado.

---

## 5. Botones

### 5.1 Botón primario

| Propiedad | Valor |
|---|---|
| Fondo | Amarillo `#E6E600` |
| Texto | Oscuro |
| Borde | Ninguno |
| Radio | 4–6px |

Uso:
- Acción principal de la pantalla
- Máximo un botón primario por vista

---

### 5.2 Botón secundario

| Propiedad | Valor |
|---|---|
| Fondo | Blanco |
| Borde | Gris claro |
| Texto | Gris oscuro |

---

### 5.3 Estados de botones

- Hover: leve cambio tonal
- Disabled:
  - Opacidad reducida
  - Sin sombra
  - No interactivo

---

## 6. Navegación

### 6.1 Menú lateral izquierdo

- Posición fija
- Ícono + texto
- Fondo neutro
- Opción activa claramente destacada

Función:
> Navegación estructural y primaria.

---

### 6.2 Header superior

- Logo a la izquierda
- Información de usuario a la derecha
- Bajo peso visual
- No se usa para navegación principal

---

### 6.3 Breadcrumbs

- Texto pequeño
- Color gris
- Siempre visibles sobre el contenido

Ejemplo:
Inicio > Recomendación de inversión > Inicio

---

## 7. Presentación de contenido y datos

### 7.1 Principios

1. Contexto antes que dato
2. Estado antes que número
3. Comparar mejor que detallar
4. Las gráficas apoyan, no explican solas

---

### 7.2 Listados

- Filas tipo "card-row"
- Borde lateral sutil de color
- Acción al final (flecha)
- Fila entendida como clickeable

---

### 7.3 Gráficas

- Sobrias
- Líneas delgadas
- Colores semánticos
- Máximo 2–3 series

Siempre acompañadas de:
- Texto explicativo
- Indicadores

---

### 7.4 Estados vacíos

- Ícono central
- Mensaje claro
- Acción inmediata (CTA)

Ejemplo:
"No hay inversiones activas"
[ Crear nueva inversión ]

---

## 8. Uso con IA (Claude)

Este documento debe ser incluido como contexto base
al solicitar generación de frontend a Claude.

Claude debe:
- Respetar la paleta de colores
- Mantener jerarquía visual
- No introducir nuevos estilos sin justificación
- Priorizar consistencia sobre creatividad

---