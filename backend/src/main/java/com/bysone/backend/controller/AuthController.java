package com.bysone.backend.controller;

import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication & current-user endpoints")
public class AuthController {

    /**
     * Returns the profile of the currently authenticated user.
     * Any authenticated user can call this endpoint.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal Usuario usuario) {
        var profile = new UserProfileResponse(
                usuario.getId(),
                usuario.getNombreCompletoUsuario(),
                usuario.getCorreoUsuario(),
                usuario.getProveedorOauth(),
                usuario.getRoles().stream()
                        .map(Role::getNombreRol)
                        .collect(Collectors.toSet())
        );
        return ResponseEntity.ok(profile);
    }

    /**
     * Admin-only: list all users.
     * (Minimal stub — wire a UserService here when needed.)
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: list users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Admin access granted");
    }

    /**
     * Maintainer + Admin: access maintainer area.
     */
    @GetMapping("/maintainer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAINTAINER')")
    @Operation(summary = "Maintainer area", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> maintainerArea() {
        return ResponseEntity.ok("Maintainer access granted");
    }
}

