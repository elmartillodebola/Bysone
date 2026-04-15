package com.bysone.backend.dto.response;

public record UltimaEncuestaResponse(
        Long id,
        String fechaRealizacion,
        String estado,
        Integer puntajeTotal,
        PerfilResumenResponse perfilAsignado
) {}
