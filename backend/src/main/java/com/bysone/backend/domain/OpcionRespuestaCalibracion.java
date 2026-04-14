package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "opciones_respuesta_calibracion")
@Getter @Setter @NoArgsConstructor
public class OpcionRespuestaCalibracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion_respuesta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private PreguntaCalibracion preguntaCalibracion;

    @Column(name = "texto_opcion", nullable = false, length = 300)
    private String textoOpcion;

    @Column(name = "puntaje", nullable = false)
    private Integer puntaje;

    @Column(name = "orden", nullable = false)
    private Integer orden;
}
