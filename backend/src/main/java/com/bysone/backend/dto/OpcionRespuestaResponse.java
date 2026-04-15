package com.bysone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de opciones de respuesta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuestaResponse {
    private Long id;
    private String textoOpcion;
    private Integer puntaje;
    private Integer orden;
}
