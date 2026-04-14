package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portafolios_inversion")
@Getter @Setter @NoArgsConstructor
public class PortafolioInversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_portafolio_inversion")
    private Long id;

    @Column(name = "nombre_portafolio_inversion", nullable = false, length = 150)
    private String nombrePortafolio;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "rentabilidad_calculada_variable_minimo", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMinima;

    @Column(name = "rentabilidad_calculada_variable_maximo", nullable = false, precision = 5, scale = 2)
    private BigDecimal rentabilidadMaxima;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "portafolio_inversion_x_opciones_inversion",
        joinColumns = @JoinColumn(name = "id_portafolio_inversion"),
        inverseJoinColumns = @JoinColumn(name = "id_opcion_inversion")
    )
    private List<OpcionInversion> opciones = new ArrayList<>();
}
