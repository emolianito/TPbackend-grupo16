package tp.backend.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuración de seguridad del API Gateway con OAuth2 Resource Server (Keycloak)
 * 
 * Control de acceso por roles:
 * 
 * ROL CLIENTE:
 * - Puede registrar un pedido de traslado de contenedor
 * - Puede consultar el estado actual de su contenedor (seguimiento)
 * - Puede ver el costo y tiempo estimado de entrega
 * 
 * ROL ADMIN:
 * - Carga y actualiza ciudades, depósitos, tarifas, camiones y contenedores
 * - Asigna camiones a tramos de traslado
 * - Modifica parámetros de tarifación
 * 
 * ROL TRANSPORTISTA:
 * - Puede ver los tramos asignados que tiene
 * - Puede registrar un inicio o fin de tramo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                
                // ==========================================
                // ENDPOINTS PÚBLICOS (sin autenticación)
                // ==========================================
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // ==========================================
                // ROL ADMIN - Definir primero (más específico)
                // ==========================================
                
                // Gestión de ubicaciones (ciudades y depósitos)
                .requestMatchers(HttpMethod.POST, "/api/ubicaciones/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/ubicaciones/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/ubicaciones/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/ubicaciones/**").hasRole("admin")
                
                // Gestión de tarifas (modificar parámetros de tarifación)
                .requestMatchers(HttpMethod.POST, "/api/tarifas/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/tarifas/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/tarifas/**").hasRole("admin")
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/tarifa").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes/tarifa/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/solicitudes/tarifa/**").hasRole("admin")
                
                // Gestión de camiones
                .requestMatchers(HttpMethod.POST, "/api/transporte/camion/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/transporte/camion/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/transporte/camion/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/transporte/camion/**").hasRole("admin")
                
                // Gestión de contenedores (carga y actualización)
                .requestMatchers(HttpMethod.POST, "/api/contenedores/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/contenedores/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/contenedores/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/contenedores/**").hasAnyRole("admin", "cliente") // <-- AGREGAR ESTA LÍNEA
                
                // Gestión de clientes (CRUD completo)
                .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasRole("admin")
                
                // Asignar camiones a tramos de traslado
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/asignar-camion").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes/tramo/asignar/**").hasRole("admin")
                
                // Gestión de rutas
                .requestMatchers(HttpMethod.POST, "/api/rutas/**").hasRole("admin")
                .requestMatchers(HttpMethod.PUT, "/api/rutas/**").hasRole("admin")
                .requestMatchers(HttpMethod.DELETE, "/api/rutas/**").hasRole("admin")
                .requestMatchers(HttpMethod.GET, "/api/rutas/**").hasRole("admin")
                
                // Ver todas las solicitudes
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/solicitud").hasAnyRole("admin", "transportista")
                
                // ==========================================
                // ROL CLIENTE
                // ==========================================
                
                // Puede registrar un pedido de traslado de contenedor
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/solicitud").hasRole("cliente")
                
                // Puede consultar el estado actual de su contenedor (seguimiento)
                .requestMatchers(HttpMethod.GET, "/api/contenedores/estado/**").hasAnyRole("cliente", "admin")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/solicitud/{id}").hasAnyRole("cliente", "admin")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/solicitud/cliente/**").hasRole("cliente")
                
                // Puede ver el costo y tiempo estimado de entrega
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/tarifa/**").hasAnyRole("cliente", "admin")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/tiempo-estimado/**").hasAnyRole("cliente", "admin")
                .requestMatchers(HttpMethod.GET, "/api/tarifas/**").hasAnyRole("cliente", "admin")
                
                // ==========================================
                // ROL TRANSPORTISTA
                // ==========================================
                
                // Puede ver los tramos asignados que tiene
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/tramo/transportista/**").hasRole("transportista")
                .requestMatchers(HttpMethod.GET, "/api/transporte/transportista/me").hasRole("transportista")
                .requestMatchers(HttpMethod.GET, "/api/solicitudes/tramo/{id}").hasAnyRole("transportista", "admin")
                
                // Puede registrar un inicio o fin de tramo
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/tramo/iniciar/**").hasRole("transportista")
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/tramo/finalizar/**").hasRole("transportista")
                .requestMatchers(HttpMethod.PUT, "/api/solicitudes/tramo/estado/**").hasRole("transportista")
                
                // ==========================================
                // Cualquier otro endpoint requiere autenticación
                // ==========================================
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Convierte los roles de Keycloak a authorities de Spring Security
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    /**
     * Extrae los roles desde el token JWT de Keycloak
     * Los roles están en: jwt.realm_access.roles
     */
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Extraer roles de realm_access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            
            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return List.of();
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");

            // Convertir roles a GrantedAuthority con prefijo ROLE_
            // Ejemplo: "admin" -> "ROLE_admin"
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }
    }
}