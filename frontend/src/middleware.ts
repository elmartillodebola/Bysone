import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

// El guard de autenticación real vive en AuthGuard (client component con localStorage).
// Este middleware solo deja pasar todo — no hay cookies de sesión en este flujo JWT puro.
export function middleware(request: NextRequest) {
  return NextResponse.next()
}

export const config = {
  matcher: ['/((?!_next/static|_next/image|favicon.ico).*)'],
}
