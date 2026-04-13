-- ============================================================
-- Mi Portafolio Inteligente — Esquema inicial
-- Hackaton 2026 · Comunidad de Desarrollo de Software · Protección
-- Flyway migration: V1__create_initial_schema.sql
-- Base de datos: PostgreSQL 16 (Neon)
-- ============================================================


-- ============================================================
-- ROLES Y ACCESO FUNCIONAL
-- ============================================================

CREATE TABLE roles_bysone (
    id_rol          BIGSERIAL    PRIMARY KEY,
    nombre_rol      VARCHAR(100) NOT NULL,
    descripcion_rol VARCHAR(300)
);

CREATE TABLE opciones_funcionales_bysone (
    id_opcion               BIGSERIAL    PRIMARY KEY,
    nombre_opcion_funcional VARCHAR(150) NOT NULL
);

CREATE TABLE roles_x_opcion_funcional (
    id_rol    BIGINT NOT NULL,
    id_opcion BIGINT NOT NULL,
    CONSTRAINT pk_roles_x_opcion PRIMARY KEY (id_rol, id_opcion),
    CONSTRAINT fk_rxo_rol        FOREIGN KEY (id_rol)    REFERENCES roles_bysone               (id_rol),
    CONSTRAINT fk_rxo_opcion     FOREIGN KEY (id_opcion) REFERENCES opciones_funcionales_bysone (id_opcion)
);


-- ============================================================
-- PARÁMETROS DEL SISTEMA
-- ============================================================

CREATE TABLE parametros_bysone (
    id_parametro     BIGSERIAL    PRIMARY KEY,
    nombre_parametro VARCHAR(150) NOT NULL,
    valor_parametro  VARCHAR(500) NOT NULL
);


-- ============================================================
-- PORTAFOLIOS Y OPCIONES DE INVERSIÓN
-- ============================================================

CREATE TABLE portafolios_inversion (
    id_portafolio_inversion                BIGSERIAL    PRIMARY KEY,
    nombre_portafolio_inversion            VARCHAR(150) NOT NULL,
    descripcion                            VARCHAR(500),
    rentabilidad_calculada_variable_minimo DECIMAL(5,2) NOT NULL,
    rentabilidad_calculada_variable_maximo DECIMAL(5,2) NOT NULL
);

CREATE TABLE opciones_inversion (
    id_opcion_inversion          BIGSERIAL    PRIMARY KEY,
    nombre_opcion_inversion      VARCHAR(150) NOT NULL,
    descripcion_opcion_inversion VARCHAR(500),
    rentabilidad_minima          DECIMAL(5,2) NOT NULL,
    rentabilidad_maxima          DECIMAL(5,2) NOT NULL
);

CREATE TABLE portafolio_inversion_x_opciones_inversion (
    id_portafolio_inversion BIGINT NOT NULL,
    id_opcion_inversion     BIGINT NOT NULL,
    CONSTRAINT pk_portafolio_x_opcion PRIMARY KEY (id_portafolio_inversion, id_opcion_inversion),
    CONSTRAINT fk_pxo_portafolio      FOREIGN KEY (id_portafolio_inversion) REFERENCES portafolios_inversion (id_portafolio_inversion),
    CONSTRAINT fk_pxo_opcion          FOREIGN KEY (id_opcion_inversion)     REFERENCES opciones_inversion    (id_opcion_inversion)
);


-- ============================================================
-- PERFILES DE INVERSIÓN
-- ============================================================

CREATE TABLE perfiles_inversion (
    id_perfil_inversion     BIGSERIAL    PRIMARY KEY,
    nombre_perfil_inversion VARCHAR(100) NOT NULL
);

-- Distribución porcentual de cada perfil sobre sus portafolios (única fuente de verdad)
CREATE TABLE perfiles_inversion_x_portafolios_inversion (
    id_perfil_inversion     BIGINT       NOT NULL,
    id_portafolio_inversion BIGINT       NOT NULL,
    porcentaje              DECIMAL(5,2) NOT NULL,
    CONSTRAINT pk_perfil_x_portafolio PRIMARY KEY (id_perfil_inversion, id_portafolio_inversion),
    CONSTRAINT fk_pxp_perfil          FOREIGN KEY (id_perfil_inversion)     REFERENCES perfiles_inversion   (id_perfil_inversion),
    CONSTRAINT fk_pxp_portafolio      FOREIGN KEY (id_portafolio_inversion) REFERENCES portafolios_inversion (id_portafolio_inversion)
);

