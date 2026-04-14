'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { PageResponse, SimulacionResumen } from '@/lib/types'
import HistorialItem from '@/components/simulacion/HistorialItem'
import Spinner from '@/components/shared/Spinner'
import { useState } from 'react'
import { Button } from '@/components/ui/Button'

export default function HistorialSimulacionPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery<PageResponse<SimulacionResumen>>({
    queryKey: ['simulaciones', page],
    queryFn: async () => {
      const { data } = await api.get('/simulaciones', {
        params: { page, size: 10, sort: 'fechaSimulacion,desc' },
      })
      return data
    },
  })

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-4">
      <h1 className="text-2xl font-bold">Historial de Simulaciones</h1>

      {data?.content.length === 0 && (
        <p className="text-muted-foreground text-center py-12">
          Aún no tienes simulaciones guardadas.
        </p>
      )}

      <div className="space-y-3">
        {data?.content.map((sim) => (
          <HistorialItem key={sim.id} simulacion={sim} />
        ))}
      </div>

      {data && data.totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <Button
            variant="outline"
            size="sm"
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            Anterior
          </Button>
          <span className="text-sm text-muted-foreground self-center">
            Página {page + 1} de {data.totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page >= data.totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            Siguiente
          </Button>
        </div>
      )}
    </div>
  )
}
