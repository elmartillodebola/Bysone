-- ============================================================
-- Mi Portafolio Inteligente — Datos semilla
-- Hackaton 2026 · Comunidad de Desarrollo de Software · Protección
-- Flyway migration: V2__datos_semilla.sql
-- Base de datos: PostgreSQL 16 (Neon)
--
-- Orden de inserción respetando dependencias de FK:
--  1. roles_bysone
--  2. opciones_funcionales_bysone
--  3. parametros_bysone
--  4. portafolios_inversion
--  5. opciones_inversion
--  6. perfiles_inversion
--  7. tipos_plazo
--  8. disclaimers_bysone
--  9. roles_x_opcion_funcional
-- 10. portafolio_inversion_x_opciones_inversion
-- 11. perfiles_inversion_x_portafolios_inversion
-- 12. formulas_exposicion
-- 13. preguntas_calibracion
-- 14. opciones_respuesta_calibracion
-- 15. usuarios
-- 16. usuarios_x_rol
-- 17. encuestas_calibracion
-- 18. respuestas_encuesta_calibracion
-- 19. simulaciones_bysone
-- 20. detalle_proyeccion_simulacion
-- 21. Actualización de secuencias BIGSERIAL
-- ============================================================


-- ============================================================
-- 1. ROLES
-- ============================================================
INSERT INTO roles_bysone (id_rol, nombre_rol, descripcion_rol) VALUES
    (1, 'ADMIN',      'Acceso total al sistema: gestión de usuarios, perfiles y portafolios'),
    (2, 'MAINTAINER', 'Gestión de perfiles y portafolios de inversión'),
    (3, 'USER',       'Usuario final: realiza simulaciones y encuestas de calibración');


-- ============================================================
-- 2. OPCIONES FUNCIONALES
-- ============================================================
INSERT INTO opciones_funcionales_bysone (id_opcion, nombre_opcion_funcional) VALUES
    (1, 'GESTIONAR_USUARIOS'),
    (2, 'GESTIONAR_PERFILES'),
    (3, 'GESTIONAR_PORTAFOLIOS'),
    (4, 'REALIZAR_SIMULACION'),
    (5, 'VER_HISTORIAL_SIMULACIONES'),
    (6, 'GESTIONAR_PARAMETROS');  -- administración de parámetros del sistema (ADMIN exclusivo)


-- ============================================================
-- 3. PARÁMETROS DEL SISTEMA
-- ============================================================
INSERT INTO parametros_bysone (id_parametro, nombre_parametro, valor_parametro) VALUES
    (1, 'INTERVALO_RECALIBRACION_DIAS',          '180'),
    (2, 'PUNTAJE_PERFIL_CONSERVADOR_MAX',         '2'),
    (3, 'PUNTAJE_PERFIL_MODERADO_MIN',            '3'),
    (4, 'PUNTAJE_PERFIL_MODERADO_MAX',            '5'),
    (5, 'PUNTAJE_PERFIL_AGRESIVO_MIN',            '6'),
    -- BR-SES-001: Tiempo de inactividad de sesión antes de expirar (en minutos).
    -- El frontend controla este umbral; al superarlo limpia el token y fuerza nuevo login.
    (6, 'TIMEOUT_SESION_INACTIVIDAD_MINUTOS',     '5');


-- ============================================================
-- 4. PORTAFOLIOS DE INVERSIÓN
-- ============================================================
INSERT INTO portafolios_inversion (id_portafolio_inversion, nombre_portafolio_inversion, descripcion, rentabilidad_calculada_variable_minimo, rentabilidad_calculada_variable_maximo) VALUES
    (1, 'Renta Fija',               'Portafolio de bajo riesgo compuesto por CDTs y bonos del Estado',           3.00,  6.00),
    (2, 'Renta Variable Moderada',  'Portafolio mixto con fondos de acciones nacionales e inmobiliarios',        6.00, 12.00),
    (3, 'Renta Variable Alta',      'Portafolio de alto rendimiento con fondos de acciones internacionales',    12.00, 20.00);


