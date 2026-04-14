package com.bysone.backend.repository;

import com.bysone.backend.domain.Disclaimer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisclaimerRepository extends JpaRepository<Disclaimer, Long> {

    Optional<Disclaimer> findFirstByActivoTrueOrderByFechaVigenciaDesdeDesc();
}
