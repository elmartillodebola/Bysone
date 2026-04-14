package com.bysone.backend.dto.response;

import java.math.BigDecimal;

public record FormulaExposicionResponse(
        Long idPortafolio,
        BigDecimal umbralPorcentajeMin,
        BigDecimal umbralPorcentajeMax
) {}