-- ============================================================
-- 5. OPCIONES DE INVERSIÓN
-- ============================================================
INSERT INTO opciones_inversion (id_opcion_inversion, nombre_opcion_inversion, descripcion_opcion_inversion, rentabilidad_minima, rentabilidad_maxima) VALUES
    (1, 'CDT Bancolombia',                 'Certificado de depósito a término en Bancolombia',                        4.00,  6.00),
    (2, 'Bonos TES Colombia',              'Títulos de deuda pública emitidos por el Gobierno Nacional',              5.00,  7.00),
    (3, 'Fondo de Acciones Colombia',      'Fondo de inversión colectiva en acciones del mercado colombiano',         8.00, 15.00),
    (4, 'Fondo de Acciones Internacional', 'Fondo de inversión en acciones de mercados internacionales diversificados',10.00, 18.00),
    (5, 'Fondo Inmobiliario',              'Inversión indirecta en bienes raíces a través de fondos especializados',  7.00, 12.00);


-- ============================================================
-- 6. PERFILES DE INVERSIÓN
-- ============================================================
INSERT INTO perfiles_inversion (id_perfil_inversion, nombre_perfil_inversion) VALUES
    (1, 'Conservador'),
    (2, 'Moderado'),
    (3, 'Agresivo');


-- ============================================================
-- 7. TIPOS DE PLAZO
-- ============================================================
INSERT INTO tipos_plazo (id_tipo_plazo, nombre_plazo, descripcion, factor_conversion_dias) VALUES
    (1, 'DÍA',       'Plazo expresado en días calendario',   1),
    (2, 'MES',       'Plazo expresado en meses (30 días)',  30),
    (3, 'TRIMESTRE', 'Plazo expresado en trimestres (90 días)', 90),
    (4, 'AÑO',       'Plazo expresado en años (365 días)', 365);


-- ============================================================
-- 8. DISCLAIMERS
-- ============================================================
INSERT INTO disclaimers_bysone (id_disclaimer, titulo, contenido, activo, fecha_vigencia_desde, fecha_vigencia_hasta) VALUES
    (1,
     'Aviso Legal — Simulador de Inversión',
     'El simulador de inversión de Mi Portafolio Inteligente tiene fines exclusivamente informativos y educativos. Los resultados proyectados no constituyen una garantía de rendimiento ni una asesoría financiera personalizada. Las rentabilidades pasadas no garantizan rentabilidades futuras. Protección S.A. no se hace responsable de decisiones de inversión tomadas con base en las proyecciones generadas por esta herramienta. Consulte a un asesor financiero certificado antes de tomar decisiones de inversión.',
     TRUE,
     NOW(),
     NULL),
    (2,
     'Aviso Legal — Versión Anterior (Inactivo)',
     'Versión anterior del aviso legal. Reemplazada por nueva versión el 2026-01-01.',
     FALSE,
     '2025-01-01 00:00:00',
     '2025-12-31 23:59:59');


-- ============================================================
-- 9. ROLES × OPCIONES FUNCIONALES
-- ============================================================
INSERT INTO roles_x_opcion_funcional (id_rol, id_opcion) VALUES
    -- ADMIN: acceso total
    (1, 1),   -- ADMIN → GESTIONAR_USUARIOS
    (1, 2),   -- ADMIN → GESTIONAR_PERFILES
    (1, 3),   -- ADMIN → GESTIONAR_PORTAFOLIOS
    (1, 4),   -- ADMIN → REALIZAR_SIMULACION
    (1, 5),   -- ADMIN → VER_HISTORIAL_SIMULACIONES
    (1, 6),   -- ADMIN → GESTIONAR_PARAMETROS (menú Configuración exclusivo)
    -- MAINTAINER: gestión de productos, sin configuración de sistema
    (2, 2),   -- MAINTAINER → GESTIONAR_PERFILES
    (2, 3),   -- MAINTAINER → GESTIONAR_PORTAFOLIOS
    (2, 4),   -- MAINTAINER → REALIZAR_SIMULACION
    (2, 5),   -- MAINTAINER → VER_HISTORIAL_SIMULACIONES
    -- USER: funcionalidades de usuario final
    (3, 4),   -- USER → REALIZAR_SIMULACION
    (3, 5);   -- USER → VER_HISTORIAL_SIMULACIONES


