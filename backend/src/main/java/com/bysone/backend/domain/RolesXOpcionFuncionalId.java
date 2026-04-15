package com.bysone.backend.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Clave compuesta para RolesXOpcionFuncional.
 */
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RolesXOpcionFuncionalId implements Serializable {
    private Long idRol;
    private Long idOpcion;
}
