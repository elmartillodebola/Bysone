package com.bysone.backend.dto.response;

public record EncuestaResponse(
        Long id,
        String origen,
        String estado,
        String fechaRealizacion,
        String fechaVencimiento
) {}
