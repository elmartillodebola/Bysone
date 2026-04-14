'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { PreguntaCalibracion } from '@/lib/types'
import { useCalibracion } from '@/hooks/useCalibracion'
import PreguntaCard from '@/components/calibracion/PreguntaCard'
import BarraProgreso from '@/components/calibracion/BarraProgreso'
import Spinner from '@/components/shared/Spinner'
import { Button } from '@/components/ui/Button'
import { useRouter } from 'next/navigation'
import { useEffect } from 'react'

export default function CalibracionPage() {
  const router = useRouter()

  const { data: preguntas = [], isLoading } = useQuery<PreguntaCalibracion[]>({
    queryKey: ['preguntas-calibracion'],
    queryFn: async () => {
      const { data } = await api.get('/calibracion/preguntas')
      return data
    },
  })

  const {
    pasoActual,
    resultado,
    cargando,
    error,
    progreso,
    esUltimaPregunta,
    iniciar,
    responder,
    completar,
  } = useCalibracion(preguntas)

  useEffect(() => {
    if (preguntas.length > 0) {
      iniciar()
    }
  }, [preguntas.length])

  useEffect(() => {
    if (resultado) {
      router.push('/calibracion/resultado')
    }
  }, [resultado])

  if (isLoading) return <Spinner />

  if (preguntas.length === 0) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        No hay preguntas de calibración disponibles.
      </div>
    )
  }

  const preguntaActual = preguntas[pasoActual]

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Calibración de Perfil</h1>
        <p className="text-muted-foreground text-sm mt-1">
          Responde todas las preguntas para descubrir tu perfil de inversión
        </p>
      </div>

      <BarraProgreso progreso={progreso} paso={pasoActual + 1} total={preguntas.length} />

      {error && (
        <div className="bg-destructive/10 text-destructive rounded-md p-3 text-sm">{error}</div>
      )}

      <PreguntaCard
        pregunta={preguntaActual}
        onSeleccionar={esUltimaPregunta ? undefined : responder}
        cargando={cargando}
      />

      {esUltimaPregunta && (
        <Button onClick={completar} disabled={cargando} className="w-full">
          {cargando ? 'Procesando...' : 'Completar encuesta'}
        </Button>
      )}
    </div>
  )
}
