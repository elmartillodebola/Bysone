package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "respuestas_encuesta_calibracion")
@Getter @Setter @NoArgsConstructor
public class RespuestaEncuestaCalibracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encuesta", nullable = false)
    private EncuestaCalibracion encuestaCalibracion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private PreguntaCalibracion preguntaCalibracion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_opcion_respuesta", nullable = false)
    private OpcionRespuestaCalibracion opcionRespuesta;
}
