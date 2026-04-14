'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { PerfilInversion, UsuarioMe } from '@/lib/types'
import PerfilCard from '@/components/perfil/PerfilCard'
import PortafolioBreakdown from '@/components/perfil/PortafolioBreakdown'
import Spinner from '@/components/shared/Spinner'
import Link from 'next/link'
import { Button } from '@/components/ui/Button'

export default function PerfilPage() {
  const { data: usuario, isLoading: cargandoUsuario } = useQuery<UsuarioMe>({
    queryKey: ['usuario-me'],
    queryFn: async () => {
      const { data } = await api.get('/usuarios/me')
      return data
    },
  })

  const { data: perfiles, isLoading: cargandoPerfiles } = useQuery<PerfilInversion[]>({
    queryKey: ['perfiles'],
    queryFn: async () => {
      const { data } = await api.get('/perfiles')
      return data
    },
  })

  if (cargandoUsuario || cargandoPerfiles) return <Spinner />

  const miPerfil = perfiles?.find(
    (p) => p.id === usuario?.perfilInversion?.id
  )

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Mi Perfil de Inversión</h1>
        {usuario?.requiereRecalibracion && (
          <Link href="/calibracion">
            <Button size="sm">Recalibrar perfil</Button>
          </Link>
        )}
      </div>

      {!miPerfil && (
        <div className="bg-card border rounded-lg p-6 text-center space-y-3">
          <p className="text-muted-foreground">
            Aún no tienes un perfil asignado. Completa la calibración para comenzar.
          </p>
          <Link href="/calibracion">
            <Button>Iniciar calibración</Button>
          </Link>
        </div>
      )}

      {miPerfil && (
        <>
          <PerfilCard perfil={miPerfil} />
          <PortafolioBreakdown portafolios={miPerfil.portafolios} />
        </>
      )}
    </div>
  )
}
