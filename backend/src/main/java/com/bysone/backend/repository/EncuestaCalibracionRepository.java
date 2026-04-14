package com.bysone.backend.repository;

import com.bysone.backend.domain.EncuestaCalibracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncuestaCalibracionRepository extends JpaRepository<EncuestaCalibracion, Long> {

    Optional<EncuestaCalibracion> findByUsuarioIdAndEstado(Long idUsuario, String estado);

    boolean existsByUsuarioIdAndEstado(Long idUsuario, String estado);
}
