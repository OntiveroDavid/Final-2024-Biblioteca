package com.library.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // TODO
        /**
         * Completar el retorno de las rutas para las APIS
         * /api/personas/     8081
         * /api/biblioteca/   8082
         */
        return builder.routes()
                // Ruta para el microservicio de Clientes (Personas)
                .route("clientes-service", r -> r.path("/api/personas/**")
                        .uri("http://clientes-service:8081"))

                // Ruta para el microservicio de Biblioteca
                .route("biblioteca-service", r -> r.path("/api/biblioteca/**")
                        .uri("http://biblioteca-service:8082"))

                .build();
    }
}