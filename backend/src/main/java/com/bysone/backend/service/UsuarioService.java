package com.bysone.backend.service;

import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.response.PerfilResumenResponse;
import com.bysone.backend.dto.response.UsuarioMeResponse;
import com.bysone.backend.repository.ParametroBysoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String PARAM_INTERVALO = "INTERVALO_RECALIBRACION_DIAS";
    private static final int INTERVALO_DEFAULT_DIAS = 365;

    private final ParametroBysoneRepository parametroRepo;

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
                usuario.getRoles().stream().map(r -> r.getNombreRol()).collect(Collectors.toList()),
                perfilResumen,
                requiereRecalibracion
        );
    }

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

    private String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}
