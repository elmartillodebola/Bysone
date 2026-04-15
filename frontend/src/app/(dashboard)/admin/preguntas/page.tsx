'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface OpcionRespuesta { id: number; textoOpcion: string; puntaje: number; orden: number }
interface Pregunta { id: number; textoPregunta: string; orden: number; activa: boolean; opciones: OpcionRespuesta[] }

const emptyPregunta = { textoPregunta: '', orden: '' }
const emptyOpcion = { textoOpcion: '', puntaje: '', orden: '' }

export default function PreguntasPage() {
  const queryClient = useQueryClient()
  const router = useRouter()
  const [formPregunta, setFormPregunta] = useState(emptyPregunta)
  const [editandoPreguntaId, setEditandoPreguntaId] = useState<number | null>(null)
  const [expandidaId, setExpandidaId] = useState<number | null>(null)
  const [formOpcion, setFormOpcion] = useState(emptyOpcion)
  const [editandoOpcionId, setEditandoOpcionId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const { data: preguntas = [], isLoading } = useQuery<Pregunta[]>({
    queryKey: ['admin-preguntas'],
    queryFn: async () => (await api.get('/admin/preguntas')).data,
  })

  const mutGuardarPregunta = useMutation({
    mutationFn: () => {
      const body = { textoPregunta: formPregunta.textoPregunta, orden: parseInt(formPregunta.orden), activa: true }
      return editandoPreguntaId
        ? api.put(`/admin/preguntas/${editandoPreguntaId}`, body)
        : api.post('/admin/preguntas', body)
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-preguntas'] }); setFormPregunta(emptyPregunta); setEditandoPreguntaId(null); setError(null) },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar pregunta.'),
  })

  const mutToggleActiva = useMutation({
    mutationFn: ({ id, activa }: { id: number; activa: boolean }) =>
      api.patch(`/admin/preguntas/${id}/activa?activa=${activa}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-preguntas'] }),
  })

  const mutGuardarOpcion = useMutation({
    mutationFn: () => {
      const body = { textoOpcion: formOpcion.textoOpcion, puntaje: parseInt(formOpcion.puntaje), orden: parseInt(formOpcion.orden) }
      return editandoOpcionId
        ? api.put(`/admin/preguntas/${expandidaId}/opciones/${editandoOpcionId}`, body)
        : api.post(`/admin/preguntas/${expandidaId}/opciones`, body)
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-preguntas'] }); setFormOpcion(emptyOpcion); setEditandoOpcionId(null); setError(null) },
    onError: (err: any) => setError(err.response?.data?.message ?? 'Error al guardar opción.'),
  })

  const mutEliminarOpcion = useMutation({
    mutationFn: ({ idPregunta, idOpcion }: { idPregunta: number; idOpcion: number }) =>
      api.delete(`/admin/preguntas/${idPregunta}/opciones/${idOpcion}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-preguntas'] }),
    onError: (err: any) => setError(err.response?.data?.message ?? 'No se puede eliminar la opción.'),
  })

  function handleGuardarPregunta() {
    if (!formPregunta.textoPregunta.trim()) { setError('El texto es obligatorio.'); return }
    if (!formPregunta.orden || parseInt(formPregunta.orden) < 1) { setError('El orden debe ser ≥ 1.'); return }
    setError(null); mutGuardarPregunta.mutate()
  }

  function handleGuardarOpcion() {
    if (!formOpcion.textoOpcion.trim()) { setError('El texto es obligatorio.'); return }
    if (!formOpcion.puntaje || parseInt(formOpcion.puntaje) < 1) { setError('El puntaje debe ser ≥ 1.'); return }
    if (!formOpcion.orden || parseInt(formOpcion.orden) < 1) { setError('El orden debe ser ≥ 1.'); return }
    setError(null); mutGuardarOpcion.mutate()
  }

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Cuestionario de calibración</h1>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>
      {error && <p className="text-destructive text-sm bg-destructive/10 px-3 py-2 rounded">{error}</p>}

      <div className="space-y-3">
        {preguntas.map(p => (
          <div key={p.id} className="border rounded-lg bg-card">
            <div className="flex items-center gap-3 px-4 py-3">
              <span className="text-xs text-muted-foreground w-6 text-center">{p.orden}</span>
              <p className="flex-1 text-sm font-medium">{p.textoPregunta}</p>
              <span className={`text-xs px-2 py-0.5 rounded-full ${p.activa ? 'bg-green-100 text-green-700' : 'bg-muted text-muted-foreground'}`}>
                {p.activa ? 'Activa' : 'Inactiva'}
              </span>
              <div className="flex gap-1">
                <Button size="sm" variant="outline" onClick={() => { setEditandoPreguntaId(p.id); setFormPregunta({ textoPregunta: p.textoPregunta, orden: String(p.orden) }) }}>Editar</Button>
                <Button size="sm" variant="outline" onClick={() => mutToggleActiva.mutate({ id: p.id, activa: !p.activa })}>
                  {p.activa ? 'Desactivar' : 'Activar'}
                </Button>
                <Button size="sm" variant="outline" onClick={() => { setExpandidaId(expandidaId === p.id ? null : p.id); setFormOpcion(emptyOpcion); setEditandoOpcionId(null) }}>
                  Opciones ({p.opciones.length})
                </Button>
              </div>
            </div>

            {expandidaId === p.id && (
              <div className="border-t px-4 py-3 space-y-3 bg-secondary/20">
                <div className="space-y-2">
                  {p.opciones.map(o => (
                    <div key={o.id} className="flex items-center gap-2 text-sm">
                      <span className="text-xs text-muted-foreground w-4">{o.orden}.</span>
                      <span className="flex-1">{o.textoOpcion}</span>
                      <span className="text-xs text-muted-foreground">pts: {o.puntaje}</span>
                      <Button size="sm" variant="outline" onClick={() => { setEditandoOpcionId(o.id); setFormOpcion({ textoOpcion: o.textoOpcion, puntaje: String(o.puntaje), orden: String(o.orden) }) }}>Editar</Button>
                      <Button size="sm" variant="outline" onClick={() => mutEliminarOpcion.mutate({ idPregunta: p.id, idOpcion: o.id })}>Eliminar</Button>
                    </div>
                  ))}
                </div>
                <div className="border-t pt-3 space-y-2">
                  <p className="text-xs font-medium">{editandoOpcionId ? 'Editar opción' : 'Nueva opción'}</p>
                  <div className="flex gap-2">
                    <input placeholder="Texto de la opción" value={formOpcion.textoOpcion} onChange={e => setFormOpcion(f => ({ ...f, textoOpcion: e.target.value }))} className="flex-1 border rounded px-2 py-1 text-sm" />
                    <input placeholder="Puntaje" type="number" min="1" value={formOpcion.puntaje} onChange={e => setFormOpcion(f => ({ ...f, puntaje: e.target.value }))} className="w-20 border rounded px-2 py-1 text-sm" />
                    <input placeholder="Orden" type="number" min="1" value={formOpcion.orden} onChange={e => setFormOpcion(f => ({ ...f, orden: e.target.value }))} className="w-20 border rounded px-2 py-1 text-sm" />
                    <Button size="sm" onClick={handleGuardarOpcion}>{editandoOpcionId ? 'Actualizar' : 'Agregar'}</Button>
                    {editandoOpcionId && <Button size="sm" variant="outline" onClick={() => { setEditandoOpcionId(null); setFormOpcion(emptyOpcion) }}>Cancelar</Button>}
                  </div>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">{editandoPreguntaId ? 'Editar pregunta' : 'Nueva pregunta'}</h2>
        <div className="flex gap-3">
          <input placeholder="Texto de la pregunta" value={formPregunta.textoPregunta} onChange={e => setFormPregunta(f => ({ ...f, textoPregunta: e.target.value }))} className="flex-1 border rounded px-3 py-2 text-sm" />
          <input placeholder="Orden" type="number" min="1" value={formPregunta.orden} onChange={e => setFormPregunta(f => ({ ...f, orden: e.target.value }))} className="w-24 border rounded px-3 py-2 text-sm" />
          <Button onClick={handleGuardarPregunta} disabled={mutGuardarPregunta.isPending}>{editandoPreguntaId ? 'Actualizar' : 'Agregar'}</Button>
          {editandoPreguntaId && <Button variant="outline" onClick={() => { setEditandoPreguntaId(null); setFormPregunta(emptyPregunta) }}>Cancelar</Button>}
        </div>
      </div>
    </div>
  )
}
