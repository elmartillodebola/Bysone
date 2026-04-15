package com.bysone.backend.controller;

import com.bysone.backend.domain.Disclaimer;
import com.bysone.backend.repository.DisclaimerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDisclaimerController — RN-DIS-04 y validación de fechas")
class AdminDisclaimerControllerTest {

    @Mock DisclaimerRepository disclaimerRepo;
    @InjectMocks AdminDisclaimerController controller;

    @Test
    @DisplayName("Crea disclaimer con fechas válidas")
    void creaDisclaimerValido() {
        var desde = LocalDateTime.now();
        var hasta = desde.plusMonths(6);
        var req = new AdminDisclaimerController.DisclaimerRequest(
                "Aviso legal", "Contenido del aviso", true, desde, hasta);

        Disclaimer saved = new Disclaimer();
        saved.setTitulo("Aviso legal");
        when(disclaimerRepo.save(any())).thenReturn(saved);

        Disclaimer resultado = controller.crear(req);
        assertThat(resultado.getTitulo()).isEqualTo("Aviso legal");
    }

    @Test
    @DisplayName("Rechaza disclaimer cuando fecha inicio > fecha fin")
    void rechazaFechaDesdePosterioreAHasta() {
        var desde = LocalDateTime.now().plusDays(10);
        var hasta = LocalDateTime.now();
        var req = new AdminDisclaimerController.DisclaimerRequest(
                "Aviso", "Contenido", true, desde, hasta);

        assertThatThrownBy(() -> controller.crear(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        verify(disclaimerRepo, never()).save(any());
    }

    @Test
    @DisplayName("Crea disclaimer sin fecha fin (vigencia indefinida)")
    void creaDisclaimerSinFechaHasta() {
        var req = new AdminDisclaimerController.DisclaimerRequest(
                "Aviso", "Contenido", true, LocalDateTime.now(), null);

        when(disclaimerRepo.save(any())).thenReturn(new Disclaimer());
        assertThatNoException().isThrownBy(() -> controller.crear(req));
    }

    @Test
    @DisplayName("RN-DIS-04 — desactiva disclaimer en lugar de eliminar")
    void desactivaEnLugarDeEliminar() {
        Disclaimer d = new Disclaimer();
        d.setActivo(true);
        when(disclaimerRepo.findById(1L)).thenReturn(Optional.of(d));
        when(disclaimerRepo.save(any())).thenReturn(d);

        controller.toggleActivo(1L, false);
        assertThat(d.isActivo()).isFalse();
        verify(disclaimerRepo).save(d);
        // No se llama a deleteById
        verify(disclaimerRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Lanza 404 al desactivar un disclaimer inexistente")
    void lanza404AlDesactivarInexistente() {
        when(disclaimerRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.toggleActivo(99L, false))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }
}
