# 📦 Product Service - Microservicio Reactivo Backend

Microservicio reactivo desarrollado en Java 17 y Spring Boot 3 para la gestión de productos, aplicando programación funcional, arquitectura reactiva de extremo a extremo (Spring WebFlux y Spring Data R2DBC), pruebas unitarias con StepVerifier y WebTestClient, y empaquetamiento en Docker.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 17 (Uso de Records, Streams, Lambdas e Inmutabilidad)
- **Framework:** Spring Boot 3.2.x
- **Módulo Web:** Spring WebFlux (Functional Endpoints: `RouterFunction` + `HandlerFunction`)
- **Persistencia:** Spring Data R2DBC + H2 Database (Driver no bloqueante)
- **Pruebas:** JUnit 5, Mockito, Project Reactor Test (`StepVerifier`), `WebTestClient`
- **Cobertura:** JaCoCo Plugin (Mínimo 70% en capa de servicios)
- **Contenedores:** Docker & Docker Compose (Multi-stage Build)

---

## 🏛️ Arquitectura y Principios de Diseño

El proyecto sigue una arquitectura limpia por capas y se rige estrictamente bajo los principios SOLID:

### Separación de Responsabilidades:

- `api/router`: Define las rutas y verbos HTTP.
- `api/handler`: Procesa las peticiones (`ServerRequest`) y respuestas HTTP (`ServerResponse`).
- `service`: Contiene la lógica de negocio pura y contratos reactivos (`Mono`/`Flux`).
- `repository`: Persistencia no bloqueante con R2DBC.
- `domain`: Contiene modelos inmutables (Java Records), mapeadores funcionales y la entidad R2DBC.
- `exception`: Manejador global de excepciones reactivo estandarizado (`@RestControllerAdvice`).

**Programación Funcional Estricta:** Uso de Java Records para garantizar inmutabilidad, transformaciones con Streams y Optional, e inyección de dependencias por constructor.

---

## 🚀 Requisitos Previos

- **JDK 17 o superior**

  **Instalación de Java 17:**
  
  - **Windows (Winget):**
    ```powershell
    winget install EclipseAdoptium.Temurin.17.JDK
    ```
  - **Linux / macOS (SDKMAN!):**
    ```bash
    sdk install java 17.0.10-tem
    ```
  - **Ubuntu / Debian (APT):**
    ```bash
    sudo apt update && sudo apt install openjdk-17-jdk -y
    ```
  - **macOS (Homebrew):**
    ```bash
    brew install openjdk@17
    ```

- **Apache Maven 3.8+**
- **Docker & Docker Compose** (para ejecución contenerizada)

---

## ⚙️ Compilación, Pruebas y Reporte de Cobertura

### 1. Compilar y ejecutar las Pruebas Unitarias
Ejecuta el siguiente comando para correr el suite completo de pruebas (`StepVerifier` y `WebTestClient`):

```bash
mvn clean test
```

### 2. Generar y consultar el reporte de JaCoCo
El plugin de JaCoCo genera un reporte visual en HTML tras ejecutar los tests. Abre el siguiente archivo en tu navegador:

```plaintext
target/site/jacoco/index.html
```

> **Nota de Calidad:** La configuración de JaCoCo en el `pom.xml` exige un mínimo del 70% de cobertura en las clases de servicio para dar por exitosa la fase de prueba.

---

## 💻 Ejecución Local (Sin Docker)

Para ejecutar la aplicación localmente sin utilizar Docker, puedes iniciarla directamente con Maven:

```bash
mvn spring-boot:run
```

El servidor reactivo (Netty) se iniciará en `http://localhost:8080` utilizando la base de datos H2 en memoria inicializada por el script `schema.sql`.

---

## 🐳 Ejecución con Docker (Un solo comando)

Para levantar la solución completa dentro de un contenedor aislado con Docker Compose:

```bash
docker-compose up --build
```

El servidor reactivo (Netty) iniciará en el puerto `8080` y creará automáticamente la tabla en la base de datos H2 en memoria mediante el archivo `schema.sql`.

Para detener los contenedores:

```bash
docker-compose down
```

---

## 📡 Ejemplos de Consumo de la API (cURL)

### 1. Listar todos los productos (GET) — Event Streaming SSE / JSON
```bash
curl -N -X GET http://localhost:8080/api/products -H "Accept: text/event-stream"
```

### 2. Obtener producto por ID (GET /{id})
```bash
curl -X GET http://localhost:8080/api/products/1
```

### 3. Crear un nuevo producto (POST)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teclado Mecánico RGB",
    "description": "Switch Red, conector USB-C",
    "price": 89.99,
    "stock": 20,
    "status": "ACTIVE"
  }'
```

### 4. Actualizar un producto existente (PUT /{id})
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teclado Mecánico RGB Pro",
    "description": "Switch Red Inalámbrico",
    "price": 109.99,
    "stock": 15,
    "status": "ACTIVE"
  }'
```

### 5. Eliminar un producto (DELETE /{id})
```bash
curl -X DELETE -i http://localhost:8080/api/products/1
```

### 6. Probar Manejo de Errores Reactivo (404 Not Found)
```bash
curl -X GET http://localhost:8080/api/products/999
```

**Respuesta:**
```json
{
  "timestamp": "2026-07-24T17:00:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con el id: 999"
}
```

---

## 📄 Estructura del Proyecto

```plaintext
product-service/
├── src/
│   ├── main/
│   │   ├── java/com/challenge/productservice/
│   │   │   ├── api/
│   │   │   │   ├── handler/ProductHandler.java
│   │   │   │   └── router/ProductRouter.java
│   │   │   ├── domain/
│   │   │   │   ├── Product.java
│   │   │   │   ├── ProductMapper.java
│   │   │   │   ├── ProductRequest.java
│   │   │   │   └── ProductResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java
│   │   │   └── service/
│   │   │       ├── ProductService.java
│   │   │       └── ProductServiceImpl.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── schema.sql
│   └── test/
│       └── java/com/challenge/productservice/
│           ├── api/ProductApiTest.java
│           └── service/ProductServiceImplTest.java
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```
