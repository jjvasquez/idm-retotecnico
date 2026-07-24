package com.challenge.productservice.service;

import com.challenge.productservice.domain.ProductMapper;
import com.challenge.productservice.domain.ProductRequest;
import com.challenge.productservice.domain.ProductResponse;
import com.challenge.productservice.exception.ResourceNotFoundException;
import com.challenge.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ProductResponse> findAll() {
        return repository.findAll()
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> findById(Long id) {
        return repository.findById(id)
                .map(ProductMapper::toResponse)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con el id: " + id)));
    }

    @Override
    public Mono<ProductResponse> create(ProductRequest request) {
        return Mono.just(request)
                .map(ProductMapper::toEntity)
                .flatMap(repository::save)
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> update(Long id, ProductRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con el id: " + id)))
                .map(existing -> ProductMapper.copyWithUpdates(existing, request))
                .flatMap(repository::save)
                .map(ProductMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Producto no encontrado con el id: " + id)))
                .flatMap(repository::delete);
    }
}