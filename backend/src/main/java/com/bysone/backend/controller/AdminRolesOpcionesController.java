package com.bysone.backend.controller;

import com.bysone.backend.dto.AsignarOpcionARolRequest;
import com.bysone.backend.dto.RolOpcionResponse;
import com.bysone.backend.domain.OpcionFuncional;
import com.bysone.backend.domain.Role;
import com.bysone.backend.service.RolesOpcionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de Roles × Opciones Funcionales.
 * Permite gestionar qué opciones tiene acceso cada rol.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/roles-opciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Roles × Opciones", description = "Gestión de permisos (solo ADMIN)")
public class AdminRolesOpcionesController {

    private final RolesOpcionesService rolesOpcionesService;

    @GetMapping("/roles")
    @Operation(summary = "Listar todos los roles")
    public List<Role> listarRoles() {
        return rolesOpcionesService.obtenerTodosLosRoles();
    }

    @GetMapping("/opciones")
    @Operation(summary = "Listar todas las opciones funcionales")
    public List<OpcionFuncional> listarOpciones() {
        return rolesOpcionesService.obtenerTodasLasOpciones();
    }

    @GetMapping("/rol/{idRol}")
    @Operation(summary = "Obtener opciones asignadas a un rol")
    public List<RolOpcionResponse> obtenerOpcionesDelRol(@PathVariable Long idRol) {
        return rolesOpcionesService.obtenerOpcionesDelRol(idRol);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Asignar una opción funcional a un rol")
    public RolOpcionResponse asignarOpcion(@Valid @RequestBody AsignarOpcionARolRequest req) {
        return rolesOpcionesService.asignarOpcionARole(req.getIdRol(), req.getIdOpcion());
    }

    @DeleteMapping("/{idRol}/{idOpcion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desasignar una opción funcional de un rol")
    public void desasignarOpcion(@PathVariable Long idRol, @PathVariable Long idOpcion) {
        rolesOpcionesService.desasignarOpcionDelRol(idRol, idOpcion);
    }
}
