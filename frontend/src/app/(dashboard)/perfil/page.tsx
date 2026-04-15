'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { PerfilInversion } from '@/lib/types'
import { useUsuarioMe, useUltimaCalibracion } from '@/hooks/useUsuario'
import DatosUsuarioCard from '@/components/perfil/DatosUsuarioCard'
import EstadoCalibracionCard from '@/components/perfil/EstadoCalibracionCard'
import PerfilCard from '@/components/perfil/PerfilCard'
import PortafolioBreakdown from '@/components/perfil/PortafolioBreakdown'
import Spinner from '@/components/shared/Spinner'

export default function PerfilPage() {
  const { data: usuario, isLoading: cargandoUsuario, isError: errorUsuario } = useUsuarioMe()

  const { data: perfiles, isLoading: cargandoPerfiles } = useQuery<PerfilInversion[]>({
    queryKey: ['perfiles'],
    queryFn: async () => {
      const { data } = await api.get('/perfiles')
      return data
    },
  })

  // La query de calibracion no bloquea el render principal
  const { data: ultimaCalibracion } = useUltimaCalibracion()

  if (cargandoUsuario || cargandoPerfiles) return <Spinner />

  if (errorUsuario || !usuario) {
    return (
      <div className="max-w-3xl mx-auto py-16 text-center space-y-3">
        <p className="text-muted-foreground">No se pudo cargar la información del perfil.</p>
        <button
          className="text-sm underline text-primary"
          onClick={() => window.location.reload()}
        >
          Reintentar
        </button>
      </div>
    )
  }

  const miPerfil = perfiles?.find((p) => p.id === usuario.perfilInversion?.id)

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <h1 className="text-2xl font-bold">Mi Perfil</h1>

      {/* Sección 1 — Datos personales */}
      <DatosUsuarioCard usuario={usuario} />

      {/* Sección 2 — Estado de calibración */}
      <EstadoCalibracionCard
        usuario={usuario}
        ultimaCalibracion={ultimaCalibracion ?? null}
      />

      {/* Sección 3 — Perfil de inversión (solo si tiene perfil asignado) */}
      {miPerfil && (
        <div className="space-y-4">
          <h2 className="text-lg font-semibold">Mi perfil de inversión</h2>
          <PerfilCard perfil={miPerfil} />
          <PortafolioBreakdown portafolios={miPerfil.portafolios} />
        </div>
      )}
    </div>
  )
}
