package tp.backend.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // CLIENTE - Solicitudes y seguimiento
                .requestMatchers("POST", "/api/solicitudes/solicitud/**").hasRole("cliente")
                .requestMatchers("GET", "/api/solicitudes/solicitud/{id}").hasAnyRole("admin", "cliente")
                .requestMatchers("GET", "/api/solicitudes/ruta/generar").hasRole("cliente")
                .requestMatchers("GET", "/api/clientes/contenedor/estado/**").hasRole("cliente")
                
                // ADMIN - Gestión completa
                .requestMatchers("/api/ubicaciones/deposito/**").hasRole("admin")
                .requestMatchers("/api/ubicaciones/ubicacion/**").hasRole("admin")
                .requestMatchers("/api/solicitudes/tarifa/**").hasRole("admin")
                .requestMatchers("/api/solicitudes/solicitud/asignar-camion").hasRole("admin")
                .requestMatchers("/api/solicitudes/solicitud/**").hasRole("admin")
                .requestMatchers("/api/transporte/camion/**").hasRole("admin")
                .requestMatchers("/api/clientes/contenedor/**").hasRole("admin")
                .requestMatchers("/api/clientes/cliente/**").hasRole("admin")
                .requestMatchers("/api/clientes/estado-contenedor/**").hasRole("admin")
                
                // TRANSPORTISTA - Tramos
                .requestMatchers("/api/solicitudes/tramo/transportista/**").hasRole("transportista")
                .requestMatchers("/api/solicitudes/tramo/iniciar/**").hasRole("transportista")
                .requestMatchers("/api/solicitudes/tramo/finalizar/**").hasRole("transportista")
                .requestMatchers("/api/transporte/transportista/me").hasRole("transportista")
                .requestMatchers("/api/transporte/transportista/**").hasAnyRole("admin", "transportista")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        
        // Keycloak almacena roles en realm_access.roles
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        return jwtAuthenticationConverter;
    }
}