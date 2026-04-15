package com.bysone.backend.repository;

import com.bysone.backend.domain.PerfilInversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PerfilInversionRepository extends JpaRepository<PerfilInversion, Long> {

    @Query("SELECT p FROM PerfilInversion p LEFT JOIN FETCH p.portafolios pp LEFT JOIN FETCH pp.portafolioInversion LEFT JOIN FETCH p.formulasExposicion")
    List<PerfilInversion> findAllWithPortafolios();

    @Query("SELECT p FROM PerfilInversion p LEFT JOIN FETCH p.portafolios pp LEFT JOIN FETCH pp.portafolioInversion LEFT JOIN FETCH p.formulasExposicion WHERE p.id = :id")
    Optional<PerfilInversion> findWithAllById(@Param("id") Long id);

    boolean existsByNombrePerfilIgnoreCase(String nombrePerfil);
}
