package com.bysone.backend.repository;

import com.bysone.backend.domain.PerfilPortafolio;
import com.bysone.backend.domain.PerfilPortafolioId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerfilPortafolioRepository extends JpaRepository<PerfilPortafolio, PerfilPortafolioId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PerfilPortafolio pp WHERE pp.perfilInversion.id = :perfilId")
    void deleteByPerfilId(@Param("perfilId") Long perfilId);
}
