'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface OpcionInversion {
  id: number; nombreOpcion: string; descripcionOpcion?: string
  rentabilidadMinima: number; rentabilidadMaxima: number
}

const empty = { nombre: '', descripcion: '', rentabilidadMinima: '', rentabilidadMaxima: '' }

export default function OpcionesInversionPage() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: opciones = [], isLoading } = useQuery<OpcionInversion[]>({
    queryKey: ['admin-opciones-inversion'],
    queryFn: async () => (await api.get('/admin/opciones-inversion')).data,
  })

  function validar() {
    if (!form.nombre.trim()) return 'El nombre es obligatorio.'
    const min = parseFloat(form.rentabilidadMinima)
    const max = parseFloat(form.rentabilidadMaxima)
    if (isNaN(min) || isNaN(max)) return 'Las rentabilidades deben ser números.'
    if (min < 0) return 'La rentabilidad mínima debe ser ≥ 0.'
    if (max <= 0) return 'La rentabilidad máxima debe ser > 0.'
    if (min > max) return 'La rentabilidad mínima no puede ser mayor que la máxima.'
    return null
  }

  const mutGuardar = useMutation({
    mutationFn: () => {
      const body = { nombre: form.nombre, descripcion: form.descripcion || undefined,
        rentabilidadMinima: parseFloat(form.rentabilidadMinima),
        rentabilidadMaxima: parseFloat(form.rentabilidadMaxima) }
      return editandoId
        ? api.put(`/admin/opciones-inversion/${editandoId}`, body)
        : api.post('/admin/opciones-inversion', body)
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-opciones-inversion'] }); setForm(empty); setEditandoId(null); setError(null) },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/opciones-inversion/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-opciones-inversion'] }),
    onError: () => setError('No se puede eliminar: la opción está asignada a un portafolio.'),
  })

  function iniciarEdicion(o: OpcionInversion) {
    setEditandoId(o.id)
    setForm({ nombre: o.nombreOpcion, descripcion: o.descripcionOpcion ?? '', rentabilidadMinima: String(o.rentabilidadMinima), rentabilidadMaxima: String(o.rentabilidadMaxima) })
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
      <h1 className="text-xl font-bold">Opciones de inversión</h1>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Nombre</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Mín %</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Máx %</th>
              <th className="px-4 py-3 w-36" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {opciones.map(o => (
              <tr key={o.id} className="bg-card">
                <td className="px-4 py-3">
                  <p className="font-medium">{o.nombreOpcion}</p>
                  {o.descripcionOpcion && <p className="text-xs text-muted-foreground">{o.descripcionOpcion}</p>}
                </td>
                <td className="px-4 py-3 text-right">{o.rentabilidadMinima}%</td>
                <td className="px-4 py-3 text-right">{o.rentabilidadMaxima}%</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2 justify-end">
                    <Button size="sm" variant="outline" onClick={() => iniciarEdicion(o)}>Editar</Button>
                    <Button size="sm" variant="outline" onClick={() => mutEliminar.mutate(o.id)} disabled={mutEliminar.isPending}>Eliminar</Button>
                  </div>
                </td>
              </tr>
            ))}
            {opciones.length === 0 && <tr><td colSpan={4} className="px-4 py-6 text-center text-muted-foreground">Sin opciones registradas</td></tr>}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar opción' : 'Nueva opción'}</h2>
        {error && <p className="text-destructive text-sm">{error}</p>}
        <div className="grid grid-cols-2 gap-3">
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Nombre *</label>
            <input value={form.nombre} onChange={e => setForm(f => ({ ...f, nombre: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" placeholder="CDT Bancolombia" />
          </div>
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Descripción</label>
            <input value={form.descripcion} onChange={e => setForm(f => ({ ...f, descripcion: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Rentabilidad mínima % *</label>
            <input type="number" step="0.01" min="0" value={form.rentabilidadMinima} onChange={e => setForm(f => ({ ...f, rentabilidadMinima: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Rentabilidad máxima % *</label>
            <input type="number" step="0.01" min="0.01" value={form.rentabilidadMaxima} onChange={e => setForm(f => ({ ...f, rentabilidadMaxima: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
        </div>
        <div className="flex gap-2">
          <Button onClick={handleGuardar} disabled={mutGuardar.isPending}>{editandoId ? 'Actualizar' : 'Agregar'}</Button>
          {editandoId && <Button variant="outline" onClick={() => { setEditandoId(null); setForm(empty); setError(null) }}>Cancelar</Button>}
        </div>
      </div>
    </div>
  )
}
