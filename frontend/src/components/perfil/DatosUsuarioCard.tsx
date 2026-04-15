'use client'

import { useState } from 'react'
import { UsuarioMe, ActualizarUsuarioRequest } from '@/lib/types'
import { useActualizarUsuario } from '@/hooks/useUsuario'
import { Button } from '@/components/ui/Button'

interface Props {
  usuario: UsuarioMe
}

const PROVEEDOR_LABEL: Record<string, string> = {
  GOOGLE: 'Google',
  MICROSOFT: 'Microsoft',
}

export default function DatosUsuarioCard({ usuario }: Props) {
  const [editando, setEditando] = useState(false)
  const [nombre, setNombre] = useState(usuario.nombreCompleto)
  const [celular, setCelular] = useState(usuario.celular ?? '')
  const [error, setError] = useState<string | null>(null)

  const { mutate: actualizar, isPending } = useActualizarUsuario()

  function iniciarEdicion() {
    setNombre(usuario.nombreCompleto)
    setCelular(usuario.celular ?? '')
    setError(null)
    setEditando(true)
  }

  function cancelar() {
    setEditando(false)
    setError(null)
  }

  function guardar() {
    if (!nombre.trim()) {
      setError('El nombre no puede estar vacío.')
      return
    }
    const payload: ActualizarUsuarioRequest = {
      nombreCompleto: nombre.trim(),
      celular: celular.trim() || undefined,
    }
    actualizar(payload, {
      onSuccess: () => setEditando(false),
      onError: () => setError('No se pudo guardar. Intenta de nuevo.'),
    })
  }

  return (
    <div className="bg-card border rounded-lg p-6 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Datos personales</h2>
        {!editando && (
          <Button size="sm" variant="outline" onClick={iniciarEdicion}>
            Editar
          </Button>
        )}
      </div>

      {editando ? (
        <div className="space-y-3">
          <div>
            <label htmlFor="edit-nombre" className="block text-xs text-muted-foreground mb-1">Nombre completo</label>
            <input
              id="edit-nombre"
              type="text"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              maxLength={200}
              className="w-full border rounded px-3 py-2 text-sm bg-background focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label htmlFor="edit-celular" className="block text-xs text-muted-foreground mb-1">Celular (opcional)</label>
            <input
              id="edit-celular"
              type="tel"
              value={celular}
              onChange={(e) => setCelular(e.target.value)}
              maxLength={20}
              placeholder="+57 300 123 4567"
              className="w-full border rounded px-3 py-2 text-sm bg-background focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          {error && <p className="text-sm text-destructive">{error}</p>}
          <div className="flex gap-2">
            <Button size="sm" onClick={guardar} disabled={isPending}>
              {isPending ? 'Guardando…' : 'Guardar'}
            </Button>
            <Button size="sm" variant="outline" onClick={cancelar} disabled={isPending}>
              Cancelar
            </Button>
          </div>
        </div>
      ) : (
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
          <div>
            <dt className="text-xs text-muted-foreground">Nombre</dt>
            <dd className="font-medium">{usuario.nombreCompleto}</dd>
          </div>
          <div>
            <dt className="text-xs text-muted-foreground">Correo</dt>
            <dd className="font-medium">{usuario.correo}</dd>
          </div>
          <div>
            <dt className="text-xs text-muted-foreground">Celular</dt>
            <dd className="font-medium">{usuario.celular ?? <span className="text-muted-foreground italic">No registrado</span>}</dd>
          </div>
          <div>
            <dt className="text-xs text-muted-foreground">Proveedor</dt>
            <dd className="font-medium">{PROVEEDOR_LABEL[usuario.proveedorOauth] ?? usuario.proveedorOauth}</dd>
          </div>
          <div>
            <dt className="text-xs text-muted-foreground">Miembro desde</dt>
            <dd className="font-medium">{new Date(usuario.fechaRegistro).toLocaleDateString('es-CO')}</dd>
          </div>
        </dl>
      )}
    </div>
  )
}
