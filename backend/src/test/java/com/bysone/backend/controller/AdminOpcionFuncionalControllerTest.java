package com.bysone.backend.controller;

import com.bysone.backend.domain.OpcionFuncional;
import com.bysone.backend.domain.RolesXOpcionFuncional;
import com.bysone.backend.repository.OpcionFuncionalRepository;
import com.bysone.backend.repository.RolesXOpcionFuncionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminOpcionFuncionalController — validaciones de negocio")
class AdminOpcionFuncionalControllerTest {

    @Mock OpcionFuncionalRepository opcionRepo;
    @Mock RolesXOpcionFuncionalRepository rxoRepo;
    @InjectMocks AdminOpcionFuncionalController controller;

    private AdminOpcionFuncionalController.OpcionFuncionalRequest req(String nombre) {
        return new AdminOpcionFuncionalController.OpcionFuncionalRequest(nombre);
    }

    @Test
    @DisplayName("Crea opción funcional con nombre único correctamente")
    void creaOpcionUnica() {
        when(opcionRepo.existsByNombreOpcionFuncionalIgnoreCase("VER_REPORTES")).thenReturn(false);
        OpcionFuncional saved = new OpcionFuncional();
        saved.setNombreOpcionFuncional("VER_REPORTES");
        when(opcionRepo.save(any())).thenReturn(saved);

        OpcionFuncional resultado = controller.crear(req("VER_REPORTES"));
        assertThat(resultado.getNombreOpcionFuncional()).isEqualTo("VER_REPORTES");
        verify(opcionRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza creación con nombre duplicado (409)")
    void rechazaNombreDuplicado() {
        when(opcionRepo.existsByNombreOpcionFuncionalIgnoreCase("GESTIONAR_USUARIOS")).thenReturn(true);

        assertThatThrownBy(() -> controller.crear(req("GESTIONAR_USUARIOS")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(opcionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 404 al actualizar opción funcional inexistente")
    void lanza404ActualizarInexistente() {
        when(opcionRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.actualizar(99L, req("NUEVA")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Actualiza opción funcional correctamente")
    void actualizaCorrectamente() {
        OpcionFuncional existing = new OpcionFuncional();
        existing.setNombreOpcionFuncional("VER_REPORTES");
        when(opcionRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(opcionRepo.existsByNombreOpcionFuncionalIgnoreCaseAndIdNot("EXPORTAR_DATOS", 1L)).thenReturn(false);
        when(opcionRepo.save(any())).thenReturn(existing);

        OpcionFuncional resultado = controller.actualizar(1L, req("EXPORTAR_DATOS"));
        assertThat(resultado).isNotNull();
        verify(opcionRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza actualización con nombre que colisiona (409)")
    void rechazaActualizacionConColision() {
        OpcionFuncional existing = new OpcionFuncional();
        existing.setNombreOpcionFuncional("VER_REPORTES");
        when(opcionRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(opcionRepo.existsByNombreOpcionFuncionalIgnoreCaseAndIdNot("GESTIONAR_USUARIOS", 1L)).thenReturn(true);

        assertThatThrownBy(() -> controller.actualizar(1L, req("GESTIONAR_USUARIOS")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    @DisplayName("Lanza 404 al eliminar opción funcional inexistente")
    void lanza404EliminarInexistente() {
        when(opcionRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> controller.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Bloquea eliminación cuando la opción está asignada a roles (409)")
    void bloqueaEliminacionConRoles() {
        when(opcionRepo.existsById(1L)).thenReturn(true);
        RolesXOpcionFuncional rxo = mock(RolesXOpcionFuncional.class);
        when(rxoRepo.findByIdOpcion(1L)).thenReturn(List.of(rxo));

        assertThatThrownBy(() -> controller.eliminar(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(opcionRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Elimina correctamente cuando no está asignada a ningún rol")
    void eliminaCorrectamente() {
        when(opcionRepo.existsById(1L)).thenReturn(true);
        when(rxoRepo.findByIdOpcion(1L)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> controller.eliminar(1L));
        verify(opcionRepo).deleteById(1L);
    }
}
