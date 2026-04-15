package com.bysone.backend.controller;

import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.response.PerfilResumenResponse;
import com.bysone.backend.dto.response.UsuarioAdminResponse;
import com.bysone.backend.repository.ParametroBysoneRepository;
import com.bysone.backend.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Usuarios", description = "Consulta de usuarios (solo lectura)")
@RequiredArgsConstructor
public class AdminUsuariosController {

    private static final String PARAM_INTERVALO = "INTERVALO_RECALIBRACION_DIAS";
    private static final int INTERVALO_DEFAULT_DIAS = 365;

    private final UsuarioRepository usuarioRepository;
    private final ParametroBysoneRepository parametroRepo;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios registrados",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UsuarioAdminResponse>> listar() {
        int intervaloDias = parametroRepo.findByNombreParametro(PARAM_INTERVALO)
                .map(p -> Integer.parseInt(p.getValorParametro()))
                .orElse(INTERVALO_DEFAULT_DIAS);

        List<UsuarioAdminResponse> usuarios = usuarioRepository.findAll()
                .stream()
                .map(u -> toResponse(u, intervaloDias))
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un usuario por ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioAdminResponse> detalle(@PathVariable Long id) {
        int intervaloDias = parametroRepo.findByNombreParametro(PARAM_INTERVALO)
                .map(p -> Integer.parseInt(p.getValorParametro()))
                .orElse(INTERVALO_DEFAULT_DIAS);

        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(toResponse(u, intervaloDias)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private UsuarioAdminResponse toResponse(Usuario u, int intervaloDias) {
        PerfilInversion perfil = u.getPerfilInversion();
        PerfilResumenResponse perfilRes = perfil != null
                ? new PerfilResumenResponse(perfil.getId(), perfil.getNombrePerfil())
                : null;

        return new UsuarioAdminResponse(
                u.getId(),
                u.getNombreCompletoUsuario(),
                u.getCorreoUsuario(),
                u.getCelularUsuario(),
                u.getProveedorOauth(),
                format(u.getFechaRegistro()),
                format(u.getFechaUltimaActualizacionPerfilInversion()),
                u.getRoles().stream().map(r -> r.getNombreRol()).collect(Collectors.toList()),
                perfilRes,
                calcularRequiere(u, intervaloDias)
        );
    }

    private boolean calcularRequiere(Usuario u, int intervaloDias) {
        if (u.getPerfilInversion() == null) return true;
        if (u.getFechaUltimaActualizacionPerfilInversion() == null) return true;
        LocalDateTime vencimiento = u.getFechaUltimaActualizacionPerfilInversion()
                .plusDays(intervaloDias);
        return LocalDateTime.now().isAfter(vencimiento);
    }

    private String format(LocalDateTime dt) {
        return dt != null ? dt.format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}
