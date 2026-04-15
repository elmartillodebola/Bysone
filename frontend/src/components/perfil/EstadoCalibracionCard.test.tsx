/**
 * EstadoCalibracionCard — RN-CAL-01, CA-USU-11
 *
 * 1. Sin calibración: muestra badge "Sin calibrar" y botón para iniciar.
 * 2. Con calibración y perfil al día: muestra datos y badge "Al día".
 * 3. Con calibración vencida: muestra badge "Recalibración requerida" y CTA.
 * 4. Muestra nombre del perfil asignado y puntaje correctamente.
 */

import React from 'react'
import { render, screen } from '@testing-library/react'
import EstadoCalibracionCard from './EstadoCalibracionCard'
import { UsuarioMe, UltimaEncuesta } from '@/lib/types'

const usuarioSinPerfil: UsuarioMe = {
  id: 1,
  nombreCompleto: 'María Rodríguez',
  correo: 'maria@gmail.com',
  proveedorOauth: 'GOOGLE',
  fechaRegistro: '2026-01-01T00:00:00',
  roles: ['USER'],
  perfilInversion: null,
  requiereRecalibracion: true,
}

const usuarioAlDia: UsuarioMe = {
  ...usuarioSinPerfil,
  perfilInversion: { id: 2, nombrePerfil: 'Moderado' },
  fechaUltimaActualizacionPerfil: '2026-03-01T10:00:00',
  requiereRecalibracion: false,
}

const usuarioVencido: UsuarioMe = {
  ...usuarioSinPerfil,
  perfilInversion: { id: 1, nombrePerfil: 'Conservador' },
  fechaUltimaActualizacionPerfil: '2025-06-01T10:00:00',
  requiereRecalibracion: true,
}

const encuestaCompletada: UltimaEncuesta = {
  id: 10,
  fechaRealizacion: '2026-03-01T10:00:00',
  estado: 'COMPLETADA',
  puntajeTotal: 4,
  perfilAsignado: { id: 2, nombrePerfil: 'Moderado' },
}

describe('EstadoCalibracionCard — RN-CAL-01 y CA-USU-11', () => {
  it('sin calibración: badge "Sin calibrar" y botón iniciar calibración', () => {
    render(<EstadoCalibracionCard usuario={usuarioSinPerfil} ultimaCalibracion={null} />)

    expect(screen.getByText(/sin calibrar/i)).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /iniciar calibración/i })).toBeInTheDocument()
    expect(screen.getByText(/aún no has completado/i)).toBeInTheDocument()
  })

  it('con calibración al día: badge "Al día" y datos de la encuesta', () => {
    render(<EstadoCalibracionCard usuario={usuarioAlDia} ultimaCalibracion={encuestaCompletada} />)

    expect(screen.getByText(/al día/i)).toBeInTheDocument()
    expect(screen.getByText('Moderado')).toBeInTheDocument()
    expect(screen.getByText('4')).toBeInTheDocument()
    expect(screen.queryByText(/recalibrar ahora/i)).not.toBeInTheDocument()
  })

  it('con perfil vencido: badge "Recalibración requerida" y CTA recalibrar', () => {
    const encuesta: UltimaEncuesta = {
      ...encuestaCompletada,
      perfilAsignado: { id: 1, nombrePerfil: 'Conservador' },
    }
    render(<EstadoCalibracionCard usuario={usuarioVencido} ultimaCalibracion={encuesta} />)

    expect(screen.getByText(/recalibración requerida/i)).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /recalibrar ahora/i })).toBeInTheDocument()
  })

  it('muestra nombre del perfil asignado correctamente', () => {
    render(<EstadoCalibracionCard usuario={usuarioAlDia} ultimaCalibracion={encuestaCompletada} />)
    expect(screen.getByText('Moderado')).toBeInTheDocument()
  })
})
