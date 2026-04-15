package com.bysone.backend.service;

import com.bysone.backend.repository.ParametroBysoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BR-SES-001 — Tiempo de inactividad de sesión.
 *
 * <p>El sistema expone el umbral de inactividad configurado en {@code parametros_bysone}
 * bajo la clave {@code TIMEOUT_SESION_INACTIVIDAD_MINUTOS}.  El frontend lo lee al
 * iniciar y activa un temporizador que se reinicia con cualquier evento del usuario
 * (clic, tecla, scroll, etc.).  Al cumplirse el umbral sin actividad, el token JWT
 * se elimina del almacenamiento local y se obliga al usuario a iniciar sesión de nuevo.
 *
 * <p>El valor por defecto es 5 minutos.  Un administrador puede cambiarlo directamente
 * en la tabla {@code parametros_bysone} sin redespliegue.
 */
@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    static final String PARAM_TIMEOUT = "TIMEOUT_SESION_INACTIVIDAD_MINUTOS";
    static final int TIMEOUT_DEFAULT  = 5;

    private final ParametroBysoneRepository parametroRepo;

    /** Devuelve el tiempo de inactividad de sesión en minutos. */
    @Transactional(readOnly = true)
    public int getTimeoutInactividadMinutos() {
        return parametroRepo.findByNombreParametro(PARAM_TIMEOUT)
                .map(p -> {
                    try { return Integer.parseInt(p.getValorParametro()); }
                    catch (NumberFormatException e) { return TIMEOUT_DEFAULT; }
                })
                .orElse(TIMEOUT_DEFAULT);
    }
}
