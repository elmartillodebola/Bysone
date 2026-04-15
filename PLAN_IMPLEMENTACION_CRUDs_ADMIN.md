# 🎯 PLAN DE IMPLEMENTACIÓN — CRUDs Admin Bysone
**Fecha:** 14 de abril de 2026  
**Objetivo:** Implementar 10 pasos de forma controlada y secuencial para completar toda la gestión administrativa

---

## 📊 PLAN RECOMENDADO (Orden de Ejecución)

```
┌─────────────────────────────────────────────────────────────────┐
│                   FASE 1: FUNDACIÓN (PASOS 1-2)                 │
│                    (Configuración base del sistema)             │
├─────────────────────────────────────────────────────────────────┤
│  PASO 1: Configuración de Roles × Opciones Funcionales         │
│  └─ AdminRolesOpcionesController                               │
│  └─ Matriz de permisos: roles × opciones                       │
│  └─ Tests: 4 tests                                             │
│  └─ Salida: Permisos base para todos los demás CRUDs           │
├─────────────────────────────────────────────────────────────────┤
│  PASO 2: Configuración de Encuesta de Calibración              │
│  └─ AdminEncuestaCalibracionController                         │
│  └─ CRUD: Preguntas + Opciones de respuesta anidadas           │
│  └─ Validación: Cada pregunta ≥2 opciones (CA-ORC-05)         │
│  └─ Tests: 5 tests                                             │
│  └─ Salida: Base de encuesta lista para calibración            │
├─────────────────────────────────────────────────────────────────┤
│  PASO 3: CRUDs Básicos (Perfiles y Preguntas adicionales)      │
│  └─ AdminPerfilController (CRUD completo)                      │
│  └─ AdminPreguntaController (legacy, si se necesita)           │
│  └─ Tests: 6 tests                                             │
│  └─ Salida: Gestión de perfiles independiente                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│               FASE 2: INVERSIONES (PASOS 4-6)                   │
│            (Estructura de portafolios e inversiones)            │
├─────────────────────────────────────────────────────────────────┤
│  PASO 4: Portafolios × Opciones (M2M)                          │
│  └─ AdminPortafolioOpcionController                            │
│  └─ Asignar opciones de inversión a portafolios                │
│  └─ Validación: No duplicar opción por portafolio              │
│  └─ Tests: 4 tests                                             │
│  └─ Salida: Portafolios con sus componentes                    │
├─────────────────────────────────────────────────────────────────┤
│  PASO 5: Perfiles × Portafolios (M2M)                          │
│  └─ AdminPerfilPortafolioController                            │
│  └─ Asignar portafolios a perfiles con %                       │
│  └─ Validación CRÍTICA: Suma de % = 100.00%                   │
│  └─ Tests: 5 tests (incluye validación de suma)                │
│  └─ Salida: Perfiles completos con distribución                │
├─────────────────────────────────────────────────────────────────┤
│  PASO 6: Fórmulas de Exposición                                │
│  └─ AdminFormulaExposicionController                           │
│  └─ Umbrales mín/máx por perfil-portafolio                     │
│  └─ Validación: umbral_min ≤ umbral_max, rango 0-100%        │
│  └─ Tests: 3 tests                                             │
│  └─ Salida: Límites de riesgo configurables                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│              FASE 3: INFORMACIÓN (PASO 7)                        │
│                  (Disclaimers y términos)                       │
├─────────────────────────────────────────────────────────────────┤
│  PASO 7: Disclaimers Completo                                  │
│  └─ AdminDisclaimerController (completar)                      │
│  └─ Validación: No solapamiento de vigencia                    │
│  └─ Tests: 4 tests                                             │
│  └─ Salida: Información legal gestionable                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│           FASE 4: INTERFAZ (PASOS 8-10)                         │
│          (Frontend + Documentación + Integración)               │
├─────────────────────────────────────────────────────────────────┤
│  PASO 8: Frontend Admin Unificado                              │
│  └─ 8 páginas: /admin/{roles|encuesta|perfiles|etc}           │
│  └─ Componentes CRUD reutilizables                             │
│  └─ Tablas editables in-place + modales                        │
│  └─ Tests: N/A (manual)                                        │
│  └─ Salida: Panel admin completo                               │
├─────────────────────────────────────────────────────────────────┤
│  PASO 9: Documentación                                         │
│  └─ prompts_utilizados.md (este prompt + ejecución)            │
│  └─ Nuevas reglas de negocio (BR-*)                            │
│  └─ Criterios actualizados                                     │
│  └─ Salida: Proyecto documentado                               │
├─────────────────────────────────────────────────────────────────┤
│  PASO 10: Integración y Validación Final                       │
│  └─ mvn clean install (backend)                                │
│  └─ npm run build (frontend)                                   │
│  └─ Ejecución de todos los tests (34 tests totales)            │
│  └─ Pruebas manuales de flujos                                 │
│  └─ Validar: Build SUCCESS + Tests 34/34                       │
│  └─ Salida: Proyecto listo para despliegue                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔗 DEPENDENCIAS (Por qué este orden)

```
PASO 1 (Roles×Opciones)
  ├─ No depende de nada
  └─ REQUERIDO ANTES DE: PASO 2-7 (todos los demás)
     (Protección @PreAuthorize("hasRole('ADMIN')"))

PASO 2 (Encuesta Calibración)
  ├─ Depende de: PASO 1
  └─ INDEPENDIENTE de: PASO 3-7
     (Las preguntas/opciones no dependen de perfiles/portafolios)

