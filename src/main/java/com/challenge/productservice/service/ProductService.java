package com.challenge.productservice.service;

import com.challenge.productservice.domain.ProductRequest;
import com.challenge.productservice.domain.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<ProductResponse> findAll();
    Mono<ProductResponse> findById(Long id);
    Mono<ProductResponse> create(ProductRequest request);
    Mono<ProductResponse> update(Long id, ProductRequest request);
    Mono<Void> delete(Long id);
}