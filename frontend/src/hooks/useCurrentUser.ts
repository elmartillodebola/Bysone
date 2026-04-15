'use client'

import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { UsuarioMe } from '@/lib/types'

/**
 * Datos del usuario autenticado, incluyendo roles.
 * Usable en cualquier componente del dashboard para decisiones de UI.
 */
export function useCurrentUser() {
  const { data: usuario, isLoading } = useQuery<UsuarioMe>({
    queryKey: ['usuario-me'],
    queryFn: async () => {
      const { data } = await api.get('/usuarios/me')
      return data
    },
    staleTime: 5 * 60 * 1000,
  })

  const esAdmin = usuario?.roles?.includes('ADMIN') ?? false

  return { usuario, isLoading, esAdmin }
}
