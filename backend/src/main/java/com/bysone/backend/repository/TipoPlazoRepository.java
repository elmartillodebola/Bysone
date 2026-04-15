package com.bysone.backend.repository;

import com.bysone.backend.domain.TipoPlazo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoPlazoRepository extends JpaRepository<TipoPlazo, Long> {
    boolean existsByNombrePlazoIgnoreCase(String nombrePlazo);
    boolean existsByNombrePlazoIgnoreCaseAndIdNot(String nombrePlazo, Long id);
}
