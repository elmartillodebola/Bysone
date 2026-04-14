'use client'

import { PreguntaCalibracion } from '@/lib/types'
import { Button } from '@/components/ui/Button'

interface Props {
  pregunta: PreguntaCalibracion
  onSeleccionar?: (idOpcion: number) => void
  cargando: boolean
}

export default function PreguntaCard({ pregunta, onSeleccionar, cargando }: Props) {
  return (
    <div className="bg-card border rounded-lg p-6 space-y-4">
      <p className="font-medium text-lg">{pregunta.textoPregunta}</p>
      <div className="space-y-2">
        {pregunta.opciones.map((opcion) => (
          <button
            key={opcion.id}
            onClick={() => onSeleccionar?.(opcion.id)}
            disabled={cargando || !onSeleccionar}
            className="w-full text-left px-4 py-3 rounded-md border hover:bg-accent transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {opcion.textoOpcion}
          </button>
        ))}
      </div>
    </div>
  )
}
