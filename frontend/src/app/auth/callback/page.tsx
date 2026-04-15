'use client'

import { useEffect, useState, Suspense } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import Spinner from '@/components/shared/Spinner'

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080'

/**
 * El backend redirige aquí tras el login OAuth2 con el JWT en ?token=...
 * Antes de guardar el token, lo verificamos contra el backend.
 * Solo si /api/v1/usuarios/me responde 200 se almacena y se redirige al dashboard.
 * useSearchParams requiere Suspense boundary para el build estático de Next.js.
 */
function CallbackHandler() {
  const router = useRouter()
  const params = useSearchParams()
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const token = params.get('token')
    if (!token) {
      router.replace('/login')
      return
    }

    // Verificar el token contra el backend antes de almacenarlo
    fetch(`${API_URL}/api/v1/usuarios/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        localStorage.setItem('bysone_token', token)
        router.replace('/')
      })
      .catch(() => {
        setError('No fue posible verificar tu identidad. Intenta iniciar sesión nuevamente.')
      })
  }, [params, router])

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="flex flex-col items-center gap-4 max-w-sm text-center">
          <p className="text-destructive font-semibold">Error de autenticación</p>
          <p className="text-muted-foreground text-sm">{error}</p>
          <a
            href="/login"
            className="text-sm underline text-primary hover:opacity-80"
          >
            Volver al inicio de sesión
          </a>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <Spinner />
        <p className="text-muted-foreground text-sm">Verificando tu identidad...</p>
      </div>
    </div>
  )
}

export default function AuthCallbackPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen flex items-center justify-center">
          <Spinner />
        </div>
      }
    >
      <CallbackHandler />
    </Suspense>
  )
}
