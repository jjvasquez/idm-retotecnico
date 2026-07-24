package com.challenge.productservice.api.router;

import com.challenge.productservice.api.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route()
                .nest(path("/api/products"), builder -> builder
                        .GET("/stream", handler::getAllStream)
                        .GET("", accept(MediaType.TEXT_EVENT_STREAM), handler::getAllStream)
                        .GET("", handler::getAll)
                        .GET("/{id}", handler::getById)
                        .POST("", handler::create)
                        .PUT("/{id}", handler::update)
                        .DELETE("/{id}", handler::delete)
                )
                .build();
    }
}