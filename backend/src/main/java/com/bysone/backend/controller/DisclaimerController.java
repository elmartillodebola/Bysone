package com.bysone.backend.controller;

import com.bysone.backend.dto.response.DisclaimerResponse;
import com.bysone.backend.service.DisclaimerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/disclaimers")
@Tag(name = "Disclaimers", description = "Textos legales vigentes")
@RequiredArgsConstructor
public class DisclaimerController {

    private final DisclaimerService disclaimerService;

    @GetMapping("/vigente")
    @Operation(summary = "Obtener disclaimer vigente",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<DisclaimerResponse> vigente() {
        return ResponseEntity.ok(disclaimerService.obtenerVigente());
    }
}
