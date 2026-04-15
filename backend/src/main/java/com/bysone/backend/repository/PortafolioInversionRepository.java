package com.bysone.backend.repository;

import com.bysone.backend.domain.PortafolioInversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortafolioInversionRepository extends JpaRepository<PortafolioInversion, Long> {

    boolean existsByNombrePortafolio(String nombrePortafolio);

    @Query("SELECT p FROM PortafolioInversion p LEFT JOIN FETCH p.opciones")
    List<PortafolioInversion> findAllWithOpciones();
}
