package tp.backend.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/**
 * Configuración de rutas del API Gateway
 * 
 * Control de acceso por roles (configurado en SecurityConfig.java):
 * 
 * ROL CLIENTE:
 * - POST /api/solicitudes/solicitud - Registrar pedido de traslado
 * - GET /api/contenedores/estado/** - Consultar estado del contenedor
 * - GET /api/solicitudes/tarifa/** - Ver costo estimado
 * 
 * ROL ADMIN:
 * - /api/ubicaciones/** - Gestionar ciudades y depósitos
 * - /api/tarifas/** - Gestionar tarifas
 * - /api/transporte/camion/** - Gestionar camiones
 * - /api/contenedores/** - Gestionar contenedores (POST, PUT, DELETE)
 * - /api/clientes/** - Gestionar clientes
 * - POST /api/solicitudes/solicitud/asignar-camion - Asignar camiones
 * 
 * ROL TRANSPORTISTA:
 * - GET /api/solicitudes/tramo/transportista/** - Ver tramos asignados
 * - POST /api/solicitudes/tramo/iniciar/** - Iniciar tramo
 * - POST /api/solicitudes/tramo/finalizar/** - Finalizar tramo
 */
@Configuration
public class GatewayRoutesConfig {

    @Value("${clientes.service.url:http://localhost:8081}")
    private String clientesServiceUrl;

    @Value("${ubicaciones.service.url:http://localhost:8084}")
    private String ubicacionesServiceUrl;

    @Value("${solicitudes.service.url:http://localhost:8082}")
    private String solicitudesServiceUrl;

    @Value("${transporte.service.url:http://localhost:8083}")
    private String transporteServiceUrl;

    /**
     * Rutas para el microservicio de Clientes y Contenedores
     * 
     * Endpoints:
     * - /api/clientes/** (ADMIN: CRUD completo)
     * - /api/contenedores/** (ADMIN: POST/PUT/DELETE, CLIENTE: GET estado)
     */
    @Bean
    public RouterFunction<ServerResponse> clientesRoutes() {
        return route("clientes-contenedores-route")
                .route(path("/api/clientes/**"), http())
                .route(path("/api/contenedores/**"), http())
                .before(uri(clientesServiceUrl))
                .build();
    }

    /**
     * Rutas para el microservicio de Ubicaciones
     * 
     * Endpoints:
     * - /api/ubicaciones/** (ADMIN: gestionar ciudades, depósitos y ubicaciones)
     * - /ubicaciones/** (ADMIN: endpoints alternativos)
     */
    @Bean
    public RouterFunction<ServerResponse> ubicacionesRoutes() {
        return route("ubicaciones-route")
                .route(path("/api/ubicaciones/**"), http())
                .route(path("/ubicaciones/**"), http())
                .before(uri(ubicacionesServiceUrl))
                .build();
    }

    /**
     * Rutas para el microservicio de Solicitudes
     * 
     * Endpoints:
     * - POST /api/solicitudes/solicitud (CLIENTE: crear solicitud)
     * - GET /api/solicitudes/solicitud/cliente/** (CLIENTE: ver sus solicitudes)
     * - POST /api/solicitudes/solicitud/asignar-camion (ADMIN: asignar camiones)
     * - GET /api/solicitudes/tarifa/** (CLIENTE: ver costos, ADMIN: gestionar)
     * - GET /api/solicitudes/tramo/transportista/** (TRANSPORTISTA: ver tramos)
     * - POST /api/solicitudes/tramo/iniciar/** (TRANSPORTISTA: iniciar tramo)
     * - POST /api/solicitudes/tramo/finalizar/** (TRANSPORTISTA: finalizar tramo)
     * - /api/rutas/** (ADMIN: gestionar rutas)
     * - /api/tarifas/** (ADMIN: gestionar tarifas)
     */
    @Bean
    public RouterFunction<ServerResponse> solicitudesRoutes() {
        return route("solicitudes-route")
                // Rutas con prefijo /api
                .route(path("/api/solicitudes/**"), http())
                .route(path("/api/rutas/**"), http())
                .route(path("/api/tarifas/**"), http())
                
                // Rutas sin prefijo /api (endpoints alternativos)
                .route(path("/rutas/**"), http())
                .route(path("/solicitudes/**"), http())
                .route(path("/tarifas/**"), http())
                
                .before(uri(solicitudesServiceUrl))
                .build();
    }

    /**
     * Rutas para el microservicio de Transporte
     * 
     * Endpoints:
     * - /api/transporte/camion/** (ADMIN: gestionar camiones)
     * - /api/transporte/transportista/me (TRANSPORTISTA: ver su información)
     * - /camiones/** (ADMIN: endpoints alternativos)
     * - /transportistas/** (ADMIN: gestionar transportistas)
     */
    @Bean
    public RouterFunction<ServerResponse> transporteRoutes() {
        return route("transporte-route")
                // Rutas con prefijo /api
                .route(path("/api/transporte/**"), http())
                
                // Rutas sin prefijo /api (endpoints alternativos)
                .route(path("/camiones/**"), http())
                .route(path("/transportistas/**"), http())
                
                .before(uri(transporteServiceUrl))
                .build();
    }
}