package com.bysone.backend.dto.response;

public record DisclaimerResponse(
        Long id,
        String titulo,
        String contenido,
        String fechaVigenciaDesde,
        String fechaVigenciaHasta
) {}
