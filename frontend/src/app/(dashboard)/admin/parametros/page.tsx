'use client'

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import api from '@/lib/api'
import { Button } from '@/components/ui/Button'
import Spinner from '@/components/shared/Spinner'

interface Parametro { id: number; nombreParametro: string; valorParametro: string }

export default function ParametrosPage() {
  const queryClient = useQueryClient()
  const [editandoId, setEditandoId] = useState<number | null>(null)
  const [valorEdit, setValorEdit] = useState('')
  const [nuevoNombre, setNuevoNombre] = useState('')
  const [nuevoValor, setNuevoValor] = useState('')
  const [errorForm, setErrorForm] = useState<string | null>(null)

  const { data: parametros = [], isLoading } = useQuery<Parametro[]>({
    queryKey: ['admin-parametros'],
    queryFn: async () => (await api.get('/admin/parametros')).data,
  })

  const mutActualizar = useMutation({
    mutationFn: ({ id, valor }: { id: number; valor: string }) =>
      api.put(`/admin/parametros/${id}`, { valorParametro: valor }),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-parametros'] }); setEditandoId(null) },
  })

  const mutCrear = useMutation({
    mutationFn: () => api.post('/admin/parametros', { nombreParametro: nuevoNombre, valorParametro: nuevoValor }),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['admin-parametros'] }); setNuevoNombre(''); setNuevoValor(''); setErrorForm(null) },
    onError: () => setErrorForm('No se pudo crear. Verifica que el nombre sea único.'),
  })

  if (isLoading) return <Spinner />

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <h1 className="text-xl font-bold">Parámetros del sistema</h1>

      <div className="border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="text-left px-4 py-3 font-medium">Parámetro</th>
              <th className="text-left px-4 py-3 font-medium w-40">Valor</th>
              <th className="px-4 py-3 w-36" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {parametros.map(p => (
              <tr key={p.id} className="bg-card">
                <td className="px-4 py-3 font-mono text-xs text-muted-foreground">{p.nombreParametro}</td>
                <td className="px-4 py-3">
                  {editandoId === p.id
                    ? <input value={valorEdit} onChange={e => setValorEdit(e.target.value)} className="w-full border rounded px-2 py-1 text-sm" autoFocus />
                    : <span className="font-medium">{p.valorParametro}</span>}
                </td>
                <td className="px-4 py-3">
                  <div className="flex gap-2 justify-end">
                    {editandoId === p.id ? (
                      <>
                        <Button size="sm" onClick={() => mutActualizar.mutate({ id: p.id, valor: valorEdit })} disabled={mutActualizar.isPending}>Guardar</Button>
                        <Button size="sm" variant="outline" onClick={() => setEditandoId(null)}>Cancelar</Button>
                      </>
                    ) : (
                      <Button size="sm" variant="outline" onClick={() => { setEditandoId(p.id); setValorEdit(p.valorParametro) }}>Editar</Button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="border rounded-lg p-5 space-y-3 bg-card">
        <h2 className="font-semibold text-sm">Nuevo parámetro</h2>
        {errorForm && <p className="text-destructive text-sm">{errorForm}</p>}
        <div className="flex gap-3">
          <input placeholder="NOMBRE_PARAMETRO" value={nuevoNombre} onChange={e => setNuevoNombre(e.target.value.toUpperCase().replace(/\s/g, '_'))} className="flex-1 border rounded px-3 py-2 text-sm font-mono" />
          <input placeholder="valor" value={nuevoValor} onChange={e => setNuevoValor(e.target.value)} className="w-36 border rounded px-3 py-2 text-sm" />
          <Button onClick={() => mutCrear.mutate()} disabled={!nuevoNombre || !nuevoValor || mutCrear.isPending}>Agregar</Button>
        </div>
        <p className="text-xs text-muted-foreground">El valor es siempre texto — la aplicación lo interpreta según el contexto.</p>
      </div>
    </div>
  )
}
