package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "opciones_inversion")
@Getter @Setter @NoArgsConstructor
public class OpcionInversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion_inversion")
    private Long id;

    @Column(name = "nombre_opcion_inversion", nullable = false, length = 150)
    private String nombreOpcion;

    @Column(name = "descripcion_opcion_inversion", length = 500)
    private String descripcionOpcion;

    @Column(name = "rentabilidad_minima", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMinima;

    @Column(name = "rentabilidad_maxima", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMaxima;
}
