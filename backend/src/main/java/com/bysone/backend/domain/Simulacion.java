package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulaciones_bysone")
@Getter @Setter @NoArgsConstructor
public class Simulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_simulacion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_simulacion", nullable = false)
    private LocalDateTime fechaSimulacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_inversion", nullable = false)
    private PerfilInversion perfilInversion;

    @Column(name = "nombre_perfil_simulado", nullable = false, length = 100)
    private String nombrePerfilSimulado;

    @Column(name = "valor_inversion_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorInversionInicial;

    @Column(name = "valor_inversion_periodica", precision = 18, scale = 2)
    private BigDecimal valorInversionPeriodica;

    @Column(name = "plazo_inversion", nullable = false)
    private Integer plazoInversion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_plazo", nullable = false)
    private TipoPlazo tipoPlazo;

    @Column(name = "rentabilidad_se_reinvierte_plazo_inversion", nullable = false)
    private boolean rentabilidadSeReinvierte = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_disclaimer")
    private Disclaimer disclaimer;

    @OneToMany(mappedBy = "simulacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("periodo ASC")
    private List<DetalleProyeccionSimulacion> detalles = new ArrayList<>();
}
