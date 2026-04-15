package com.bysone.backend.dto.response;

import java.util.List;

public record UsuarioAdminResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String celular,
        String proveedorOauth,
        String fechaRegistro,
        String fechaUltimaActualizacionPerfil,
        List<String> roles,
        PerfilResumenResponse perfilInversion,
        boolean requiereRecalibracion
) {}
