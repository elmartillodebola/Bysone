package com.bysone.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PerfilInversionResponse(
        Long id,
        String nombrePerfil,
        BigDecimal rentabilidadMinima,
        BigDecimal rentabilidadMedia,
        BigDecimal rentabilidadMaxima,
        List<PortafolioResponse> portafolios,
        List<FormulaExposicionResponse> formulasExposicion
) {}
