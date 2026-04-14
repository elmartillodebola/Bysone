package com.bysone.backend.controller;

import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.response.UsuarioMeResponse;
import com.bysone.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
