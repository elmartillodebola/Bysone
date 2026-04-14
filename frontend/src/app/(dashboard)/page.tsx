import { auth } from '@/lib/auth'
import Link from 'next/link'
import { Button } from '@/components/ui/Button'

export default async function DashboardPage() {
  const session = await auth()

  return (
    <div className="max-w-3xl mx-auto space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-foreground">
          Bienvenido, {session?.user?.name?.split(' ')[0]}
        </h1>
        <p className="text-muted-foreground mt-1">
          Simula y compara perfiles de inversión para tu pensión voluntaria
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link href="/calibracion">
          <div className="bg-card rounded-lg border p-6 hover:shadow-md transition-shadow cursor-pointer">
            <h2 className="font-semibold text-lg">Calibración</h2>
            <p className="text-muted-foreground text-sm mt-1">
              Descubre tu perfil de riesgo respondiendo algunas preguntas
            </p>
          </div>
        </Link>

        <Link href="/simulacion">
          <div className="bg-card rounded-lg border p-6 hover:shadow-md transition-shadow cursor-pointer">
            <h2 className="font-semibold text-lg">Simulación</h2>
            <p className="text-muted-foreground text-sm mt-1">
              Proyecta cómo crecería tu inversión con distintos perfiles
            </p>
          </div>
        </Link>

        <Link href="/perfil">
          <div className="bg-card rounded-lg border p-6 hover:shadow-md transition-shadow cursor-pointer">
            <h2 className="font-semibold text-lg">Mi Perfil</h2>
            <p className="text-muted-foreground text-sm mt-1">
              Consulta tu perfil asignado y sus portafolios
            </p>
          </div>
        </Link>
      </div>
    </div>
  )
}
