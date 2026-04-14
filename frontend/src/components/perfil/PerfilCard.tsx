import { PerfilInversion } from '@/lib/types'
import { formatPercent } from '@/lib/utils'

interface Props {
  perfil: PerfilInversion
}

export default function PerfilCard({ perfil }: Props) {
  return (
    <div className="bg-card border rounded-lg p-6 space-y-3">
      <h2 className="text-xl font-bold">{perfil.nombrePerfil}</h2>
      {perfil.descripcion && <p className="text-muted-foreground text-sm">{perfil.descripcion}</p>}
      <div className="flex gap-6">
        <div>
          <p className="text-xs text-muted-foreground">Rentabilidad mínima</p>
          <p className="font-semibold">{formatPercent(perfil.rentabilidadMinima)}</p>
        </div>
        <div>
          <p className="text-xs text-muted-foreground">Rentabilidad media</p>
          <p className="font-semibold">{formatPercent(perfil.rentabilidadMedia)}</p>
        </div>
        <div>
          <p className="text-xs text-muted-foreground">Rentabilidad máxima</p>
          <p className="font-semibold">{formatPercent(perfil.rentabilidadMaxima)}</p>
        </div>
      </div>
    </div>
  )
}
