package com.bysone.backend.repository;

import com.bysone.backend.domain.PreguntaCalibracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaCalibracionRepository extends JpaRepository<PreguntaCalibracion, Long> {
    List<PreguntaCalibracion> findByActivaTrueOrderByOrdenAsc();
    long countByActivaTrue();
}
