package com.bysone.backend.service;

import com.bysone.backend.domain.OpcionFuncional;
import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.RolesXOpcionFuncional;
import com.bysone.backend.domain.RolesXOpcionFuncionalId;
import com.bysone.backend.dto.RolOpcionResponse;
import com.bysone.backend.repository.OpcionFuncionalRepository;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.RolesXOpcionFuncionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la relación entre Roles y Opciones Funcionales.
 */
@Service
@RequiredArgsConstructor
public class RolesOpcionesService {

    private final RoleRepository roleRepo;
    private final OpcionFuncionalRepository opcionRepo;
    private final RolesXOpcionFuncionalRepository rolesXOpcionRepo;

    /**
     * Obtiene todas las asignaciones de opciones a un rol.
     */
    public List<RolOpcionResponse> obtenerOpcionesDelRol(Long idRol) {
        // Validar que el rol existe
        roleRepo.findById(idRol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        return rolesXOpcionRepo.findByIdRol(idRol)
                .stream()
                .map(rxo -> new RolOpcionResponse(
                        rxo.getIdRol(),
                        rxo.getRol().getNombreRol(),
                        rxo.getIdOpcion(),
                        rxo.getOpcionFuncional().getNombreOpcionFuncional()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Asigna una opción funcional a un rol.
     * Validación CA-RXO-01: No duplicar opción en mismo rol
     */
    public RolOpcionResponse asignarOpcionARole(Long idRol, Long idOpcion) {
        // Validar que el rol existe
        Role rol = roleRepo.findById(idRol)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        // Validar que la opción existe
        OpcionFuncional opcion = opcionRepo.findById(idOpcion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opción funcional no encontrada"));

        // Validar que la asignación no exista (CA-RXO-01)
        RolesXOpcionFuncionalId id = new RolesXOpcionFuncionalId(idRol, idOpcion);
        if (rolesXOpcionRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "La opción ya está asignada a este rol");
        }

        // Crear y guardar la asignación
        RolesXOpcionFuncional asignacion = new RolesXOpcionFuncional(idRol, idOpcion, rol, opcion);
        rolesXOpcionRepo.save(asignacion);

        return new RolOpcionResponse(
                asignacion.getIdRol(),
                rol.getNombreRol(),
                asignacion.getIdOpcion(),
                opcion.getNombreOpcionFuncional()
        );
    }

    /**
     * Desasigna una opción funcional de un rol.
     */
    public void desasignarOpcionDelRol(Long idRol, Long idOpcion) {
        RolesXOpcionFuncionalId id = new RolesXOpcionFuncionalId(idRol, idOpcion);
        if (!rolesXOpcionRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Asignación no encontrada");
        }
        rolesXOpcionRepo.deleteById(id);
    }

    /**
     * Obtiene todas las opciones funcionales disponibles.
     */
    public List<OpcionFuncional> obtenerTodasLasOpciones() {
        return opcionRepo.findAll();
    }

    /**
     * Obtiene todos los roles.
     */
    public List<Role> obtenerTodosLosRoles() {
        return roleRepo.findAll();
    }
}
