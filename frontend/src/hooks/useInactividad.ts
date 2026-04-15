'use client'

/**
 * BR-SES-001 — Cierre automático de sesión por inactividad.
 *
 * Consulta el timeout configurado en el backend (/api/v1/config/sesion) y
 * reinicia el temporizador ante cualquier evento del usuario (mousemove,
 * keydown, click, touchstart, scroll). Al vencerse el plazo sin actividad,
 * elimina el token JWT del localStorage y redirige a /login.
 */

import { useEffect, useRef } from 'react'
import { useRouter } from 'next/navigation'
import api from '@/lib/api'

const TIMEOUT_DEFAULT_MS = 5 * 60 * 1000  // 5 minutos fallback

const ACTIVITY_EVENTS = [
  'mousemove',
  'keydown',
  'click',
  'touchstart',
  'scroll',
] as const

export function useInactividad() {
  const router = useRouter()
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const timeoutMsRef = useRef<number>(TIMEOUT_DEFAULT_MS)

  useEffect(() => {
    let isMounted = true

    // Obtener el timeout configurado; usar fallback si falla
    api.get<{ timeoutInactividadMinutos: number }>('/config/sesion')
      .then(res => {
        if (isMounted) {
          const minutos = res.data?.timeoutInactividadMinutos
          if (typeof minutos === 'number' && minutos > 0) {
            timeoutMsRef.current = minutos * 60 * 1000
          }
          startTimer()
        }
      })
      .catch(() => {
        if (isMounted) startTimer()
      })

    function logout() {
      localStorage.removeItem('bysone_token')
      router.replace('/login')
    }

    function startTimer() {
      clearTimer()
      timerRef.current = setTimeout(logout, timeoutMsRef.current)
    }

    function clearTimer() {
      if (timerRef.current !== null) {
        clearTimeout(timerRef.current)
        timerRef.current = null
      }
    }

    function handleActivity() {
      startTimer()
    }

    ACTIVITY_EVENTS.forEach(event =>
      window.addEventListener(event, handleActivity, { passive: true })
    )

    return () => {
      isMounted = false
      clearTimer()
      ACTIVITY_EVENTS.forEach(event =>
        window.removeEventListener(event, handleActivity)
      )
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])
}
