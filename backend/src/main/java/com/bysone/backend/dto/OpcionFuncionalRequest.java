package com.bysone.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar opciones funcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionFuncionalRequest {

    @NotBlank(message = "El nombre de la opción funcional no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombreOpcionFuncional;
}