-- ============================================================
-- 10. PORTAFOLIOS × OPCIONES DE INVERSIÓN
-- ============================================================
INSERT INTO portafolio_inversion_x_opciones_inversion (id_portafolio_inversion, id_opcion_inversion) VALUES
    (1, 1),   -- Renta Fija              → CDT Bancolombia
    (1, 2),   -- Renta Fija              → Bonos TES Colombia
    (2, 3),   -- Renta Variable Moderada → Fondo de Acciones Colombia
    (2, 5),   -- Renta Variable Moderada → Fondo Inmobiliario
    (3, 4);   -- Renta Variable Alta     → Fondo de Acciones Internacional


-- ============================================================
-- 11. PERFILES × PORTAFOLIOS (con porcentaje — suma = 100 por perfil)
-- ============================================================
INSERT INTO perfiles_inversion_x_portafolios_inversion (id_perfil_inversion, id_portafolio_inversion, porcentaje) VALUES
    (1, 1, 100.00),   -- Conservador: 100% Renta Fija
    (2, 1,  40.00),   -- Moderado:     40% Renta Fija
    (2, 2,  60.00),   -- Moderado:     60% Renta Variable Moderada
    (3, 2,  30.00),   -- Agresivo:     30% Renta Variable Moderada
    (3, 3,  70.00);   -- Agresivo:     70% Renta Variable Alta


-- ============================================================
-- 12. FÓRMULAS DE EXPOSICIÓN
-- ============================================================
INSERT INTO formulas_exposicion (id_formula_exposicion, id_perfil_inversion, umbral_porcentaje_min_1, umbral_porcentaje_max_1, id_portafolio_inversion) VALUES
    (1, 1, 80.00, 100.00, 1),   -- Conservador + Renta Fija:              80% – 100%
    (2, 2, 30.00,  50.00, 1),   -- Moderado    + Renta Fija:              30% – 50%
    (3, 2, 50.00,  70.00, 2),   -- Moderado    + Renta Variable Moderada: 50% – 70%
    (4, 3, 20.00,  40.00, 2),   -- Agresivo    + Renta Variable Moderada: 20% – 40%
    (5, 3, 60.00,  80.00, 3);   -- Agresivo    + Renta Variable Alta:     60% – 80%


-- ============================================================
-- 13. PREGUNTAS DE CALIBRACIÓN
-- ============================================================
INSERT INTO preguntas_calibracion (id_pregunta, texto_pregunta, orden, activa) VALUES
    (1, '¿Cuál es su objetivo principal al invertir su pensión voluntaria?',                           1, TRUE),
    (2, '¿Por cuánto tiempo planea mantener su inversión sin necesitar el dinero?',                    2, TRUE),
    (3, '¿Cómo reaccionaría si su inversión pierde un 15% de su valor en un trimestre?',              3, TRUE),
    (4, '¿Cuál es su experiencia previa en productos de inversión?',                                   4, TRUE),
    (5, '¿Qué porcentaje de sus ingresos mensuales puede destinar a ahorro e inversión voluntaria?',   5, TRUE);


-- ============================================================
-- 14. OPCIONES DE RESPUESTA DE CALIBRACIÓN
-- ============================================================
INSERT INTO opciones_respuesta_calibracion (id_opcion_respuesta, id_pregunta, texto_opcion, puntaje, orden) VALUES
    -- Pregunta 1: Objetivo principal de inversión
    (1,  1, 'Proteger mi capital y mantener su poder adquisitivo',                    1, 1),
    (2,  1, 'Obtener un crecimiento moderado con riesgo controlado',                  2, 2),
    (3,  1, 'Maximizar el crecimiento aunque implique mayor riesgo y volatilidad',    3, 3),
    -- Pregunta 2: Horizonte de inversión
    (4,  2, 'Menos de 3 años',                                                        1, 1),
    (5,  2, 'Entre 3 y 7 años',                                                       2, 2),
    (6,  2, 'Más de 7 años',                                                          3, 3),
    -- Pregunta 3: Reacción ante pérdida del 15%
    (7,  3, 'Retiraría mi dinero inmediatamente para evitar pérdidas mayores',        1, 1),
    (8,  3, 'Me preocuparía pero esperaría a que el mercado se recupere',             2, 2),
    (9,  3, 'Lo vería como una oportunidad y consideraría invertir más',              3, 3),
    -- Pregunta 4: Experiencia previa en inversión
    (10, 4, 'Ninguna, es la primera vez que invierto',                                1, 1),
    (11, 4, 'Tengo experiencia básica en CDTs o fondos de bajo riesgo',               2, 2),
    (12, 4, 'He invertido en acciones, fondos de renta variable o criptoactivos',     3, 3),
    -- Pregunta 5: Porcentaje de ingresos disponibles para ahorro
    (13, 5, 'Menos del 5%',                                                           1, 1),
    (14, 5, 'Entre el 5% y el 15%',                                                  2, 2),
    (15, 5, 'Más del 15%',                                                            3, 3);


