package com.backend.UbicacionesMicroservicio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            // 1. Permite TODAS las solicitudes a CUALQUIER endpoint
            .anyRequest().permitAll())
        // 2. Desactiva CSRF (necesario para que funcionen POST, PUT, DELETE)
        .csrf(csrf -> csrf.disable());

    // 3. (Importante) Hemos ELIMINADO la línea .oauth2ResourceServer()

    return http.build();
  }
}