package com.challenge.productservice.api.handler;

import com.challenge.productservice.domain.ProductRequest;
import com.challenge.productservice.domain.ProductResponse;
import com.challenge.productservice.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ProductHandler {

    private final ProductService productService;

    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll(), ProductResponse.class);
    }

    public Mono<ServerResponse> getAllStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(productService.findAll(), ProductResponse.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.findById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productService::create)
                .flatMap(product -> ServerResponse
                        .created(URI.create("/api/products/" + product.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productReq -> productService.update(id, productReq))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return productService.delete(id)
                .then(ServerResponse.noContent().build());
    }
}