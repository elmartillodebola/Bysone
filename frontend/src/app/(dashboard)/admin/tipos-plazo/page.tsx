'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface TipoPlazo {
  id: number
  nombrePlazo: string
  descripcion?: string
  factorConversionDias: number
}

const empty = { nombrePlazo: '', descripcion: '', factorConversionDias: '' }

export default function TiposPlazoPage() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const [form, setForm] = useState(empty)
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: tipos = [], isLoading } = useQuery<TipoPlazo[]>({
    queryKey: ['admin-tipos-plazo'],
    queryFn: async () => (await api.get('/admin/tipos-plazo')).data,
  })

  function validar() {
    if (!form.nombrePlazo.trim()) return 'El nombre es obligatorio.'
    const factor = parseInt(form.factorConversionDias)
    if (isNaN(factor) || factor < 1) return 'El factor de conversión debe ser al menos 1 día.'
    return null
  }

  const mutGuardar = useMutation({
    mutationFn: () => {
      const body = {
        nombrePlazo: form.nombrePlazo.trim(),
        descripcion: form.descripcion || undefined,
        factorConversionDias: parseInt(form.factorConversionDias),
      }
      return editandoId
        ? api.put(`/admin/tipos-plazo/${editandoId}`, body)
        : api.post('/admin/tipos-plazo', body)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-tipos-plazo'] })
      setForm(empty)
      setEditandoId(null)
      setError(null)
    },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/tipos-plazo/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-tipos-plazo'] }),
    onError: (err: any) =>
      setError(err.response?.data?.message ?? 'No se puede eliminar: el tipo de plazo está en uso.'),
  })

  function iniciarEdicion(t: TipoPlazo) {
    setEditandoId(t.id)
    setForm({
      nombrePlazo: t.nombrePlazo,
      descripcion: t.descripcion ?? '',
      factorConversionDias: String(t.factorConversionDias),
    })
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
        <h1 className="text-xl font-bold">Tipos de plazo</h1>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Nombre</th>
              <th className="text-left px-4 py-3 font-medium">Descripción</th>
              <th className="text-right px-4 py-3 font-medium">Factor (días)</th>
              <th className="px-4 py-3 w-36" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {tipos.map(t => (
              <tr key={t.id} className="bg-card">
                <td className="px-4 py-3 font-medium">{t.nombrePlazo}</td>
                <td className="px-4 py-3 text-muted-foreground">{t.descripcion ?? '—'}</td>
                <td className="px-4 py-3 text-right">{t.factorConversionDias}</td>
                <td className="px-4 py-3">
                  <div className="flex gap-2 justify-end">
                    <Button size="sm" variant="outline" onClick={() => iniciarEdicion(t)}>Editar</Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => mutEliminar.mutate(t.id)}
                      disabled={mutEliminar.isPending}
                    >
                      Eliminar
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
            {tipos.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-6 text-center text-muted-foreground">
                  Sin tipos de plazo registrados
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoId ? 'Editar tipo de plazo' : 'Nuevo tipo de plazo'}</h2>
        {error && <p className="text-destructive text-sm">{error}</p>}
        <div className="grid grid-cols-2 gap-3">
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Nombre *</label>
            <input
              value={form.nombrePlazo}
              onChange={e => setForm(f => ({ ...f, nombrePlazo: e.target.value }))}
              className="w-full border rounded px-3 py-2 text-sm mt-1"
              placeholder="Meses"
            />
          </div>
          <div className="col-span-2">
            <label className="text-xs text-muted-foreground">Descripción</label>
            <input
              value={form.descripcion}
              onChange={e => setForm(f => ({ ...f, descripcion: e.target.value }))}
              className="w-full border rounded px-3 py-2 text-sm mt-1"
              placeholder="Período mensual de 30 días"
            />
          </div>
          <div>
            <label className="text-xs text-muted-foreground">Factor de conversión a días *</label>
            <input
              type="number"
              min="1"
              step="1"
              value={form.factorConversionDias}
              onChange={e => setForm(f => ({ ...f, factorConversionDias: e.target.value }))}
              className="w-full border rounded px-3 py-2 text-sm mt-1"
              placeholder="30"
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
