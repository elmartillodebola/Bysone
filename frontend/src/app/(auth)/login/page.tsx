import { Button } from '@/components/ui/Button'

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080'

export default function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-secondary">
      <div className="bg-card rounded-lg shadow-lg p-10 flex flex-col items-center gap-6 w-full max-w-sm">
        <h1 className="text-2xl font-bold text-foreground">Mi Portafolio Inteligente</h1>
        <p className="text-muted-foreground text-sm text-center">
          Inicia sesión para simular y comparar perfiles de inversión
        </p>

        {/* El backend (Spring Security) maneja el flujo OAuth2 completo */}
        <a href={`${API_URL}/oauth2/authorization/google`} className="w-full">
          <Button type="button" className="w-full" variant="default">
            Continuar con Google
          </Button>
        </a>

        <div className="w-full">
          <Button
            type="button"
            className="w-full opacity-50 cursor-not-allowed"
            variant="outline"
            disabled
          >
            Continuar con Microsoft
          </Button>
          <p className="text-xs text-amber-600 text-center mt-2">
            🔜 Disponible en la próxima versión — usa Google por ahora
          </p>
        </div>

        <p className="text-xs text-muted-foreground text-center mt-2">
          Hackaton 2026 · Comunidad de Desarrollo de Software · Protección
        </p>
      </div>
    </div>
  )
}
