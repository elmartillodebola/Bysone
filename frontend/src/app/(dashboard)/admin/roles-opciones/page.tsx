'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface Rol {
  id: number
  nombreRol: string
  descripcionRol?: string
}

interface OpcionFuncional {
  id: number
  nombreOpcionFuncional: string
}

interface RolOpcion {
  idRol: number
  idOpcion: number
}

export default function RolesOpcionesPage() {
  const queryClient = useQueryClient()
  const router = useRouter()

  const { data: roles = [], isLoading: loadingRoles } = useQuery<Rol[]>({
    queryKey: ['admin-roles'],
    queryFn: async () => (await api.get('/admin/roles-opciones/roles')).data,
  })

  const { data: opciones = [], isLoading: loadingOpciones } = useQuery<OpcionFuncional[]>({
    queryKey: ['admin-opciones-funcionales'],
    queryFn: async () => (await api.get('/admin/roles-opciones/opciones')).data,
  })

  // Cargamos las opciones de cada rol y construimos un Set de claves "idRol-idOpcion"
  const { data: asignaciones = [], isLoading: loadingAsignaciones } = useQuery<RolOpcion[]>({
    queryKey: ['admin-roles-asignaciones'],
    enabled: roles.length > 0,
    queryFn: async () => {
      const results = await Promise.all(
        roles.map(r =>
          api.get(`/admin/roles-opciones/rol/${r.id}`).then(res =>
            (res.data as { idRol: number; idOpcion: number }[]).map(a => ({
              idRol: a.idRol,
              idOpcion: a.idOpcion,
            }))
          )
        )
      )
      return results.flat()
    },
  })

  const asignadasSet = new Set(asignaciones.map(a => `${a.idRol}-${a.idOpcion}`))

  const mutAsignar = useMutation({
    mutationFn: ({ idRol, idOpcion }: { idRol: number; idOpcion: number }) =>
      api.post('/admin/roles-opciones', { idRol, idOpcion }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-roles-asignaciones'] }),
  })

  const mutDesasignar = useMutation({
    mutationFn: ({ idRol, idOpcion }: { idRol: number; idOpcion: number }) =>
      api.delete(`/admin/roles-opciones/${idRol}/${idOpcion}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-roles-asignaciones'] }),
  })

  function toggle(idRol: number, idOpcion: number) {
    const key = `${idRol}-${idOpcion}`
    if (asignadasSet.has(key)) {
      mutDesasignar.mutate({ idRol, idOpcion })
    } else {
      mutAsignar.mutate({ idRol, idOpcion })
    }
  }

  const isLoading = loadingRoles || loadingOpciones || loadingAsignaciones

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold">Roles × Opciones funcionales</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Activa o desactiva qué opciones tiene acceso cada rol.
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>

      <div className="border rounded-lg overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium min-w-[160px]">Rol</th>
              {opciones.map(op => (
                <th key={op.id} className="px-3 py-3 text-center font-medium text-xs">
                  {op.nombreOpcionFuncional.replace(/_/g, ' ')}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {roles.map(rol => (
              <tr key={rol.id} className="bg-card">
                <td className="px-4 py-3">
                  <p className="font-medium">{rol.nombreRol}</p>
                  {rol.descripcionRol && (
                    <p className="text-xs text-muted-foreground">{rol.descripcionRol}</p>
                  )}
                </td>
                {opciones.map(op => {
                  const asignada = asignadasSet.has(`${rol.id}-${op.id}`)
                  return (
                    <td key={op.id} className="px-3 py-3 text-center">
                      <input
                        type="checkbox"
                        checked={asignada}
                        onChange={() => toggle(rol.id, op.id)}
                        className="h-4 w-4 cursor-pointer accent-primary"
                        aria-label={`${rol.nombreRol} — ${op.nombreOpcionFuncional}`}
                      />
                    </td>
                  )
                })}
              </tr>
            ))}
            {roles.length === 0 && (
              <tr>
                <td colSpan={opciones.length + 1} className="px-4 py-6 text-center text-muted-foreground">
                  Sin roles configurados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
