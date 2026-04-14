package com.bysone.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulacionRequest(
        @NotNull(message = "El perfil es obligatorio")
        Long idPerfil,

        @NotNull @Positive(message = "La inversión inicial debe ser mayor a 0")
        BigDecimal valorInversionInicial,

        @NotNull @Min(value = 0, message = "El aporte mensual debe ser 0 o mayor")
        BigDecimal aporteMensual,

        @NotNull @Positive(message = "El plazo debe ser mayor a 0")
        Integer plazo,

        @NotNull(message = "El tipo de plazo es obligatorio")
        Long idTipoPlazo,

        Long idDisclaimer
) {}
