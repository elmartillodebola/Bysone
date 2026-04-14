'use client'

import { useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import Spinner from '@/components/shared/Spinner'

/**
 * El backend redirige aquí tras el login OAuth2 con el JWT en ?token=...
 * Lo guardamos en localStorage y redirigimos al dashboard.
 */
export default function AuthCallbackPage() {
  const router = useRouter()
  const params = useSearchParams()

  useEffect(() => {
    const token = params.get('token')
    if (token) {
      localStorage.setItem('bysone_token', token)
      router.replace('/')
    } else {
      router.replace('/login')
    }
  }, [params, router])

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <Spinner />
        <p className="text-muted-foreground text-sm">Iniciando sesión...</p>
      </div>
    </div>
  )
}
