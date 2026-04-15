package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionInversion;
import com.bysone.backend.domain.PortafolioInversion;
import com.bysone.backend.repository.OpcionInversionRepository;
import com.bysone.backend.repository.PortafolioInversionRepository;
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
 * CRUD de portafolios de inversión y gestión de sus opciones asociadas.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/portafolios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Portafolios", description = "Gestión de portafolios de inversión (solo ADMIN)")
public class AdminPortafolioController {

    private final PortafolioInversionRepository portafolioRepo;
    private final OpcionInversionRepository opcionRepo;

    @GetMapping
    @Operation(summary = "Listar todos los portafolios con sus opciones de inversión")
    public List<PortafolioInversion> listar() {
        return portafolioRepo.findAllWithOpciones();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un portafolio por ID")
    public PortafolioInversion obtener(@PathVariable Long id) {
        return portafolioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portafolio no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo portafolio")
    public PortafolioInversion crear(@Valid @RequestBody PortafolioRequest req) {
        validarRentabilidades(req.rentabilidadMinima(), req.rentabilidadMaxima());
        if (portafolioRepo.existsByNombrePortafolio(req.nombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un portafolio con ese nombre");
        }
        PortafolioInversion portafolio = new PortafolioInversion();
        portafolio.setNombrePortafolio(req.nombre());
        portafolio.setDescripcion(req.descripcion());
        portafolio.setRentabilidadMinima(req.rentabilidadMinima());
        portafolio.setRentabilidadMaxima(req.rentabilidadMaxima());
        return portafolioRepo.save(portafolio);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un portafolio")
    public PortafolioInversion actualizar(@PathVariable Long id, @Valid @RequestBody PortafolioRequest req) {
        PortafolioInversion portafolio = portafolioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portafolio no encontrado"));
        validarRentabilidades(req.rentabilidadMinima(), req.rentabilidadMaxima());
        portafolio.setNombrePortafolio(req.nombre());
        portafolio.setDescripcion(req.descripcion());
        portafolio.setRentabilidadMinima(req.rentabilidadMinima());
        portafolio.setRentabilidadMaxima(req.rentabilidadMaxima());
        return portafolioRepo.save(portafolio);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un portafolio")
    public void eliminar(@PathVariable Long id) {
        if (!portafolioRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Portafolio no encontrado");
        }
        portafolioRepo.deleteById(id);
    }

    /** Reemplaza completamente las opciones de inversión asociadas al portafolio. */
    @PutMapping("/{id}/opciones")
    @Operation(summary = "Asignar opciones de inversión a un portafolio (reemplaza las existentes)")
    public PortafolioInversion asignarOpciones(@PathVariable Long id,
                                               @RequestBody List<Long> idsOpciones) {
        PortafolioInversion portafolio = portafolioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portafolio no encontrado"));
        List<OpcionInversion> opciones = opcionRepo.findAllById(idsOpciones);
        if (opciones.size() != idsOpciones.size()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Una o más opciones de inversión no existen");
        }
        portafolio.setOpciones(opciones);
        return portafolioRepo.save(portafolio);
    }

    // ── Validación de negocio ─────────────────────────────────────────────────

    private void validarRentabilidades(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La rentabilidad mínima no puede ser mayor que la máxima");
        }
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record PortafolioRequest(
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
