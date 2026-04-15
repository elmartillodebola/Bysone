'use client'

import Link from 'next/link'

const secciones = [
  {
    href: '/admin/usuarios',
    titulo: 'Usuarios registrados',
    descripcion: 'Consulta de usuarios: perfil asignado, estado de calibración y roles.',
  },
  {
    href: '/admin/perfiles',
    titulo: 'Perfiles de inversión',
    descripcion: 'Crear y configurar perfiles con su composición de portafolios y fórmulas de exposición.',
  },
  {
    href: '/admin/parametros',
    titulo: 'Parámetros del sistema',
    descripcion: 'Timeouts, intervalos de recalibración y umbrales de puntaje.',
  },
  {
    href: '/admin/opciones-inversion',
    titulo: 'Opciones de inversión',
    descripcion: 'Instrumentos financieros individuales con sus rentabilidades.',
  },
  {
    href: '/admin/portafolios',
    titulo: 'Portafolios de inversión',
    descripcion: 'Portafolios y las opciones de inversión que los componen.',
  },
  {
    href: '/admin/preguntas',
    titulo: 'Cuestionario de calibración',
    descripcion: 'Preguntas activas/inactivas y sus opciones de respuesta.',
  },
  {
    href: '/admin/disclaimers',
    titulo: 'Disclaimers legales',
    descripcion: 'Textos legales con gestión de vigencia.',
  },
  {
    href: '/admin/tipos-plazo',
    titulo: 'Tipos de plazo',
    descripcion: 'Catálogo de unidades de tiempo para simulaciones (Días, Meses, Trimestres, Años).',
  },
  {
    href: '/admin/roles-opciones',
    titulo: 'Roles × Opciones funcionales',
    descripcion: 'Asignación de opciones funcionales a cada rol del sistema.',
  },
  {
    href: '/admin/roles',
    titulo: 'Roles',
    descripcion: 'Crear y renombrar roles del sistema (ADMIN, USER, MAINTAINER…).',
  },
  {
    href: '/admin/opciones-funcionales',
    titulo: 'Opciones funcionales',
    descripcion: 'Catálogo de permisos del sistema (GESTIONAR_USUARIOS, REALIZAR_SIMULACION…).',
  },
]

export default function AdminHubPage() {
  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Configuración Bysone</h1>
        <p className="text-muted-foreground text-sm mt-1">
          Administración del sistema. Los cambios aplican de inmediato sin redespliegue.
        </p>
      </div>
      <div className="grid gap-4">
        {secciones.map(({ href, titulo, descripcion }) => (
          <Link
            key={href}
            href={href}
            className="block border rounded-lg p-5 bg-card hover:bg-accent transition-colors"
          >
            <p className="font-semibold">{titulo}</p>
            <p className="text-sm text-muted-foreground mt-1">{descripcion}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}