-- Umbrales de exposición permitidos por perfil-portafolio
-- [REVIEW] UNIQUE garantiza una sola fórmula por par perfil-portafolio
CREATE TABLE formulas_exposicion (
    id_formula_exposicion   BIGSERIAL    PRIMARY KEY,
    id_perfil_inversion     BIGINT       NOT NULL,
    umbral_porcentaje_min_1 DECIMAL(5,2) NOT NULL,
    umbral_porcentaje_max_1 DECIMAL(5,2) NOT NULL,
    id_portafolio_inversion BIGINT       NOT NULL,
    CONSTRAINT uq_fe_perfil_portafolio UNIQUE      (id_perfil_inversion, id_portafolio_inversion),
    CONSTRAINT fk_fe_perfil            FOREIGN KEY (id_perfil_inversion)     REFERENCES perfiles_inversion   (id_perfil_inversion),
    CONSTRAINT fk_fe_portafolio        FOREIGN KEY (id_portafolio_inversion) REFERENCES portafolios_inversion (id_portafolio_inversion)
);


-- ============================================================
-- USUARIOS
-- ============================================================

-- proveedor_oauth + oauth_sub: campos requeridos para mapear el token OAuth2 al registro del usuario
-- oauth_sub es el identificador único que retorna Google / Microsoft en el JWT
CREATE TABLE usuarios (
    id_usuario                                  BIGSERIAL    PRIMARY KEY,
    nombre_completo_usuario                     VARCHAR(200) NOT NULL,
    correo_usuario                              VARCHAR(150) NOT NULL UNIQUE,
    celular_usuario                             VARCHAR(20),
    proveedor_oauth                             VARCHAR(20)  NOT NULL,
    oauth_sub                                   VARCHAR(255) NOT NULL UNIQUE,
    id_perfil_inversion                         BIGINT,
    fecha_registro                              TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_ultima_actualizacion_perfil_inversion TIMESTAMP,
    CONSTRAINT chk_u_proveedor CHECK (proveedor_oauth IN ('GOOGLE', 'MICROSOFT')),
    CONSTRAINT fk_u_perfil     FOREIGN KEY (id_perfil_inversion) REFERENCES perfiles_inversion (id_perfil_inversion)
);

CREATE TABLE usuarios_x_rol (
    id_usuario BIGINT NOT NULL,
    id_rol     BIGINT NOT NULL,
    CONSTRAINT pk_usuarios_x_rol PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_uxr_usuario    FOREIGN KEY (id_usuario) REFERENCES usuarios     (id_usuario),
    CONSTRAINT fk_uxr_rol        FOREIGN KEY (id_rol)     REFERENCES roles_bysone (id_rol)
);


-- ============================================================
-- CALIBRACIÓN DE PERFIL
-- ============================================================

