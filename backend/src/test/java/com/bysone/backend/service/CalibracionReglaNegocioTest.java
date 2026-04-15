package com.bysone.backend.service;

import com.bysone.backend.domain.*;
import com.bysone.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BR-CAL-001 — Encuesta retomable: si el usuario recarga la calibración con
 * una encuesta PENDIENTE, el sistema devuelve 409 con el número de preguntas
 * ya respondidas para que el frontend retome desde el paso correcto.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CalibracionService — BR-CAL-001 encuesta retomable")
class CalibracionReglaNegocioTest {

    @Mock PreguntaCalibracionRepository preguntaRepo;
    @Mock EncuestaCalibracionRepository encuestaRepo;
    @Mock RespuestaEncuestaCalibracionRepository respuestaRepo;
    @Mock OpcionRespuestaCalibracionRepository opcionRepo;
    @Mock PerfilInversionRepository perfilRepo;
    @Mock ParametroBysoneRepository parametroRepo;

    @InjectMocks CalibracionService calibracionService;

    private Usuario usuario;
    private EncuestaCalibracion encuestaPendiente;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        encuestaPendiente = new EncuestaCalibracion();
        encuestaPendiente.setId(42L);
        encuestaPendiente.setUsuario(usuario);
        encuestaPendiente.setOrigen("DEMANDA");
        encuestaPendiente.setEstado("PENDIENTE");
    }

    @Test
    @DisplayName("Lanza EncuestaPendienteException (409) cuando ya existe una encuesta PENDIENTE")
    void lanzaExcepcionCuandoExisteEncuestaPendiente() {
        when(encuestaRepo.findByUsuarioIdAndEstado(1L, "PENDIENTE"))
                .thenReturn(Optional.of(encuestaPendiente));
        when(respuestaRepo.countByEncuestaCalibracionId(42L)).thenReturn(2);

        assertThatThrownBy(() -> calibracionService.crearEncuesta(usuario))
                .isInstanceOf(CalibracionService.EncuestaPendienteException.class)
                .satisfies(ex -> {
                    CalibracionService.EncuestaPendienteException epe =
                            (CalibracionService.EncuestaPendienteException) ex;
                    assertThat(epe.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
                    assertThat(epe.getEncuestaPendiente().getId()).isEqualTo(42L);
                    assertThat(epe.getPreguntasRespondidas()).isEqualTo(2);
                });
    }

    @Test
    @DisplayName("EncuestaPendienteException incluye preguntasRespondidas = 0 cuando no hay respuestas aún")
    void preguntasRespondidasEsCeroSinRespuestas() {
        when(encuestaRepo.findByUsuarioIdAndEstado(1L, "PENDIENTE"))
                .thenReturn(Optional.of(encuestaPendiente));
        when(respuestaRepo.countByEncuestaCalibracionId(42L)).thenReturn(0);

        assertThatThrownBy(() -> calibracionService.crearEncuesta(usuario))
                .isInstanceOf(CalibracionService.EncuestaPendienteException.class)
                .satisfies(ex -> {
                    CalibracionService.EncuestaPendienteException epe =
                            (CalibracionService.EncuestaPendienteException) ex;
                    assertThat(epe.getPreguntasRespondidas()).isEqualTo(0);
                });
    }

    @Test
    @DisplayName("Crea nueva encuesta correctamente cuando no hay encuesta PENDIENTE")
    void creaEncuestaCuandoNoHayPendiente() {
        when(encuestaRepo.findByUsuarioIdAndEstado(1L, "PENDIENTE"))
                .thenReturn(Optional.empty());

        EncuestaCalibracion nueva = new EncuestaCalibracion();
        nueva.setId(99L);
        nueva.setUsuario(usuario);
        when(encuestaRepo.save(any(EncuestaCalibracion.class))).thenReturn(nueva);

        assertThatNoException().isThrownBy(() -> calibracionService.crearEncuesta(usuario));
        verify(encuestaRepo).save(any(EncuestaCalibracion.class));
    }
}
