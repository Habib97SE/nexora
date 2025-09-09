package io.nexora.catalog.interfaces.rest.v1;

import io.nexora.catalog.application.ProductApplicationService;
import io.nexora.catalog.domain.Product;
import io.nexora.catalog.interfaces.rest.v1.dto.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST Controller for Product operations.
 * 
 * This controller provides HTTP endpoints for Product use cases, following
 * RESTful principles and serving as the interface layer between external
 * clients and the application layer.
 * 
 * Responsibilities:
 * - Handle HTTP requests and responses
 * - Validate input data and parameters
 * - Transform between HTTP and domain representations
 * - Handle HTTP-specific concerns (status codes, headers)
 * - Provide RESTful API design
 * - Error handling and response formatting
 * 
 * Design Principles Applied:
 * - Single Responsibility: Focuses solely on HTTP request/response handling
 * - Open/Closed: Extensible for new endpoints without modification
 * - Dependency Inversion: Depends on abstractions (application service)
 * - Domain-Driven Design: Separates interface concerns from business logic
 * - RESTful Design: Follows REST conventions and HTTP semantics
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductApplicationService productApplicationService;

    // ==================== PRODUCT CRUD OPERATIONS ====================
    /**
     * Creates a new product.
     * 
     * @param request The product creation request
     * @return The created product with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        try {
            ProductApplicationService.CreateProductCommand command = 
                    new ProductApplicationService.CreateProductCommand(
                            request.getName(),
                            request.getDescription(),
                            request.getAmount(),
                            request.getCurrency().getCurrencyCode(),
                            request.getStockQuantity(),
                            request.getCategoryId()
                    );
            Product product = productApplicationService.createProduct(command);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            log.info("Successfully created product with ID: {}", product.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create product: {}", request.getName(), e);
            throw new ProductControllerException("Failed to create product: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a product by ID.
     * 
     * @param productId The product ID
     * @return The product with HTTP 200 status
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        log.debug("Retrieving product with ID: {}", productId);
        
        try {
            Product product = productApplicationService.findProductById(productId);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve product with ID: {}", productId, e);
            throw new ProductControllerException("Failed to retrieve product: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing product.
     * 
     * @param productId The product ID
     * @param request The product update request
     * @return The updated product with HTTP 200 status
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("Updating product with ID: {}", productId);
        
        try {
            ProductApplicationService.UpdateProductCommand command = 
                    new ProductApplicationService.UpdateProductCommand(
                            request.getName(),
                            request.getDescription(),
                            request.getPrice(),
                            request.getCurrency(),
                            request.getStockQuantity(),
                            request.getCategoryId()
                    );
            
            Product product = productApplicationService.updateProduct(productId, command);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            log.info("Successfully updated product with ID: {}", productId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update product with ID: {}", productId, e);
            throw new ProductControllerException("Failed to update product: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a product.
     * 
     * @param productId The product ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable UUID productId) {
        log.info("Deactivating product with ID: {}", productId);
        
        try {
            productApplicationService.deactivateProduct(productId);
            
            log.info("Successfully deactivated product with ID: {}", productId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Failed to deactivate product with ID: {}", productId, e);
            throw new ProductControllerException("Failed to deactivate product: " + e.getMessage(), e);
        }
    }

    // ==================== PRODUCT QUERY OPERATIONS ====================

    /**
     * Retrieves all products with pagination.
     * 
     * @param pageable Pagination parameters
     * @return A page of products with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        log.debug("Retrieving all products with pagination: {}", pageable);
        
        try {
            Page<Product> products = productApplicationService.findAllProducts(pageable);
            Page<ProductResponse> response = products.map(ProductResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve all products", e);
            throw new ProductControllerException("Failed to retrieve products: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves products by category with pagination.
     * 
     * @param categoryId The category ID
     * @param pageable Pagination parameters
     * @return A page of products with HTTP 200 status
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable UUID categoryId,
            Pageable pageable) {
        log.debug("Retrieving products by category ID: {} with pagination: {}", categoryId, pageable);
        
        try {
            Page<Product> products = productApplicationService.findProductsByCategory(categoryId, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve products by category ID: {}", categoryId, e);
            throw new ProductControllerException("Failed to retrieve products by category: " + e.getMessage(), e);
        }
    }

    /**
     * Searches products by text with pagination.
     * 
     * @param searchText The search text
     * @param pageable Pagination parameters
     * @return A page of products with HTTP 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String searchText,
            Pageable pageable) {
        log.debug("Searching products with text: {} and pagination: {}", searchText, pageable);
        
        try {
            Page<Product> products = productApplicationService.searchProducts(searchText, pageable);
            Page<ProductResponse> response = products.map(ProductResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to search products with text: {}", searchText, e);
            throw new ProductControllerException("Failed to search products: " + e.getMessage(), e);
        }
    }

    // ==================== PRODUCT MANAGEMENT OPERATIONS ====================

    /**
     * Adjusts product stock.
     * 
     * @param productId The product ID
     * @param request The stock adjustment request
     * @return The updated product with HTTP 200 status
     */
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductResponse> adjustStock(
            @PathVariable UUID productId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        log.info("Adjusting stock for product ID: {} by {}", productId, request.getQuantity());
        
        try {
            ProductApplicationService.StockAdjustmentCommand command = 
                    new ProductApplicationService.StockAdjustmentCommand(request.getQuantity());
            
            Product product = productApplicationService.adjustStock(productId, command);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            log.info("Successfully adjusted stock for product ID: {}", productId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to adjust stock for product ID: {}", productId, e);
            throw new ProductControllerException("Failed to adjust stock: " + e.getMessage(), e);
        }
    }

    /**
     * Changes product category.
     * 
     * @param productId The product ID
     * @param request The category change request
     * @return The updated product with HTTP 200 status
     */
    @PatchMapping("/{productId}/category")
    public ResponseEntity<ProductResponse> changeCategory(
            @PathVariable UUID productId,
            @Valid @RequestBody ChangeCategoryRequest request) {
        log.info("Changing category for product ID: {} to category: {}", productId, request.getCategoryId());
        
        try {
            ProductApplicationService.ChangeCategoryCommand command = 
                    new ProductApplicationService.ChangeCategoryCommand(request.getCategoryId());
            
            Product product = productApplicationService.changeCategory(productId, command);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            log.info("Successfully changed category for product ID: {}", productId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to change category for product ID: {}", productId, e);
            throw new ProductControllerException("Failed to change category: " + e.getMessage(), e);
        }
    }

    /**
     * Updates product price.
     * 
     * @param productId The product ID
     * @param request The price update request
     * @return The updated product with HTTP 200 status
     */
    @PatchMapping("/{productId}/price")
    public ResponseEntity<ProductResponse> updatePrice(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdatePriceRequest request) {
        log.info("Updating price for product ID: {} to {}", productId, request.getPrice());
        
        try {
            ProductApplicationService.UpdatePriceCommand command = 
                    new ProductApplicationService.UpdatePriceCommand(
                            request.getPrice(),
                            request.getCurrency()
                    );
            
            Product product = productApplicationService.updatePrice(productId, command);
            ProductResponse response = ProductResponse.fromDomain(product);
            
            log.info("Successfully updated price for product ID: {}", productId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update price for product ID: {}", productId, e);
            throw new ProductControllerException("Failed to update price: " + e.getMessage(), e);
        }
    }

    // ==================== REPORTING OPERATIONS ====================

    /**
     * Gets product statistics.
     * 
     * @return Product statistics with HTTP 200 status
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatisticsResponse> getProductStatistics() {
        log.debug("Getting product statistics");
        
        try {
            ProductApplicationService.ProductStatistics statistics = 
                    productApplicationService.getProductStatistics();
            ProductStatisticsResponse response = ProductStatisticsResponse.fromDomain(statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get product statistics", e);
            throw new ProductControllerException("Failed to get product statistics: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ProductResponse> deleteCategory(@PathVariable UUID categoryId) {
        log.info("Deleting category with ID: {}", categoryId);

        try {
            productApplicationService.deleteCategory(categoryId);

            log.info("Successfully deleted category with ID: {}", categoryId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Failed to delete category with ID: {}", categoryId, e);
            throw new ProductControllerException("Failed to delete category: " + e.getMessage(), e);
        }

    }

    // ==================== REQUEST/RESPONSE DTOs ====================

    /**
     * Request DTO for creating a product.
     */


    /**
     * Request DTO for updating a product.
     */
    public static class UpdateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String currency;
        private Integer stockQuantity;
        private UUID categoryId;

        // Constructors, getters, and setters
        public UpdateProductRequest() {}

        public UpdateProductRequest(String name, String description, BigDecimal price, 
                                  String currency, Integer stockQuantity, UUID categoryId) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.currency = currency;
            this.stockQuantity = stockQuantity;
            this.categoryId = categoryId;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }

    /**
     * Request DTO for stock adjustment.
     */
    public static class StockAdjustmentRequest {
        private Integer quantity;

        // Constructors, getters, and setters
        public StockAdjustmentRequest() {}

        public StockAdjustmentRequest(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    /**
     * Request DTO for category change.
     */
    public static class ChangeCategoryRequest {
        private UUID categoryId;

        // Constructors, getters, and setters
        public ChangeCategoryRequest() {}

        public ChangeCategoryRequest(UUID categoryId) {
            this.categoryId = categoryId;
        }

        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }

    /**
     * Request DTO for price update.
     */
    public static class UpdatePriceRequest {
        private BigDecimal price;
        private String currency;

        // Constructors, getters, and setters
        public UpdatePriceRequest() {}

        public UpdatePriceRequest(BigDecimal price, String currency) {
            this.price = price;
            this.currency = currency;
        }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    /**
     * Response DTO for product data.
     */
    public static class ProductResponse {
        private String id;
        private String name;
        private String description;
        private BigDecimal price;
        private String currency;
        private Integer stockQuantity;
        private String categoryId;
        private String categoryName;
        private String createdAt;
        private String updatedAt;

        // Constructors, getters, and setters
        public ProductResponse() {}

        public ProductResponse(String id, String name, String description, BigDecimal price, 
                             String currency, Integer stockQuantity, String categoryId, 
                             String categoryName, String createdAt, String updatedAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.currency = currency;
            this.stockQuantity = stockQuantity;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static ProductResponse fromDomain(Product product) {
            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice().amount(),
                    product.getPrice().currency().getCurrencyCode(),
                    product.getStockQuantity(),
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCreatedAt().toString(),
                    product.getUpdatedAt().toString()
            );
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    /**
     * Response DTO for product statistics.
     */
    public static class ProductStatisticsResponse {
        private Long totalProducts;

        // Constructors, getters, and setters
        public ProductStatisticsResponse() {}

        public ProductStatisticsResponse(Long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public static ProductStatisticsResponse fromDomain(ProductApplicationService.ProductStatistics statistics) {
            return new ProductStatisticsResponse(statistics.getTotalProducts());
        }

        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
    }

    /**
     * Controller-specific exception for product operations.
     */
    public static class ProductControllerException extends RuntimeException {
        public ProductControllerException(String message) {
            super(message);
        }

        public ProductControllerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