-- ============================================================
-- 15. USUARIOS
-- ============================================================
INSERT INTO usuarios (id_usuario, nombre_completo_usuario, correo_usuario, celular_usuario, proveedor_oauth, oauth_sub, id_perfil_inversion, fecha_registro, fecha_ultima_actualizacion_perfil_inversion) VALUES
    (1, 'Ana García López',        'ana.garcia@gmail.com',         '3001234567', 'GOOGLE',    'google-sub-ana-001',      2, NOW(), NOW()),
    (2, 'Carlos Martínez Ruiz',    'carlos.martinez@outlook.com',  '3109876543', 'MICROSOFT', 'microsoft-sub-carlos-002', 1, NOW(), NOW()),
    (3, 'María Rodríguez Pérez',   'maria.rodriguez@gmail.com',    '3205551234', 'GOOGLE',    'google-sub-maria-003',    NULL, NOW(), NULL),
    -- Usuario administrador inicial del sistema; oauth_sub se actualiza al primer login con Google
    (4, 'Martillo de Bola',        'martillodebola@gmail.com',     NULL,         'GOOGLE',    'pending-google-martillodebola', NULL, NOW(), NULL);


-- ============================================================
-- 16. USUARIOS × ROLES
-- ============================================================
INSERT INTO usuarios_x_rol (id_usuario, id_rol) VALUES
    (1, 1),   -- Ana          → ADMIN
    (1, 3),   -- Ana          → USER
    (2, 2),   -- Carlos       → MAINTAINER
    (2, 3),   -- Carlos       → USER
    (3, 3),   -- María        → USER
    (4, 1),   -- Martillodebola → ADMIN
    (4, 3);   -- Martillodebola → USER


-- ============================================================
-- 17. ENCUESTAS DE CALIBRACIÓN
-- ============================================================
INSERT INTO encuestas_calibracion (id_encuesta, id_usuario, fecha_realizacion, fecha_vencimiento, origen, estado, puntaje_total, id_perfil_resultado) VALUES
    (1, 1, NOW(), NOW() + INTERVAL '180 days', 'SISTEMA',  'COMPLETADA', 4, 2),   -- Ana:   puntaje 4 → Moderado
    (2, 2, NOW(), NOW() + INTERVAL '180 days', 'SISTEMA',  'COMPLETADA', 2, 1),   -- Carlos: puntaje 2 → Conservador
    (3, 3, NOW(), NOW() + INTERVAL '180 days', 'SISTEMA',  'PENDIENTE',  NULL, NULL); -- María: pendiente


-- ============================================================
-- 18. RESPUESTAS DE ENCUESTA DE CALIBRACIÓN
-- ============================================================
INSERT INTO respuestas_encuesta_calibracion (id_respuesta, id_encuesta, id_pregunta, id_opcion_respuesta) VALUES
    (1, 1, 1, 2),   -- Ana    | pregunta 1 → opción "crecimiento moderado"   (puntaje 2)
    (2, 1, 2, 5),   -- Ana    | pregunta 2 → opción "entre 3 y 7 años"       (puntaje 2) | total = 4
    (3, 2, 1, 1),   -- Carlos | pregunta 1 → opción "proteger mi capital"     (puntaje 1)
    (4, 2, 2, 4);   -- Carlos | pregunta 2 → opción "menos de 3 años"         (puntaje 1) | total = 2


-- ============================================================
-- 19. SIMULACIONES
-- ============================================================
-- Rentabilidades Moderado (40% Renta Fija + 60% Renta Variable Moderada):
--   min ponderada: 0.40*3.00 + 0.60*6.00  = 4.80%
--   max ponderada: 0.40*6.00 + 0.60*12.00 = 9.60%
--
-- Rentabilidades Conservador (100% Renta Fija):
--   min: 3.00% | max: 6.00%
INSERT INTO simulaciones_bysone (id_simulacion, id_usuario, fecha_simulacion, id_perfil_inversion, nombre_perfil_simulado, valor_inversion_inicial, valor_inversion_periodica, plazo_inversion, id_tipo_plazo, rentabilidad_se_reinvierte_plazo_inversion, id_disclaimer) VALUES
    (1, 1, NOW(), 2, 'Moderado',     5000000.00, 500000.00, 2, 4, TRUE,  1),
    (2, 2, NOW(), 1, 'Conservador',  3000000.00, 200000.00, 3, 4, FALSE, 1);


