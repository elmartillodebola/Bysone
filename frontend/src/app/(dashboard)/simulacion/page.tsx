'use client'

import { useSimulacion } from '@/hooks/useSimulacion'
import FormularioSimulacion from '@/components/simulacion/FormularioSimulacion'
import GraficaProyeccion from '@/components/simulacion/GraficaProyeccion'
import { SimulacionRequest } from '@/lib/types'
import { Button } from '@/components/ui/Button'
import Link from 'next/link'

export default function SimulacionPage() {
  const { resultado, cargando, error, calcular, guardar, descartar } = useSimulacion()

  const handleCalcular = (data: SimulacionRequest) => calcular(data)

  const handleGuardar = async (data: SimulacionRequest) => {
    try {
      await guardar(data)
    } catch {
      // el error ya está en el estado del hook
    }
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Simulación de Inversión</h1>
          <p className="text-muted-foreground text-sm mt-1">
            Proyecta cómo crecería tu ahorro según tu perfil de riesgo
          </p>
        </div>
        <Link href="/simulacion/historial">
          <Button variant="outline" size="sm">Ver historial</Button>
        </Link>
      </div>

      {error && (
        <div className="bg-destructive/10 text-destructive rounded-md p-3 text-sm">{error}</div>
      )}

      <FormularioSimulacion onCalcular={handleCalcular} cargando={cargando} />

      {resultado && (
        <div className="space-y-4">
          <GraficaProyeccion proyeccion={resultado.proyeccion} resumen={resultado.resumen} />
          <div className="flex gap-3">
            <Button onClick={() => handleGuardar({
              idPerfil: resultado.idPerfil,
              valorInversionInicial: resultado.valorInversionInicial,
              aporteMensual: resultado.aporteMensual,
              plazo: resultado.plazo,
              idTipoPlazo: 1,
            })}>
              Guardar simulación
            </Button>
            <Button variant="outline" onClick={descartar}>Descartar</Button>
          </div>
        </div>
      )}
    </div>
  )
}
