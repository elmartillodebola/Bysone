'use client'

import Link from 'next/link'
import { UsuarioMe, UltimaEncuesta } from '@/lib/types'
import { Button } from '@/components/ui/Button'

interface Props {
  usuario: UsuarioMe
  ultimaCalibracion: UltimaEncuesta | null
}

const PERFIL_COLOR: Record<string, string> = {
  Conservador: 'text-blue-600 bg-blue-50',
  Moderado: 'text-amber-600 bg-amber-50',
  Agresivo: 'text-red-600 bg-red-50',
}

export default function EstadoCalibracionCard({ usuario, ultimaCalibracion }: Props) {
  const sinCalibracion = !ultimaCalibracion
  const requiereRecalibrar = usuario.requiereRecalibracion && !!ultimaCalibracion

  return (
    <div className="bg-card border rounded-lg p-6 space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Estado de calibración</h2>
        {sinCalibracion && (
          <span className="text-xs font-medium px-2 py-1 rounded-full bg-destructive/10 text-destructive">
            Sin calibrar
          </span>
        )}
        {!sinCalibracion && requiereRecalibrar && (
          <span className="text-xs font-medium px-2 py-1 rounded-full bg-amber-100 text-amber-700">
            Recalibración requerida
          </span>
        )}
        {!sinCalibracion && !requiereRecalibrar && (
          <span className="text-xs font-medium px-2 py-1 rounded-full bg-green-100 text-green-700">
            Al día
          </span>
        )}
      </div>

      {sinCalibracion && (
        <div className="space-y-3 text-center py-2">
          <p className="text-sm text-muted-foreground">
            Aún no has completado la calibración de perfil. Responde unas preguntas
            rápidas para que podamos asignarte el perfil de inversión que mejor se
            adapta a ti.
          </p>
          <Link href="/calibracion">
            <Button>Iniciar calibración</Button>
          </Link>
        </div>
      )}

      {!sinCalibracion && (
        <div className="space-y-3">
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm">
            <div>
              <dt className="text-xs text-muted-foreground">Perfil asignado</dt>
              <dd>
                {ultimaCalibracion.perfilAsignado ? (
                  <span
                    className={`inline-block font-semibold px-2 py-0.5 rounded-full text-sm ${
                      PERFIL_COLOR[ultimaCalibracion.perfilAsignado.nombrePerfil] ??
                      'text-primary bg-primary/10'
                    }`}
                  >
                    {ultimaCalibracion.perfilAsignado.nombrePerfil}
                  </span>
                ) : (
                  <span className="text-muted-foreground italic">Desconocido</span>
                )}
              </dd>
            </div>
            <div>
              <dt className="text-xs text-muted-foreground">Puntaje obtenido</dt>
              <dd className="font-medium">{ultimaCalibracion.puntajeTotal ?? '—'}</dd>
            </div>
            <div>
              <dt className="text-xs text-muted-foreground">Fecha de calibración</dt>
              <dd className="font-medium">
                {new Date(ultimaCalibracion.fechaRealizacion).toLocaleDateString('es-CO', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                })}
              </dd>
            </div>
            {usuario.fechaUltimaActualizacionPerfil && (
              <div>
                <dt className="text-xs text-muted-foreground">Último cambio de perfil</dt>
                <dd className="font-medium">
                  {new Date(usuario.fechaUltimaActualizacionPerfil).toLocaleDateString('es-CO', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                  })}
                </dd>
              </div>
            )}
          </dl>

          {requiereRecalibrar && (
            <div className="rounded-md bg-amber-50 border border-amber-200 p-3 text-sm text-amber-800 space-y-2">
              <p>Tu perfil ha vencido. Te recomendamos recalibrar para mantener una estrategia de inversión actualizada.</p>
              <Link href="/calibracion">
                <Button size="sm">Recalibrar ahora</Button>
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
