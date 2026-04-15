package com.bysone.backend.security;

import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google | microsoft
        String provider       = registrationId.toUpperCase();

        // OIDC standard: "sub" is the unique subject identifier
        String oauthSub = oidcUser.getSubject();
        String email    = resolveEmail(oidcUser, provider);
        String name     = resolveName(oidcUser, email);

        // 1. Buscar por oauth_sub (login normal)
        // 2. Si no existe, buscar por email (usuario pre-sembrado, p.ej. admin inicial)
        //    → vincular el oauth_sub real y conservar roles asignados en semilla
        // 3. Si tampoco existe, crear nuevo usuario con rol USER
        Usuario usuario = usuarioRepository.findByOauthSub(oauthSub)
                .orElseGet(() -> usuarioRepository.findByCorreoUsuario(email)
                        .map(u -> { u.setOauthSub(oauthSub); return usuarioRepository.save(u); })
                        .orElseGet(() -> createUser(oauthSub, email, name, provider)));

        return new OAuth2UsuarioPrincipal(usuario, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String resolveEmail(OidcUser user, String provider) {
        String email = user.getEmail();
        if (email != null && !email.isBlank()) return email;
        return "unknown@" + provider.toLowerCase() + ".noreply";
    }

    private String resolveName(OidcUser user, String emailFallback) {
        String name = user.getFullName();
        if (name != null && !name.isBlank()) return name;
        String given  = user.getGivenName();
        String family = user.getFamilyName();
        if (given != null || family != null) {
            return ((given  != null ? given  + " " : "") +
                    (family != null ? family : "")).trim();
        }
        return emailFallback;
    }

    private Usuario createUser(String oauthSub, String email, String name, String provider) {
        Role defaultRole = roleRepository.findByNombreRol("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found in DB"));

        Usuario user = new Usuario();
        user.setOauthSub(oauthSub);
        user.setCorreoUsuario(email);
        user.setNombreCompletoUsuario(name);
        user.setProveedorOauth(provider);
        user.getRoles().add(defaultRole);
        return usuarioRepository.save(user);
    }
}

