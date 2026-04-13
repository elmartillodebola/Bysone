package com.bysone.backend.security;

import com.bysone.backend.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

/**
 * Unified principal for both plain OAuth2 (GitHub) and OIDC (Google, Microsoft) flows.
 * Implements {@link OidcUser} which extends {@link org.springframework.security.oauth2.core.user.OAuth2User},
 * so it satisfies both user-service return types.
 */
public class OAuth2UsuarioPrincipal implements OidcUser {

    private final Usuario usuario;
    private final Map<String, Object> attributes;
    private final OidcIdToken  idToken;   // null for plain OAuth2 (GitHub)
    private final OidcUserInfo userInfo;  // null for plain OAuth2 (GitHub)

    /** Constructor for plain OAuth2 providers (GitHub). */
    public OAuth2UsuarioPrincipal(Usuario usuario, Map<String, Object> attributes) {
        this(usuario, attributes, null, null);
    }

    /** Constructor for OIDC providers (Google, Microsoft). */
    public OAuth2UsuarioPrincipal(Usuario usuario, Map<String, Object> attributes,
                                   OidcIdToken idToken, OidcUserInfo userInfo) {
        this.usuario    = usuario;
        this.attributes = attributes;
        this.idToken    = idToken;
        this.userInfo   = userInfo;
    }

    public Usuario getUsuario() { return usuario; }

    // ── OAuth2User ────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getAuthorities();
    }

    @Override
    public String getName() { return usuario.getOauthSub(); }

    // ── OidcUser ──────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> getClaims() {
        return idToken != null ? idToken.getClaims() : attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() { return userInfo; }

    @Override
    public OidcIdToken getIdToken() { return idToken; }
}

