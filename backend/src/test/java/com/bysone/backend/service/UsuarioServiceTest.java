package com.bysone.backend.service;

import com.bysone.backend.domain.EncuestaCalibracion;
import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.dto.request.ActualizarUsuarioRequest;
import com.bysone.backend.dto.response.UltimaEncuestaResponse;
import com.bysone.backend.dto.response.UsuarioMeResponse;
import com.bysone.backend.repository.EncuestaCalibracionRepository;
import com.bysone.backend.repository.ParametroBysoneRepository;
import com.bysone.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RN-USU-06 — Edición de datos personales (nombre y celular).
 * RN-USU-07 — El correo y proveedor OAuth no son editables.
 * CA-USU-11 — requiereRecalibracion calculado correctamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService — edición de perfil y calibración")
class UsuarioServiceTest {

    @Mock ParametroBysoneRepository parametroRepo;
    @Mock UsuarioRepository usuarioRepo;
    @Mock EncuestaCalibracionRepository encuestaRepo;

    @InjectMocks UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Role rol = new Role();
        rol.setNombreRol("USER");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompletoUsuario("Ana García");
        usuario.setCorreoUsuario("ana@gmail.com");
        usuario.setCelularUsuario(null);
        usuario.setProveedorOauth("GOOGLE");
        usuario.setOauthSub("google-sub-123");
        usuario.setFechaRegistro(LocalDateTime.now().minusDays(10));
        usuario.setRoles(Set.of(rol));
    }

    // ── toMeResponse ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("toMeResponse — sin perfil: requiereRecalibracion = true")
    void sinPerfilRequiereRecalibracion() {
        usuario.setPerfilInversion(null);

        UsuarioMeResponse resp = service.toMeResponse(usuario);

        assertThat(resp.perfilInversion()).isNull();
        assertThat(resp.requiereRecalibracion()).isTrue();
        assertThat(resp.fechaUltimaActualizacionPerfil()).isNull();
    }

    @Test
    @DisplayName("toMeResponse — con perfil reciente: requiereRecalibracion = false")
    void conPerfilRecienteNoRequiereRecalibracion() {
        PerfilInversion perfil = new PerfilInversion();
        perfil.setId(2L);
        perfil.setNombrePerfil("Moderado");
        usuario.setPerfilInversion(perfil);
        usuario.setFechaUltimaActualizacionPerfilInversion(LocalDateTime.now().minusDays(30));

        when(parametroRepo.findByNombreParametro("INTERVALO_RECALIBRACION_DIAS"))
                .thenReturn(Optional.empty()); // usa default 365

        UsuarioMeResponse resp = service.toMeResponse(usuario);

        assertThat(resp.perfilInversion().id()).isEqualTo(2L);
        assertThat(resp.requiereRecalibracion()).isFalse();
        assertThat(resp.fechaUltimaActualizacionPerfil()).isNotNull();
    }

    @Test
    @DisplayName("toMeResponse — perfil con intervalo corto: requiereRecalibracion = true")
    void perfilVencidoRequiereRecalibracion() {
        PerfilInversion perfil = new PerfilInversion();
        perfil.setId(1L);
        perfil.setNombrePerfil("Conservador");
        usuario.setPerfilInversion(perfil);
        usuario.setFechaUltimaActualizacionPerfilInversion(
                LocalDateTime.now().minusDays(200));

        // intervalo configurado en 180 días → 200 días > 180 → vencido
        com.bysone.backend.domain.ParametroBysone param = new com.bysone.backend.domain.ParametroBysone();
        param.setValorParametro("180");
        when(parametroRepo.findByNombreParametro("INTERVALO_RECALIBRACION_DIAS"))
                .thenReturn(Optional.of(param));

        UsuarioMeResponse resp = service.toMeResponse(usuario);
        assertThat(resp.requiereRecalibracion()).isTrue();
    }

    // ── actualizarDatos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarDatos — actualiza nombre y celular correctamente")
    void actualizaNombreYCelular() {
        // usuario sin perfil → calcularRequiereRecalibracion retorna true sin consultar parametroRepo
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest("Ana López", "+573001234567");
        UsuarioMeResponse resp = service.actualizarDatos(usuario, req);

        assertThat(resp.nombreCompleto()).isEqualTo("Ana López");
        assertThat(resp.celular()).isEqualTo("+573001234567");
        assertThat(resp.correo()).isEqualTo("ana@gmail.com"); // no cambia
        verify(usuarioRepo).save(usuario);
    }

    @Test
    @DisplayName("actualizarDatos — celular nulo/vacío se guarda como null (RN-USU-06)")
    void celularVacioSeGuardaComoNull() {
        // usuario sin perfil → calcularRequiereRecalibracion retorna true sin consultar parametroRepo
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest("Ana García", "  ");
        UsuarioMeResponse resp = service.actualizarDatos(usuario, req);

        assertThat(resp.celular()).isNull();
        assertThat(usuario.getCelularUsuario()).isNull();
    }

    @Test
    @DisplayName("actualizarDatos — correo y proveedor OAuth no se modifican (RN-USU-07)")
    void correoYProveedorNoSonEditables() {
        // usuario sin perfil → calcularRequiereRecalibracion retorna true sin consultar parametroRepo
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest("Otro Nombre", null);
        service.actualizarDatos(usuario, req);

        assertThat(usuario.getCorreoUsuario()).isEqualTo("ana@gmail.com");
        assertThat(usuario.getProveedorOauth()).isEqualTo("GOOGLE");
    }

    // ── getUltimaEncuestaCompletada ───────────────────────────────────────────

    @Test
    @DisplayName("getUltimaEncuestaCompletada — retorna Optional vacío cuando no hay encuestas completadas")
    void sinEncuestaCompletadaRetornaVacio() {
        when(encuestaRepo.findTopByUsuarioAndEstadoOrderByFechaRealizacionDesc(usuario, "COMPLETADA"))
                .thenReturn(Optional.empty());

        Optional<UltimaEncuestaResponse> resultado = service.getUltimaEncuestaCompletada(usuario);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("getUltimaEncuestaCompletada — retorna datos de la última encuesta completada")
    void retornaUltimaEncuestaCompletada() {
        PerfilInversion perfil = new PerfilInversion();
        perfil.setId(2L);
        perfil.setNombrePerfil("Moderado");

        EncuestaCalibracion enc = new EncuestaCalibracion();
        enc.setId(10L);
        enc.setEstado("COMPLETADA");
        enc.setPuntajeTotal(4);
        enc.setFechaRealizacion(LocalDateTime.now().minusDays(5));
        enc.setPerfilResultado(perfil);

        when(encuestaRepo.findTopByUsuarioAndEstadoOrderByFechaRealizacionDesc(usuario, "COMPLETADA"))
                .thenReturn(Optional.of(enc));

        Optional<UltimaEncuestaResponse> resultado = service.getUltimaEncuestaCompletada(usuario);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(10L);
        assertThat(resultado.get().puntajeTotal()).isEqualTo(4);
        assertThat(resultado.get().perfilAsignado().nombrePerfil()).isEqualTo("Moderado");
    }
}
