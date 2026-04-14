package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encuestas_calibracion")
@Getter @Setter @NoArgsConstructor
public class EncuestaCalibracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encuesta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDateTime fechaRealizacion = LocalDateTime.now();

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "origen", nullable = false, length = 10)
    private String origen = "DEMANDA"; // DEMANDA | SISTEMA

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "PENDIENTE"; // PENDIENTE | COMPLETADA

    @Column(name = "puntaje_total")
    private Integer puntajeTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_resultado")
    private PerfilInversion perfilResultado;

    @OneToMany(mappedBy = "encuestaCalibracion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RespuestaEncuestaCalibracion> respuestas = new ArrayList<>();
}
