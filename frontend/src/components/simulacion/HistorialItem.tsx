import { SimulacionResumen } from '@/lib/types'
import { formatCurrency, formatPercent } from '@/lib/utils'

interface Props {
  simulacion: SimulacionResumen
}

export default function HistorialItem({ simulacion }: Props) {
  return (
    <div className="bg-card border rounded-lg p-4 flex items-center justify-between">
      <div>
        <p className="font-medium">{simulacion.nombrePerfilSimulado}</p>
        <p className="text-sm text-muted-foreground">
          {simulacion.plazo} {simulacion.nombreTipoPlazo.toLowerCase()}(s) ·{' '}
          {new Date(simulacion.fechaSimulacion).toLocaleDateString('es-CO')}
        </p>
      </div>
      <div className="text-right">
        <p className="font-semibold text-primary">{formatCurrency(simulacion.gananciaEsperada)}</p>
        <p className="text-sm text-muted-foreground">{formatPercent(simulacion.rendimientoPorcentualTotal)}</p>
      </div>
    </div>
  )
}
