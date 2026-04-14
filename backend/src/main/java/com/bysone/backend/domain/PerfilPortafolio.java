package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "perfiles_inversion_x_portafolios_inversion")
@IdClass(PerfilPortafolioId.class)
@Getter @Setter @NoArgsConstructor
public class PerfilPortafolio {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_inversion", nullable = false)
    private PerfilInversion perfilInversion;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_portafolio_inversion", nullable = false)
    private PortafolioInversion portafolioInversion;

    @Column(name = "porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;
}
