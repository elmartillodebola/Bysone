package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionInversion;
import com.bysone.backend.repository.OpcionInversionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminOpcionInversionController — validaciones de negocio")
class AdminOpcionInversionControllerTest {

    @Mock OpcionInversionRepository opcionRepo;
    @InjectMocks AdminOpcionInversionController controller;

    @Test
    @DisplayName("Crea opción cuando rentabilidad mínima < máxima")
    void creaOpcionValida() {
        var req = new AdminOpcionInversionController.OpcionInversionRequest(
                "CDT Bancolombia", "Certificado de depósito a término",
                new BigDecimal("3.00"), new BigDecimal("5.00"));

        OpcionInversion saved = new OpcionInversion();
        saved.setNombreOpcion(req.nombre());
        saved.setRentabilidadMinima(req.rentabilidadMinima());
        saved.setRentabilidadMaxima(req.rentabilidadMaxima());
        when(opcionRepo.save(any())).thenReturn(saved);

        OpcionInversion resultado = controller.crear(req);
        assertThat(resultado.getNombreOpcion()).isEqualTo("CDT Bancolombia");
        verify(opcionRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza creación cuando rentabilidad mínima > máxima")
    void rechazaRentabilidadMinMayorQueMax() {
        var req = new AdminOpcionInversionController.OpcionInversionRequest(
                "CDT", null, new BigDecimal("8.00"), new BigDecimal("3.00"));

        assertThatThrownBy(() -> controller.crear(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()));
        verify(opcionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Rechaza creación cuando rentabilidad mínima = máxima (borde válido)")
    void aceptaRentabilidadMinIgualMax() {
        var req = new AdminOpcionInversionController.OpcionInversionRequest(
                "CDT", null, new BigDecimal("5.00"), new BigDecimal("5.00"));

        OpcionInversion saved = new OpcionInversion();
        when(opcionRepo.save(any())).thenReturn(saved);

        assertThatNoException().isThrownBy(() -> controller.crear(req));
    }

    @Test
    @DisplayName("Lanza 404 al actualizar una opción inexistente")
    void lanza404AlActualizarInexistente() {
        when(opcionRepo.findById(99L)).thenReturn(Optional.empty());
        var req = new AdminOpcionInversionController.OpcionInversionRequest(
                "X", null, new BigDecimal("1.00"), new BigDecimal("2.00"));

        assertThatThrownBy(() -> controller.actualizar(99L, req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Lanza 404 al eliminar una opción inexistente")
    void lanza404AlEliminarInexistente() {
        when(opcionRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> controller.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }
}
