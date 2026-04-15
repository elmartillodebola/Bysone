import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '@/lib/api'
import { ActualizarUsuarioRequest, UltimaEncuesta, UsuarioMe } from '@/lib/types'

export function useUsuarioMe() {
  return useQuery<UsuarioMe>({
    queryKey: ['usuario-me'],
    queryFn: async () => {
      const { data } = await api.get('/usuarios/me')
      return data
    },
  })
}

export function useActualizarUsuario() {
  const queryClient = useQueryClient()
  return useMutation<UsuarioMe, Error, ActualizarUsuarioRequest>({
    mutationFn: async (payload) => {
      const { data } = await api.put('/usuarios/me', payload)
      return data
    },
    onSuccess: (data) => {
      queryClient.setQueryData(['usuario-me'], data)
    },
  })
}

export function useUltimaCalibracion() {
  return useQuery<UltimaEncuesta | null>({
    queryKey: ['ultima-calibracion'],
    queryFn: async () => {
      try {
        const { data, status } = await api.get('/usuarios/me/calibracion', {
          validateStatus: (s) => s === 200 || s === 204,
        })
        return status === 204 ? null : data
      } catch {
        return null
      }
    },
  })
}
