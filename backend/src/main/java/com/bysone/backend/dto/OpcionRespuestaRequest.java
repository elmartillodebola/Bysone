package com.bysone.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar opciones de respuesta dentro de una pregunta.
 * Validaciones:
 * - CA-ORC-01: textoOpcion no vacío, ≤300 caracteres
 * - CA-ORC-02: puntaje ≥ 0
 * - CA-ORC-03: orden > 0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuestaRequest {

    @NotBlank(message = "El texto de la opción no puede estar vacío")
    @Size(max = 300, message = "El texto no puede superar 300 caracteres")
    private String textoOpcion;

    @NotNull(message = "El puntaje no puede ser nulo")
    @PositiveOrZero(message = "El puntaje debe ser mayor o igual a 0")
    private Integer puntaje;

    @NotNull(message = "El orden no puede ser nulo")
    @Positive(message = "El orden debe ser mayor que 0")
    private Integer orden;
}
