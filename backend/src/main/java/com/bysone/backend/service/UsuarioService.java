package com.bysone.backend.service;

import com.bysone.backend.domain.EncuestaCalibracion;
import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.request.ActualizarUsuarioRequest;
import com.bysone.backend.dto.response.PerfilResumenResponse;
import com.bysone.backend.dto.response.UltimaEncuestaResponse;
import com.bysone.backend.dto.response.UsuarioMeResponse;
import com.bysone.backend.repository.EncuestaCalibracionRepository;
import com.bysone.backend.repository.ParametroBysoneRepository;
import com.bysone.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String PARAM_INTERVALO = "INTERVALO_RECALIBRACION_DIAS";
    private static final int INTERVALO_DEFAULT_DIAS = 365;

    private final ParametroBysoneRepository parametroRepo;
    private final UsuarioRepository usuarioRepo;
    private final EncuestaCalibracionRepository encuestaRepo;

    // ── GET /me ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UsuarioMeResponse toMeResponse(Usuario usuario) {
        PerfilInversion perfil = usuario.getPerfilInversion();
        PerfilResumenResponse perfilResumen = perfil != null
                ? new PerfilResumenResponse(perfil.getId(), perfil.getNombrePerfil())
                : null;

        boolean requiereRecalibracion = calcularRequiereRecalibracion(usuario);

        return new UsuarioMeResponse(
                usuario.getId(),
                usuario.getNombreCompletoUsuario(),
                usuario.getCorreoUsuario(),
                usuario.getCelularUsuario(),
                usuario.getProveedorOauth(),
                formatDateTime(usuario.getFechaRegistro()),
                formatDateTime(usuario.getFechaUltimaActualizacionPerfilInversion()),
                usuario.getRoles().stream().map(r -> r.getNombreRol()).collect(Collectors.toList()),
                perfilResumen,
                requiereRecalibracion
        );
    }

    // ── PUT /me ──────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos editables del usuario: nombre y celular.
     * El correo y el proveedor OAuth no son editables (provienen del IdP).
     * RN-USU-06 / CA-USU-12, CA-USU-13.
     */
    @Transactional
    public UsuarioMeResponse actualizarDatos(Usuario usuario, ActualizarUsuarioRequest req) {
        usuario.setNombreCompletoUsuario(req.nombreCompleto().trim());
        usuario.setCelularUsuario(
                req.celular() != null && !req.celular().isBlank() ? req.celular().trim() : null
        );
        usuarioRepo.save(usuario);
        return toMeResponse(usuario);
    }

    // ── GET /me/calibracion ───────────────────────────────────────────────────

    /**
     * Devuelve la última encuesta completada del usuario, o vacío si no existe.
     */
    @Transactional(readOnly = true)
    public Optional<UltimaEncuestaResponse> getUltimaEncuestaCompletada(Usuario usuario) {
        return encuestaRepo
                .findTopByUsuarioAndEstadoOrderByFechaRealizacionDesc(usuario, "COMPLETADA")
                .map(this::toUltimaEncuestaResponse);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean calcularRequiereRecalibracion(Usuario usuario) {
        if (usuario.getPerfilInversion() == null) return true;
        if (usuario.getFechaUltimaActualizacionPerfilInversion() == null) return true;

        int intervaloDias = parametroRepo.findByNombreParametro(PARAM_INTERVALO)
                .map(p -> Integer.parseInt(p.getValorParametro()))
                .orElse(INTERVALO_DEFAULT_DIAS);

        LocalDateTime vencimiento = usuario.getFechaUltimaActualizacionPerfilInversion()
                .plusDays(intervaloDias);
        return LocalDateTime.now().isAfter(vencimiento);
    }

    private UltimaEncuestaResponse toUltimaEncuestaResponse(EncuestaCalibracion enc) {
        PerfilResumenResponse perfilRes = enc.getPerfilResultado() != null
                ? new PerfilResumenResponse(
                        enc.getPerfilResultado().getId(),
                        enc.getPerfilResultado().getNombrePerfil())
                : null;
        return new UltimaEncuestaResponse(
                enc.getId(),
                formatDateTime(enc.getFechaRealizacion()),
                enc.getEstado(),
                enc.getPuntajeTotal(),
                perfilRes
        );
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}
