'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface OpcionFuncional {
  id: number
  nombreOpcionFuncional: string
}

const empty = { nombreOpcionFuncional: '' }

export default function OpcionesFuncionalesPage() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: opciones = [], isLoading } = useQuery<OpcionFuncional[]>({
    queryKey: ['admin-opciones-funcionales'],
    queryFn: async () => (await api.get('/admin/opciones-funcionales')).data,
  })

  function validar() {
    if (!form.nombreOpcionFuncional.trim()) return 'El nombre de la opción es obligatorio.'
    return null
  }

  const mutGuardar = useMutation({
    mutationFn: () => {
      const body = { nombreOpcionFuncional: form.nombreOpcionFuncional.trim().toUpperCase() }
      return editandoId
        ? api.put(`/admin/opciones-funcionales/${editandoId}`, body)
        : api.post('/admin/opciones-funcionales', body)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-opciones-funcionales'] })
      setForm(empty)
      setEditandoId(null)
      setError(null)
    },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/opciones-funcionales/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-opciones-funcionales'] }),
    onError: (err: any) =>
      setError(err.response?.data?.message ?? 'No se puede eliminar: la opción está asignada a un rol.'),
  })

  function iniciarEdicion(o: OpcionFuncional) {
    setEditandoId(o.id)
    setForm({ nombreOpcionFuncional: o.nombreOpcionFuncional })
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
        <h1 className="text-xl font-bold">Opciones funcionales</h1>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>
      <p className="text-xs text-muted-foreground">
        Los nombres se normalizan automáticamente a MAYÚSCULAS_CON_GUIONES. Se asignan a roles desde "Roles × Opciones funcionales".
      </p>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Nombre</th>
              <th className="px-4 py-3 w-36" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {opciones.map(o => (
              <tr key={o.id} className="bg-card">
                <td className="px-4 py-3 font-mono text-sm">{o.nombreOpcionFuncional}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2 justify-end">
                    <Button size="sm" variant="outline" onClick={() => iniciarEdicion(o)}>Editar</Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => mutEliminar.mutate(o.id)}
                      disabled={mutEliminar.isPending}
                    >
                      Eliminar
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            {opciones.length === 0 && (
              <tr>
                <td colSpan={2} className="px-4 py-6 text-center text-muted-foreground">
                  Sin opciones funcionales registradas
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar opción funcional' : 'Nueva opción funcional'}</h2>
        {error && <p className="text-destructive text-sm">{error}</p>}
        <div>
          <label className="text-xs text-muted-foreground">Nombre *</label>
          <input
            required
            value={form.nombreOpcionFuncional}
            onChange={e => setForm({ nombreOpcionFuncional: e.target.value.toUpperCase() })}
            className="w-full border rounded px-3 py-2 text-sm mt-1 font-mono"
            placeholder="GESTIONAR_REPORTES"
          />
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
