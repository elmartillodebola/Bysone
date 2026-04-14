'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Spinner from './Spinner'

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const [checking, setChecking] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('bysone_token')
    if (!token) {
      router.replace('/login')
    } else {
      setChecking(false)
    }
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
