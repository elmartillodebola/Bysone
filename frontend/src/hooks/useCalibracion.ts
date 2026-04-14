'use client'

import { useState } from 'react'
import { PreguntaCalibracion, EncuestaCompletada } from '@/lib/types'
import api from '@/lib/api'

interface RespuestaLocal {
  idPregunta: number
  idOpcionRespuesta: number
}

/**
 * Maneja el avance del wizard de calibración paso a paso.
 * Permite iniciar, responder preguntas y completar la encuesta.
 */
export function useCalibracion(preguntas: PreguntaCalibracion[]) {
  const [idEncuesta, setIdEncuesta] = useState<number | null>(null)
  const [pasoActual, setPasoActual] = useState(0)
  const [respuestas, setRespuestas] = useState<RespuestaLocal[]>([])
  const [resultado, setResultado] = useState<EncuestaCompletada | null>(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const iniciar = async () => {
    setCargando(true)
    setError(null)
    try {
      const { data } = await api.post('/calibracion/encuestas')
      setIdEncuesta(data.id)
      setPasoActual(0)
    } catch (err: unknown) {
      const apiErr = err as {
        response?: { data?: { code?: string; encuestaPendiente?: { id: number } } }
      }
      if (
        apiErr.response?.data?.code === 'SURVEY_ALREADY_PENDING' &&
        apiErr.response.data.encuestaPendiente
      ) {
        setIdEncuesta(apiErr.response.data.encuestaPendiente.id)
        setPasoActual(0)
      } else {
        setError('No se pudo iniciar la encuesta')
      }
    } finally {
      setCargando(false)
    }
  }

  const responder = async (idOpcionRespuesta: number) => {
    if (!idEncuesta) return
    const pregunta = preguntas[pasoActual]
    setCargando(true)
    setError(null)
    try {
      await api.post(`/calibracion/encuestas/${idEncuesta}/respuestas`, {
        idPregunta: pregunta.id,
        idOpcionRespuesta,
      })
      setRespuestas((prev) => [
        ...prev,
        { idPregunta: pregunta.id, idOpcionRespuesta },
      ])
      if (pasoActual < preguntas.length - 1) {
        setPasoActual((p) => p + 1)
      }
    } catch {
      setError('Error al registrar la respuesta')
    } finally {
      setCargando(false)
    }
  }

  const completar = async () => {
    if (!idEncuesta) return
    setCargando(true)
    setError(null)
    try {
      const { data } = await api.post<EncuestaCompletada>(
        `/calibracion/encuestas/${idEncuesta}/completar`
      )
      setResultado(data)
    } catch {
      setError('Error al completar la encuesta')
    } finally {
      setCargando(false)
    }
  }

  const progreso = preguntas.length > 0 ? (pasoActual / preguntas.length) * 100 : 0
  const esUltimaPregunta = pasoActual === preguntas.length - 1

  return {
    idEncuesta,
    pasoActual,
    respuestas,
    resultado,
    cargando,
    error,
    progreso,
    esUltimaPregunta,
    iniciar,
    responder,
    completar,
  }
}
