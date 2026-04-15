'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface Rol {
  id: number
  nombreRol: string
  descripcionRol?: string
}

const empty = { nombreRol: '', descripcionRol: '' }

export default function RolesPage() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: roles = [], isLoading } = useQuery<Rol[]>({
    queryKey: ['admin-roles'],
    queryFn: async () => (await api.get('/admin/roles')).data,
  })

  function validar() {
    if (!form.nombreRol.trim()) return 'El nombre del rol es obligatorio.'
    return null
  }

  const mutGuardar = useMutation({
    mutationFn: () => {
      const body = {
        nombreRol: form.nombreRol.trim().toUpperCase(),
        descripcionRol: form.descripcionRol || undefined,
      }
      return editandoId
        ? api.put(`/admin/roles/${editandoId}`, body)
        : api.post('/admin/roles', body)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-roles'] })
      setForm(empty)
      setEditandoId(null)
      setError(null)
    },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/roles/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-roles'] }),
    onError: (err: any) =>
      setError(err.response?.data?.message ?? 'No se puede eliminar: el rol está en uso.'),
  })

  function iniciarEdicion(r: Rol) {
    setEditandoId(r.id)
    setForm({ nombreRol: r.nombreRol, descripcionRol: r.descripcionRol ?? '' })
    setError(null)
  }

  function cancelarEdicion() {
    setEditandoId(null)
    setForm(empty)
    setError(null)
  }

  function handleGuardar() {
    const err = validar()
    if (err) { setError(err); return }
    setError(null)
    mutGuardar.mutate()
  }

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Roles del sistema</h1>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>
      <p className="text-xs text-muted-foreground">
        Los nombres de roles se normalizan automáticamente a MAYÚSCULAS.
      </p>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Nombre</th>
              <th className="text-left px-4 py-3 font-medium">Descripción</th>
              <th className="px-4 py-3 w-36" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {roles.map(r => (
              <tr key={r.id} className="bg-card">
                <td className="px-4 py-3 font-mono font-medium">{r.nombreRol}</td>
                <td className="px-4 py-3 text-muted-foreground">{r.descripcionRol ?? '—'}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2 justify-end">
                    <Button size="sm" variant="outline" onClick={() => iniciarEdicion(r)}>Editar</Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => mutEliminar.mutate(r.id)}
                      disabled={mutEliminar.isPending}
                    >
                      Eliminar
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            {roles.length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-muted-foreground">
                  Sin roles registrados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar rol' : 'Nuevo rol'}</h2>
        {error && <p className="text-destructive text-sm">{error}</p>}
        <div className="space-y-3">
          <div>
            <label className="text-xs text-muted-foreground">Nombre *</label>
            <input
              required
              value={form.nombreRol}
              onChange={e => setForm(f => ({ ...f, nombreRol: e.target.value.toUpperCase() }))}
              className="w-full border rounded px-3 py-2 text-sm mt-1 font-mono"
              placeholder="NUEVO_ROL"
            />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Descripción</label>
            <input
              value={form.descripcionRol}
              onChange={e => setForm(f => ({ ...f, descripcionRol: e.target.value }))}
              className="w-full border rounded px-3 py-2 text-sm mt-1"
              placeholder="Descripción del rol y sus responsabilidades"
            />
          </div>
        </div>
        <div className="flex gap-2">
          <Button onClick={handleGuardar} disabled={mutGuardar.isPending}>
            {editandoId ? 'Actualizar' : 'Agregar'}
          </Button>
          {editandoId && (
            <Button variant="outline" onClick={cancelarEdicion}>Cancelar</Button>
          )}
        </div>
      </div>
    </div>
  )
}
