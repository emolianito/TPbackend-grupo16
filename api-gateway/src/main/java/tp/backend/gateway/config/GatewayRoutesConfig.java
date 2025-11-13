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

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {

        // Cambiá el 8081 por el puerto real donde corre tu clientesms
        String backendBaseUrl = "http://localhost:8081";

        return route("clientes-contenedores-route")
                // Cualquier método (GET, POST, PUT, DELETE, ...) con este path
                .route(path("/api/clientes/**"), http())
                .route(path("/api/contenedores/**"), http())
                // Define a dónde se proxyean las requests
                .before(uri(backendBaseUrl))
                .build();
    }
}