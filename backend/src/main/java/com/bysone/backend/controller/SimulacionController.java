package com.bysone.backend.controller;

import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.request.SimulacionRequest;
import com.bysone.backend.dto.response.*;
import com.bysone.backend.service.SimulacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/simulaciones")
@Tag(name = "Simulaciones", description = "Proyecciones de inversión")
@RequiredArgsConstructor
public class SimulacionController {

    private final SimulacionService simulacionService;

    @PostMapping("/calcular")
    @Operation(summary = "Calcular proyección sin persistir",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SimulacionCalculadaResponse> calcular(
            @Valid @RequestBody SimulacionRequest request) {
        return ResponseEntity.ok(simulacionService.calcular(request));
    }

    @PostMapping
    @Operation(summary = "Guardar simulación",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SimulacionGuardadaResponse> guardar(
            @Valid @RequestBody SimulacionRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(simulacionService.guardar(request, usuario));
    }

    @GetMapping
    @Operation(summary = "Listar simulaciones del usuario (paginadas)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<SimulacionResumenResponse>> listar(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "fechaSimulacion") Pageable pageable) {
        return ResponseEntity.ok(simulacionService.listar(usuario, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una simulación guardada",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SimulacionGuardadaResponse> detalle(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(simulacionService.detalle(id, usuario));
    }
}
