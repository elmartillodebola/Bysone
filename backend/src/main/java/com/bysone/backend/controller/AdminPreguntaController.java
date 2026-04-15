package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionRespuestaCalibracion;
import com.bysone.backend.domain.PreguntaCalibracion;
import com.bysone.backend.repository.OpcionRespuestaCalibracionRepository;
import com.bysone.backend.repository.PreguntaCalibracionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CRUD de preguntas de calibración y sus opciones de respuesta.
 * RN-CAL-06: solo las preguntas activas se presentan al usuario.
 * Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/v1/admin/preguntas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin — Preguntas Calibración", description = "Gestión del cuestionario de calibración (solo ADMIN)")
public class AdminPreguntaController {

    private final PreguntaCalibracionRepository preguntaRepo;
    private final OpcionRespuestaCalibracionRepository opcionRepo;

    // ── Preguntas ──────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todas las preguntas (activas e inactivas) con sus opciones")
    public List<PreguntaCalibracion> listar() {
        return preguntaRepo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public PreguntaCalibracion obtener(@PathVariable Long id) {
        return preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva pregunta de calibración")
    public PreguntaCalibracion crear(@Valid @RequestBody PreguntaRequest req) {
        PreguntaCalibracion pregunta = new PreguntaCalibracion();
        pregunta.setTextoPregunta(req.textoPregunta());
        pregunta.setOrden(req.orden());
        pregunta.setActiva(req.activa() != null ? req.activa() : true);
        return preguntaRepo.save(pregunta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta de calibración")
    public PreguntaCalibracion actualizar(@PathVariable Long id, @Valid @RequestBody PreguntaRequest req) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        pregunta.setTextoPregunta(req.textoPregunta());
        pregunta.setOrden(req.orden());
        if (req.activa() != null) pregunta.setActiva(req.activa());
        return preguntaRepo.save(pregunta);
    }

    @PatchMapping("/{id}/activa")
    @Operation(summary = "Activar o desactivar una pregunta (RN-CAL-06)")
    public PreguntaCalibracion toggleActiva(@PathVariable Long id, @RequestParam boolean activa) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        pregunta.setActiva(activa);
        return preguntaRepo.save(pregunta);
    }

    // ── Opciones de respuesta ─────────────────────────────────────────────────

    @PostMapping("/{idPregunta}/opciones")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agregar una opción de respuesta a una pregunta")
    public OpcionRespuestaCalibracion crearOpcion(@PathVariable Long idPregunta,
                                                  @Valid @RequestBody OpcionRequest req) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(idPregunta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));

        OpcionRespuestaCalibracion opcion = new OpcionRespuestaCalibracion();
        opcion.setPreguntaCalibracion(pregunta);
        opcion.setTextoOpcion(req.textoOpcion());
        opcion.setPuntaje(req.puntaje());
        opcion.setOrden(req.orden());
        return opcionRepo.save(opcion);
    }

    @PutMapping("/{idPregunta}/opciones/{idOpcion}")
    @Operation(summary = "Actualizar una opción de respuesta")
    public OpcionRespuestaCalibracion actualizarOpcion(@PathVariable Long idPregunta,
                                                       @PathVariable Long idOpcion,
                                                       @Valid @RequestBody OpcionRequest req) {
        OpcionRespuestaCalibracion opcion = opcionRepo.findById(idOpcion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción no encontrada"));
        if (!opcion.getPreguntaCalibracion().getId().equals(idPregunta)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La opción no pertenece a la pregunta indicada");
        }
        opcion.setTextoOpcion(req.textoOpcion());
        opcion.setPuntaje(req.puntaje());
        opcion.setOrden(req.orden());
        return opcionRepo.save(opcion);
    }

    @DeleteMapping("/{idPregunta}/opciones/{idOpcion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una opción de respuesta (mínimo 2 opciones por pregunta activa)")
    public void eliminarOpcion(@PathVariable Long idPregunta, @PathVariable Long idOpcion) {
        OpcionRespuestaCalibracion opcion = opcionRepo.findById(idOpcion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción no encontrada"));
        if (!opcion.getPreguntaCalibracion().getId().equals(idPregunta)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La opción no pertenece a la pregunta indicada");
        }
        PreguntaCalibracion pregunta = preguntaRepo.findById(idPregunta).orElseThrow();
        // CA-ORC-05: mínimo 2 opciones por pregunta activa
        if (pregunta.isActiva() && pregunta.getOpciones().size() <= 2) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Una pregunta activa debe tener al menos 2 opciones de respuesta");
        }
        opcionRepo.deleteById(idOpcion);
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    record PreguntaRequest(
            @NotBlank(message = "El texto de la pregunta es obligatorio")
            @Size(max = 500, message = "El texto no puede superar 500 caracteres")
            String textoPregunta,

            @NotNull(message = "El orden es obligatorio")
            @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
            Integer orden,

            Boolean activa
    ) {}

    record OpcionRequest(
            @NotBlank(message = "El texto de la opción es obligatorio")
            @Size(max = 300, message = "El texto no puede superar 300 caracteres")
            String textoOpcion,

            @NotNull(message = "El puntaje es obligatorio")
            @Min(value = 1, message = "El puntaje debe ser mayor o igual a 1")
            Integer puntaje,

            @NotNull(message = "El orden es obligatorio")
            @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
            Integer orden
    ) {}
}
