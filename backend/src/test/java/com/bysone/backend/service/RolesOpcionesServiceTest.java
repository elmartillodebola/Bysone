package com.bysone.backend.service;

import com.bysone.backend.domain.OpcionFuncional;
import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.RolesXOpcionFuncional;
import com.bysone.backend.domain.RolesXOpcionFuncionalId;
import com.bysone.backend.dto.RolOpcionResponse;
import com.bysone.backend.repository.OpcionFuncionalRepository;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.RolesXOpcionFuncionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolesOpcionesServiceTest {

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private OpcionFuncionalRepository opcionRepo;

    @Mock
    private RolesXOpcionFuncionalRepository rolesXOpcionRepo;

    @InjectMocks
    private RolesOpcionesService rolesOpcionesService;

    private Role adminRole;
    private OpcionFuncional opcion;
    private RolesXOpcionFuncional asignacion;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setNombreRol("ADMIN");
        adminRole.setDescripcionRol("Administrador del sistema");

        opcion = new OpcionFuncional();
        opcion.setId(1L);
        opcion.setNombreOpcionFuncional("GESTIONAR_PARAMETROS");

        asignacion = new RolesXOpcionFuncional(1L, 1L, adminRole, opcion);
    }

    /**
     * Test 1: Asignar una opción a un rol exitosamente (CA-RXO-02)
     */
    @Test
    void testAsignarOpcionARole_Success() {
        when(roleRepo.findById(1L)).thenReturn(Optional.of(adminRole));
        when(opcionRepo.findById(1L)).thenReturn(Optional.of(opcion));
        when(rolesXOpcionRepo.existsById(any(RolesXOpcionFuncionalId.class))).thenReturn(false);
        when(rolesXOpcionRepo.save(any(RolesXOpcionFuncional.class))).thenReturn(asignacion);

        RolOpcionResponse resultado = rolesOpcionesService.asignarOpcionARole(1L, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        assertEquals(1L, resultado.getIdOpcion());
        assertEquals("ADMIN", resultado.getNombreRol());
        assertEquals("GESTIONAR_PARAMETROS", resultado.getNombreOpcion());
        verify(rolesXOpcionRepo, times(1)).save(any(RolesXOpcionFuncional.class));
    }

    /**
     * Test 2: Validar que no se puede asignar opción duplicada (CA-RXO-01)
     */
    @Test
    void testAsignarOpcionARole_Duplicada() {
        when(roleRepo.findById(1L)).thenReturn(Optional.of(adminRole));
        when(opcionRepo.findById(1L)).thenReturn(Optional.of(opcion));
        when(rolesXOpcionRepo.existsById(any(RolesXOpcionFuncionalId.class))).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> rolesOpcionesService.asignarOpcionARole(1L, 1L));

        assertEquals("La opción ya está asignada a este rol", exception.getReason());
        verify(rolesXOpcionRepo, never()).save(any(RolesXOpcionFuncional.class));
    }

    /**
     * Test 3: Desasignar una opción de un rol exitosamente
     */
    @Test
    void testDesasignarOpcionDelRol_Success() {
        when(rolesXOpcionRepo.existsById(any(RolesXOpcionFuncionalId.class))).thenReturn(true);

        rolesOpcionesService.desasignarOpcionDelRol(1L, 1L);

        verify(rolesXOpcionRepo, times(1)).deleteById(any(RolesXOpcionFuncionalId.class));
    }

    /**
     * Test 4: Obtener opciones de un rol exitosamente
     */
    @Test
    void testObtenerOpcionesDelRol_Success() {
        when(roleRepo.findById(1L)).thenReturn(Optional.of(adminRole));
        when(rolesXOpcionRepo.findByIdRol(1L)).thenReturn(List.of(asignacion));

        List<RolOpcionResponse> resultado = rolesOpcionesService.obtenerOpcionesDelRol(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("GESTIONAR_PARAMETROS", resultado.get(0).getNombreOpcion());
        verify(roleRepo, times(1)).findById(1L);
    }
}
