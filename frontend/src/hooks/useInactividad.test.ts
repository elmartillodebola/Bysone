/**
 * BR-SES-001 — useInactividad
 *
 * Verifica que el hook:
 * 1. Elimina el token JWT y redirige a /login al vencer el tiempo de inactividad.
 * 2. Reinicia el temporizador ante actividad del usuario.
 * 3. Usa el timeout configurado en el backend cuando está disponible.
 * 4. Usa el timeout por defecto cuando el endpoint falla.
 */

import { renderHook, act } from '@testing-library/react'
import { useInactividad } from './useInactividad'

// Mock next/navigation
const mockReplace = jest.fn()
jest.mock('next/navigation', () => ({
  useRouter: () => ({ replace: mockReplace }),
}))

// Mock api
const mockApiGet = jest.fn()
jest.mock('@/lib/api', () => ({
  __esModule: true,
  default: { get: (...args: unknown[]) => mockApiGet(...args) },
}))

describe('useInactividad — BR-SES-001', () => {
  beforeEach(() => {
    jest.useFakeTimers()
    mockReplace.mockClear()
    localStorage.setItem('bysone_token', 'test-token')
  })

  afterEach(() => {
    jest.useRealTimers()
    localStorage.clear()
  })

  it('redirige a /login y elimina el token al vencer el tiempo de inactividad (timeout configurado)', async () => {
    // Backend devuelve 2 minutos
    mockApiGet.mockResolvedValueOnce({ data: { timeoutInactividadMinutos: 2 } })

    renderHook(() => useInactividad())

    // Dejar que la promesa de la API se resuelva
    await act(async () => {
      await Promise.resolve()
    })

    // Avanzar 2 minutos exactos
    act(() => {
      jest.advanceTimersByTime(2 * 60 * 1000)
    })

    expect(localStorage.getItem('bysone_token')).toBeNull()
    expect(mockReplace).toHaveBeenCalledWith('/login')
  })

  it('usa el timeout por defecto (5 min) cuando el endpoint falla', async () => {
    mockApiGet.mockRejectedValueOnce(new Error('Network error'))

    renderHook(() => useInactividad())

    await act(async () => {
      await Promise.resolve()
    })

    // 4 minutos y 59 segundos — aún no debe cerrar sesión
    act(() => {
      jest.advanceTimersByTime(4 * 60 * 1000 + 59_000)
    })
    expect(mockReplace).not.toHaveBeenCalled()

    // 1 segundo más → total 5 min → debe cerrar sesión
    act(() => {
      jest.advanceTimersByTime(1_000)
    })
    expect(localStorage.getItem('bysone_token')).toBeNull()
    expect(mockReplace).toHaveBeenCalledWith('/login')
  })

  it('reinicia el temporizador ante eventos de actividad del usuario', async () => {
    mockApiGet.mockResolvedValueOnce({ data: { timeoutInactividadMinutos: 2 } })

    renderHook(() => useInactividad())

    await act(async () => {
      await Promise.resolve()
    })

    // Avanzar 1 minuto 50 segundos
    act(() => {
      jest.advanceTimersByTime(1 * 60 * 1000 + 50_000)
    })

    // Simular actividad: dispara mousemove → reinicia el temporizador
    act(() => {
      window.dispatchEvent(new MouseEvent('mousemove'))
    })

    // Avanzar otros 1 minuto 50 segundos desde el reinicio — aún no debe expirar
    act(() => {
      jest.advanceTimersByTime(1 * 60 * 1000 + 50_000)
    })
    expect(mockReplace).not.toHaveBeenCalled()

    // Ahora avanzar los 10 segundos restantes para completar 2 min desde la actividad
    act(() => {
      jest.advanceTimersByTime(10_000)
    })
    expect(mockReplace).toHaveBeenCalledWith('/login')
  })

  it('no deja timers activos después de desmontar el hook', async () => {
    mockApiGet.mockResolvedValueOnce({ data: { timeoutInactividadMinutos: 1 } })

    const { unmount } = renderHook(() => useInactividad())

    await act(async () => {
      await Promise.resolve()
    })

    unmount()

    // Avanzar más del timeout configurado — no debe llamar al router
    act(() => {
      jest.advanceTimersByTime(2 * 60 * 1000)
    })
    expect(mockReplace).not.toHaveBeenCalled()
  })
})
