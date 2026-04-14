'use client'

import { useRouter } from 'next/navigation'
import { Button } from '@/components/ui/Button'
import { useEffect, useState } from 'react'
import api from '@/lib/api'

export default function Header() {
  const router = useRouter()
  const [email, setEmail] = useState<string>('')

  useEffect(() => {
    api.get('/usuarios/me')
      .then(res => setEmail(res.data.correoUsuario ?? ''))
      .catch(() => {})
  }, [])

  function handleLogout() {
    localStorage.removeItem('bysone_token')
    router.replace('/login')
  }

  return (
    <header className="h-14 border-b bg-card flex items-center justify-between px-6">
      <span className="font-semibold text-primary">Mi Portafolio Inteligente</span>
      <div className="flex items-center gap-3">
        {email && <span className="text-sm text-muted-foreground">{email}</span>}
        <Button type="button" variant="outline" size="sm" onClick={handleLogout}>
          Cerrar sesión
        </Button>
      </div>
    </header>
  )
}
