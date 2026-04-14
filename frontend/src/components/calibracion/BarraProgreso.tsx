interface Props {
  progreso: number
  paso: number
  total: number
}

export default function BarraProgreso({ progreso, paso, total }: Props) {
  return (
    <div className="space-y-1">
      <div className="flex justify-between text-sm text-muted-foreground">
        <span>Pregunta {paso} de {total}</span>
        <span>{Math.round(progreso)}%</span>
      </div>
      <div className="w-full bg-secondary rounded-full h-2">
        <div
          className="bg-primary h-2 rounded-full transition-all duration-300"
          style={{ width: `${progreso}%` }}
        />
      </div>
    </div>
  )
}
