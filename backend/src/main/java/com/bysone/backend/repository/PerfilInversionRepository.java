package com.bysone.backend.repository;

import com.bysone.backend.domain.PerfilInversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfilInversionRepository extends JpaRepository<PerfilInversion, Long> {

    @Query("SELECT DISTINCT p FROM PerfilInversion p LEFT JOIN FETCH p.portafolios pp LEFT JOIN FETCH pp.portafolioInversion")
    List<PerfilInversion> findAllWithPortafolios();

    @Query("SELECT DISTINCT p FROM PerfilInversion p LEFT JOIN FETCH p.formulasExposicion fe LEFT JOIN FETCH fe.portafolioInversion")
    List<PerfilInversion> findAllWithFormulas();
}
