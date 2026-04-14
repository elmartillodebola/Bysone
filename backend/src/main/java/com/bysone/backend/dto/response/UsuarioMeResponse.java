package com.bysone.backend.dto.response;

import java.util.List;

public record UsuarioMeResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String celular,
        String proveedorOauth,
        String fechaRegistro,
        List<String> roles,
        PerfilResumenResponse perfilInversion,
        boolean requiereRecalibracion
) {}
