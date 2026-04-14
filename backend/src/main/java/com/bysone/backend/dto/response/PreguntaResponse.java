package com.bysone.backend.dto.response;

import java.util.List;

public record PreguntaResponse(
        Long id,
        String textoPregunta,
        Integer orden,
        List<OpcionRespuestaResponse> opciones
) {}
