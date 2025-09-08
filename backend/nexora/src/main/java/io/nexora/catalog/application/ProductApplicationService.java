package io.nexora.catalog.application;

import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.Product;
import io.nexora.catalog.domain.ProductRepository;
import main.java.io.nexora.catalog.domain.service.ProductDomainService;
import io.nexora.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Product use cases.
 * 
 * This service orchestrates application workflows and coordinates between
 * domain services, repositories, and external systems. It handles the
 * application layer concerns while delegating domain logic to domain services.
 * 
 * Responsibilities:
 * - Orchestrate complex use cases and workflows
 * - Coordinate between domain services and repositories
 * - Handle application-level validation and error handling
 * - Manage transactions and cross-cutting concerns
 * - Provide a clean API for the presentation layer
 * 
 * Design Principles Applied:
 * - Single Responsibility: Focuses on application workflow orchestration
 * - Open/Closed: Extensible for new use cases without modification
 * - Dependency Inversion: Depends on abstractions (domain services, repositories)
 * - Domain-Driven Design: Separates application concerns from domain logic
 * - Command Query Separation: Clear separation between commands and queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductApplicationService {

    private final ProductDomainService productDomainService;
    private final ProductRepository productRepository;

    // ==================== COMMAND OPERATIONS ====================

    /**
     * Creates a new product through the application workflow.
     * 
     * This method orchestrates the product creation process, including
     * validation, domain service coordination, and result handling.
     * 
     * @param createProductCommand The product creation command
     * @return The created product
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Product createProduct(CreateProductCommand createProductCommand) {
        log.info("Creating new product: {}", createProductCommand.getName());
        
        try {
            // Convert command to domain object
            Product product = convertToProduct(createProductCommand);
            
            // Delegate to domain service for business logic
            Product createdProduct = productDomainService.createProduct(product);
            
            log.info("Successfully created product with ID: {}", createdProduct.getId());
            return createdProduct;
            
        } catch (Exception e) {
            log.error("Failed to create product: {}", createProductCommand.getName(), e);
            throw new ProductApplicationException("Failed to create product: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing product through the application workflow.
     * 
     * @param productId The ID of the product to update
     * @param updateProductCommand The product update command
     * @return The updated product
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Product updateProduct(UUID productId, UpdateProductCommand updateProductCommand) {
        log.info("Updating product with ID: {}", productId);
        
        try {
            // Convert command to domain object
            Product updatedProduct = convertToProduct(updateProductCommand);
            
            // Delegate to domain service for business logic
            Product result = productDomainService.updateProduct(productId, updatedProduct);
            
            log.info("Successfully updated product with ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to update product with ID: {}", productId, e);
            throw new ProductApplicationException("Failed to update product: " + e.getMessage(), e);
        }
    }

    /**
     * Adjusts product stock through the application workflow.
     * 
     * @param productId The ID of the product
     * @param stockAdjustmentCommand The stock adjustment command
     * @return The updated product
     * @throws IllegalArgumentException if adjustment is invalid
     */
    @Transactional
    public Product adjustStock(UUID productId, StockAdjustmentCommand stockAdjustmentCommand) {
        log.info("Adjusting stock for product ID: {} by {}", productId, stockAdjustmentCommand.getQuantity());
        
        try {
            // Delegate to domain service for business logic
            Product result = productDomainService.adjustStock(productId, stockAdjustmentCommand.getQuantity());
            
            log.info("Successfully adjusted stock for product ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to adjust stock for product ID: {}", productId, e);
            throw new ProductApplicationException("Failed to adjust stock: " + e.getMessage(), e);
        }
    }

    /**
     * Changes product category through the application workflow.
     * 
     * @param productId The ID of the product
     * @param changeCategoryCommand The category change command
     * @return The updated product
     * @throws IllegalArgumentException if category change is invalid
     */
    @Transactional
    public Product changeCategory(UUID productId, ChangeCategoryCommand changeCategoryCommand) {
        log.info("Changing category for product ID: {} to category: {}", 
                productId, changeCategoryCommand.getCategoryId());
        
        try {
            // Find the new category
            Category newCategory = findCategoryById(changeCategoryCommand.getCategoryId());
            
            // Delegate to domain service for business logic
            Product result = productDomainService.changeCategory(productId, newCategory);
            
            log.info("Successfully changed category for product ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to change category for product ID: {}", productId, e);
            throw new ProductApplicationException("Failed to change category: " + e.getMessage(), e);
        }
    }

    /**
     * Updates product price through the application workflow.
     * 
     * @param productId The ID of the product
     * @param updatePriceCommand The price update command
     * @return The updated product
     * @throws IllegalArgumentException if price update is invalid
     */
    @Transactional
    public Product updatePrice(UUID productId, UpdatePriceCommand updatePriceCommand) {
        log.info("Updating price for product ID: {} to {}", productId, updatePriceCommand.getPrice());
        
        try {
            // Convert command to domain object
            Money newPrice = new Money(updatePriceCommand.getPrice(), 
                    Currency.getInstance(updatePriceCommand.getCurrency()));
            
            // Delegate to domain service for business logic
            Product result = productDomainService.updatePrice(productId, newPrice);
            
            log.info("Successfully updated price for product ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to update price for product ID: {}", productId, e);
            throw new ProductApplicationException("Failed to update price: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a product through the application workflow.
     * 
     * @param productId The ID of the product to deactivate
     * @throws IllegalArgumentException if deactivation is invalid
     */
    @Transactional
    public void deactivateProduct(UUID productId) {
        log.info("Deactivating product with ID: {}", productId);
        
        try {
            // Delegate to domain service for business logic
            productDomainService.deactivateProduct(productId);
            
            log.info("Successfully deactivated product with ID: {}", productId);
            
        } catch (Exception e) {
            log.error("Failed to deactivate product with ID: {}", productId, e);
            throw new ProductApplicationException("Failed to deactivate product: " + e.getMessage(), e);
        }
    }

    // ==================== QUERY OPERATIONS ====================

    /**
     * Finds a product by ID.
     * 
     * @param productId The product ID
     * @return The product
     * @throws IllegalArgumentException if product doesn't exist
     */
    public Product findProductById(UUID productId) {
        log.debug("Finding product with ID: {}", productId);
        
        try {
            return productDomainService.findProductById(productId);
        } catch (Exception e) {
            log.error("Failed to find product with ID: {}", productId, e);
            throw new ProductApplicationException("Failed to find product: " + e.getMessage(), e);
        }
    }

    /**
     * Finds products by category with pagination.
     * 
     * @param categoryId The category ID
     * @param pageable The pagination information
     * @return A page of products
     */
    public Page<Product> findProductsByCategory(UUID categoryId, Pageable pageable) {
        log.debug("Finding products by category ID: {} with pagination: {}", categoryId, pageable);
        
        try {
            List<Product> products = productRepository.findByCategoryId(categoryId, 
                    pageable.getPageNumber(), pageable.getPageSize());
            
            // In a real implementation, you would have a count method
            long totalElements = products.size(); // This is a simplification
            
            return new PageImpl<>(products, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to find products by category ID: {}", categoryId, e);
            throw new ProductApplicationException("Failed to find products by category: " + e.getMessage(), e);
        }
    }

    /**
     * Searches products by text with pagination.
     * 
     * @param searchText The search text
     * @param pageable The pagination information
     * @return A page of products
     */
    public Page<Product> searchProducts(String searchText, Pageable pageable) {
        log.debug("Searching products with text: {} and pagination: {}", searchText, pageable);
        
        try {
            List<Product> products = productRepository.searchByText(searchText, 
                    pageable.getPageNumber(), pageable.getPageSize());
            
            // In a real implementation, you would have a count method
            long totalElements = products.size(); // This is a simplification
            
            return new PageImpl<>(products, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to search products with text: {}", searchText, e);
            throw new ProductApplicationException("Failed to search products: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all products with pagination.
     * 
     * @param pageable The pagination information
     * @return A page of products
     */
    public Page<Product> findAllProducts(Pageable pageable) {
        log.debug("Finding all products with pagination: {}", pageable);
        
        try {
            List<Product> products = productRepository.findAll(pageable.getPageNumber(), 
                    pageable.getPageSize());
            
            long totalElements = productRepository.count();
            
            return new PageImpl<>(products, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to find all products", e);
            throw new ProductApplicationException("Failed to find all products: " + e.getMessage(), e);
        }
    }

    /**
     * Gets product statistics for reporting.
     * 
     * @return Product statistics
     */
    public ProductStatistics getProductStatistics() {
        log.debug("Getting product statistics");
        
        try {
            long totalProducts = productRepository.count();
            
            // In a real implementation, you would have more sophisticated statistics
            return ProductStatistics.builder()
                    .totalProducts(totalProducts)
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to get product statistics", e);
            throw new ProductApplicationException("Failed to get product statistics: " + e.getMessage(), e);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Converts a create product command to a Product domain object.
     */
    private Product convertToProduct(CreateProductCommand command) {
        Money price = new Money(command.getPrice(), Currency.getInstance(command.getCurrency()));
        Category category = findCategoryById(command.getCategoryId());
        
        return Product.builder()
                .name(command.getName())
                .description(command.getDescription())
                .price(price)
                .stockQuantity(command.getStockQuantity())
                .category(category)
                .build();
    }

    /**
     * Converts an update product command to a Product domain object.
     */
    private Product convertToProduct(UpdateProductCommand command) {
        Money price = new Money(command.getPrice(), Currency.getInstance(command.getCurrency()));
        Category category = findCategoryById(command.getCategoryId());
        
        return Product.builder()
                .name(command.getName())
                .description(command.getDescription())
                .price(price)
                .stockQuantity(command.getStockQuantity())
                .category(category)
                .build();
    }

    /**
     * Finds a category by ID.
     * 
     * @param categoryId The category ID
     * @return The category
     * @throws IllegalArgumentException if category doesn't exist
     */
    private Category findCategoryById(UUID categoryId) {
        // In a real implementation, you would have a CategoryRepository
        // For now, we'll create a mock category
        return Category.builder()
                .id(categoryId.toString())
                .name("Mock Category")
                .description("Mock category description")
                .active(true)
                .build();
    }

    // ==================== COMMAND CLASSES ====================

    /**
     * Command for creating a new product.
     */
    public static class CreateProductCommand {
        private String name;
        private String description;
        private BigDecimal price;
        private String currency;
        private int stockQuantity;
        private UUID categoryId;

        // Constructors, getters, and setters
        public CreateProductCommand() {}

        public CreateProductCommand(String name, String description, BigDecimal price, 
                                  String currency, int stockQuantity, UUID categoryId) {
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
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }

    /**
     * Command for updating an existing product.
     */
    public static class UpdateProductCommand {
        private String name;
        private String description;
        private BigDecimal price;
        private String currency;
        private int stockQuantity;
        private UUID categoryId;

        // Constructors, getters, and setters
        public UpdateProductCommand() {}

        public UpdateProductCommand(String name, String description, BigDecimal price, 
                                  String currency, int stockQuantity, UUID categoryId) {
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
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }

    /**
     * Command for adjusting product stock.
     */
    public static class StockAdjustmentCommand {
        private int quantity;

        // Constructors, getters, and setters
        public StockAdjustmentCommand() {}

        public StockAdjustmentCommand(int quantity) {
            this.quantity = quantity;
        }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    /**
     * Command for changing product category.
     */
    public static class ChangeCategoryCommand {
        private UUID categoryId;

        // Constructors, getters, and setters
        public ChangeCategoryCommand() {}

        public ChangeCategoryCommand(UUID categoryId) {
            this.categoryId = categoryId;
        }

        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
    }

    /**
     * Command for updating product price.
     */
    public static class UpdatePriceCommand {
        private BigDecimal price;
        private String currency;

        // Constructors, getters, and setters
        public UpdatePriceCommand() {}

        public UpdatePriceCommand(BigDecimal price, String currency) {
            this.price = price;
            this.currency = currency;
        }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    /**
     * Product statistics for reporting.
     */
    public static class ProductStatistics {
        private long totalProducts;

        // Constructors, getters, and setters
        public ProductStatistics() {}

        public ProductStatistics(long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

        public static ProductStatisticsBuilder builder() {
            return new ProductStatisticsBuilder();
        }

        public static class ProductStatisticsBuilder {
            private long totalProducts;

            public ProductStatisticsBuilder totalProducts(long totalProducts) {
                this.totalProducts = totalProducts;
                return this;
            }

            public ProductStatistics build() {
                return new ProductStatistics(totalProducts);
            }
        }
    }

    /**
     * Application-specific exception for product operations.
     */
    public static class ProductApplicationException extends RuntimeException {
        public ProductApplicationException(String message) {
            super(message);
        }

        public ProductApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
