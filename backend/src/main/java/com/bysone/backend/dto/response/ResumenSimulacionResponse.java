package com.bysone.backend.dto.response;

import java.math.BigDecimal;

public record ResumenSimulacionResponse(
        BigDecimal gananciaEsperada,
        BigDecimal rendimientoPorcentualTotal
) {}
