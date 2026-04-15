import Link from 'next/link'
import { Button } from '@/components/ui/Button'

export default function ResultadoCalibracionPage() {
  return (
    <div className="max-w-xl mx-auto text-center space-y-6 py-12">
      <div className="text-5xl">🎯</div>
      <h1 className="text-2xl font-bold">¡Calibración completada!</h1>
      <p className="text-muted-foreground">
        Tu perfil de inversión ha sido asignado. Puedes consultarlo en la sección{' '}
        <strong>Mi Perfil</strong> y comenzar a simular proyecciones.
      </p>
      <div className="flex gap-3 justify-center">
        <Link href="/perfil">
          <Button>Ver mi perfil</Button>
        </Link>
        <Link href="/simulacion">
          <Button variant="outline">Ir a simulación</Button>
        </Link>
      </div>
    </div>
  )
}
