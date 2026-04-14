package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bysone.backend.domain.PerfilInversion;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre_completo_usuario", nullable = false, length = 200)
    private String nombreCompletoUsuario;

    @Column(name = "correo_usuario", nullable = false, unique = true, length = 150)
    private String correoUsuario;

    @Column(name = "celular_usuario", length = 20)
    private String celularUsuario;

    @Column(name = "proveedor_oauth", nullable = false, length = 20)
    private String proveedorOauth;

    @Column(name = "oauth_sub", nullable = false, unique = true, length = 255)
    private String oauthSub;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "fecha_ultima_actualizacion_perfil_inversion")
    private LocalDateTime fechaUltimaActualizacionPerfilInversion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil_inversion")
    private PerfilInversion perfilInversion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_x_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Role> roles = new HashSet<>();

    // ---- UserDetails ----

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getNombreRol()))
                .collect(Collectors.toSet());
    }

    /** OAuth2 users have no local password. */
    @Override public String getPassword() { return null; }

    /** We use the OAuth sub as the unique username for the JWT subject. */
    @Override public String getUsername() { return oauthSub; }

    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }
}

