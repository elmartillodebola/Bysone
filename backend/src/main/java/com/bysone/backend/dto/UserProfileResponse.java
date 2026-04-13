package com.bysone.backend.dto;

import java.util.Set;

/**
 * Payload returned by GET /api/auth/me
 */
public record UserProfileResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String proveedorOauth,
        Set<String> roles
) {}

