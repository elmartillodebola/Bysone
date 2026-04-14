package com.bysone.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SimulacionCalculadaResponse(
        Long idPerfil,
        String nombrePerfilSimulado,
        BigDecimal valorInversionInicial,
        BigDecimal aporteMensual,
        Integer plazo,
        String nombreTipoPlazo,
        List<PeriodoProyeccionResponse> proyeccion,
        ResumenSimulacionResponse resumen
) {}
