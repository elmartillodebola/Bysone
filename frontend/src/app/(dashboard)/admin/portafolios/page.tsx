'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface OpcionInversion { id: number; nombreOpcion: string }
interface Portafolio {
  id: number; nombrePortafolio: string; descripcion?: string
  rentabilidadMinima: number; rentabilidadMaxima: number
  opciones: OpcionInversion[]
}

const empty = { nombre: '', descripcion: '', rentabilidadMinima: '', rentabilidadMaxima: '' }

export default function PortafoliosPage() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [gestionandoOpciones, setGestionandoOpciones] = useState<number | null>(null)
  const [opcionesSeleccionadas, setOpcionesSeleccionadas] = useState<number[]>([])
  const [error, setError] = useState<string | null>(null)

  const { data: portafolios = [], isLoading } = useQuery<Portafolio[]>({
    queryKey: ['admin-portafolios'],
    queryFn: async () => (await api.get('/admin/portafolios')).data,
  })

  const { data: todasOpciones = [] } = useQuery<OpcionInversion[]>({
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
      return editandoId ? api.put(`/admin/portafolios/${editandoId}`, body) : api.post('/admin/portafolios', body)
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-portafolios'] }); setForm(empty); setEditandoId(null); setError(null) },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/portafolios/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-portafolios'] }),
    onError: () => setError('No se puede eliminar: el portafolio está asignado a un perfil.'),
  })

  const mutAsignarOpciones = useMutation({
    mutationFn: ({ id, ids }: { id: number; ids: number[] }) => api.put(`/admin/portafolios/${id}/opciones`, ids),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-portafolios'] }); setGestionandoOpciones(null) },
    onError: () => setError('Error al asignar opciones.'),
  })

  function iniciarEdicion(p: Portafolio) {
    setEditandoId(p.id)
    setForm({ nombre: p.nombrePortafolio, descripcion: p.descripcion ?? '', rentabilidadMinima: String(p.rentabilidadMinima), rentabilidadMaxima: String(p.rentabilidadMaxima) })
  }

  function abrirGestionOpciones(p: Portafolio) {
    setGestionandoOpciones(p.id)
    setOpcionesSeleccionadas(p.opciones.map(o => o.id))
  }

  function toggleOpcion(id: number) {
    setOpcionesSeleccionadas(prev => prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id])
  }

  function handleGuardar() {
    const err = validar(); if (err) { setError(err); return }
    setError(null); mutGuardar.mutate()
  }

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Portafolios de inversión</h1>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>

      {gestionandoOpciones !== null && (
        <div className="border rounded-lg p-5 space-y-3 bg-card">
          <h2 className="font-semibold text-sm">Opciones de inversión del portafolio</h2>
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {todasOpciones.map(o => (
              <label key={o.id} className="flex items-center gap-3 cursor-pointer">
                <input type="checkbox" checked={opcionesSeleccionadas.includes(o.id)} onChange={() => toggleOpcion(o.id)} />
                <span className="text-sm">{o.nombreOpcion}</span>
              </label>
            ))}
          </div>
          <div className="flex gap-2">
            <Button onClick={() => mutAsignarOpciones.mutate({ id: gestionandoOpciones, ids: opcionesSeleccionadas })} disabled={mutAsignarOpciones.isPending}>Guardar asignación</Button>
            <Button variant="outline" onClick={() => setGestionandoOpciones(null)}>Cancelar</Button>
          </div>
        </div>
      )}

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Portafolio</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Mín</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Máx</th>
              <th className="px-4 py-3 w-52" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {portafolios.map(p => (
              <tr key={p.id} className="bg-card">
                <td className="px-4 py-3">
                  <p className="font-medium">{p.nombrePortafolio}</p>
                  <p className="text-xs text-muted-foreground">{p.opciones.length} opciones asignadas</p>
                </td>
                <td className="px-4 py-3 text-right">{p.rentabilidadMinima}%</td>
                <td className="px-4 py-3 text-right">{p.rentabilidadMaxima}%</td>
                <td className="px-4 py-3">
                  <div className="flex gap-1 justify-end flex-wrap">
                    <Button size="sm" variant="outline" onClick={() => iniciarEdicion(p)}>Editar</Button>
                    <Button size="sm" variant="outline" onClick={() => abrirGestionOpciones(p)}>Opciones</Button>
                    <Button size="sm" variant="outline" onClick={() => mutEliminar.mutate(p.id)} disabled={mutEliminar.isPending}>Eliminar</Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar portafolio' : 'Nuevo portafolio'}</h2>
        {error && <p className="text-destructive text-sm">{error}</p>}
        <div className="grid grid-cols-2 gap-3">
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Nombre *</label>
            <input value={form.nombre} onChange={e => setForm(f => ({ ...f, nombre: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Descripción</label>
            <input value={form.descripcion} onChange={e => setForm(f => ({ ...f, descripcion: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Rent. mínima % *</label>
            <input type="number" step="0.01" value={form.rentabilidadMinima} onChange={e => setForm(f => ({ ...f, rentabilidadMinima: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Rent. máxima % *</label>
            <input type="number" step="0.01" value={form.rentabilidadMaxima} onChange={e => setForm(f => ({ ...f, rentabilidadMaxima: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
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
