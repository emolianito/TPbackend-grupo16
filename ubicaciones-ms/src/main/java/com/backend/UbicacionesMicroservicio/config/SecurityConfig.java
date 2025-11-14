package com.backend.UbicacionesMicroservicio.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Bean
  public JwtDecoder jwtDecoder() {
    // Construye la URL del JWK Set
    String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            // Rutas públicas (sin autenticación)
            .requestMatchers("/publico/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

            // Rutas solo para ADMIN
            .requestMatchers("/ubicaciones/depositos/**").hasRole("ADMIN")

            // Rutas para ADMIN y OPERADOR
            .requestMatchers(HttpMethod.GET, "/ubicaciones/localizaciones/**")
            .hasAnyRole("ADMIN", "OPERADOR")
            .requestMatchers(HttpMethod.POST, "/ubicaciones/localizaciones/**")
            .hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/ubicaciones/localizaciones/**")
            .hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/ubicaciones/localizaciones/**")
            .hasRole("ADMIN")

            // Rutas para TRANSPORTISTA
            .requestMatchers("/api/distancia/**").hasAnyRole("ADMIN", "TRANSPORTISTA")

            // Cualquier otra ruta requiere autenticación
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .csrf(csrf -> csrf.disable());

    return http.build();
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    return jwt -> {
      Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");

      if (realmAccess == null || realmAccess.get("roles") == null) {
        return new JwtAuthenticationToken(jwt, List.of());
      }

      List<GrantedAuthority> authorities = realmAccess.get("roles")
          .stream()
          .map(role -> String.format("ROLE_%s", role.toUpperCase()))
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());

      return new JwtAuthenticationToken(jwt, authorities);
    };
  }
}