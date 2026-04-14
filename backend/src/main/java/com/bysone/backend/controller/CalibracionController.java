package com.bysone.backend.controller;

import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.request.RespuestaEncuestaRequest;
import com.bysone.backend.dto.response.*;
import com.bysone.backend.service.CalibracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calibracion")
@Tag(name = "Calibración", description = "Encuestas de calibración de perfil de inversión")
@RequiredArgsConstructor
public class CalibracionController {

    private final CalibracionService calibracionService;

    @GetMapping("/preguntas")
    @Operation(summary = "Listar preguntas activas ordenadas",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<PreguntaResponse>> listarPreguntas() {
        return ResponseEntity.ok(calibracionService.listarPreguntas());
    }

    @PostMapping("/encuestas")
    @Operation(summary = "Crear nueva encuesta de calibración",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> crearEncuesta(@AuthenticationPrincipal Usuario usuario) {
        try {
            EncuestaResponse encuesta = calibracionService.crearEncuesta(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(encuesta);
        } catch (CalibracionService.EncuestaPendienteException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new java.util.LinkedHashMap<>() {{
                        put("timestamp", java.time.Instant.now().toString());
                        put("status", 409);
                        put("error", "Conflict");
                        put("code", "SURVEY_ALREADY_PENDING");
                        put("message", "Ya tienes una encuesta pendiente");
                        put("encuestaPendiente", java.util.Map.of(
                                "id", ex.getEncuestaPendiente().getId(),
                                "fechaRealizacion", ex.getEncuestaPendiente().getFechaRealizacion().toString(),
                                "origen", ex.getEncuestaPendiente().getOrigen()
                        ));
                    }}
            );
        }
    }

    @PostMapping("/encuestas/{idEncuesta}/respuestas")
    @Operation(summary = "Registrar respuesta a una pregunta",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> registrarRespuesta(
            @PathVariable Long idEncuesta,
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody RespuestaEncuestaRequest request) {
        calibracionService.registrarRespuesta(idEncuesta, usuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/encuestas/{idEncuesta}/completar")
    @Operation(summary = "Completar encuesta y asignar perfil",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<EncuestaCompletadaResponse> completar(
            @PathVariable Long idEncuesta,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(calibracionService.completarEncuesta(idEncuesta, usuario));
    }
}
