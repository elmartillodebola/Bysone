'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useQuery } from '@tanstack/react-query'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface PerfilResumen { id: number; nombrePerfil: string }

interface UsuarioAdmin {
  id: number
  nombreCompleto: string
  correo: string
  celular?: string
  proveedorOauth: string
  fechaRegistro: string
  fechaUltimaActualizacionPerfil?: string
  roles: string[]
  perfilInversion: PerfilResumen | null
  requiereRecalibracion: boolean
}

const PROVEEDOR_BADGE: Record<string, string> = {
  GOOGLE: 'bg-red-100 text-red-700',
  MICROSOFT: 'bg-blue-100 text-blue-700',
}

const PERFIL_BADGE: Record<string, string> = {
  Conservador: 'bg-blue-100 text-blue-700',
  Moderado: 'bg-amber-100 text-amber-700',
  Agresivo: 'bg-red-100 text-red-700',
}

export default function AdminUsuariosPage() {
  const router = useRouter()
  const [busqueda, setBusqueda] = useState('')
  const [detalle, setDetalle] = useState<UsuarioAdmin | null>(null)

  const { data: usuarios = [], isLoading } = useQuery<UsuarioAdmin[]>({
    queryKey: ['admin-usuarios'],
    queryFn: async () => (await api.get('/admin/usuarios')).data,
  })

  const filtrados = usuarios.filter((u) => {
    const q = busqueda.toLowerCase()
    return (
      u.nombreCompleto.toLowerCase().includes(q) ||
      u.correo.toLowerCase().includes(q) ||
      (u.perfilInversion?.nombrePerfil ?? '').toLowerCase().includes(q)
    )
  })

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-xl font-bold">Usuarios registrados</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Consulta de solo lectura · {usuarios.length} usuario{usuarios.length !== 1 ? 's' : ''}
          </p>
        </div>
        <Button variant="outline" onClick={() => router.push('/admin')}>Cancelar</Button>
      </div>

      {/* Buscador */}
      <input
        type="search"
        placeholder="Buscar por nombre, correo o perfil…"
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        className="w-full border rounded px-3 py-2 text-sm bg-background focus:outline-none focus:ring-2 focus:ring-primary"
      />

      {/* Tabla */}
      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Nombre</th>
              <th className="text-left px-4 py-3 font-medium">Correo</th>
              <th className="text-left px-4 py-3 font-medium">Proveedor</th>
              <th className="text-left px-4 py-3 font-medium">Perfil</th>
              <th className="text-left px-4 py-3 font-medium">Roles</th>
              <th className="text-left px-4 py-3 font-medium">Estado</th>
              <th className="px-4 py-3 w-20" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {filtrados.length === 0 && (
              <tr>
                <td colSpan={7} className="text-center py-8 text-muted-foreground">
                  {busqueda ? 'Sin resultados para la búsqueda.' : 'No hay usuarios registrados.'}
                </td>
              </tr>
            )}
            {filtrados.map((u) => (
              <tr key={u.id} className="hover:bg-muted/40 transition-colors">
                <td className="px-4 py-3 font-medium">{u.nombreCompleto}</td>
                <td className="px-4 py-3 text-muted-foreground">{u.correo}</td>
                <td className="px-4 py-3">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${PROVEEDOR_BADGE[u.proveedorOauth] ?? 'bg-secondary text-secondary-foreground'}`}>
                    {u.proveedorOauth}
                  </span>
                </td>
                <td className="px-4 py-3">
                  {u.perfilInversion ? (
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${PERFIL_BADGE[u.perfilInversion.nombrePerfil] ?? 'bg-secondary'}`}>
                      {u.perfilInversion.nombrePerfil}
                    </span>
                  ) : (
                    <span className="text-xs text-muted-foreground italic">Sin calibrar</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  <div className="flex flex-wrap gap-1">
                    {u.roles.map((r) => (
                      <span key={r} className="text-xs bg-secondary px-1.5 py-0.5 rounded">
                        {r}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="px-4 py-3">
                  {u.requiereRecalibracion ? (
                    <span className="text-xs px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 font-medium">
                      Recalibrar
                    </span>
                  ) : (
                    <span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700 font-medium">
                      Al día
                    </span>
                  )}
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => setDetalle(u)}
                    className="text-xs text-primary underline-offset-2 hover:underline"
                  >
                    Ver más
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Panel de detalle */}
      {detalle && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card border rounded-xl shadow-xl max-w-lg w-full p-6 space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold">Detalle del usuario</h2>
              <button
                onClick={() => setDetalle(null)}
                className="text-muted-foreground hover:text-foreground text-xl leading-none"
              >
                ×
              </button>
            </div>

            <dl className="grid grid-cols-2 gap-3 text-sm">
              <div className="col-span-2">
                <dt className="text-xs text-muted-foreground">Nombre completo</dt>
                <dd className="font-medium">{detalle.nombreCompleto}</dd>
              </div>
              <div className="col-span-2">
                <dt className="text-xs text-muted-foreground">Correo</dt>
                <dd>{detalle.correo}</dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Celular</dt>
                <dd>{detalle.celular ?? <span className="italic text-muted-foreground">No registrado</span>}</dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Proveedor OAuth</dt>
                <dd>{detalle.proveedorOauth}</dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Perfil de inversión</dt>
                <dd>{detalle.perfilInversion?.nombrePerfil ?? <span className="italic text-muted-foreground">Sin perfil</span>}</dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Estado calibración</dt>
                <dd>
                  {detalle.requiereRecalibracion
                    ? <span className="text-amber-600 font-medium">Requiere recalibrar</span>
                    : <span className="text-green-600 font-medium">Al día</span>}
                </dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Roles</dt>
                <dd>{detalle.roles.join(', ')}</dd>
              </div>
              <div>
                <dt className="text-xs text-muted-foreground">Miembro desde</dt>
                <dd>{new Date(detalle.fechaRegistro).toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' })}</dd>
              </div>
              {detalle.fechaUltimaActualizacionPerfil && (
                <div className="col-span-2">
                  <dt className="text-xs text-muted-foreground">Última calibración</dt>
                  <dd>{new Date(detalle.fechaUltimaActualizacionPerfil).toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' })}</dd>
                </div>
              )}
            </dl>

            <div className="pt-2 border-t text-right">
              <button
                onClick={() => setDetalle(null)}
                className="text-sm px-4 py-1.5 rounded border hover:bg-muted transition-colors"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
