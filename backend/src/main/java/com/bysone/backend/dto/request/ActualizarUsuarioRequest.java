package com.bysone.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        String nombreCompleto,

        @Pattern(regexp = "^[+]?[0-9\\s\\-]{7,20}$",
                 message = "El celular solo puede contener dígitos, espacios, guiones y '+', entre 7 y 20 caracteres")
        @Size(max = 20, message = "El celular no puede superar 20 caracteres")
        String celular
) {}
