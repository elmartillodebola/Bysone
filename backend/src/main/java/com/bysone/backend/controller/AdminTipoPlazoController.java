package com.bysone.backend.controller;

import com.bysone.backend.domain.TipoPlazo;
import com.bysone.backend.repository.SimulacionRepository;
import com.bysone.backend.repository.TipoPlazoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CRUD del catálogo de tipos de plazo (Días, Meses, Trimestres, Años).
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/tipos-plazo")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Tipos de Plazo", description = "Catálogo de unidades de tiempo para simulaciones (solo ADMIN)")
public class AdminTipoPlazoController {

    private final TipoPlazoRepository tipoPlazoRepo;
    private final SimulacionRepository simulacionRepo;

    @GetMapping
    @Operation(summary = "Listar todos los tipos de plazo")
    public List<TipoPlazo> listar() {
        return tipoPlazoRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tipo de plazo por ID")
    public TipoPlazo obtener(@PathVariable Long id) {
        return tipoPlazoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de plazo no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo tipo de plazo")
    public TipoPlazo crear(@Valid @RequestBody TipoPlazoRequest req) {
        if (tipoPlazoRepo.existsByNombrePlazoIgnoreCase(req.nombrePlazo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un tipo de plazo con ese nombre");
        }
        TipoPlazo tp = new TipoPlazo();
        tp.setNombrePlazo(req.nombrePlazo().trim());
        tp.setDescripcion(req.descripcion());
        tp.setFactorConversionDias(req.factorConversionDias());
        return tipoPlazoRepo.save(tp);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo de plazo")
    public TipoPlazo actualizar(@PathVariable Long id, @Valid @RequestBody TipoPlazoRequest req) {
        TipoPlazo tp = tipoPlazoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de plazo no encontrado"));
        if (tipoPlazoRepo.existsByNombrePlazoIgnoreCaseAndIdNot(req.nombrePlazo(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otro tipo de plazo con ese nombre");
        }
        tp.setNombrePlazo(req.nombrePlazo().trim());
        tp.setDescripcion(req.descripcion());
        tp.setFactorConversionDias(req.factorConversionDias());
        return tipoPlazoRepo.save(tp);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un tipo de plazo")
    public void eliminar(@PathVariable Long id) {
        if (!tipoPlazoRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de plazo no encontrado");
        }
        if (simulacionRepo.existsByTipoPlazoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el tipo de plazo está en uso en simulaciones existentes");
        }
        tipoPlazoRepo.deleteById(id);
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record TipoPlazoRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
            String nombrePlazo,

            @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
            String descripcion,

            @NotNull(message = "El factor de conversión a días es obligatorio")
            @Min(value = 1, message = "El factor de conversión debe ser al menos 1 día")
            Integer factorConversionDias
    ) {}
}
