package com.bysone.backend.controller;

import com.bysone.backend.domain.Role;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminRolController — validaciones de negocio")
class AdminRolControllerTest {

    @Mock RoleRepository rolRepo;
    @Mock UsuarioRepository usuarioRepo;
    @InjectMocks AdminRolController controller;

    private AdminRolController.RolRequest req(String nombre) {
        return new AdminRolController.RolRequest(nombre, "Descripción de prueba");
    }

    @Test
    @DisplayName("Crea rol con nombre único correctamente")
    void creaRolUnico() {
        when(rolRepo.existsByNombreRolIgnoreCase("TESTER")).thenReturn(false);
        Role saved = new Role();
        saved.setNombreRol("TESTER");
        when(rolRepo.save(any())).thenReturn(saved);

        Role resultado = controller.crear(req("TESTER"));
        assertThat(resultado.getNombreRol()).isEqualTo("TESTER");
        verify(rolRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza creación con nombre duplicado (409)")
    void rechazaNombreDuplicado() {
        when(rolRepo.existsByNombreRolIgnoreCase("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> controller.crear(req("ADMIN")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(rolRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 404 al actualizar rol inexistente")
    void lanza404ActualizarInexistente() {
        when(rolRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.actualizar(99L, req("NUEVO")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Actualiza rol correctamente sin colisión de nombre")
    void actualizaCorrectamente() {
        Role existing = new Role();
        existing.setNombreRol("USER");
        when(rolRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(rolRepo.existsByNombreRolIgnoreCaseAndIdNot("VIEWER", 1L)).thenReturn(false);
        when(rolRepo.save(any())).thenReturn(existing);

        Role resultado = controller.actualizar(1L, req("VIEWER"));
        assertThat(resultado).isNotNull();
        verify(rolRepo).save(any());
    }

    @Test
    @DisplayName("Rechaza actualización con nombre que colisiona con otro rol (409)")
    void rechazaActualizacionConColision() {
        Role existing = new Role();
        existing.setNombreRol("USER");
        when(rolRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(rolRepo.existsByNombreRolIgnoreCaseAndIdNot("ADMIN", 1L)).thenReturn(true);

        assertThatThrownBy(() -> controller.actualizar(1L, req("ADMIN")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
    }

    @Test
    @DisplayName("Lanza 404 al eliminar rol inexistente")
    void lanza404EliminarInexistente() {
        when(rolRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> controller.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @DisplayName("Bloquea eliminación cuando el rol está asignado a usuarios (409)")
    void bloqueaEliminacionConUsuarios() {
        when(rolRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByRolesId(1L)).thenReturn(true);

        assertThatThrownBy(() -> controller.eliminar(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value())
                        .isEqualTo(HttpStatus.CONFLICT.value()));
        verify(rolRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Elimina correctamente cuando no hay usuarios con el rol")
    void eliminaCorrectamente() {
        when(rolRepo.existsById(1L)).thenReturn(true);
        when(usuarioRepo.existsByRolesId(1L)).thenReturn(false);

        assertThatNoException().isThrownBy(() -> controller.eliminar(1L));
        verify(rolRepo).deleteById(1L);
    }
}
