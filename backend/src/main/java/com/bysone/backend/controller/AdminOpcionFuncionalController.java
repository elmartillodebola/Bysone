package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionFuncional;
import com.bysone.backend.repository.OpcionFuncionalRepository;
import com.bysone.backend.repository.RolesXOpcionFuncionalRepository;
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
 * CRUD del catálogo de opciones funcionales del sistema.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/opciones-funcionales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Opciones Funcionales", description = "Gestión del catálogo de opciones funcionales (solo ADMIN)")
public class AdminOpcionFuncionalController {

    private final OpcionFuncionalRepository opcionRepo;
    private final RolesXOpcionFuncionalRepository rxoRepo;

    @GetMapping
    @Operation(summary = "Listar todas las opciones funcionales")
    public List<OpcionFuncional> listar() {
        return opcionRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una opción funcional por ID")
    public OpcionFuncional obtener(@PathVariable Long id) {
        return opcionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción funcional no encontrada"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva opción funcional")
    public OpcionFuncional crear(@Valid @RequestBody OpcionFuncionalRequest req) {
        if (opcionRepo.existsByNombreOpcionFuncionalIgnoreCase(req.nombreOpcionFuncional())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una opción funcional con ese nombre");
        }
        OpcionFuncional opcion = new OpcionFuncional();
        opcion.setNombreOpcionFuncional(req.nombreOpcionFuncional().trim().toUpperCase());
        return opcionRepo.save(opcion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una opción funcional")
    public OpcionFuncional actualizar(@PathVariable Long id, @Valid @RequestBody OpcionFuncionalRequest req) {
        OpcionFuncional opcion = opcionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción funcional no encontrada"));
        if (opcionRepo.existsByNombreOpcionFuncionalIgnoreCaseAndIdNot(req.nombreOpcionFuncional(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otra opción funcional con ese nombre");
        }
        opcion.setNombreOpcionFuncional(req.nombreOpcionFuncional().trim().toUpperCase());
        return opcionRepo.save(opcion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una opción funcional")
    public void eliminar(@PathVariable Long id) {
        if (!opcionRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción funcional no encontrada");
        }
        if (!rxoRepo.findByIdOpcion(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la opción está asignada a uno o más roles");
        }
        opcionRepo.deleteById(id);
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record OpcionFuncionalRequest(
            @NotBlank(message = "El nombre de la opción es obligatorio")
            @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
            String nombreOpcionFuncional
    ) {}
}
