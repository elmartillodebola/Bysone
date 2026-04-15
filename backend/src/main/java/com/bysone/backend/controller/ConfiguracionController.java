package com.bysone.backend.controller;

import com.bysone.backend.service.ConfiguracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Expone parámetros de configuración del sistema que el frontend necesita
 * sin requerir autenticación (p. ej. antes del login).
 */
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@Tag(name = "Configuración", description = "Parámetros públicos del sistema")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    /**
     * BR-SES-001 — Retorna el timeout de inactividad de sesión.
     *
     * @return {@code { "timeoutInactividadMinutos": N }}
     */
    @GetMapping("/sesion")
    @Operation(summary = "Parámetros de sesión (público)")
    public ResponseEntity<Map<String, Object>> getSesionConfig() {
        int timeout = configuracionService.getTimeoutInactividadMinutos();
        return ResponseEntity.ok(Map.of("timeoutInactividadMinutos", timeout));
    }
}
