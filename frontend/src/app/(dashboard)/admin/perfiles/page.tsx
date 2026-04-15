'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface Opcion { id: number; nombreOpcion: string }
interface Portafolio {
  id: number
  nombrePortafolio: string
  rentabilidadMinima: number
  rentabilidadMaxima: number
  opciones: Opcion[]
}
interface ComposicionItem { idPortafolio: number; porcentaje: number }
interface FormulaItem { idPortafolio: number; umbralMin: number; umbralMax: number }
interface Perfil {
  id: number
  nombrePerfil: string
  rentabilidadMinima: number
  rentabilidadMedia: number
  rentabilidadMaxima: number
  portafolios: {
    id: number
    nombrePortafolio: string
    porcentaje: number
  }[]
}

type Modal = 'crear' | 'editar' | 'composicion' | 'formulas' | null

export default function PerfilesAdminPage() {
  const queryClient = useQueryClient()
  const router = useRouter()

  const [modal, setModal] = useState<Modal>(null)
  const [perfilActivo, setPerfilActivo] = useState<Perfil | null>(null)
  const [nombre, setNombre] = useState('')
  const [error, setError] = useState<string | null>(null)

  // Composición: mapa portafolioId → porcentaje (string para el input)
  const [composicion, setComposicion] = useState<Record<number, string>>({})
  // Fórmulas: mapa portafolioId → { min, max }
  const [formulas, setFormulas] = useState<Record<number, { min: string; max: string }>>({})

  const { data: perfiles = [], isLoading } = useQuery<Perfil[]>({
    queryKey: ['admin-perfiles'],
    queryFn: async () => (await api.get('/admin/perfiles')).data,
  })

  const { data: portafolios = [] } = useQuery<Portafolio[]>({
    queryKey: ['admin-portafolios'],
    queryFn: async () => (await api.get('/admin/portafolios')).data,
  })

  // ── Mutaciones ──────────────────────────────────────────────────────────────

  const mutCrear = useMutation({
    mutationFn: () => api.post('/admin/perfiles', { nombre }),
    onSuccess: () => { invalidar(); cerrar() },
    onError: (e: any) => setError(e.response?.data?.message ?? 'Error al crear.'),
  })

  const mutRenombrar = useMutation({
    mutationFn: () => api.put(`/admin/perfiles/${perfilActivo!.id}`, { nombre }),
    onSuccess: () => { invalidar(); cerrar() },
    onError: (e: any) => setError(e.response?.data?.message ?? 'Error al guardar.'),
  })

  const mutEliminar = useMutation({
    mutationFn: (id: number) => api.delete(`/admin/perfiles/${id}`),
    onSuccess: () => invalidar(),
    onError: (e: any) => setError(e.response?.data?.message ?? 'No se puede eliminar: hay usuarios con este perfil asignado.'),
  })

  const mutComposicion = useMutation({
    mutationFn: (items: ComposicionItem[]) =>
      api.put(`/admin/perfiles/${perfilActivo!.id}/composicion`, items),
    onSuccess: () => { invalidar(); cerrar() },
    onError: (e: any) => setError(e.response?.data?.message ?? 'Error al guardar composición.'),
  })

  const mutFormulas = useMutation({
    mutationFn: (items: FormulaItem[]) =>
      api.put(`/admin/perfiles/${perfilActivo!.id}/formulas`, items),
    onSuccess: () => { invalidar(); cerrar() },
    onError: (e: any) => setError(e.response?.data?.message ?? 'Error al guardar fórmulas.'),
  })

  // ── Helpers ─────────────────────────────────────────────────────────────────

  function invalidar() {
    queryClient.invalidateQueries({ queryKey: ['admin-perfiles'] })
  }

  function cerrar() {
    setModal(null)
    setPerfilActivo(null)
    setNombre('')
    setError(null)
    setComposicion({})
    setFormulas({})
  }

  function abrirCrear() {
    setNombre('')
    setError(null)
    setModal('crear')
  }

  function abrirEditar(p: Perfil) {
    setPerfilActivo(p)
    setNombre(p.nombrePerfil)
    setError(null)
    setModal('editar')
  }

  function abrirComposicion(p: Perfil) {
    setPerfilActivo(p)
    setError(null)
    // Pre-cargar porcentajes actuales
    const inicial: Record<number, string> = {}
    p.portafolios.forEach(pp => { inicial[pp.id] = String(pp.porcentaje) })
    setComposicion(inicial)
    setModal('composicion')
  }

  function abrirFormulas(p: Perfil) {
    setPerfilActivo(p)
    setError(null)
    setFormulas({})
    setModal('formulas')
  }

  function toggleComposicion(id: number) {
    setComposicion(prev => {
      const siguiente = { ...prev }
      if (id in siguiente) { delete siguiente[id] } else { siguiente[id] = '' }
      return siguiente
    })
  }

  function sumaComposicion() {
    return Object.values(composicion)
      .map(v => parseFloat(v) || 0)
      .reduce((a, b) => a + b, 0)
  }

  function guardarComposicion() {
    const items: ComposicionItem[] = Object.entries(composicion)
      .filter(([, v]) => v !== '')
      .map(([id, v]) => ({ idPortafolio: Number(id), porcentaje: parseFloat(v) }))
    mutComposicion.mutate(items)
  }

  function toggleFormula(id: number) {
    setFormulas(prev => {
      const siguiente = { ...prev }
      if (id in siguiente) { delete siguiente[id] } else { siguiente[id] = { min: '', max: '' } }
      return siguiente
    })
  }

  function guardarFormulas() {
    const items: FormulaItem[] = Object.entries(formulas).map(([id, v]) => ({
      idPortafolio: Number(id),
      umbralMin: parseFloat(v.min),
      umbralMax: parseFloat(v.max),
    }))
    mutFormulas.mutate(items)
  }

  // ── Render ──────────────────────────────────────────────────────────────────

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold">Perfiles de inversión</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {perfiles.length} perfil{perfiles.length !== 1 ? 'es' : ''} registrados
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
          <Button onClick={abrirCrear}>+ Nuevo perfil</Button>
        </div>
      </div>

      {error && !modal && (
        <p className="text-destructive text-sm border border-destructive/30 rounded px-3 py-2 bg-destructive/5">
          {error}
        </p>
      )}

      {/* Tabla de perfiles */}
      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Perfil</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Mín</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Media</th>
              <th className="text-right px-4 py-3 font-medium">Rent. Máx</th>
              <th className="text-right px-4 py-3 font-medium">Portafolios</th>
              <th className="px-4 py-3 w-64" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {perfiles.length === 0 && (
              <tr>
                <td colSpan={6} className="text-center py-8 text-muted-foreground">
                  No hay perfiles registrados. Crea el primero.
                </td>
              </tr>
            )}
            {perfiles.map(p => (
              <tr key={p.id} className="bg-card hover:bg-muted/40 transition-colors">
                <td className="px-4 py-3 font-medium">{p.nombrePerfil}</td>
                <td className="px-4 py-3 text-right text-muted-foreground">
                  {p.portafolios.length > 0 ? `${p.rentabilidadMinima}%` : '—'}
                </td>
                <td className="px-4 py-3 text-right text-muted-foreground">
                  {p.portafolios.length > 0 ? `${p.rentabilidadMedia}%` : '—'}
                </td>
                <td className="px-4 py-3 text-right text-muted-foreground">
                  {p.portafolios.length > 0 ? `${p.rentabilidadMaxima}%` : '—'}
                </td>
                <td className="px-4 py-3 text-right text-muted-foreground">
                  {p.portafolios.length}
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-1 justify-end flex-wrap">
                    <Button size="sm" variant="outline" onClick={() => abrirEditar(p)}>Editar</Button>
                    <Button size="sm" variant="outline" onClick={() => abrirComposicion(p)}>Composición</Button>
                    <Button size="sm" variant="outline" onClick={() => abrirFormulas(p)}>Fórmulas</Button>
                    <Button
                      size="sm" variant="outline"
                      onClick={() => { setError(null); mutEliminar.mutate(p.id) }}
                      disabled={mutEliminar.isPending}
                    >
                      Eliminar
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* ── Modal: Crear / Editar nombre ─────────────────────────────────────── */}
      {(modal === 'crear' || modal === 'editar') && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card border rounded-xl shadow-xl max-w-sm w-full p-6 space-y-4">
            <h2 className="text-lg font-semibold">
              {modal === 'crear' ? 'Nuevo perfil de inversión' : 'Editar perfil'}
            </h2>
            {error && <p className="text-destructive text-sm">{error}</p>}
            <div>
              <label className="text-xs text-muted-foreground">Nombre del perfil *</label>
              <input
                value={nombre}
                onChange={e => setNombre(e.target.value)}
                className="w-full border rounded px-3 py-2 text-sm mt-1 bg-background"
                placeholder="Ej: Conservador, Moderado, Agresivo…"
                maxLength={100}
              />
            </div>
            <div className="flex gap-2 justify-end pt-2">
              <Button variant="outline" onClick={cerrar}>Cancelar</Button>
              <Button
                onClick={() => {
                  if (!nombre.trim()) { setError('El nombre es obligatorio.'); return }
                  setError(null)
                  modal === 'crear' ? mutCrear.mutate() : mutRenombrar.mutate()
                }}
                disabled={mutCrear.isPending || mutRenombrar.isPending}
              >
                {modal === 'crear' ? 'Crear' : 'Guardar'}
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* ── Modal: Composición de portafolios ────────────────────────────────── */}
      {modal === 'composicion' && perfilActivo && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card border rounded-xl shadow-xl max-w-lg w-full p-6 space-y-4 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-semibold">
              Composición — <span className="text-primary">{perfilActivo.nombrePerfil}</span>
            </h2>
            <p className="text-xs text-muted-foreground">
              Selecciona los portafolios y asigna el porcentaje a cada uno. La suma debe ser exactamente 100%.
            </p>
            {error && <p className="text-destructive text-sm">{error}</p>}

            <div className="space-y-2">
              {portafolios.map(port => {
                const activo = port.id in composicion
                return (
                  <div key={port.id} className="flex items-center gap-3 border rounded p-3">
                    <input
                      type="checkbox"
                      checked={activo}
                      onChange={() => toggleComposicion(port.id)}
                      className="h-4 w-4"
                    />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium truncate">{port.nombrePortafolio}</p>
                      <p className="text-xs text-muted-foreground">
                        {port.rentabilidadMinima}% – {port.rentabilidadMaxima}%
                      </p>
                    </div>
                    {activo && (
                      <div className="flex items-center gap-1 shrink-0">
                        <input
                          type="number"
                          step="0.01"
                          min="0.01"
                          max="100"
                          value={composicion[port.id]}
                          onChange={e => setComposicion(prev => ({ ...prev, [port.id]: e.target.value }))}
                          className="w-20 border rounded px-2 py-1 text-sm text-right bg-background"
                          placeholder="0.00"
                        />
                        <span className="text-sm text-muted-foreground">%</span>
                      </div>
                    )}
                  </div>
                )
              })}
            </div>

            {/* Indicador de suma */}
            <div className={`flex justify-between text-sm font-medium rounded px-3 py-2 ${
              Math.abs(sumaComposicion() - 100) < 0.001
                ? 'bg-green-50 text-green-700'
                : 'bg-amber-50 text-amber-700'
            }`}>
              <span>Suma total</span>
              <span>{sumaComposicion().toFixed(2)}% / 100%</span>
            </div>

            <div className="flex gap-2 justify-end pt-2 border-t">
              <Button variant="outline" onClick={cerrar}>Cancelar</Button>
              <Button onClick={guardarComposicion} disabled={mutComposicion.isPending}>
                Guardar composición
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* ── Modal: Fórmulas de exposición ────────────────────────────────────── */}
      {modal === 'formulas' && perfilActivo && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card border rounded-xl shadow-xl max-w-lg w-full p-6 space-y-4 max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-semibold">
              Fórmulas de exposición — <span className="text-primary">{perfilActivo.nombrePerfil}</span>
            </h2>
            <p className="text-xs text-muted-foreground">
              Define los umbrales de porcentaje mínimo y máximo por portafolio para este perfil.
            </p>
            {error && <p className="text-destructive text-sm">{error}</p>}

            <div className="space-y-2">
              {portafolios.map(port => {
                const activo = port.id in formulas
                return (
                  <div key={port.id} className="border rounded p-3 space-y-2">
                    <div className="flex items-center gap-3">
                      <input
                        type="checkbox"
                        checked={activo}
                        onChange={() => toggleFormula(port.id)}
                        className="h-4 w-4"
                      />
                      <p className="text-sm font-medium">{port.nombrePortafolio}</p>
                    </div>
                    {activo && (
                      <div className="grid grid-cols-2 gap-2 pl-7">
                        <div>
                          <label className="text-xs text-muted-foreground">Umbral mín %</label>
                          <input
                            type="number" step="0.01" min="0"
                            value={formulas[port.id].min}
                            onChange={e => setFormulas(prev => ({
                              ...prev, [port.id]: { ...prev[port.id], min: e.target.value }
                            }))}
                            className="w-full border rounded px-2 py-1 text-sm mt-1 bg-background"
                          />
                        </div>
                        <div>
                          <label className="text-xs text-muted-foreground">Umbral máx %</label>
                          <input
                            type="number" step="0.01" min="0"
                            value={formulas[port.id].max}
                            onChange={e => setFormulas(prev => ({
                              ...prev, [port.id]: { ...prev[port.id], max: e.target.value }
                            }))}
                            className="w-full border rounded px-2 py-1 text-sm mt-1 bg-background"
                          />
                        </div>
                      </div>
                    )}
                  </div>
                )
              })}
            </div>

            <div className="flex gap-2 justify-end pt-2 border-t">
              <Button variant="outline" onClick={cerrar}>Cancelar</Button>
              <Button onClick={guardarFormulas} disabled={mutFormulas.isPending}>
                Guardar fórmulas
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
