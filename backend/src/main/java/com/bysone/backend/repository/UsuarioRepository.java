package com.bysone.backend.repository;

import com.bysone.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByOauthSub(String oauthSub);
    Optional<Usuario> findByCorreoUsuario(String correoUsuario);
    boolean existsByPerfilInversionId(Long perfilInversionId);
    boolean existsByRolesId(Long rolId);
}

