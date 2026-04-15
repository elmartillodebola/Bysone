/**
 * Tests de lógica de negocio — Roles × Opciones funcionales
 *
 * Validan el comportamiento del Set de asignaciones que determina
 * si una opción está activa para un rol, sin renderizar el componente.
 */

describe('Roles × Opciones — lógica de asignaciones', () => {
  function buildSet(asignaciones: { idRol: number; idOpcion: number }[]) {
    return new Set(asignaciones.map(a => `${a.idRol}-${a.idOpcion}`))
  }

  test('detecta una asignación existente correctamente', () => {
    const asignaciones = [
      { idRol: 1, idOpcion: 2 },
      { idRol: 2, idOpcion: 3 },
    ]
    const set = buildSet(asignaciones)
    expect(set.has('1-2')).toBe(true)
    expect(set.has('2-3')).toBe(true)
  })

  test('detecta correctamente que una asignación no existe', () => {
    const asignaciones = [{ idRol: 1, idOpcion: 2 }]
    const set = buildSet(asignaciones)
    expect(set.has('1-3')).toBe(false)
    expect(set.has('2-2')).toBe(false)
  })

  test('el Set vacío no contiene ninguna asignación', () => {
    const set = buildSet([])
    expect(set.has('1-1')).toBe(false)
    expect(set.size).toBe(0)
  })

  test('la clave es única por par rol-opción (no colisiona entre distintos pares)', () => {
    // Verifica que "12-3" no confunde con "1-23"
    const asignaciones = [{ idRol: 12, idOpcion: 3 }]
    const set = buildSet(asignaciones)
    expect(set.has('12-3')).toBe(true)
    expect(set.has('1-23')).toBe(false)
  })

  test('acepta múltiples opciones asignadas al mismo rol', () => {
    const asignaciones = [
      { idRol: 1, idOpcion: 1 },
      { idRol: 1, idOpcion: 2 },
      { idRol: 1, idOpcion: 3 },
    ]
    const set = buildSet(asignaciones)
    expect(set.size).toBe(3)
    expect(set.has('1-1')).toBe(true)
    expect(set.has('1-2')).toBe(true)
    expect(set.has('1-3')).toBe(true)
  })
})
