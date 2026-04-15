package com.bysone.backend.repository;

import com.bysone.backend.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombreRol(String nombreRol);
    boolean existsByNombreRolIgnoreCase(String nombreRol);
    boolean existsByNombreRolIgnoreCaseAndIdNot(String nombreRol, Long id);
}

