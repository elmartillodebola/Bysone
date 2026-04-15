package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionInversion;
import com.bysone.backend.domain.PortafolioInversion;
import com.bysone.backend.repository.OpcionInversionRepository;
import com.bysone.backend.repository.PortafolioInversionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminPortafolioController — validaciones de negocio")
class AdminPortafolioControllerTest {

    @Mock PortafolioInversionRepository portafolioRepo;
    @Mock OpcionInversionRepository opcionRepo;
    @InjectMocks AdminPortafolioController controller;

    @Test
    @DisplayName("Crea portafolio válido")
    void creaPortafolioValido() {
        var req = new AdminPortafolioController.PortafolioRequest(
                "Renta Fija", "Portafolio conservador",
                new BigDecimal("3.00"), new BigDecimal("6.00"));

        when(portafolioRepo.existsByNombrePortafolio("Renta Fija")).thenReturn(false);
        PortafolioInversion saved = new PortafolioInversion();
        saved.setNombrePortafolio("Renta Fija");
        when(portafolioRepo.save(any())).thenReturn(saved);

        PortafolioInversion resultado = controller.crear(req);
        assertThat(resultado.getNombrePortafolio()).isEqualTo("Renta Fija");
    }

    @Test
    @DisplayName("Rechaza nombre duplicado")
    void rechazaNombreDuplicado() {
        var req = new AdminPortafolioController.PortafolioRequest(
                "Renta Fija", null,
                new BigDecimal("3.00"), new BigDecimal("6.00"));
        when(portafolioRepo.existsByNombrePortafolio("Renta Fija")).thenReturn(true);

        assertThatThrownBy(() -> controller.crear(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    @DisplayName("Rechaza cuando rentabilidad mínima > máxima")
    void rechazaRentabilidadInvalida() {
        var req = new AdminPortafolioController.PortafolioRequest(
                "Test", null, new BigDecimal("9.00"), new BigDecimal("3.00"));

        assertThatThrownBy(() -> controller.crear(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @Test
    @DisplayName("Asigna opciones de inversión correctamente")
    void asignaOpcionesCorrectamente() {
        PortafolioInversion portafolio = new PortafolioInversion();
        portafolio.setNombrePortafolio("Renta Fija");
        when(portafolioRepo.findById(1L)).thenReturn(Optional.of(portafolio));

        OpcionInversion o1 = new OpcionInversion(); o1.setNombreOpcion("CDT");
        OpcionInversion o2 = new OpcionInversion(); o2.setNombreOpcion("TES");
        when(opcionRepo.findAllById(List.of(1L, 2L))).thenReturn(List.of(o1, o2));
        when(portafolioRepo.save(any())).thenReturn(portafolio);

        controller.asignarOpciones(1L, List.of(1L, 2L));
        assertThat(portafolio.getOpciones()).hasSize(2);
    }

    @Test
    @DisplayName("Rechaza asignación si algún ID de opción no existe")
    void rechazaOpcionesInexistentes() {
        PortafolioInversion portafolio = new PortafolioInversion();
        when(portafolioRepo.findById(1L)).thenReturn(Optional.of(portafolio));
        when(opcionRepo.findAllById(List.of(1L, 99L))).thenReturn(List.of(new OpcionInversion())); // solo 1 de 2

        assertThatThrownBy(() -> controller.asignarOpciones(1L, List.of(1L, 99L)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }
}
