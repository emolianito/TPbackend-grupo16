package tp.backend.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    // Clientes + contenedores (microservicio en 8081)
    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {

        String backendBaseUrl = "http://localhost:8081";

        return route("clientes-contenedores-route")
                .route(path("/api/clientes/**"), http())
                .route(path("/api/contenedores/**"), http())
                .before(uri(backendBaseUrl))
                .build();
    }

    // Ubicaciones (microservicio en 8083)
    @Bean
    public RouterFunction<ServerResponse> ubicacionesRoutes() {
        return route("ubicaciones-route")
                .route(path("/ubicaciones/depositos/**"), http())
                .route(path("/ubicaciones/localizaciones/**"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> solicitudesRoutes() {
        return route("solicitudes-route")
                // Rutas del microservicio de Solicitudes
                .route(path("/rutas/**"), http())                 // RutaController
                .route(path("/solicitudes/**"), http())           // SolicitudController + TramoController
                .route(path("/tarifas/**"), http())               // TarifaController

                // Se envían al microservicio real en puerto 8083
                .before(uri("http://localhost:8083"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> transporteRoutes() {
        return route("transporte-route")
                // Camiones
                .route(path("/camiones/**"), http())
                // Transportistas
                .route(path("/transportistas/**"), http())
                // URL real del microservicio de transporte
                .before(uri("http://localhost:8084"))
                .build();
    }
}