import { Portafolio } from '@/lib/types'
import { formatPercent } from '@/lib/utils'

interface Props {
  portafolios: Portafolio[]
}

export default function PortafolioBreakdown({ portafolios }: Props) {
  return (
    <div className="space-y-3">
      <h3 className="font-semibold text-lg">Distribución del portafolio</h3>
      {portafolios.map((p) => (
        <div key={p.id} className="bg-card border rounded-lg p-4 space-y-2">
          <div className="flex justify-between items-center">
            <span className="font-medium">{p.nombrePortafolio}</span>
            <span className="text-primary font-semibold">{formatPercent(p.porcentaje)}</span>
          </div>
          <div className="w-full bg-secondary rounded-full h-2">
            <div
              className="bg-primary h-2 rounded-full"
              style={{ width: `${p.porcentaje}%` }}
            />
          </div>
          <div className="flex gap-4 text-xs text-muted-foreground">
            <span>Rent. mín: {formatPercent(p.rentabilidadMinima)}</span>
            <span>Rent. máx: {formatPercent(p.rentabilidadMaxima)}</span>
          </div>
          {p.opciones.length > 0 && (
            <div className="flex flex-wrap gap-1 mt-1">
              {p.opciones.map((o) => (
                <span key={o.id} className="bg-secondary text-xs px-2 py-0.5 rounded-full">
                  {o.nombreOpcion}
                </span>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  )
}
