package com.bysone.backend.service;

import com.bysone.backend.domain.*;
import com.bysone.backend.dto.request.RespuestaEncuestaRequest;
import com.bysone.backend.dto.response.*;
import com.bysone.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalibracionService {

    private static final String PARAM_INTERVALO = "INTERVALO_RECALIBRACION_DIAS";
    private static final int INTERVALO_DEFAULT = 365;

    private final PreguntaCalibracionRepository preguntaRepo;
    private final EncuestaCalibracionRepository encuestaRepo;
    private final RespuestaEncuestaCalibracionRepository respuestaRepo;
    private final OpcionRespuestaCalibracionRepository opcionRepo;
    private final PerfilInversionRepository perfilRepo;
    private final ParametroBysoneRepository parametroRepo;

    @Transactional(readOnly = true)
    public List<PreguntaResponse> listarPreguntas() {
        return preguntaRepo.findByActivaTrueOrderByOrdenAsc().stream()
                .map(this::toPreguntaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EncuestaResponse crearEncuesta(Usuario usuario) {
        encuestaRepo.findByUsuarioIdAndEstado(usuario.getId(), "PENDIENTE")
                .ifPresent(enc -> {
                    throw new EncuestaPendienteException(enc, preguntaRepo, respuestaRepo);
                });

        EncuestaCalibracion encuesta = new EncuestaCalibracion();
        encuesta.setUsuario(usuario);
        encuesta.setOrigen("DEMANDA");
        encuesta = encuestaRepo.save(encuesta);

        return toEncuestaResponse(encuesta);
    }

    @Transactional
    public void registrarRespuesta(Long idEncuesta, Usuario usuario, RespuestaEncuestaRequest req) {
        EncuestaCalibracion encuesta = encuestaRepo.findById(idEncuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuesta no encontrada"));

        if (!encuesta.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
        }
        if ("COMPLETADA".equals(encuesta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La encuesta ya fue completada");
        }
        if (respuestaRepo.existsByEncuestaCalibracionIdAndPreguntaCalibracionId(idEncuesta, req.idPregunta())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pregunta ya respondida");
        }

        PreguntaCalibracion pregunta = preguntaRepo.findById(req.idPregunta())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        OpcionRespuestaCalibracion opcion = opcionRepo.findById(req.idOpcionRespuesta())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción no encontrada"));

        RespuestaEncuestaCalibracion respuesta = new RespuestaEncuestaCalibracion();
        respuesta.setEncuestaCalibracion(encuesta);
        respuesta.setPreguntaCalibracion(pregunta);
        respuesta.setOpcionRespuesta(opcion);
        respuestaRepo.save(respuesta);
    }

    @Transactional
    public EncuestaCompletadaResponse completarEncuesta(Long idEncuesta, Usuario usuario) {
        EncuestaCalibracion encuesta = encuestaRepo.findById(idEncuesta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encuesta no encontrada"));

        if (!encuesta.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
        }
        if ("COMPLETADA".equals(encuesta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La encuesta ya fue completada");
        }

        List<RespuestaEncuestaCalibracion> respuestas = respuestaRepo.findByEncuestaCalibracionId(idEncuesta);
        int totalPreguntas = (int) preguntaRepo.countByActivaTrue();
        if (respuestas.size() < totalPreguntas) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Faltan " + (totalPreguntas - respuestas.size()) + " pregunta(s) por responder");
        }

        int puntaje = respuestas.stream()
                .mapToInt(r -> r.getOpcionRespuesta().getPuntaje())
                .sum();

        encuesta.setPuntajeTotal(puntaje);
        encuesta.setEstado("COMPLETADA");

        PerfilInversion perfil = asignarPerfil(puntaje);
        usuario.setPerfilInversion(perfil);
        usuario.setFechaUltimaActualizacionPerfilInversion(java.time.LocalDateTime.now());

        int intervaloDias = parametroRepo.findByNombreParametro(PARAM_INTERVALO)
                .map(p -> Integer.parseInt(p.getValorParametro()))
                .orElse(INTERVALO_DEFAULT);

        String fechaVencimiento = encuesta.getFechaRealizacion()
                .plusDays(intervaloDias)
                .format(DateTimeFormatter.ISO_DATE_TIME);

        return new EncuestaCompletadaResponse(
                encuesta.getId(),
                puntaje,
                "COMPLETADA",
                new PerfilResumenResponse(perfil.getId(), perfil.getNombrePerfil()),
                encuesta.getFechaRealizacion().format(DateTimeFormatter.ISO_DATE_TIME),
                fechaVencimiento
        );
    }

    private PerfilInversion asignarPerfil(int puntaje) {
        List<PerfilInversion> perfiles = perfilRepo.findAll();
        // Conservador: puntaje bajo, Moderado: medio, Agresivo: alto
        // Umbral basado en parámetros del sistema (simplificado: 3 perfiles divididos en tercios)
        int max = perfiles.size() * 3; // estimado simplificado
        if (puntaje <= max / 3) {
            return perfiles.stream().filter(p -> p.getNombrePerfil().equalsIgnoreCase("CONSERVADOR"))
                    .findFirst().orElse(perfiles.get(0));
        } else if (puntaje <= (max * 2) / 3) {
            return perfiles.stream().filter(p -> p.getNombrePerfil().equalsIgnoreCase("MODERADO"))
                    .findFirst().orElse(perfiles.get(1));
        } else {
            return perfiles.stream().filter(p -> p.getNombrePerfil().equalsIgnoreCase("AGRESIVO"))
                    .findFirst().orElse(perfiles.get(perfiles.size() - 1));
        }
    }

    private PreguntaResponse toPreguntaResponse(PreguntaCalibracion p) {
        List<OpcionRespuestaResponse> opciones = p.getOpciones().stream()
                .map(o -> new OpcionRespuestaResponse(o.getId(), o.getTextoOpcion(), o.getOrden()))
                .collect(Collectors.toList());
        return new PreguntaResponse(p.getId(), p.getTextoPregunta(), p.getOrden(), opciones);
    }

    private EncuestaResponse toEncuestaResponse(EncuestaCalibracion e) {
        return new EncuestaResponse(
                e.getId(), e.getOrigen(), e.getEstado(),
                e.getFechaRealizacion().format(DateTimeFormatter.ISO_DATE_TIME),
                null);
    }

    // Excepción interna para encuesta pendiente (409)
    public static class EncuestaPendienteException extends ResponseStatusException {
        private final EncuestaCalibracion encuestaPendiente;

        public EncuestaPendienteException(EncuestaCalibracion enc,
                                          PreguntaCalibracionRepository preguntaRepo,
                                          RespuestaEncuestaCalibracionRepository respuestaRepo) {
            super(HttpStatus.CONFLICT, "SURVEY_ALREADY_PENDING");
            this.encuestaPendiente = enc;
        }

        public EncuestaCalibracion getEncuestaPendiente() { return encuestaPendiente; }
    }
}
