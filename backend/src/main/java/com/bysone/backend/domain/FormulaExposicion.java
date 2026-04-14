package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "formulas_exposicion")
@Getter @Setter @NoArgsConstructor
public class FormulaExposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formula_exposicion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_inversion", nullable = false)
    private PerfilInversion perfilInversion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_portafolio_inversion", nullable = false)
    private PortafolioInversion portafolioInversion;

    @Column(name = "umbral_porcentaje_min_1", nullable = false, precision = 5, scale = 2)
    private BigDecimal umbralPorcentajeMin;

    @Column(name = "umbral_porcentaje_max_1", nullable = false, precision = 5, scale = 2)
    private BigDecimal umbralPorcentajeMax;
}
