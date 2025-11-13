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
}