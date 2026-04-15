package com.bysone.backend.repository;

import com.bysone.backend.domain.FormulaExposicion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FormulaExposicionRepository extends JpaRepository<FormulaExposicion, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM FormulaExposicion f WHERE f.perfilInversion.id = :perfilId")
    void deleteByPerfilId(@Param("perfilId") Long perfilId);

    @Query("SELECT f FROM FormulaExposicion f WHERE f.perfilInversion.id = :perfilId AND f.portafolioInversion.id = :portafolioId")
    Optional<FormulaExposicion> findByPerfilIdAndPortafolioId(
            @Param("perfilId") Long perfilId,
            @Param("portafolioId") Long portafolioId);
}
