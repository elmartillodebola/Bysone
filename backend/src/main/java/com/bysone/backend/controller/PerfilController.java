package com.bysone.backend.controller;

import com.bysone.backend.dto.response.PerfilInversionResponse;
import com.bysone.backend.service.PerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfiles")
@Tag(name = "Perfiles", description = "Perfiles de inversión y portafolios")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping
    @Operation(summary = "Listar perfiles de inversión con distribución porcentual",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<PerfilInversionResponse>> listar() {
        return ResponseEntity.ok(perfilService.listarPerfiles());
    }
}
