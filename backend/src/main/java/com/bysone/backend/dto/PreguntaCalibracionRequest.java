package com.bysone.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear/actualizar preguntas de calibración con sus opciones de respuesta anidadas.
 * Validaciones:
 * - CA-PRG-01: textoPregunta no vacío, ≤500 caracteres
 * - CA-PRG-02: orden > 0
 * - CA-ORC-05: cada pregunta debe tener ≥2 opciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaCalibracionRequest {

    @NotBlank(message = "El texto de la pregunta no puede estar vacío")
    @Size(max = 500, message = "El texto no puede superar 500 caracteres")
    private String textoPregunta;

    @NotNull(message = "El orden no puede ser nulo")
    @Positive(message = "El orden debe ser mayor que 0")
    private Integer orden;

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activa;

    @NotEmpty(message = "Debe proporcionar al menos 2 opciones de respuesta")
    @Size(min = 2, message = "Cada pregunta debe tener al menos 2 opciones de respuesta (CA-ORC-05)")
    @Valid
    private List<OpcionRespuestaRequest> opciones;
}
