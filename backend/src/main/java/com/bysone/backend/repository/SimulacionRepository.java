package com.bysone.backend.repository;

import com.bysone.backend.domain.Simulacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimulacionRepository extends JpaRepository<Simulacion, Long> {
    Page<Simulacion> findByUsuarioId(Long idUsuario, Pageable pageable);
    Optional<Simulacion> findByIdAndUsuarioId(Long id, Long idUsuario);
}
