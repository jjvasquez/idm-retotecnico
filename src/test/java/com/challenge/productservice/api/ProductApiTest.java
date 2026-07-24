package com.challenge.productservice.api;

import com.challenge.productservice.api.handler.ProductHandler;
import com.challenge.productservice.api.router.ProductRouter;
import com.challenge.productservice.domain.ProductRequest;
import com.challenge.productservice.domain.ProductResponse;
import com.challenge.productservice.exception.GlobalExceptionHandler;
import com.challenge.productservice.exception.ResourceNotFoundException;
import com.challenge.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ProductRouter.class, ProductHandler.class, GlobalExceptionHandler.class})
class ProductApiTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    private ProductResponse sampleResponse;
    private ProductRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new ProductResponse(1L, "Mouse", "Mouse Óptico", new BigDecimal("25.00"), 50, "ACTIVE");
        sampleRequest = new ProductRequest("Mouse", "Mouse Óptico", new BigDecimal("25.00"), 50, "ACTIVE");
    }

    @Test
    @DisplayName("GET /api/products debe retornar HTTP 200 y la lista de productos")
    void getAllProducts_ShouldReturn200AndFlux() {
        when(productService.findAll()).thenReturn(Flux.just(sampleResponse));

        webTestClient.get()
                .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductResponse.class)
                .hasSize(1)
                .contains(sampleResponse);
    }

    @Test
    @DisplayName("GET /api/products/stream debe retornar HTTP 200 y eventos en streaming TEXT_EVENT_STREAM")
    void getAllProducts_ShouldReturnStreamWhenRequested() {
        when(productService.findAll()).thenReturn(Flux.just(sampleResponse));

        webTestClient.get()
                .uri("/api/products/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(ProductResponse.class)
                .hasSize(1)
                .contains(sampleResponse);
    }

    @Test
    @DisplayName("GET /api/products/{id} debe retornar HTTP 200 cuando existe")
    void getProductById_WhenExists_ShouldReturn200() {
        when(productService.findById(1L)).thenReturn(Mono.just(sampleResponse));

        webTestClient.get()
                .uri("/api/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Mouse");
    }

    @Test
    @DisplayName("GET /api/products/{id} debe retornar HTTP 404 cuando no existe")
    void getProductById_WhenNotExists_ShouldReturn404() {
        when(productService.findById(99L)).thenReturn(Mono.error(new ResourceNotFoundException("Producto no encontrado con el id: 99")));

        webTestClient.get()
                .uri("/api/products/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("POST /api/products debe crear el producto y retornar HTTP 201")
    void createProduct_ShouldReturn201() {
        when(productService.create(any(ProductRequest.class))).thenReturn(Mono.just(sampleResponse));

        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Mouse");
    }

    @Test
    @DisplayName("DELETE /api/products/{id} debe eliminar y retornar HTTP 204 No Content")
    void deleteProduct_ShouldReturn204() {
        when(productService.delete(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}