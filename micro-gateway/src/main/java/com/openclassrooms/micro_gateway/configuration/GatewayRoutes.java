package com.openclassrooms.micro_gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.web.servlet.function.RequestPredicates.path;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutes {

    @Value("${app.gateway.basicurl}")
    private String basicUrl;


    @Bean
    public RouterFunction<ServerResponse> gatewayRouter() {   // <-- Changé ici
        return route("MicroserviceHospital")
                .route(path("/Hospital/**"), http())
                .before(stripPrefix(1))
                .before(uri("http://"+basicUrl+":4200"))
                .build()
                .and(route("MicroServiceAppointment")
                        .route(path("/Appointment/**"), http())
                        .before(stripPrefix(1))
                        .before(uri("http://"+basicUrl+":4100"))
                        .build())

                .and(route("MicroserviceBedAvailability")
                        .route(path("/Bed/**"), http())
                        .before(stripPrefix(1))
                        .before(uri("http://"+basicUrl+":4300"))
                        .build());

    }
}
