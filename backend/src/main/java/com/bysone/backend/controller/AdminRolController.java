package com.bysone.backend.controller;

import com.bysone.backend.domain.Role;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CRUD del catálogo de roles del sistema.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Roles", description = "Gestión del catálogo de roles (solo ADMIN)")
public class AdminRolController {

    private final RoleRepository rolRepo;
    private final UsuarioRepository usuarioRepo;

    @GetMapping
    @Operation(summary = "Listar todos los roles")
    public List<Role> listar() {
        return rolRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un rol por ID")
    public Role obtener(@PathVariable Long id) {
        return rolRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo rol")
    public Role crear(@Valid @RequestBody RolRequest req) {
        if (rolRepo.existsByNombreRolIgnoreCase(req.nombreRol())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un rol con ese nombre");
        }
        Role rol = new Role();
        rol.setNombreRol(req.nombreRol().trim().toUpperCase());
        rol.setDescripcionRol(req.descripcionRol());
        return rolRepo.save(rol);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un rol")
    public Role actualizar(@PathVariable Long id, @Valid @RequestBody RolRequest req) {
        Role rol = rolRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
        if (rolRepo.existsByNombreRolIgnoreCaseAndIdNot(req.nombreRol(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro rol con ese nombre");
        }
        rol.setNombreRol(req.nombreRol().trim().toUpperCase());
        rol.setDescripcionRol(req.descripcionRol());
        return rolRepo.save(rol);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un rol")
    public void eliminar(@PathVariable Long id) {
        if (!rolRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }
        if (usuarioRepo.existsByRolesId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el rol está asignado a uno o más usuarios");
        }
        rolRepo.deleteById(id);
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record RolRequest(
            @NotBlank(message = "El nombre del rol es obligatorio")
            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombreRol,

            @Size(max = 300, message = "La descripción no puede superar 300 caracteres")
            String descripcionRol
    ) {}
}
