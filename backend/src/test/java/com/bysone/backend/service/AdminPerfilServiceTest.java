package com.bysone.backend.service;

import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.PerfilPortafolio;
import com.bysone.backend.domain.PortafolioInversion;
import com.bysone.backend.dto.response.PerfilInversionResponse;
import com.bysone.backend.repository.FormulaExposicionRepository;
import com.bysone.backend.repository.PerfilInversionRepository;
import com.bysone.backend.repository.PerfilPortafolioRepository;
import com.bysone.backend.repository.PortafolioInversionRepository;
import com.bysone.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AdminPerfilService.
 * Cubre: CRUD de perfiles + gestión de composición de portafolios.
 */
@ExtendWith(MockitoExtension.class)
class AdminPerfilServiceTest {

    @Mock PerfilInversionRepository perfilRepo;
    @Mock PerfilPortafolioRepository perfilPortafolioRepo;
    @Mock FormulaExposicionRepository formulaRepo;
    @Mock PortafolioInversionRepository portafolioRepo;
    @Mock UsuarioRepository usuarioRepo;

    @InjectMocks AdminPerfilService service;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PerfilInversion perfilVacio(Long id, String nombre) {
        PerfilInversion p = new PerfilInversion();
        p.setId(id);
        p.setNombrePerfil(nombre);
        p.setPortafolios(new ArrayList<>());
        p.setFormulasExposicion(new ArrayList<>());
        return p;
    }

    private PortafolioInversion portafolioSimple(Long id) {
        PortafolioInversion p = new PortafolioInversion();
        p.setId(id);
        p.setNombrePortafolio("Portafolio " + id);
        p.setRentabilidadMinima(new BigDecimal("3.00"));
        p.setRentabilidadMaxima(new BigDecimal("8.00"));
        p.setOpciones(new ArrayList<>());
        return p;
    }

    // ── crear ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear — nombre único crea el perfil correctamente")
    void crear_nombreUnico_creaCorrectamente() {
        when(perfilRepo.existsByNombrePerfilIgnoreCase("Agresivo")).thenReturn(false);
        PerfilInversion guardado = perfilVacio(1L, "Agresivo");
        when(perfilRepo.save(any())).thenReturn(guardado);

        PerfilInversionResponse resp = service.crear("Agresivo");

        assertThat(resp.nombrePerfil()).isEqualTo("Agresivo");
        verify(perfilRepo).save(any(PerfilInversion.class));
    }

    @Test
    @DisplayName("crear — nombre duplicado lanza CONFLICT 409")
    void crear_nombreDuplicado_lanzaConflict() {
        when(perfilRepo.existsByNombrePerfilIgnoreCase("Conservador")).thenReturn(true);

        assertThatThrownBy(() -> service.crear("Conservador"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe un perfil");
    }

    @Test
    @DisplayName("crear — nombre en blanco lanza BAD_REQUEST 400")
    void crear_nombreBlanco_lanzaBadRequest() {
        assertThatThrownBy(() -> service.crear("   "))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("obligatorio");
    }

    // ── renombrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("renombrar — mismo nombre (case insensitive) no verifica unicidad")
    void renombrar_mismoNombre_noVerificaUnicidad() {
        PerfilInversion perfil = perfilVacio(2L, "Moderado");
        when(perfilRepo.findWithAllById(2L)).thenReturn(Optional.of(perfil));
        when(perfilRepo.save(any())).thenReturn(perfil);
        when(perfilRepo.findWithAllById(2L)).thenReturn(Optional.of(perfil));

        assertThatNoException().isThrownBy(() -> service.renombrar(2L, "MODERADO"));
        verify(perfilRepo, never()).existsByNombrePerfilIgnoreCase(argThat(s -> s.equalsIgnoreCase("moderado") == false));
    }

    @Test
    @DisplayName("renombrar — perfil inexistente lanza NOT_FOUND 404")
    void renombrar_perfilInexistente_lanzaNotFound() {
        when(perfilRepo.findWithAllById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renombrar(99L, "Nuevo"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrado");
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar — perfil sin usuarios asignados se elimina correctamente")
    void eliminar_sinUsuarios_eliminaCorrectamente() {
        when(perfilRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByPerfilInversionId(1L)).thenReturn(false);

        assertThatNoException().isThrownBy(() -> service.eliminar(1L));

        verify(formulaRepo).deleteByPerfilId(1L);
        verify(perfilPortafolioRepo).deleteByPerfilId(1L);
        verify(perfilRepo).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar — perfil con usuarios asignados lanza CONFLICT 409")
    void eliminar_conUsuariosAsignados_lanzaConflict() {
        when(perfilRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByPerfilInversionId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarios con este perfil");
    }

    @Test
    @DisplayName("eliminar — perfil inexistente lanza NOT_FOUND 404")
    void eliminar_perfilInexistente_lanzaNotFound() {
        when(perfilRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrado");
    }

    // ── actualizarComposicion ─────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarComposicion — porcentajes suman 100 se guarda correctamente")
    void actualizarComposicion_suma100_guardaCorrectamente() {
        PerfilInversion perfil = perfilVacio(1L, "Agresivo");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));
        when(portafolioRepo.findById(10L)).thenReturn(Optional.of(portafolioSimple(10L)));
        when(portafolioRepo.findById(20L)).thenReturn(Optional.of(portafolioSimple(20L)));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("60.00")),
                new AdminPerfilService.ComposicionItemRequest(20L, new BigDecimal("40.00"))
        );

        service.actualizarComposicion(1L, items);

        verify(perfilPortafolioRepo).deleteByPerfilId(1L);
        verify(perfilPortafolioRepo, times(2)).save(any(PerfilPortafolio.class));
    }

    @Test
    @DisplayName("actualizarComposicion — porcentajes no suman 100 lanza UNPROCESSABLE_ENTITY 422")
    void actualizarComposicion_noSuma100_lanzaUnprocessable() {
        PerfilInversion perfil = perfilVacio(1L, "Agresivo");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("60.00")),
                new AdminPerfilService.ComposicionItemRequest(20L, new BigDecimal("30.00"))
        );

        assertThatThrownBy(() -> service.actualizarComposicion(1L, items))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("sumar exactamente 100%");
    }

    @Test
    @DisplayName("actualizarComposicion — portafolio inexistente lanza UNPROCESSABLE_ENTITY 422")
    void actualizarComposicion_portafolioNoExiste_lanzaUnprocessable() {
        PerfilInversion perfil = perfilVacio(1L, "Agresivo");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));
        when(portafolioRepo.findById(99L)).thenReturn(Optional.empty());

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(99L, new BigDecimal("100.00"))
        );

