package com.bysone.backend.service;

import com.bysone.backend.domain.ParametroBysone;
import com.bysone.backend.repository.ParametroBysoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * BR-SES-001 — Tiempo de inactividad de sesión.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfiguracionService — BR-SES-001 timeout de inactividad")
class ConfiguracionServiceTest {

    @Mock
    ParametroBysoneRepository parametroRepo;

    @InjectMocks
    ConfiguracionService service;

    @Test
    @DisplayName("Devuelve el valor configurado cuando el parámetro existe y es válido")
    void retornaValorConfigurado() {
        ParametroBysone param = new ParametroBysone();
        param.setNombreParametro(ConfiguracionService.PARAM_TIMEOUT);
        param.setValorParametro("10");

        when(parametroRepo.findByNombreParametro(ConfiguracionService.PARAM_TIMEOUT))
                .thenReturn(Optional.of(param));

        assertThat(service.getTimeoutInactividadMinutos()).isEqualTo(10);
    }

    @Test
    @DisplayName("Devuelve el valor por defecto cuando el parámetro no existe")
    void retornaDefaultCuandoNoExisteParametro() {
        when(parametroRepo.findByNombreParametro(ConfiguracionService.PARAM_TIMEOUT))
                .thenReturn(Optional.empty());

        assertThat(service.getTimeoutInactividadMinutos())
                .isEqualTo(ConfiguracionService.TIMEOUT_DEFAULT);
    }

    @Test
    @DisplayName("Devuelve el valor por defecto cuando el valor del parámetro no es numérico")
    void retornaDefaultCuandoValorNoEsNumerico() {
        ParametroBysone param = new ParametroBysone();
        param.setNombreParametro(ConfiguracionService.PARAM_TIMEOUT);
        param.setValorParametro("no-es-numero");

        when(parametroRepo.findByNombreParametro(ConfiguracionService.PARAM_TIMEOUT))
                .thenReturn(Optional.of(param));

        assertThat(service.getTimeoutInactividadMinutos())
                .isEqualTo(ConfiguracionService.TIMEOUT_DEFAULT);
    }
}
