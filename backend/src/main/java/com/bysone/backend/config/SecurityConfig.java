package com.bysone.backend.config;

import com.bysone.backend.security.CustomOAuth2UserService;
import com.bysone.backend.security.CustomOidcUserService;
import com.bysone.backend.security.JwtAuthFilter;
import com.bysone.backend.security.OAuth2AuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2UserService customOAuth2UserService;   // GitHub (plain OAuth2)
    private final CustomOidcUserService customOidcUserService;       // Google / Microsoft (OIDC)
    private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Stateless — no sessions
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable CSRF (stateless JWT API)
                .csrf(AbstractHttpConfigurer::disable)
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public: OAuth2 login flow + API docs
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Maintainer + Admin
                        .requestMatchers("/api/maintainer/**").hasAnyRole("ADMIN", "MAINTAINER")
                        // Everything else needs a valid JWT
                        .anyRequest().authenticated()
                )
                // OAuth2 login — GitHub (OAuth2) + Google + Microsoft (OIDC)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(ui -> ui
                                .userService(customOAuth2UserService)   // plain OAuth2 (GitHub)
                                .oidcUserService(customOidcUserService)  // OIDC (Google, Microsoft)
                        )
                        .successHandler(oAuth2AuthSuccessHandler)
                )
                // JWT filter runs before Spring's UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

