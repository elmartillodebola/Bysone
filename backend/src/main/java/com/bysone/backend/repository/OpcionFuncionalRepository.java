package com.bysone.backend.repository;

import com.bysone.backend.domain.OpcionFuncional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OpcionFuncionalRepository extends JpaRepository<OpcionFuncional, Long> {
    Optional<OpcionFuncional> findByNombreOpcionFuncional(String nombre);
    boolean existsByNombreOpcionFuncionalIgnoreCase(String nombre);
    boolean existsByNombreOpcionFuncionalIgnoreCaseAndIdNot(String nombre, Long id);
}
