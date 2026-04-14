package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas_calibracion")
@Getter @Setter @NoArgsConstructor
public class PreguntaCalibracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Long id;

    @Column(name = "texto_pregunta", nullable = false, length = 500)
    private String textoPregunta;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "preguntaCalibracion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("orden ASC")
    private List<OpcionRespuestaCalibracion> opciones = new ArrayList<>();
}
