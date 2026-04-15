'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Spinner from './Spinner'
import { useInactividad } from '@/hooks/useInactividad'

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const [checking, setChecking] = useState(true)

  // BR-SES-001: cierre automático por inactividad
  useInactividad()

  useEffect(() => {
    const token = localStorage.getItem('bysone_token')
    if (!token) {
      router.replace('/login')
      return
    }
    setChecking(false)
  }, [router])

  if (checking) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner />
      </div>
    )
  }

  return <>{children}</>
}
