package com.challenge.productservice.domain;

import java.util.Optional;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        return Optional.ofNullable(product)
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock(),
                        p.getStatus()
                ))
                .orElse(null);
    }

    public static Product toEntity(ProductRequest request) {
        return Optional.ofNullable(request)
                .map(req -> new Product(
                        null,
                        req.name(),
                        req.description(),
                        req.price(),
                        req.stock(),
                        req.status()
                ))
                .orElse(null);
    }

    // Método puro funcional: Retorna una nueva instancia sin mutar la previa
    public static Product copyWithUpdates(Product existing, ProductRequest request) {
        return Optional.ofNullable(existing)
                .flatMap(ext -> Optional.ofNullable(request)
                        .map(req -> new Product(
                                ext.getId(),
                                req.name(),
                                req.description(),
                                req.price(),
                                req.stock(),
                                req.status()
                        ))
                )
                .orElse(null);
    }
}