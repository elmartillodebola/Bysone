package com.bysone.backend.service;

import com.bysone.backend.domain.*;
import com.bysone.backend.dto.request.SimulacionRequest;
import com.bysone.backend.dto.response.SimulacionCalculadaResponse;
import com.bysone.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulacionService — motor de cálculo")
class SimulacionServiceTest {

    @Mock PerfilInversionRepository perfilRepo;
    @Mock TipoPlazoRepository tipoPlazoRepo;
    @Mock DisclaimerRepository disclaimerRepo;
    @Mock SimulacionRepository simulacionRepo;

    @InjectMocks SimulacionService simulacionService;

    private PerfilInversion perfilModerado;
    private TipoPlazo tipoPlazoAnual;

    @BeforeEach
    void setUp() {
        // Portafolio con 60% + 40%
        PortafolioInversion p1 = new PortafolioInversion();
        p1.setId(1L);
        p1.setNombrePortafolio("Renta Variable");
        p1.setRentabilidadMinima(new BigDecimal("6.00"));
        p1.setRentabilidadMaxima(new BigDecimal("12.00"));
        p1.setOpciones(List.of());

        PortafolioInversion p2 = new PortafolioInversion();
        p2.setId(2L);
        p2.setNombrePortafolio("Renta Fija");
        p2.setRentabilidadMinima(new BigDecimal("3.00"));
        p2.setRentabilidadMaxima(new BigDecimal("7.00"));
        p2.setOpciones(List.of());

        PerfilPortafolio pp1 = new PerfilPortafolio();
        pp1.setPortafolioInversion(p1);
        pp1.setPorcentaje(new BigDecimal("60.00"));

        PerfilPortafolio pp2 = new PerfilPortafolio();
        pp2.setPortafolioInversion(p2);
        pp2.setPorcentaje(new BigDecimal("40.00"));

        perfilModerado = new PerfilInversion();
        perfilModerado.setId(2L);
        perfilModerado.setNombrePerfil("MODERADO");
        perfilModerado.setPortafolios(List.of(pp1, pp2));
        perfilModerado.setFormulasExposicion(List.of());

        tipoPlazoAnual = new TipoPlazo();
        tipoPlazoAnual.setId(1L);
        tipoPlazoAnual.setNombrePlazo("AÑO");
        tipoPlazoAnual.setFactorConversionDias(365);
    }

    @Test
    @DisplayName("calcular — proyección a 5 años genera 5 períodos")
    void calcular_generaCincoPeriodos() {
        when(perfilRepo.findById(2L)).thenReturn(Optional.of(perfilModerado));
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(tipoPlazoAnual));

        SimulacionRequest req = new SimulacionRequest(
                2L, new BigDecimal("10000000"), new BigDecimal("500000"), 5, 1L, null);

        SimulacionCalculadaResponse resultado = simulacionService.calcular(req);

        assertThat(resultado).isNotNull();
        assertThat(resultado.proyeccion()).hasSize(5);
        assertThat(resultado.nombrePerfilSimulado()).isEqualTo("MODERADO");
    }

    @Test
    @DisplayName("calcular — valor esperado es mayor a la inversión inicial")
    void calcular_valorFinalMayorQueInicial() {
        when(perfilRepo.findById(2L)).thenReturn(Optional.of(perfilModerado));
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(tipoPlazoAnual));

        SimulacionRequest req = new SimulacionRequest(
                2L, new BigDecimal("5000000"), new BigDecimal("0"), 10, 1L, null);

        SimulacionCalculadaResponse resultado = simulacionService.calcular(req);

        BigDecimal valorFinal = resultado.proyeccion().get(9).valorProyectadoEsperado();
        assertThat(valorFinal).isGreaterThan(new BigDecimal("5000000"));
    }

    @Test
    @DisplayName("calcular — mínimo siempre menor o igual al esperado y al máximo")
    void calcular_ordenRentabilidades() {
        when(perfilRepo.findById(2L)).thenReturn(Optional.of(perfilModerado));
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(tipoPlazoAnual));

        SimulacionRequest req = new SimulacionRequest(
                2L, new BigDecimal("1000000"), new BigDecimal("100000"), 3, 1L, null);

        SimulacionCalculadaResponse resultado = simulacionService.calcular(req);

        resultado.proyeccion().forEach(p -> {
            assertThat(p.valorProyectadoMinimo()).isLessThanOrEqualTo(p.valorProyectadoEsperado());
            assertThat(p.valorProyectadoEsperado()).isLessThanOrEqualTo(p.valorProyectadoMaximo());
        });
    }

    @Test
    @DisplayName("calcular — resumen contiene ganancia positiva con aportes")
    void calcular_resumenGananciaPositiva() {
        when(perfilRepo.findById(2L)).thenReturn(Optional.of(perfilModerado));
        when(tipoPlazoRepo.findById(1L)).thenReturn(Optional.of(tipoPlazoAnual));

        SimulacionRequest req = new SimulacionRequest(
                2L, new BigDecimal("2000000"), new BigDecimal("200000"), 5, 1L, null);

        SimulacionCalculadaResponse resultado = simulacionService.calcular(req);

        assertThat(resultado.resumen().gananciaEsperada()).isGreaterThan(BigDecimal.ZERO);
        assertThat(resultado.resumen().rendimientoPorcentualTotal()).isGreaterThan(BigDecimal.ZERO);
    }
}
