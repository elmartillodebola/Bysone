package com.bysone.backend.repository;

import com.bysone.backend.domain.RolesXOpcionFuncional;
import com.bysone.backend.domain.RolesXOpcionFuncionalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesXOpcionFuncionalRepository extends JpaRepository<RolesXOpcionFuncional, RolesXOpcionFuncionalId> {
    List<RolesXOpcionFuncional> findByIdRol(Long idRol);
    List<RolesXOpcionFuncional> findByIdOpcion(Long idOpcion);
}
