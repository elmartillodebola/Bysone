package com.bysone.backend.repository;

import com.bysone.backend.domain.RespuestaEncuestaCalibracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaEncuestaCalibracionRepository extends JpaRepository<RespuestaEncuestaCalibracion, Long> {
    List<RespuestaEncuestaCalibracion> findByEncuestaCalibracionId(Long idEncuesta);
    boolean existsByEncuestaCalibracionIdAndPreguntaCalibracionId(Long idEncuesta, Long idPregunta);
}
