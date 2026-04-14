'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { cn } from '@/lib/utils'

const links = [
  { href: '/', label: 'Inicio' },
  { href: '/calibracion', label: 'Calibración' },
  { href: '/simulacion', label: 'Simulación' },
  { href: '/perfil', label: 'Mi Perfil' },
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
            pathname === href
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
