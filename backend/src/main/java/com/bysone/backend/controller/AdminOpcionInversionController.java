package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionInversion;
import com.bysone.backend.repository.OpcionInversionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

/**
 * CRUD de opciones de inversión (instrumentos financieros individuales).
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/opciones-inversion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Opciones de Inversión", description = "Gestión de instrumentos financieros (solo ADMIN)")
public class AdminOpcionInversionController {

    private final OpcionInversionRepository opcionRepo;

    @GetMapping
    @Operation(summary = "Listar todas las opciones de inversión")
    public List<OpcionInversion> listar() {
        return opcionRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una opción de inversión por ID")
    public OpcionInversion obtener(@PathVariable Long id) {
        return opcionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción de inversión no encontrada"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva opción de inversión")
    public OpcionInversion crear(@Valid @RequestBody OpcionInversionRequest req) {
        validarRentabilidades(req.rentabilidadMinima(), req.rentabilidadMaxima());
        OpcionInversion opcion = new OpcionInversion();
        opcion.setNombreOpcion(req.nombre());
        opcion.setDescripcionOpcion(req.descripcion());
        opcion.setRentabilidadMinima(req.rentabilidadMinima());
        opcion.setRentabilidadMaxima(req.rentabilidadMaxima());
        return opcionRepo.save(opcion);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una opción de inversión")
    public OpcionInversion actualizar(@PathVariable Long id, @Valid @RequestBody OpcionInversionRequest req) {
        OpcionInversion opcion = opcionRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción de inversión no encontrada"));
        validarRentabilidades(req.rentabilidadMinima(), req.rentabilidadMaxima());
        opcion.setNombreOpcion(req.nombre());
        opcion.setDescripcionOpcion(req.descripcion());
        opcion.setRentabilidadMinima(req.rentabilidadMinima());
        opcion.setRentabilidadMaxima(req.rentabilidadMaxima());
        return opcionRepo.save(opcion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una opción de inversión")
    public void eliminar(@PathVariable Long id) {
        if (!opcionRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción de inversión no encontrada");
        }
        opcionRepo.deleteById(id);
    }

    // ── Validación de negocio ─────────────────────────────────────────────────

    private void validarRentabilidades(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La rentabilidad mínima no puede ser mayor que la máxima");
        }
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record OpcionInversionRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
            String nombre,

            @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
            String descripcion,

            @NotNull(message = "La rentabilidad mínima es obligatoria")
            @DecimalMin(value = "0.00", message = "La rentabilidad mínima debe ser mayor o igual a 0")
            BigDecimal rentabilidadMinima,

            @NotNull(message = "La rentabilidad máxima es obligatoria")
            @DecimalMin(value = "0.01", message = "La rentabilidad máxima debe ser mayor a 0")
            BigDecimal rentabilidadMaxima
    ) {}
}
