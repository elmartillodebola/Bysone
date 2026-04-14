package com.bysone.backend.dto.response;

import java.math.BigDecimal;

public record SimulacionResumenResponse(
        Long id,
        String nombrePerfilSimulado,
        BigDecimal valorInversionInicial,
        BigDecimal aporteMensual,
        Integer plazo,
        String nombreTipoPlazo,
        BigDecimal gananciaEsperada,
        BigDecimal rendimientoPorcentualTotal,
        String fechaSimulacion
) {}
