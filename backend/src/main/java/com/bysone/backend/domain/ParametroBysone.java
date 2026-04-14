package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parametros_bysone")
@Getter @Setter @NoArgsConstructor
public class ParametroBysone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long id;

    @Column(name = "nombre_parametro", nullable = false, unique = true, length = 150)
    private String nombreParametro;

    @Column(name = "valor_parametro", nullable = false, length = 500)
    private String valorParametro;
}
