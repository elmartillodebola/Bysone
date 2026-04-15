package com.bysone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de asignación rol × opción.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolOpcionResponse {
    private Long idRol;
    private String nombreRol;
    private Long idOpcion;
    private String nombreOpcion;
}
