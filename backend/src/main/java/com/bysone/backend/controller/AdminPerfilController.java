package com.bysone.backend.controller;

import com.bysone.backend.dto.response.PerfilInversionResponse;
import com.bysone.backend.service.AdminPerfilService;
import com.bysone.backend.service.AdminPerfilService.ComposicionItemRequest;
import com.bysone.backend.service.AdminPerfilService.FormulaItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/perfiles")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Perfiles de Inversión", description = "CRUD de perfiles y gestión de composición (solo ADMIN)")
@RequiredArgsConstructor
public class AdminPerfilController {

    private final AdminPerfilService service;

    // ── Consultas ─────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todos los perfiles con su composición",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<PerfilInversionResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un perfil por ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public PerfilInversionResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    // ── CRUD básico ───────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo perfil de inversión",
               security = @SecurityRequirement(name = "bearerAuth"))
    public PerfilInversionResponse crear(@Valid @RequestBody NombreRequest req) {
        return service.crear(req.nombre());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Renombrar un perfil de inversión",
               security = @SecurityRequirement(name = "bearerAuth"))
    public PerfilInversionResponse renombrar(@PathVariable Long id,
                                             @Valid @RequestBody NombreRequest req) {
        return service.renombrar(id, req.nombre());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un perfil (solo si ningún usuario lo tiene asignado)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    // ── Composición (portafolios + porcentaje) ────────────────────────────────

    @PutMapping("/{id}/composicion")
    @Operation(summary = "Reemplazar la composición de portafolios del perfil (porcentajes deben sumar 100%)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public PerfilInversionResponse actualizarComposicion(@PathVariable Long id,
                                                         @RequestBody List<ComposicionItemRequest> items) {
        return service.actualizarComposicion(id, items);
    }

    // ── Fórmulas de exposición ────────────────────────────────────────────────

    @PutMapping("/{id}/formulas")
    @Operation(summary = "Reemplazar las fórmulas de exposición del perfil",
               security = @SecurityRequirement(name = "bearerAuth"))
    public PerfilInversionResponse actualizarFormulas(@PathVariable Long id,
                                                      @RequestBody List<FormulaItemRequest> items) {
        return service.actualizarFormulas(id, items);
    }

    // ── DTO de entrada ────────────────────────────────────────────────────────

    record NombreRequest(
            @NotBlank(message = "El nombre del perfil es obligatorio")
            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombre
    ) {}

    record ComposicionItemRequestBody(
            @NotNull Long idPortafolio,
            @NotNull BigDecimal porcentaje
    ) {}
}