        assertThatThrownBy(() -> service.actualizarComposicion(1L, items))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Portafolio no encontrado");
    }

    // ── actualizarComposicion — validación de fórmulas de exposición ──────────

    @Test
    @DisplayName("actualizarComposicion — porcentaje dentro del umbral definido se guarda correctamente")
    void actualizarComposicion_porcentajeDentroUmbral_guardaCorrectamente() {
        // portafolio 10: 50% (fórmula 30-70% → válido), portafolio 20: 50% (sin fórmula)
        PerfilInversion perfil = perfilVacio(1L, "Moderado");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));

        var formula = new com.bysone.backend.domain.FormulaExposicion();
        formula.setUmbralPorcentajeMin(new BigDecimal("30.00"));
        formula.setUmbralPorcentajeMax(new BigDecimal("70.00"));
        when(formulaRepo.findByPerfilIdAndPortafolioId(1L, 10L)).thenReturn(Optional.of(formula));
        when(formulaRepo.findByPerfilIdAndPortafolioId(1L, 20L)).thenReturn(Optional.empty());
        when(portafolioRepo.findById(10L)).thenReturn(Optional.of(portafolioSimple(10L)));
        when(portafolioRepo.findById(20L)).thenReturn(Optional.of(portafolioSimple(20L)));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("50.00")),
                new AdminPerfilService.ComposicionItemRequest(20L, new BigDecimal("50.00"))
        );

        service.actualizarComposicion(1L, items);
        verify(perfilPortafolioRepo, times(2)).save(any(PerfilPortafolio.class));
    }

    @Test
    @DisplayName("actualizarComposicion — porcentaje menor al umbral mínimo lanza UNPROCESSABLE_ENTITY 422")
    void actualizarComposicion_porcentajeBajoUmbralMin_lanzaUnprocessable() {
        // portafolio 10: 20% (fórmula 30-70% → viola mínimo), portafolio 20: 80%
        PerfilInversion perfil = perfilVacio(1L, "Moderado");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));

        var formula = new com.bysone.backend.domain.FormulaExposicion();
        formula.setUmbralPorcentajeMin(new BigDecimal("30.00"));
        formula.setUmbralPorcentajeMax(new BigDecimal("70.00"));
        when(formulaRepo.findByPerfilIdAndPortafolioId(1L, 10L)).thenReturn(Optional.of(formula));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("20.00")),
                new AdminPerfilService.ComposicionItemRequest(20L, new BigDecimal("80.00"))
        );

        assertThatThrownBy(() -> service.actualizarComposicion(1L, items))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("viola la fórmula de exposición");
        verify(perfilPortafolioRepo, never()).deleteByPerfilId(any());
    }

    @Test
    @DisplayName("actualizarComposicion — porcentaje mayor al umbral máximo lanza UNPROCESSABLE_ENTITY 422")
    void actualizarComposicion_porcentajeSobreUmbralMax_lanzaUnprocessable() {
        // portafolio 10: 80% (fórmula 30-70% → viola máximo), portafolio 20: 20%
        PerfilInversion perfil = perfilVacio(1L, "Moderado");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));

        var formula = new com.bysone.backend.domain.FormulaExposicion();
        formula.setUmbralPorcentajeMin(new BigDecimal("30.00"));
        formula.setUmbralPorcentajeMax(new BigDecimal("70.00"));
        when(formulaRepo.findByPerfilIdAndPortafolioId(1L, 10L)).thenReturn(Optional.of(formula));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("80.00")),
                new AdminPerfilService.ComposicionItemRequest(20L, new BigDecimal("20.00"))
        );

        assertThatThrownBy(() -> service.actualizarComposicion(1L, items))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("viola la fórmula de exposición");
        verify(perfilPortafolioRepo, never()).deleteByPerfilId(any());
    }

    @Test
    @DisplayName("actualizarComposicion — sin fórmula definida no aplica restricción de umbral")
    void actualizarComposicion_sinFormula_noAplicaRestriccion() {
        PerfilInversion perfil = perfilVacio(1L, "Agresivo");
        when(perfilRepo.findWithAllById(1L)).thenReturn(Optional.of(perfil));
        when(formulaRepo.findByPerfilIdAndPortafolioId(1L, 10L)).thenReturn(Optional.empty());
        when(portafolioRepo.findById(10L)).thenReturn(Optional.of(portafolioSimple(10L)));

        List<AdminPerfilService.ComposicionItemRequest> items = List.of(
                new AdminPerfilService.ComposicionItemRequest(10L, new BigDecimal("100.00"))
        );

        assertThatNoException().isThrownBy(() -> service.actualizarComposicion(1L, items));
    }
}
