package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "disclaimers_bysone")
@Getter @Setter @NoArgsConstructor
public class Disclaimer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disclaimer")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_vigencia_desde")
    private LocalDateTime fechaVigenciaDesde;

    @Column(name = "fecha_vigencia_hasta")
    private LocalDateTime fechaVigenciaHasta;
}