PASO 3 (CRUDs Básicos - Perfiles)
  ├─ Depende de: PASO 1
  └─ REQUERIDO ANTES DE: PASO 5
     (PASO 5 asigna portafolios a perfiles)

PASO 4 (Portafolio×Opción M2M)
  ├─ Depende de: PASO 1
  └─ REQUERIDO ANTES DE: PASO 5
     (PASO 5 necesita portafolios completos)

PASO 5 (Perfil×Portafolio M2M)
  ├─ Depende de: PASO 3 + PASO 4
  └─ REQUERIDO ANTES DE: PASO 6
     (PASO 6 usa perfil-portafolio como clave)

PASO 6 (Fórmulas Exposición)
  ├─ Depende de: PASO 5
  └─ INDEPENDIENTE de: PASO 7
     (Opcional en lógica, pero complementa PASO 5)

PASO 7 (Disclaimers)
  ├─ Depende de: PASO 1 (solo permisos)
  └─ INDEPENDIENTE de: PASO 2-6
     (No se usa en calibración ni perfiles)

PASO 8 (Frontend)
  ├─ Depende de: PASO 1-7 (todos los endpoints)
  └─ REQUIERE: Todos los controladores deployed

PASO 9 (Documentación)
  ├─ Depende de: PASO 1-8
  └─ Registra todo lo hecho

PASO 10 (Integración)
  ├─ Depende de: PASO 1-9
  └─ Valida todo funcionando
```

---

## 📈 TABLA DE PROGRESO

| Fase | Pasos | Validaciones | Tests | Frontend | Duración Est. |
|------|-------|-------------|-------|----------|---------------|
| **1: Fundación** | 1-3 | Permisos, Encuesta, Perfiles | 15 | N/A | ~2h |
| **2: Inversiones** | 4-6 | M2M, Fórmulas, Umbrales | 12 | N/A | ~3h |
| **3: Información** | 7 | Disclaimers, Vigencia | 4 | N/A | ~1h |
| **4: Interfaz** | 8-10 | Frontend, Docs, Build | 3 | Sí | ~2h |
| **TOTAL** | **1-10** | **34 criterios** | **34 tests** | **Sí** | **~8h** |

---

## 🧪 RESUMEN DE TESTS

```
PASO 1: AdminRolesOpcionesTest ............................ 4 tests
PASO 2: AdminEncuestaCalibracionTest ..................... 5 tests
PASO 3: AdminPerfilControllerTest ........................ 6 tests
PASO 4: AdminPortafolioOpcionTest ........................ 4 tests
PASO 5: AdminPerfilPortafolioTest ........................ 5 tests
PASO 6: AdminFormulaExposicionTest ....................... 3 tests
PASO 7: AdminDisclaimerTest .............................. 4 tests
PASO 8: Frontend (manual) ................................ N/A
PASO 9: Documentación .................................... N/A
PASO 10: Build + All Tests ............................... 3 tests (build, integration)
                                                    ─────────────
                                          TOTAL:     34 tests
```

---

## ⚠️ PUNTOS CRÍTICOS DE VALIDACIÓN

### Por Paso:

**PASO 1 — Roles × Opciones:**
- ✓ No duplicar opción en mismo rol
- ✓ IDs existen en tablas padre
- ✓ Matriz completa: 3 roles × 6 opciones

**PASO 2 — Encuesta Calibración:**
- ✓ Cada pregunta tiene ≥2 opciones (CA-ORC-05) — **CRÍTICO**
- ✓ Orden único por pregunta
- ✓ Puntaje ≥ 0

**PASO 3 — Perfiles:**
- ✓ Nombre único en sistema
- ✓ Rentabilidad calculada (suma ponderada)

**PASO 4 — Portafolio×Opción:**
- ✓ No duplicar opción
- ✓ Portafolio tiene ≥1 opción

**PASO 5 — Perfil×Portafolio:**
- ✓ **SUMA DE PORCENTAJES = 100.00%** — **MUY CRÍTICO**
- ✓ 0 < % ≤ 100
- ✓ No duplicar portafolio

**PASO 6 — Fórmulas:**
- ✓ umbral_min ≤ umbral_max
- ✓ Rango 0-100%
- ✓ Una por perfil-portafolio

**PASO 7 — Disclaimers:**
- ✓ No solapamiento de vigencia — **CRÍTICO**
- ✓ desde < hasta
- ✓ Soft delete (activo=FALSE)

---

## 🚀 RECOMENDACIÓN FINAL

**Ejecutar en este orden:**

1. ✅ **PASO 1** → Protege todos los demás
2. ✅ **PASO 2** → Base de calibración (independiente)
3. ✅ **PASO 3** → Perfiles (necesario para PASO 5)
4. ✅ **PASO 4** → Portafolio×Opción (necesario para PASO 5)
5. ✅ **PASO 5** → Perfil×Portafolio (núcleo de perfiles)
6. ✅ **PASO 6** → Fórmulas (complemento de PASO 5)
7. ✅ **PASO 7** → Disclaimers (independiente, puede ser en paralelo)
8. ✅ **PASO 8** → Frontend (consume todos)
9. ✅ **PASO 9** → Documentar (registra todo)
10. ✅ **PASO 10** → Validar (integración final)

**Tiempo total estimado:** ~8 horas

---

## ✨ PRÓXIMOS PASOS

- [ ] Confirmar plan con el usuario
- [ ] Dar "GO" para comenzar PASO 1
- [ ] Ejecutar pasos secuencialmente
- [ ] Validar tras cada paso antes de pasar al siguiente

---

> **¿Aprobado el plan? Espero tu GO para comenzar el PASO 1.**
