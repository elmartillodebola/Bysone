'use client'

import { useState } from 'react'
import { SimulacionCalculada, SimulacionRequest } from '@/lib/types'
import api from '@/lib/api'

/**
 * Gestiona el estado pre-guardado de la simulación.
 * El resultado se mantiene en memoria hasta que el usuario confirma guardar.
 * Si navega sin guardar, el resultado se descarta.
 */
export function useSimulacion() {
  const [resultado, setResultado] = useState<SimulacionCalculada | null>(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const calcular = async (request: SimulacionRequest) => {
    setCargando(true)
    setError(null)
    try {
      const { data } = await api.post<SimulacionCalculada>(
        '/simulaciones/calcular',
        request
      )
      setResultado(data)
    } catch (err: unknown) {
      const mensaje =
        (err as { response?: { data?: { message?: string } } })?.response?.data
          ?.message ?? 'Error al calcular la simulación'
      setError(mensaje)
    } finally {
      setCargando(false)
    }
  }

  const guardar = async (request: SimulacionRequest) => {
    setCargando(true)
    setError(null)
    try {
      await api.post('/simulaciones', request)
      setResultado(null)
    } catch (err: unknown) {
      const mensaje =
        (err as { response?: { data?: { message?: string } } })?.response?.data
          ?.message ?? 'Error al guardar la simulación'
      setError(mensaje)
      throw err
    } finally {
      setCargando(false)
    }
  }

  const descartar = () => setResultado(null)

  return { resultado, cargando, error, calcular, guardar, descartar }
}
