package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionRespuestaCalibracion;
import com.bysone.backend.domain.PreguntaCalibracion;
import com.bysone.backend.repository.OpcionRespuestaCalibracionRepository;
import com.bysone.backend.repository.PreguntaCalibracionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminPreguntaController — RN-CAL-06 y CA-ORC-05")
class AdminPreguntaControllerTest {

    @Mock PreguntaCalibracionRepository preguntaRepo;
    @Mock OpcionRespuestaCalibracionRepository opcionRepo;
    @InjectMocks AdminPreguntaController controller;

    @Test
    @DisplayName("Crea pregunta activa correctamente")
    void creaPreguntaActiva() {
        var req = new AdminPreguntaController.PreguntaRequest(
                "¿Cuál es su horizonte de inversión?", 1, true);

        PreguntaCalibracion saved = new PreguntaCalibracion();
        saved.setTextoPregunta(req.textoPregunta());
        when(preguntaRepo.save(any())).thenReturn(saved);

        PreguntaCalibracion resultado = controller.crear(req);
        assertThat(resultado.getTextoPregunta()).isEqualTo("¿Cuál es su horizonte de inversión?");
    }

    @Test
    @DisplayName("RN-CAL-06 — toggle activa cambia el estado de la pregunta")
    void toggleActivaCambiaEstado() {
        PreguntaCalibracion pregunta = new PreguntaCalibracion();
        pregunta.setActiva(true);
        when(preguntaRepo.findById(1L)).thenReturn(Optional.of(pregunta));
        when(preguntaRepo.save(any())).thenReturn(pregunta);

        controller.toggleActiva(1L, false);
        assertThat(pregunta.isActiva()).isFalse();
        verify(preguntaRepo).save(pregunta);
    }

    @Test
    @DisplayName("CA-ORC-05 — no puede eliminar opción si la pregunta activa quedaría con menos de 2")
    void rechazaEliminarOpcionSiQuedariaConMenosDeDos() {
        PreguntaCalibracion pregunta = new PreguntaCalibracion();
        pregunta.setId(1L);
        pregunta.setActiva(true);

        OpcionRespuestaCalibracion op1 = new OpcionRespuestaCalibracion();
        op1.setPreguntaCalibracion(pregunta);
        OpcionRespuestaCalibracion op2 = new OpcionRespuestaCalibracion();
        op2.setPreguntaCalibracion(pregunta);
        pregunta.setOpciones(new ArrayList<>(List.of(op1, op2)));

        when(opcionRepo.findById(1L)).thenReturn(Optional.of(op1));
        when(preguntaRepo.findById(1L)).thenReturn(Optional.of(pregunta));

        // La pregunta ya tiene 2 opciones → no se puede eliminar ninguna
        assertThatThrownBy(() -> controller.eliminarOpcion(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        verify(opcionRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("CA-ORC-05 — permite eliminar opción si pregunta tiene más de 2")
    void permiteEliminarOpcionConMasDeDos() {
        PreguntaCalibracion pregunta = new PreguntaCalibracion();
        pregunta.setId(1L);
        pregunta.setActiva(true);

        OpcionRespuestaCalibracion op1 = new OpcionRespuestaCalibracion();
        op1.setPreguntaCalibracion(pregunta);
        OpcionRespuestaCalibracion op2 = new OpcionRespuestaCalibracion();
        op2.setPreguntaCalibracion(pregunta);
        OpcionRespuestaCalibracion op3 = new OpcionRespuestaCalibracion();
        op3.setPreguntaCalibracion(pregunta);
        pregunta.setOpciones(new ArrayList<>(List.of(op1, op2, op3)));

        when(opcionRepo.findById(1L)).thenReturn(Optional.of(op1));
        when(preguntaRepo.findById(1L)).thenReturn(Optional.of(pregunta));

        assertThatNoException().isThrownBy(() -> controller.eliminarOpcion(1L, 1L));
        verify(opcionRepo).deleteById(1L);
    }
}
