'use client'

import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts'
import { PeriodoProyeccion, ResumenSimulacion } from '@/lib/types'
import { formatCurrency, formatPercent } from '@/lib/utils'

interface Props {
  proyeccion: PeriodoProyeccion[]
  resumen: ResumenSimulacion
}

export default function GraficaProyeccion({ proyeccion, resumen }: Props) {
  return (
    <div className="bg-card border rounded-lg p-6 space-y-4">
      <div className="flex gap-6">
        <div>
          <p className="text-sm text-muted-foreground">Ganancia esperada</p>
          <p className="text-xl font-bold text-primary">{formatCurrency(resumen.gananciaEsperada)}</p>
        </div>
        <div>
          <p className="text-sm text-muted-foreground">Rendimiento total</p>
          <p className="text-xl font-bold text-primary">{formatPercent(resumen.rendimientoPorcentualTotal)}</p>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={proyeccion}>
          <defs>
            <linearGradient id="esperado" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3} />
              <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="periodo" label={{ value: 'Período', position: 'insideBottom', offset: -4 }} />
          <YAxis tickFormatter={(v) => formatCurrency(v)} width={90} />
          <Tooltip formatter={(v: number) => formatCurrency(v)} />
          <Legend />
          <Area
            type="monotone"
            dataKey="valorProyectadoMinimo"
            name="Mínimo"
            stroke="#94a3b8"
            fill="none"
            strokeDasharray="4 4"
          />
          <Area
            type="monotone"
            dataKey="valorProyectadoEsperado"
            name="Esperado"
            stroke="#3b82f6"
            fill="url(#esperado)"
          />
          <Area
            type="monotone"
            dataKey="valorProyectadoMaximo"
            name="Máximo"
            stroke="#10b981"
            fill="none"
            strokeDasharray="4 4"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
