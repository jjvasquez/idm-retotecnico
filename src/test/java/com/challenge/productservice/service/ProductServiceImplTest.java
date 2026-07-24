package com.challenge.productservice.service;

import com.challenge.productservice.domain.Product;
import com.challenge.productservice.domain.ProductRequest;
import com.challenge.productservice.domain.ProductResponse;
import com.challenge.productservice.exception.ResourceNotFoundException;
import com.challenge.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;
    private ProductRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(1L, "Laptop", "Laptop Gamer", new BigDecimal("1200.00"), 10, "ACTIVE");
        sampleRequest = new ProductRequest("Laptop", "Laptop Gamer", new BigDecimal("1200.00"), 10, "ACTIVE");
    }

    @Test
    @DisplayName("Debe listar todos los productos en un Flux")
    void findAll_ShouldReturnFluxOfProducts() {
        when(repository.findAll()).thenReturn(Flux.just(sampleProduct));

        Flux<ProductResponse> result = productService.findAll();

        StepVerifier.create(result)
                .expectNextMatches(response -> response.name().equals("Laptop") && response.id().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener un producto por ID cuando existe")
    void findById_WhenProductExists_ShouldReturnMonoWithProduct() {
        when(repository.findById(1L)).thenReturn(Mono.just(sampleProduct));

        Mono<ProductResponse> result = productService.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.id().equals(1L) && response.price().equals(new BigDecimal("1200.00")))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el producto no existe por ID")
    void findById_WhenProductDoesNotExist_ShouldReturnError() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        Mono<ProductResponse> result = productService.findById(99L);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException &&
                        throwable.getMessage().contains("Producto no encontrado con el id: 99"))
                .verify();
    }

    @Test
    @DisplayName("Debe crear un producto exitosamente")
    void create_ShouldReturnCreatedProduct() {
        when(repository.save(any(Product.class))).thenReturn(Mono.just(sampleProduct));

        Mono<ProductResponse> result = productService.create(sampleRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.name().equals("Laptop"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar un producto existente")
    void update_WhenProductExists_ShouldReturnUpdatedProduct() {
        Product updatedProduct = new Product(1L, "Laptop Pro", "Laptop Gamer Pro", new BigDecimal("1500.00"), 5, "ACTIVE");
        ProductRequest updateRequest = new ProductRequest("Laptop Pro", "Laptop Gamer Pro", new BigDecimal("1500.00"), 5, "ACTIVE");

        when(repository.findById(1L)).thenReturn(Mono.just(sampleProduct));
        when(repository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        Mono<ProductResponse> result = productService.update(1L, updateRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.name().equals("Laptop Pro") && response.price().equals(new BigDecimal("1500.00")))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe eliminar un producto existente de forma reactiva")
    void delete_WhenProductExists_ShouldCompleteSuccessfully() {
        when(repository.findById(1L)).thenReturn(Mono.just(sampleProduct));
        when(repository.delete(sampleProduct)).thenReturn(Mono.empty());

        Mono<Void> result = productService.delete(1L);

        StepVerifier.create(result)
                .verifyComplete();
    }
}