'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { PerfilInversion } from '@/lib/types'
import { SimulacionRequest } from '@/lib/types'
import { Button } from '@/components/ui/Button'

const schema = z.object({
  idPerfil: z.coerce.number().min(1, 'Selecciona un perfil'),
  valorInversionInicial: z.coerce.number().positive('Debe ser mayor a 0'),
  aporteMensual: z.coerce.number().min(0, 'Debe ser 0 o mayor'),
  plazo: z.coerce.number().positive('Debe ser mayor a 0'),
  idTipoPlazo: z.coerce.number().min(1, 'Selecciona un tipo de plazo'),
})

type FormData = z.infer<typeof schema>

interface Props {
  onCalcular: (data: SimulacionRequest) => void
  cargando: boolean
}

export default function FormularioSimulacion({ onCalcular, cargando }: Props) {
  const { data: perfiles = [] } = useQuery<PerfilInversion[]>({
    queryKey: ['perfiles'],
    queryFn: async () => {
      const { data } = await api.get('/perfiles')
      return data
    },
  })

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) })

  return (
    <form
      onSubmit={handleSubmit(onCalcular)}
      className="bg-card border rounded-lg p-6 grid grid-cols-1 md:grid-cols-2 gap-4"
    >
      <div className="col-span-full">
        <label className="text-sm font-medium">Perfil de inversión</label>
        <select
          {...register('idPerfil')}
          className="mt-1 block w-full rounded-md border bg-background px-3 py-2 text-sm"
        >
          <option value="">Selecciona un perfil</option>
          {perfiles.map((p) => (
            <option key={p.id} value={p.id}>{p.nombrePerfil}</option>
          ))}
        </select>
        {errors.idPerfil && <p className="text-destructive text-xs mt-1">{errors.idPerfil.message}</p>}
      </div>

      <div>
        <label className="text-sm font-medium">Inversión inicial (COP)</label>
        <input
          type="number"
          {...register('valorInversionInicial')}
          className="mt-1 block w-full rounded-md border bg-background px-3 py-2 text-sm"
          placeholder="1000000"
        />
        {errors.valorInversionInicial && (
          <p className="text-destructive text-xs mt-1">{errors.valorInversionInicial.message}</p>
        )}
      </div>

      <div>
        <label className="text-sm font-medium">Aporte mensual (COP)</label>
        <input
          type="number"
          {...register('aporteMensual')}
          className="mt-1 block w-full rounded-md border bg-background px-3 py-2 text-sm"
          placeholder="500000"
        />
        {errors.aporteMensual && (
          <p className="text-destructive text-xs mt-1">{errors.aporteMensual.message}</p>
        )}
      </div>

      <div>
        <label className="text-sm font-medium">Plazo</label>
        <input
          type="number"
          {...register('plazo')}
          className="mt-1 block w-full rounded-md border bg-background px-3 py-2 text-sm"
          placeholder="10"
        />
        {errors.plazo && <p className="text-destructive text-xs mt-1">{errors.plazo.message}</p>}
      </div>

      <div>
        <label className="text-sm font-medium">Tipo de plazo</label>
        <select
          {...register('idTipoPlazo')}
          className="mt-1 block w-full rounded-md border bg-background px-3 py-2 text-sm"
        >
          <option value="">Selecciona</option>
          <option value="1">Año</option>
          <option value="2">Mes</option>
          <option value="3">Trimestre</option>
          <option value="4">Día</option>
        </select>
        {errors.idTipoPlazo && (
          <p className="text-destructive text-xs mt-1">{errors.idTipoPlazo.message}</p>
        )}
      </div>

      <div className="col-span-full">
        <Button type="submit" disabled={cargando} className="w-full">
          {cargando ? 'Calculando...' : 'Calcular proyección'}
        </Button>
      </div>
    </form>
  )
}
