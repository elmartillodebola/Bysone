package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_proyeccion_simulacion")
@Getter @Setter @NoArgsConstructor
public class DetalleProyeccionSimulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_simulacion", nullable = false)
    private Simulacion simulacion;

    @Column(name = "periodo", nullable = false)
    private Integer periodo;

    @Column(name = "valor_proyectado_minimo", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorProyectadoMinimo;

    @Column(name = "valor_proyectado_maximo", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorProyectadoMaximo;

    @Column(name = "valor_proyectado_esperado", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorProyectadoEsperado;

    @Column(name = "rentabilidad_minima_aplicada", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMinimaAplicada;

    @Column(name = "rentabilidad_maxima_aplicada", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMaximaAplicada;
}
