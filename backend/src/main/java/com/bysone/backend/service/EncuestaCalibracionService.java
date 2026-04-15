package com.bysone.backend.service;

import com.bysone.backend.domain.OpcionRespuestaCalibracion;
import com.bysone.backend.domain.PreguntaCalibracion;
import com.bysone.backend.dto.*;
import com.bysone.backend.repository.OpcionRespuestaCalibracionRepository;
import com.bysone.backend.repository.PreguntaCalibracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar preguntas de calibración y sus opciones de respuesta de forma coordinada.
 * 
 * Validaciones:
 * - CA-PRG-01 a CA-PRG-04: preguntas
 * - CA-ORC-01 a CA-ORC-05: opciones de respuesta
 */
@Service
@RequiredArgsConstructor
public class EncuestaCalibracionService {

    private final PreguntaCalibracionRepository preguntaRepo;
    private final OpcionRespuestaCalibracionRepository opcionRepo;

    /**
     * Obtiene todas las preguntas (activas e inactivas) con sus opciones.
     */
    public List<PreguntaCalibracionResponse> obtenerTodasLasPreguntas() {
        return preguntaRepo.findAll()
                .stream()
                .map(this::mapToPreguntaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo las preguntas activas con sus opciones.
     */
    public List<PreguntaCalibracionResponse> obtenerPreguntasActivas() {
        return preguntaRepo.findByActivaTrueOrderByOrdenAsc()
                .stream()
                .map(this::mapToPreguntaResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una pregunta por ID con sus opciones.
     */
    public PreguntaCalibracionResponse obtenerPreguntaPorId(Long id) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        return mapToPreguntaResponse(pregunta);
    }

    /**
     * Crea una nueva pregunta con sus opciones de respuesta.
     * 
     * Validaciones:
     * - CA-PRG-01: texto no vacío, ≤500 caracteres
     * - CA-PRG-02: orden > 0
     * - CA-ORC-05: mínimo 2 opciones
     */
    public PreguntaCalibracionResponse crearPregunta(PreguntaCalibracionRequest req) {
        // Validar orden único (CA-PRG-03)
        boolean ordenExiste = preguntaRepo.findAll()
                .stream()
                .filter(p -> p.isActiva())
                .anyMatch(p -> p.getOrden().equals(req.getOrden()));
        
        if (ordenExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Ya existe una pregunta activa con ese orden (CA-PRG-03)");
        }

        // Crear pregunta
        PreguntaCalibracion pregunta = new PreguntaCalibracion();
        pregunta.setTextoPregunta(req.getTextoPregunta());
        pregunta.setOrden(req.getOrden());
        pregunta.setActiva(req.getActiva());
        
        final PreguntaCalibracion preguntaGuardada = preguntaRepo.save(pregunta);

        // Crear opciones
        List<OpcionRespuestaCalibracion> opciones = req.getOpciones()
                .stream()
                .map(opcionReq -> {
                    OpcionRespuestaCalibracion opcion = new OpcionRespuestaCalibracion();
                    opcion.setPreguntaCalibracion(preguntaGuardada);
                    opcion.setTextoOpcion(opcionReq.getTextoOpcion());
                    opcion.setPuntaje(opcionReq.getPuntaje());
                    opcion.setOrden(opcionReq.getOrden());
                    return opcion;
                })
                .collect(Collectors.toList());

        opciones = opcionRepo.saveAll(opciones);
        preguntaGuardada.setOpciones(opciones);

        return mapToPreguntaResponse(preguntaGuardada);
    }

    /**
     * Actualiza una pregunta y sus opciones.
     * 
     * Validaciones:
     * - CA-PRG-03: orden único entre preguntas activas
     */
    public PreguntaCalibracionResponse actualizarPregunta(Long id, PreguntaCalibracionRequest req) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));

        // Validar orden único si cambió
        if (!pregunta.getOrden().equals(req.getOrden())) {
            boolean ordenExiste = preguntaRepo.findAll()
                    .stream()
                    .filter(p -> p.isActiva() && !p.getId().equals(id))
                    .anyMatch(p -> p.getOrden().equals(req.getOrden()));
            
            if (ordenExiste) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Ya existe una pregunta activa con ese orden (CA-PRG-03)");
            }
        }

        pregunta.setTextoPregunta(req.getTextoPregunta());
        pregunta.setOrden(req.getOrden());
        pregunta.setActiva(req.getActiva());
        pregunta = preguntaRepo.save(pregunta);

        // Eliminar opciones antiguas
        opcionRepo.deleteAll(pregunta.getOpciones());
        pregunta.getOpciones().clear();

        // Crear opciones nuevas
        final PreguntaCalibracion preguntaFinal = pregunta;
        List<OpcionRespuestaCalibracion> opciones = req.getOpciones()
                .stream()
                .map(opcionReq -> {
                    OpcionRespuestaCalibracion opcion = new OpcionRespuestaCalibracion();
                    opcion.setPreguntaCalibracion(preguntaFinal);
                    opcion.setTextoOpcion(opcionReq.getTextoOpcion());
                    opcion.setPuntaje(opcionReq.getPuntaje());
                    opcion.setOrden(opcionReq.getOrden());
                    return opcion;
                })
                .collect(Collectors.toList());

        opciones = opcionRepo.saveAll(opciones);
        pregunta.setOpciones(opciones);

        return mapToPreguntaResponse(pregunta);
    }

    /**
     * Desactiva una pregunta (soft delete, CA-PRG-04).
     */
    public PreguntaCalibracionResponse desactivarPregunta(Long id) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        
        pregunta.setActiva(false);
        pregunta = preguntaRepo.save(pregunta);

        return mapToPreguntaResponse(pregunta);
    }

    /**
     * Activa una pregunta.
     */
    public PreguntaCalibracionResponse activarPregunta(Long id) {
        PreguntaCalibracion pregunta = preguntaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        
        pregunta.setActiva(true);
        pregunta = preguntaRepo.save(pregunta);

        return mapToPreguntaResponse(pregunta);
    }

    // ── Helper ────────────────────────────────────────────────────────────────────

    private PreguntaCalibracionResponse mapToPreguntaResponse(PreguntaCalibracion pregunta) {
        List<OpcionRespuestaResponse> opcionesResponse = pregunta.getOpciones()
                .stream()
                .map(opcion -> new OpcionRespuestaResponse(
                        opcion.getId(),
                        opcion.getTextoOpcion(),
                        opcion.getPuntaje(),
                        opcion.getOrden()
                ))
                .collect(Collectors.toList());

        return new PreguntaCalibracionResponse(
                pregunta.getId(),
                pregunta.getTextoPregunta(),
                pregunta.getOrden(),
                pregunta.isActiva(),
                opcionesResponse
        );
    }
}
