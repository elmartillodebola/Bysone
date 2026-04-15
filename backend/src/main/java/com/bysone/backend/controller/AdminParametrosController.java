package com.bysone.backend.controller;

import com.bysone.backend.domain.ParametroBysone;
import com.bysone.backend.repository.ParametroBysoneRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * RN-PAR-01 / GESTIONAR_PARAMETROS — Administración de parámetros del sistema.
 * Solo accesible para usuarios con rol ADMIN (/api/v1/admin/**).
 */
@RestController
@RequestMapping("/api/v1/admin/parametros")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Parámetros", description = "Gestión de parámetros del sistema (solo ADMIN)")
public class AdminParametrosController {

    private final ParametroBysoneRepository parametroRepo;

    @GetMapping
    @Operation(summary = "Listar todos los parámetros del sistema")
    public List<ParametroBysone> listar() {
        return parametroRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un parámetro por ID")
    public ParametroBysone obtener(@PathVariable Long id) {
        return parametroRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parámetro no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo parámetro del sistema")
    public ParametroBysone crear(@RequestBody ParametroRequest req) {
        if (parametroRepo.findByNombreParametro(req.nombreParametro()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un parámetro con el nombre: " + req.nombreParametro());
        }
        ParametroBysone nuevo = new ParametroBysone();
        nuevo.setNombreParametro(req.nombreParametro().toUpperCase().replace(" ", "_"));
        nuevo.setValorParametro(req.valorParametro());
        return parametroRepo.save(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar el valor de un parámetro existente")
    public ParametroBysone actualizar(@PathVariable Long id, @RequestBody ParametroRequest req) {
        ParametroBysone param = parametroRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parámetro no encontrado"));
        param.setValorParametro(req.valorParametro());
        return parametroRepo.save(param);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar un parámetro por nombre")
    public ResponseEntity<ParametroBysone> buscarPorNombre(@RequestParam String nombre) {
        return parametroRepo.findByNombreParametro(nombre.toUpperCase())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    record ParametroRequest(String nombreParametro, String valorParametro) {}
}
