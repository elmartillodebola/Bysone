package com.bysone.backend.repository;

import com.bysone.backend.domain.OpcionInversion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpcionInversionRepository extends JpaRepository<OpcionInversion, Long> {
    boolean existsByNombreOpcion(String nombreOpcion);
}
