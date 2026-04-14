/**
 * La autenticación OAuth2 la maneja el backend (Spring Security).
 * Flujo: /oauth2/authorization/google → Google → backend callback → JWT → /auth/callback?token=
 *
 * NextAuth no se usa para el login. Este módulo existe solo para compatibilidad
 * con el handler de /api/auth/[...nextauth] que Next.js requiere.
 */
import NextAuth from 'next-auth'

export const { handlers, auth, signIn, signOut } = NextAuth({
  providers: [],
  pages: { signIn: '/login' },
})
