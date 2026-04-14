package com.bysone.backend.repository;

import com.bysone.backend.domain.ParametroBysone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroBysoneRepository extends JpaRepository<ParametroBysone, Long> {
    Optional<ParametroBysone> findByNombreParametro(String nombreParametro);
}
