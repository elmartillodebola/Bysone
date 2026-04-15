'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface Disclaimer {
  id: number; titulo: string; contenido: string; activo: boolean
  fechaVigenciaDesde: string; fechaVigenciaHasta?: string
}

const empty = { titulo: '', contenido: '', fechaVigenciaDesde: '', fechaVigenciaHasta: '' }

export default function DisclaimersPage() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: disclaimers = [], isLoading } = useQuery<Disclaimer[]>({
    queryKey: ['admin-disclaimers'],
    queryFn: async () => (await api.get('/admin/disclaimers')).data,
  })

  function validar() {
    if (!form.titulo.trim()) return 'El título es obligatorio.'
    if (!form.contenido.trim()) return 'El contenido es obligatorio.'
    if (!form.fechaVigenciaDesde) return 'La fecha de inicio de vigencia es obligatoria.'
    if (form.fechaVigenciaHasta && form.fechaVigenciaDesde > form.fechaVigenciaHasta)
      return 'La fecha de inicio no puede ser posterior a la fecha de fin.'
    return null
  }

  const mutGuardar = useMutation({
    mutationFn: () => {
      const body = {
        titulo: form.titulo, contenido: form.contenido, activo: true,
        fechaVigenciaDesde: form.fechaVigenciaDesde ? new Date(form.fechaVigenciaDesde).toISOString() : undefined,
        fechaVigenciaHasta: form.fechaVigenciaHasta ? new Date(form.fechaVigenciaHasta).toISOString() : null,
      }
      return editandoId ? api.put(`/admin/disclaimers/${editandoId}`, body) : api.post('/admin/disclaimers', body)
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-disclaimers'] }); setForm(empty); setEditandoId(null); setError(null) },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutToggle = useMutation({
    mutationFn: ({ id, activo }: { id: number; activo: boolean }) =>
      api.patch(`/admin/disclaimers/${id}/activo?activo=${activo}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-disclaimers'] }),
  })

  function iniciarEdicion(d: Disclaimer) {
    setEditandoId(d.id)
    setForm({
      titulo: d.titulo, contenido: d.contenido,
      fechaVigenciaDesde: d.fechaVigenciaDesde?.slice(0, 16) ?? '',
      fechaVigenciaHasta: d.fechaVigenciaHasta?.slice(0, 16) ?? '',
    })
  }

  function handleGuardar() {
    const err = validar(); if (err) { setError(err); return }
    setError(null); mutGuardar.mutate()
  }

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <h1 className="text-xl font-bold">Disclaimers legales</h1>
      <p className="text-xs text-muted-foreground">RN-DIS-04: los disclaimers no se eliminan, solo se desactivan.</p>
      {error && <p className="text-destructive text-sm bg-destructive/10 px-3 py-2 rounded">{error}</p>}

      <div className="space-y-3">
        {disclaimers.map(d => (
          <div key={d.id} className="border rounded-lg p-4 bg-card space-y-2">
            <div className="flex items-start justify-between gap-3">
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <p className="font-semibold text-sm">{d.titulo}</p>
                  <span className={`text-xs px-2 py-0.5 rounded-full ${d.activo ? 'bg-green-100 text-green-700' : 'bg-muted text-muted-foreground'}`}>
                    {d.activo ? 'Activo' : 'Inactivo'}
                  </span>
                </div>
                <p className="text-xs text-muted-foreground mt-1">
                  Vigencia: {new Date(d.fechaVigenciaDesde).toLocaleDateString()}
                  {d.fechaVigenciaHasta ? ` → ${new Date(d.fechaVigenciaHasta).toLocaleDateString()}` : ' (sin vencimiento)'}
                </p>
                <p className="text-xs mt-2 text-muted-foreground line-clamp-2">{d.contenido}</p>
              </div>
              <div className="flex gap-1 shrink-0">
                <Button size="sm" variant="outline" onClick={() => iniciarEdicion(d)}>Editar</Button>
                <Button size="sm" variant="outline" onClick={() => mutToggle.mutate({ id: d.id, activo: !d.activo })}>
                  {d.activo ? 'Desactivar' : 'Activar'}
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar disclaimer' : 'Nuevo disclaimer'}</h2>
        <div className="space-y-3">
          <div>
            <label className="text-xs text-muted-foreground">Título *</label>
            <input value={form.titulo} onChange={e => setForm(f => ({ ...f, titulo: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Contenido *</label>
            <textarea rows={5} value={form.contenido} onChange={e => setForm(f => ({ ...f, contenido: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1 resize-none" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs text-muted-foreground">Inicio de vigencia *</label>
              <input type="datetime-local" value={form.fechaVigenciaDesde} onChange={e => setForm(f => ({ ...f, fechaVigenciaDesde: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
            </div>
            <div>
              <label className="text-xs text-muted-foreground">Fin de vigencia (opcional)</label>
              <input type="datetime-local" value={form.fechaVigenciaHasta} onChange={e => setForm(f => ({ ...f, fechaVigenciaHasta: e.target.value }))} className="w-full border rounded px-3 py-2 text-sm mt-1" />
            </div>
          </div>
        </div>
        <div className="flex gap-2">
          <Button onClick={handleGuardar} disabled={mutGuardar.isPending}>{editandoId ? 'Actualizar' : 'Crear'}</Button>
          {editandoId && <Button variant="outline" onClick={() => { setEditandoId(null); setForm(empty); setError(null) }}>Cancelar</Button>}
        </div>
      </div>
    </div>
  )
}
