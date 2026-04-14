package com.bysone.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PortafolioResponse(
        Long id,
        String nombrePortafolio,
        String descripcionPortafolio,
        BigDecimal rentabilidadMinima,
        BigDecimal rentabilidadMaxima,
        BigDecimal porcentaje,
        List<OpcionInversionResponse> opciones
) {}
