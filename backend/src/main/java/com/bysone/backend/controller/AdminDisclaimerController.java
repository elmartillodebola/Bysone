package com.bysone.backend.controller;

import com.bysone.backend.domain.Disclaimer;
import com.bysone.backend.repository.DisclaimerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD de disclaimers legales.
 * RN-DIS-04: los disclaimers no se eliminan físicamente, se desactivan.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/disclaimers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Disclaimers", description = "Gestión de textos legales (solo ADMIN)")
public class AdminDisclaimerController {

    private final DisclaimerRepository disclaimerRepo;

    @GetMapping
    @Operation(summary = "Listar todos los disclaimers (activos e inactivos)")
    public List<Disclaimer> listar() {
        return disclaimerRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un disclaimer por ID")
    public Disclaimer obtener(@PathVariable Long id) {
        return disclaimerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disclaimer no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo disclaimer")
    public Disclaimer crear(@Valid @RequestBody DisclaimerRequest req) {
        validarFechas(req.fechaVigenciaDesde(), req.fechaVigenciaHasta());
        Disclaimer d = new Disclaimer();
        d.setTitulo(req.titulo());
        d.setContenido(req.contenido());
        d.setActivo(req.activo() != null ? req.activo() : true);
        d.setFechaVigenciaDesde(req.fechaVigenciaDesde());
        d.setFechaVigenciaHasta(req.fechaVigenciaHasta());
        return disclaimerRepo.save(d);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un disclaimer existente")
    public Disclaimer actualizar(@PathVariable Long id, @Valid @RequestBody DisclaimerRequest req) {
        Disclaimer d = disclaimerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disclaimer no encontrado"));
        validarFechas(req.fechaVigenciaDesde(), req.fechaVigenciaHasta());
        d.setTitulo(req.titulo());
        d.setContenido(req.contenido());
        if (req.activo() != null) d.setActivo(req.activo());
        d.setFechaVigenciaDesde(req.fechaVigenciaDesde());
        d.setFechaVigenciaHasta(req.fechaVigenciaHasta());
        return disclaimerRepo.save(d);
    }

    /**
     * RN-DIS-04: los disclaimers no se eliminan, se desactivan.
     * Este endpoint reemplaza el DELETE físico.
     */
    @PatchMapping("/{id}/activo")
    @Operation(summary = "Activar o desactivar un disclaimer (RN-DIS-04 — no se eliminan)")
    public Disclaimer toggleActivo(@PathVariable Long id, @RequestParam boolean activo) {
        Disclaimer d = disclaimerRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disclaimer no encontrado"));
        d.setActivo(activo);
        return disclaimerRepo.save(d);
    }

    // ── Validación de negocio ─────────────────────────────────────────────────

    private void validarFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La fecha de inicio de vigencia no puede ser posterior a la fecha de fin");
        }
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record DisclaimerRequest(
            @NotBlank(message = "El título es obligatorio")
            @Size(max = 200, message = "El título no puede superar 200 caracteres")
            String titulo,

            @NotBlank(message = "El contenido es obligatorio")
            String contenido,

            Boolean activo,

            @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
            LocalDateTime fechaVigenciaDesde,

            LocalDateTime fechaVigenciaHasta
    ) {}
}
