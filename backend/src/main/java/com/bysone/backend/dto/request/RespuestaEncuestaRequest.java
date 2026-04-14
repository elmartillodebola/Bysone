package com.bysone.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record RespuestaEncuestaRequest(
        @NotNull(message = "La pregunta es obligatoria")
        Long idPregunta,

        @NotNull(message = "La opción de respuesta es obligatoria")
        Long idOpcionRespuesta
) {}
