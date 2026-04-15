package com.bysone.backend.controller;

import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.request.ActualizarUsuarioRequest;
import com.bysone.backend.dto.response.UltimaEncuestaResponse;
import com.bysone.backend.dto.response.UsuarioMeResponse;
import com.bysone.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Perfil del usuario autenticado")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioMeResponse> me(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.toMeResponse(usuario));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar datos editables del usuario autenticado (nombre y celular)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioMeResponse> actualizarMe(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarDatos(usuario, request));
    }

    @GetMapping("/me/calibracion")
    @Operation(summary = "Última encuesta de calibración completada del usuario",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UltimaEncuestaResponse> ultimaCalibracion(
            @AuthenticationPrincipal Usuario usuario) {
        return usuarioService.getUltimaEncuestaCompletada(usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
