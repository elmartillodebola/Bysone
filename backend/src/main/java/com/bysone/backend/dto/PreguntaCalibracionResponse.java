package com.bysone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de preguntas de calibración con sus opciones anidadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaCalibracionResponse {
    private Long id;
    private String textoPregunta;
    private Integer orden;
    private Boolean activa;
    private List<OpcionRespuestaResponse> opciones;
}