-- ============================================================
-- 20. DETALLE DE PROYECCIÓN
-- ============================================================
-- Simulación 1 — Ana | Moderado | 2 años | reinvierte
--   Año 1 min:  5.000.000 × 1,048 + 500.000 = 5.740.000
--   Año 1 max:  5.000.000 × 1,096 + 500.000 = 5.980.000
--   Año 1 esp:  (5.740.000 + 5.980.000) / 2 = 5.860.000
--   Año 2 min:  5.740.000 × 1,048 + 500.000 = 6.515.520
--   Año 2 max:  5.980.000 × 1,096 + 500.000 = 7.054.080
--   Año 2 esp:  (6.515.520 + 7.054.080) / 2 = 6.784.800
--
-- Simulación 2 — Carlos | Conservador | 3 años | no reinvierte
--   Año 1 min:  3.000.000 × 1,03 + 200.000 = 3.290.000
--   Año 1 max:  3.000.000 × 1,06 + 200.000 = 3.380.000
--   Año 1 esp:  (3.290.000 + 3.380.000) / 2 = 3.335.000
--   Año 2 min:  3.290.000 × 1,03 + 200.000 = 3.588.700
--   Año 2 max:  3.380.000 × 1,06 + 200.000 = 3.782.800
--   Año 2 esp:  (3.588.700 + 3.782.800) / 2 = 3.685.750
-- (Solo 5 filas para respetar el límite de semilla; año 3 de Carlos excluido)
INSERT INTO detalle_proyeccion_simulacion (id_detalle, id_simulacion, periodo, valor_proyectado_minimo, valor_proyectado_maximo, valor_proyectado_esperado, rentabilidad_minima_aplicada, rentabilidad_maxima_aplicada) VALUES
    (1, 1, 1, 5740000.00, 5980000.00, 5860000.00, 4.80, 9.60),
    (2, 1, 2, 6515520.00, 7054080.00, 6784800.00, 4.80, 9.60),
    (3, 2, 1, 3290000.00, 3380000.00, 3335000.00, 3.00, 6.00),
    (4, 2, 2, 3588700.00, 3782800.00, 3685750.00, 3.00, 6.00),
    (5, 2, 3, 3896361.00, 4209768.00, 4053064.50, 3.00, 6.00);


-- ============================================================
-- 21. ACTUALIZACIÓN DE SECUENCIAS BIGSERIAL
-- Necesario porque se insertaron IDs explícitos;
-- sin esto el próximo INSERT sin ID generaría conflicto de PK.
-- ============================================================
SELECT setval('roles_bysone_id_rol_seq',                                    3);
SELECT setval('opciones_funcionales_bysone_id_opcion_seq',                  6);
SELECT setval('parametros_bysone_id_parametro_seq',                         6);
SELECT setval('portafolios_inversion_id_portafolio_inversion_seq',          3);
SELECT setval('opciones_inversion_id_opcion_inversion_seq',                 5);
SELECT setval('perfiles_inversion_id_perfil_inversion_seq',                 3);
SELECT setval('tipos_plazo_id_tipo_plazo_seq',                              4);
SELECT setval('disclaimers_bysone_id_disclaimer_seq',                       2);
SELECT setval('formulas_exposicion_id_formula_exposicion_seq',              5);
SELECT setval('preguntas_calibracion_id_pregunta_seq',                      5);
SELECT setval('opciones_respuesta_calibracion_id_opcion_respuesta_seq',    15);
SELECT setval('usuarios_id_usuario_seq',                                     4);
SELECT setval('encuestas_calibracion_id_encuesta_seq',                      3);
SELECT setval('respuestas_encuesta_calibracion_id_respuesta_seq',           4);
SELECT setval('simulaciones_bysone_id_simulacion_seq',                      2);
SELECT setval('detalle_proyeccion_simulacion_id_detalle_seq',               5);
