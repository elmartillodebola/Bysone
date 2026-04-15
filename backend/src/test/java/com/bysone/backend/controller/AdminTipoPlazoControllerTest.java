package com.bysone.backend.controller;

import com.bysone.backend.domain.TipoPlazo;
import com.bysone.backend.repository.SimulacionRepository;
import com.bysone.backend.repository.TipoPlazoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminTipoPlazoController — validaciones de negocio")
class AdminTipoPlazoControllerTest {

    @Mock TipoPlazoRepository tipoPlazoRepo;
    @Mock SimulacionRepository simulacionRepo;
    @InjectMocks AdminTipoPlazoController controller;

    private AdminTipoPlazoController.TipoPlazoRequest req(String nombre, Integer factor) {
        return new AdminTipoPlazoController.TipoPlazoRequest(nombre, null, factor);
    }

    @Test
    @DisplayName("Crea tipo de plazo con nombre único y factor válido")
    void creaTipoPlazoValido() {
        when(tipoPlazoRepo.existsByNombrePlazoIgnoreCase("Meses")).thenReturn(false);
        TipoPlazo saved = new TipoPlazo();
        saved.setNombrePlazo("Meses");
        saved.setFactorConversionDias(30);
        when(tipoPlazoRepo.save(any())).thenReturn(saved);

        TipoPlazo resultado = controller.crear(req("Meses", 30));
        assertThat(resultado.getNombrePlazo()).isEqualTo("Meses");
        verify(tipoPlazoRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza creación con nombre duplicado (409)")
    void rechazaNombreDuplicado() {
        when(tipoPlazoRepo.existsByNombrePlazoIgnoreCase("Meses")).thenReturn(true);

        assertThatThrownBy(() -> controller.crear(req("Meses", 30)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(tipoPlazoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 404 al actualizar un tipo de plazo inexistente")
    void lanza404AlActualizarInexistente() {
        when(tipoPlazoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.actualizar(99L, req("Días", 1)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Actualiza correctamente cuando nombre no colisiona")
    void actualizaCorrectamente() {
        TipoPlazo existing = new TipoPlazo();
        existing.setNombrePlazo("Días");
        existing.setFactorConversionDias(1);
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(tipoPlazoRepo.existsByNombrePlazoIgnoreCaseAndIdNot("Semanas", 1L)).thenReturn(false);
        when(tipoPlazoRepo.save(any())).thenReturn(existing);

        TipoPlazo resultado = controller.actualizar(1L, req("Semanas", 7));
        assertThat(resultado.getFactorConversionDias()).isEqualTo(7);
    }

    @Test
    @DisplayName("Rechaza actualización cuando nuevo nombre colisiona con otro registro (409)")
    void rechazaActualizacionNombreColision() {
        TipoPlazo existing = new TipoPlazo();
        existing.setNombrePlazo("Días");
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(tipoPlazoRepo.existsByNombrePlazoIgnoreCaseAndIdNot("Meses", 1L)).thenReturn(true);

        assertThatThrownBy(() -> controller.actualizar(1L, req("Meses", 30)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    @DisplayName("Lanza 404 al eliminar tipo de plazo inexistente")
    void lanza404AlEliminarInexistente() {
        when(tipoPlazoRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> controller.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Bloquea eliminación cuando tipo de plazo está en uso en simulaciones (409)")
    void bloqueaEliminacionConSimulaciones() {
        when(tipoPlazoRepo.existsById(1L)).thenReturn(true);
        when(simulacionRepo.existsByTipoPlazoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> controller.eliminar(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(tipoPlazoRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Elimina correctamente cuando no hay simulaciones asociadas")
    void eliminaCorrectamente() {
        when(tipoPlazoRepo.existsById(1L)).thenReturn(true);
        when(simulacionRepo.existsByTipoPlazoId(1L)).thenReturn(false);

        assertThatNoException().isThrownBy(() -> controller.eliminar(1L));
        verify(tipoPlazoRepo).deleteById(1L);
    }
}
