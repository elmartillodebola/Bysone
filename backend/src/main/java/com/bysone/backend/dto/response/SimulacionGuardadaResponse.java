package com.bysone.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SimulacionGuardadaResponse(
        Long id,
        Long idPerfil,
        String nombrePerfilSimulado,
        BigDecimal valorInversionInicial,
        BigDecimal aporteMensual,
        Integer plazo,
        String nombreTipoPlazo,
        String fechaSimulacion,
        List<PeriodoProyeccionResponse> proyeccion,
        ResumenSimulacionResponse resumen
) {}
