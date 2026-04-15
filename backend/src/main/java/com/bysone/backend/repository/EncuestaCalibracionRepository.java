package com.bysone.backend.repository;

import com.bysone.backend.domain.EncuestaCalibracion;
import com.bysone.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncuestaCalibracionRepository extends JpaRepository<EncuestaCalibracion, Long> {

    Optional<EncuestaCalibracion> findByUsuarioIdAndEstado(Long idUsuario, String estado);

    boolean existsByUsuarioIdAndEstado(Long idUsuario, String estado);

    Optional<EncuestaCalibracion> findTopByUsuarioAndEstadoOrderByFechaRealizacionDesc(
            Usuario usuario, String estado);
}
