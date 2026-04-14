package com.bysone.backend.dto.response;

public record EncuestaCompletadaResponse(
        Long id,
        Integer puntajeTotal,
        String estado,
        PerfilResumenResponse perfilAsignado,
        String fechaRealizacion,
        String fechaVencimiento
) {}
