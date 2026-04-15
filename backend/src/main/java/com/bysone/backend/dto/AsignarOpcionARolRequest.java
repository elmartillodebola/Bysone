package com.bysone.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar una opción funcional a un rol.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarOpcionARolRequest {

    @NotNull(message = "El ID del rol no puede ser nulo")
    private Long idRol;

    @NotNull(message = "El ID de la opción no puede ser nulo")
    private Long idOpcion;
}
