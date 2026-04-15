package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Opciones funcionales del sistema (ej: REALIZAR_SIMULACION, GESTIONAR_PARAMETROS).
 * Estas se asignan a roles para definir permisos.
 */
@Entity
@Table(name = "opciones_funcionales_bysone")
@Getter @Setter @NoArgsConstructor
public class OpcionFuncional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion")
    private Long id;

    @Column(name = "nombre_opcion_funcional", nullable = false, length = 150)
    private String nombreOpcionFuncional;
}
