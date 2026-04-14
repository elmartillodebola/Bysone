package com.bysone.backend.dto.response;

import java.math.BigDecimal;

public record PeriodoProyeccionResponse(
        Integer periodo,
        BigDecimal valorProyectadoMinimo,
        BigDecimal valorProyectadoEsperado,
        BigDecimal valorProyectadoMaximo,
        BigDecimal rentabilidadMinimaAplicada,
        BigDecimal rentabilidadMaximaAplicada
) {}
