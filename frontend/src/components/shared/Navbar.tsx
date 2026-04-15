'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { cn } from '@/lib/utils'

// TODO BR-ROL-002: cuando finalice la implementación de funcionalidades,
// filtrar el ítem "Configuración" usando useCurrentUser().esAdmin
const links = [
  { href: '/', label: 'Inicio' },
  { href: '/calibracion', label: 'Calibración' },
  { href: '/simulacion', label: 'Simulación' },
  { href: '/perfil', label: 'Mi Perfil' },
  { href: '/admin', label: 'Configuración' },
]

export default function Navbar() {
  const pathname = usePathname()

  return (
    <nav className="w-48 border-r bg-card flex flex-col gap-1 p-3">
      {links.map(({ href, label }) => (
        <Link
          key={href}
          href={href}
          className={cn(
            'px-3 py-2 rounded-md text-sm font-medium transition-colors',
            pathname.startsWith(href) && (href !== '/' || pathname === '/')
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-accent'
          )}
        >
          {label}
        </Link>
      ))}
    </nav>
  )
}
