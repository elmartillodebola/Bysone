/**
 * DatosUsuarioCard — RN-USU-06, RN-USU-07
 *
 * 1. Muestra los datos del usuario en modo lectura.
 * 2. Al pulsar Editar, se muestran los campos editables.
 * 3. Nombre vacío muestra error y no llama a la mutación.
 * 4. Correo y proveedor no son editables (no aparecen como inputs).
 * 5. Al cancelar, vuelve al modo lectura sin cambios.
 */

import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import DatosUsuarioCard from './DatosUsuarioCard'
import { UsuarioMe } from '@/lib/types'

// Mock del hook de mutación
const mockMutate = jest.fn()
jest.mock('@/hooks/useUsuario', () => ({
  useActualizarUsuario: () => ({
    mutate: mockMutate,
    isPending: false,
  }),
}))

const usuarioBase: UsuarioMe = {
  id: 1,
  nombreCompleto: 'Ana García López',
  correo: 'ana@gmail.com',
  celular: '+573001234567',
  proveedorOauth: 'GOOGLE',
  fechaRegistro: '2026-01-15T10:00:00',
  roles: ['USER'],
  perfilInversion: null,
  requiereRecalibracion: false,
}

describe('DatosUsuarioCard — RN-USU-06 y RN-USU-07', () => {
  beforeEach(() => mockMutate.mockClear())

  it('muestra datos del usuario en modo lectura', () => {
    render(<DatosUsuarioCard usuario={usuarioBase} />)

    expect(screen.getByText('Ana García López')).toBeInTheDocument()
    expect(screen.getByText('ana@gmail.com')).toBeInTheDocument()
    expect(screen.getByText('+573001234567')).toBeInTheDocument()
    expect(screen.getByText('Google')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /editar/i })).toBeInTheDocument()
  })

  it('muestra mensaje cuando no hay celular registrado', () => {
    render(<DatosUsuarioCard usuario={{ ...usuarioBase, celular: undefined }} />)
    expect(screen.getByText(/no registrado/i)).toBeInTheDocument()
  })

  it('al pulsar Editar muestra los inputs de nombre y celular', () => {
    render(<DatosUsuarioCard usuario={usuarioBase} />)
    fireEvent.click(screen.getByRole('button', { name: /editar/i }))

    expect(screen.getByLabelText(/nombre completo/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/celular/i)).toBeInTheDocument()
    // Correo NO es un input editable (RN-USU-07)
    expect(screen.queryByDisplayValue('ana@gmail.com')).not.toBeInTheDocument()
  })

  it('nombre vacío muestra error y no llama a la mutación (CA-USU-12)', () => {
    render(<DatosUsuarioCard usuario={usuarioBase} />)
    fireEvent.click(screen.getByRole('button', { name: /editar/i }))

    const inputNombre = screen.getByLabelText(/nombre completo/i)
    fireEvent.change(inputNombre, { target: { value: '   ' } })
    fireEvent.click(screen.getByRole('button', { name: /guardar/i }))

    expect(screen.getByText(/el nombre no puede estar vacío/i)).toBeInTheDocument()
    expect(mockMutate).not.toHaveBeenCalled()
  })

  it('envía payload correcto al guardar con nombre y celular', async () => {
    mockMutate.mockImplementation((_payload: unknown, opts: { onSuccess: () => void }) =>
      opts.onSuccess()
    )

    render(<DatosUsuarioCard usuario={usuarioBase} />)
    fireEvent.click(screen.getByRole('button', { name: /editar/i }))

    const inputNombre = screen.getByLabelText(/nombre completo/i)
    fireEvent.change(inputNombre, { target: { value: 'Ana López' } })

    fireEvent.click(screen.getByRole('button', { name: /guardar/i }))

    await waitFor(() =>
      expect(mockMutate).toHaveBeenCalledWith(
        { nombreCompleto: 'Ana López', celular: '+573001234567' },
        expect.any(Object)
      )
    )
  })

  it('cancelar vuelve al modo lectura sin cambios', () => {
    render(<DatosUsuarioCard usuario={usuarioBase} />)
    fireEvent.click(screen.getByRole('button', { name: /editar/i }))

    const inputNombre = screen.getByLabelText(/nombre completo/i)
    fireEvent.change(inputNombre, { target: { value: 'Nombre Cambiado' } })
    fireEvent.click(screen.getByRole('button', { name: /cancelar/i }))

    expect(screen.getByText('Ana García López')).toBeInTheDocument()
    expect(screen.queryByRole('textbox')).not.toBeInTheDocument()
  })
})
