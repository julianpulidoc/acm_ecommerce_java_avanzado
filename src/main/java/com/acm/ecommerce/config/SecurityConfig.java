package com.acm.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Única configuración de seguridad de la app.
 * Las rutas que usa la demo de CI/CD quedan públicas de forma explícita.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API REST sin sesiones de navegador
            .authorizeHttpRequests(auth -> auth
                // Página de la demo y endpoint que usa el smoke test del pipeline
                .requestMatchers("/", "/index.html", "/version").permitAll()
                // Documentación de la API
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api/v1/api-docs/**").permitAll()
                // API de la aplicación (app de pruebas: se deja abierta)
                .requestMatchers("/api/**").permitAll()
                // Cualquier otra ruta se rechaza
                .anyRequest().denyAll()
            );
        return http.build();
    }
}