CREATE TABLE preguntas_calibracion (
    id_pregunta    BIGSERIAL    PRIMARY KEY,
    texto_pregunta VARCHAR(500) NOT NULL,
    orden          INTEGER      NOT NULL,
    activa         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE opciones_respuesta_calibracion (
    id_opcion_respuesta BIGSERIAL    PRIMARY KEY,
    id_pregunta         BIGINT       NOT NULL,
    texto_opcion        VARCHAR(300) NOT NULL,
    puntaje             INTEGER      NOT NULL,
    orden               INTEGER      NOT NULL,
    CONSTRAINT fk_orc_pregunta FOREIGN KEY (id_pregunta) REFERENCES preguntas_calibracion (id_pregunta)
);

-- [REVIEW] puntaje_total: valor calculado persistido al completar la encuesta
-- evita recalcular uniendo 3 tablas cada vez que se consulta el historial
CREATE TABLE encuestas_calibracion (
    id_encuesta         BIGSERIAL    PRIMARY KEY,
    id_usuario          BIGINT       NOT NULL,
    fecha_realizacion   TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_vencimiento   TIMESTAMP,
    origen              VARCHAR(10)  NOT NULL,
    estado              VARCHAR(15)  NOT NULL DEFAULT 'PENDIENTE',
    puntaje_total       INTEGER,
    id_perfil_resultado BIGINT,
    CONSTRAINT chk_ec_origen  CHECK (origen IN ('DEMANDA', 'SISTEMA')),
    CONSTRAINT chk_ec_estado  CHECK (estado IN ('PENDIENTE', 'COMPLETADA')),
    CONSTRAINT fk_ec_usuario  FOREIGN KEY (id_usuario)          REFERENCES usuarios           (id_usuario),
    CONSTRAINT fk_ec_perfil   FOREIGN KEY (id_perfil_resultado) REFERENCES perfiles_inversion (id_perfil_inversion)
);

CREATE TABLE respuestas_encuesta_calibracion (
    id_respuesta        BIGSERIAL PRIMARY KEY,
    id_encuesta         BIGINT    NOT NULL,
    id_pregunta         BIGINT    NOT NULL,
    id_opcion_respuesta BIGINT    NOT NULL,
    CONSTRAINT uq_rec_encuesta_pregunta UNIQUE      (id_encuesta, id_pregunta),
    CONSTRAINT fk_rec_encuesta          FOREIGN KEY (id_encuesta)         REFERENCES encuestas_calibracion          (id_encuesta),
    CONSTRAINT fk_rec_pregunta          FOREIGN KEY (id_pregunta)         REFERENCES preguntas_calibracion          (id_pregunta),
    CONSTRAINT fk_rec_opcion            FOREIGN KEY (id_opcion_respuesta) REFERENCES opciones_respuesta_calibracion (id_opcion_respuesta)
);


-- ============================================================
-- CATÁLOGOS AUXILIARES DE SIMULACIÓN
-- ============================================================

-- Catálogo de unidades de tiempo para el plazo de inversión
-- factor_conversion_dias: normaliza cualquier plazo a días para el motor de cálculo
CREATE TABLE tipos_plazo (
    id_tipo_plazo       BIGSERIAL    PRIMARY KEY,
    nombre_plazo        VARCHAR(50)  NOT NULL UNIQUE,
    descripcion         VARCHAR(200),
    factor_conversion_dias INTEGER   NOT NULL,
    CONSTRAINT chk_tp_factor CHECK (factor_conversion_dias > 0)
);

-- Texto legal/disclaimer asociado a una simulación
-- Tabla independiente de parametros_bysone porque:
--   1. Requiere TEXT (no VARCHAR) para contenido legal extenso
--   2. Tiene ciclo de vida propio (vigencia desde/hasta)
--   3. Permite versionar: el usuario siempre ve el disclaimer vigente al momento de simular
CREATE TABLE disclaimers_bysone (
    id_disclaimer        BIGSERIAL    PRIMARY KEY,
    titulo               VARCHAR(200) NOT NULL,
    contenido            TEXT         NOT NULL,
    activo               BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_vigencia_desde TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_vigencia_hasta TIMESTAMP
);


-- ============================================================
-- SIMULACIONES
-- ============================================================

-- Cabecera: inputs del usuario. Solo se inserta si el usuario decide guardar la simulación.
-- nombre_perfil_simulado: snapshot del nombre del perfil (el perfil puede cambiar luego)
-- id_tipo_plazo: define si plazo_inversion son días, meses, trimestres o años
-- id_disclaimer: referencia al texto legal vigente al momento de la simulación
CREATE TABLE simulaciones_bysone (
    id_simulacion                              BIGSERIAL     PRIMARY KEY,
    id_usuario                                 BIGINT        NOT NULL,
    fecha_simulacion                           TIMESTAMP     NOT NULL DEFAULT NOW(),
    id_perfil_inversion                        BIGINT        NOT NULL,
    nombre_perfil_simulado                     VARCHAR(100)  NOT NULL,
    valor_inversion_inicial                    DECIMAL(18,2) NOT NULL,
    valor_inversion_periodica                  DECIMAL(18,2),
    plazo_inversion                            INTEGER       NOT NULL,
    id_tipo_plazo                              BIGINT        NOT NULL,
    rentabilidad_se_reinvierte_plazo_inversion BOOLEAN       NOT NULL DEFAULT FALSE,
    id_disclaimer                              BIGINT,
    CONSTRAINT fk_sb_usuario    FOREIGN KEY (id_usuario)         REFERENCES usuarios           (id_usuario),
    CONSTRAINT fk_sb_perfil     FOREIGN KEY (id_perfil_inversion) REFERENCES perfiles_inversion (id_perfil_inversion),
    CONSTRAINT fk_sb_tipo_plazo FOREIGN KEY (id_tipo_plazo)       REFERENCES tipos_plazo        (id_tipo_plazo),
    CONSTRAINT fk_sb_disclaimer FOREIGN KEY (id_disclaimer)       REFERENCES disclaimers_bysone (id_disclaimer)
);

-- Detalle: outputs del motor por período. Snapshot de tasas para reproducibilidad futura.
CREATE TABLE detalle_proyeccion_simulacion (
    id_detalle                   BIGSERIAL     PRIMARY KEY,
    id_simulacion                BIGINT        NOT NULL,
    periodo                      INTEGER       NOT NULL,
    valor_proyectado_minimo      DECIMAL(18,2) NOT NULL,
    valor_proyectado_maximo      DECIMAL(18,2) NOT NULL,
    valor_proyectado_esperado    DECIMAL(18,2) NOT NULL,
    rentabilidad_minima_aplicada DECIMAL(5,2)  NOT NULL,
    rentabilidad_maxima_aplicada DECIMAL(5,2)  NOT NULL,
    CONSTRAINT uq_dps_simulacion_periodo UNIQUE      (id_simulacion, periodo),
    CONSTRAINT fk_dps_simulacion         FOREIGN KEY (id_simulacion) REFERENCES simulaciones_bysone (id_simulacion)
);
