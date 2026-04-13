package com.bysone.backend.security;

import com.bysone.backend.domain.Role;
import com.bysone.backend.domain.Usuario;
import com.bysone.backend.repository.RoleRepository;
import com.bysone.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // github | google | microsoft
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthSub = extractSub(registrationId, attributes);
        String email    = extractEmail(registrationId, attributes);
        String name     = extractName(registrationId, attributes, email);
        String provider = registrationId.toUpperCase();                    // GITHUB | GOOGLE | MICROSOFT

        Usuario usuario = usuarioRepository.findByOauthSub(oauthSub)
                .orElseGet(() -> createUser(oauthSub, email, name, provider));

        return new OAuth2UsuarioPrincipal(usuario, attributes);
    }

    // ── per-provider attribute extraction ────────────────────────────────────

    /**
     * Unique subject identifier per provider:
     *  - GitHub   → numeric "id"  (returned as Integer in the JSON)
     *  - Google   → "sub"         (OIDC standard)
     *  - Microsoft→ "sub"         (OIDC standard, from Graph /oidc/userinfo)
     */
    private String extractSub(String provider, Map<String, Object> attrs) {
        return switch (provider) {
            case "github"    -> String.valueOf(attrs.get("id"));
            case "google",
                 "microsoft" -> String.valueOf(attrs.get("sub"));
            default          -> String.valueOf(attrs.getOrDefault("sub", attrs.get("id")));
        };
    }

    /**
     * Email extraction per provider:
     *  - GitHub  : "email" (may be null when privacy is on → fall back to login handle)
     *  - Google  : "email" (always present with the email scope)
     *  - Microsoft: "email" (present via Graph /oidc/userinfo with the email scope)
     */
    private String extractEmail(String provider, Map<String, Object> attrs) {
        Object email = attrs.get("email");
        if (email != null && !email.toString().isBlank()) return email.toString();
        if ("github".equals(provider)) {
            return attrs.getOrDefault("login", "unknown").toString() + "@github.noreply";
        }
        return "unknown@" + provider + ".noreply";
    }

    /**
     * Display name per provider:
     *  - GitHub    : "name" or fallback to "login"
     *  - Google    : "name"
     *  - Microsoft : "name" (Graph returns this in the OIDC userinfo)
     */
    private String extractName(String provider, Map<String, Object> attrs, String emailFallback) {
        Object name = attrs.get("name");
        if (name != null && !name.toString().isBlank()) return name.toString();
        if ("github".equals(provider)) {
            Object login = attrs.get("login");
            if (login != null) return login.toString();
        }
        return emailFallback;
    }

    // ── user creation ─────────────────────────────────────────────────────────

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

