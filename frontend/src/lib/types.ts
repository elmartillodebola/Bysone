// ── Enums ────────────────────────────────────────────────────────────────────

export type ProveedorOauth = 'GOOGLE' | 'MICROSOFT'
export type OrigenEncuesta = 'DEMANDA' | 'SISTEMA'
export type EstadoEncuesta = 'PENDIENTE' | 'COMPLETADA'
export type TipoPlazo = 'DÍA' | 'MES' | 'TRIMESTRE' | 'AÑO'

// ── Error envelope ───────────────────────────────────────────────────────────

export interface ApiError {
  timestamp: string
  status: number
  error: string
  code: string
  message: string
  path: string
  details?: { field: string; message: string }[]
}

// ── Auth ─────────────────────────────────────────────────────────────────────

export interface PerfilInversionResumen {
  id: number
  nombrePerfil: string
}

export interface UsuarioMe {
  id: number
  nombreCompleto: string
  correo: string
  celular?: string
  proveedorOauth: ProveedorOauth
  fechaRegistro: string
  fechaUltimaActualizacionPerfil?: string
  roles: string[]
  perfilInversion: PerfilInversionResumen | null
  requiereRecalibracion: boolean
}

export interface ActualizarUsuarioRequest {
  nombreCompleto: string
  celular?: string
}

export interface UltimaEncuesta {
  id: number
  fechaRealizacion: string
  estado: EstadoEncuesta
  puntajeTotal: number | null
  perfilAsignado: PerfilInversionResumen | null
}

// ── Calibración ──────────────────────────────────────────────────────────────

export interface OpcionRespuesta {
  id: number
  textoOpcion: string
  orden: number
}

export interface PreguntaCalibracion {
  id: number
  textoPregunta: string
  orden: number
  opciones: OpcionRespuesta[]
}

export interface EncuestaCreada {
  id: number
  origen: OrigenEncuesta
  estado: EstadoEncuesta
  fechaRealizacion: string
  fechaVencimiento?: string
}

export interface EncuestaPendiente {
  id: number
  fechaRealizacion: string
  origen: OrigenEncuesta
  preguntasRespondidas: number
  preguntasTotales: number
  preguntasPendientes: (PreguntaCalibracion & { opciones: OpcionRespuesta[] })[]
}

export interface EncuestaCompletada {
  id: number
  puntajeTotal: number
  estado: EstadoEncuesta
  perfilAsignado: PerfilInversionResumen
  fechaRealizacion: string
  fechaVencimiento?: string
}

// ── Perfiles ─────────────────────────────────────────────────────────────────

export interface OpcionInversion {
  id: number
  nombreOpcion: string
  descripcion?: string
}

export interface Portafolio {
  id: number
  nombrePortafolio: string
  descripcion?: string
  rentabilidadMinima: number
  rentabilidadMaxima: number
  porcentaje: number
  opciones: OpcionInversion[]
}

export interface FormulaExposicion {
  idPortafolio: number
  formulaExposicion: string
}

export interface PerfilInversion {
  id: number
  nombrePerfil: string
  descripcion?: string
  rentabilidadMinima: number
  rentabilidadMedia: number
  rentabilidadMaxima: number
  portafolios: Portafolio[]
  formulasExposicion: FormulaExposicion[]
}

// ── Simulación ───────────────────────────────────────────────────────────────

export interface SimulacionRequest {
  idPerfil: number
  valorInversionInicial: number
  aporteMensual: number
  plazo: number
  idTipoPlazo: number
  idDisclaimer?: number
}

export interface PeriodoProyeccion {
  periodo: number
  valorProyectadoMinimo: number
  valorProyectadoEsperado: number
  valorProyectadoMaximo: number
  rentabilidadMinimaAplicada: number
  rentabilidadMaximaAplicada: number
}

export interface ResumenSimulacion {
  gananciaEsperada: number
  rendimientoPorcentualTotal: number
}

export interface SimulacionCalculada {
  idPerfil: number
  nombrePerfilSimulado: string
  valorInversionInicial: number
  aporteMensual: number
  plazo: number
  nombreTipoPlazo: string
  proyeccion: PeriodoProyeccion[]
  resumen: ResumenSimulacion
}

export interface SimulacionGuardada extends SimulacionCalculada {
  id: number
  fechaSimulacion: string
}

export interface SimulacionResumen {
  id: number
  nombrePerfilSimulado: string
  valorInversionInicial: number
  aporteMensual: number
  plazo: number
  nombreTipoPlazo: string
  gananciaEsperada: number
  rendimientoPorcentualTotal: number
  fechaSimulacion: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

// ── Disclaimers ───────────────────────────────────────────────────────────────

export interface Disclaimer {
  id: number
  titulo: string
  contenido: string
  fechaVigenciaDesde: string
  fechaVigenciaHasta?: string
}
