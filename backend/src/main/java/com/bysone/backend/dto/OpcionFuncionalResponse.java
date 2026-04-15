package com.bysone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de opciones funcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcionFuncionalResponse {
    private Long id;
    private String nombreOpcionFuncional;
}
