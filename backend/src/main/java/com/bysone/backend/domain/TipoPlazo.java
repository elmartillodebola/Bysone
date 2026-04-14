package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipos_plazo")
@Getter @Setter @NoArgsConstructor
public class TipoPlazo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_plazo")
    private Long id;

    @Column(name = "nombre_plazo", nullable = false, unique = true, length = 50)
    private String nombrePlazo;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "factor_conversion_dias", nullable = false)
    private Integer factorConversionDias;
}
