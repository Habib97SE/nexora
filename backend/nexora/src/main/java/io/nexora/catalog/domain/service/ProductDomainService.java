package main.java.io.nexora.catalog.domain.service;

import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.Product;
import io.nexora.catalog.domain.ProductRepository;
import io.nexora.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Service for Product aggregates.
 * 
 * This service encapsulates complex business logic that doesn't naturally fit
 * within a single Product entity but is essential to the Product domain.
 * 
 * Responsibilities:
 * - Product lifecycle management (creation, updates, deletion)
 * - Business rule enforcement across multiple aggregates
 * - Complex domain operations that require coordination between entities
 * - Domain-specific validation and invariants
 * 
 * Design Principles Applied:
 * - Single Responsibility: Focuses solely on Product domain operations
 * - Open/Closed: Extensible for new business rules without modification
 * - Dependency Inversion: Depends on abstractions (ProductRepository)
 * - Domain-Driven Design: Encapsulates domain logic, not infrastructure concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductDomainService {

    private final ProductRepository productRepository;

    /**
     * Creates a new product with business rule validation.
     * 
     * Business Rules:
     * - Product name must be unique within the same category
     * - Category must be active
     * - Price must be positive
     * - Stock quantity cannot be negative
     * 
     * @param product The product to create
     * @return The created product with generated ID and timestamps
     * @throws IllegalArgumentException if business rules are violated
     */
    @Transactional
    public Product createProduct(Product product) {
        log.debug("Creating new product: {}", product.getName());
        
        validateProductCreation(product);
        
        // Set creation timestamp
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product with ID: {}", savedProduct.getId());
        
        return savedProduct;
    }

    /**
     * Updates an existing product with business rule validation.
     * 
     * Business Rules:
     * - Product must exist
     * - Name uniqueness within category (if name changed)
     * - Category must be active (if category changed)
     * - Price must be positive
     * - Stock quantity cannot be negative
     * 
     * @param productId The ID of the product to update
     * @param updatedProduct The updated product data
     * @return The updated product
     * @throws IllegalArgumentException if product doesn't exist or business rules are violated
     */
    @Transactional
    public Product updateProduct(UUID productId, Product updatedProduct) {
        log.debug("Updating product with ID: {}", productId);
        
        Product existingProduct = findProductById(productId);
        validateProductUpdate(existingProduct, updatedProduct);
        
        // Preserve creation timestamp and ID
        updatedProduct.setId(existingProduct.getId());
        updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
        updatedProduct.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(updatedProduct);
        log.info("Successfully updated product with ID: {}", savedProduct.getId());
        
        return savedProduct;
    }

    /**
     * Adjusts product stock quantity with business rule validation.
     * 
     * Business Rules:
     * - Product must exist
     * - Stock adjustment cannot result in negative quantity
     * - Large stock adjustments require logging
     * 
     * @param productId The ID of the product
     * @param quantityAdjustment The quantity to add (positive) or subtract (negative)
     * @return The updated product
     * @throws IllegalArgumentException if adjustment would result in negative stock
     */
    @Transactional
    public Product adjustStock(UUID productId, int quantityAdjustment) {
        log.debug("Adjusting stock for product ID: {} by {}", productId, quantityAdjustment);
        
        Product product = findProductById(productId);
        int newQuantity = product.getStockQuantity() + quantityAdjustment;
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                String.format("Stock adjustment would result in negative quantity. Current: %d, Adjustment: %d", 
                    product.getStockQuantity(), quantityAdjustment)
            );
        }
        
        // Log significant stock changes
        if (Math.abs(quantityAdjustment) > 100) {
            log.warn("Large stock adjustment for product {}: {} (new total: {})", 
                product.getName(), quantityAdjustment, newQuantity);
        }
        
        product.setStockQuantity(newQuantity);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Stock adjusted for product {}: {} -> {}", 
            product.getName(), product.getStockQuantity(), newQuantity);
        
        return savedProduct;
    }

    /**
     * Changes product category with business rule validation.
     * 
     * Business Rules:
     * - Product must exist
     * - New category must be active
     * - Product name must be unique in the new category
     * 
     * @param productId The ID of the product
     * @param newCategory The new category
     * @return The updated product
     * @throws IllegalArgumentException if business rules are violated
     */
    @Transactional
    public Product changeCategory(UUID productId, Category newCategory) {
        log.debug("Changing category for product ID: {} to category: {}", productId, newCategory.getName());
        
        Product product = findProductById(productId);
        validateCategoryChange(product, newCategory);
        
        product.setCategory(newCategory);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Category changed for product {}: {} -> {}", 
            product.getName(), product.getCategory().getName(), newCategory.getName());
        
        return savedProduct;
    }

    /**
     * Updates product price with business rule validation.
     * 
     * Business Rules:
     * - Product must exist
     * - New price must be positive
     * - Price changes are logged for audit purposes
     * 
     * @param productId The ID of the product
     * @param newPrice The new price
     * @return The updated product
     * @throws IllegalArgumentException if price is invalid
     */
    @Transactional
    public Product updatePrice(UUID productId, Money newPrice) {
        log.debug("Updating price for product ID: {} to {}", productId, newPrice);
        
        Product product = findProductById(productId);
        validatePriceUpdate(newPrice);
        
        Money oldPrice = product.getPrice();
        product.setPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        log.info("Price updated for product {}: {} -> {}", 
            product.getName(), oldPrice, newPrice);
        
        return savedProduct;
    }

    /**
     * Deactivates a product (soft delete pattern).
     * 
     * Business Rules:
     * - Product must exist
     * - Product must have zero stock before deactivation
     * 
     * @param productId The ID of the product to deactivate
     * @throws IllegalArgumentException if product has remaining stock
     */
    @Transactional
    public void deactivateProduct(UUID productId) {
        log.debug("Deactivating product with ID: {}", productId);
        
        Product product = findProductById(productId);
        
        if (product.getStockQuantity() > 0) {
            throw new IllegalArgumentException(
                String.format("Cannot deactivate product '%s' with remaining stock: %d", 
                    product.getName(), product.getStockQuantity())
            );
        }
        
        // In a real implementation, you might have an 'active' field
        // For now, we'll delete the product
        productRepository.deleteById(productId);
        log.info("Product deactivated: {}", product.getName());
    }

    /**
     * Finds a product by ID with proper error handling.
     * 
     * @param productId The product ID
     * @return The product
     * @throws IllegalArgumentException if product doesn't exist
     */
    public Product findProductById(UUID productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Product with ID %s not found", productId)
            ));
    }

    /**
     * Validates product creation business rules.
     */
    private void validateProductCreation(Product product) {
        validateBasicProductRules(product);
        validateCategoryIsActive(product.getCategory());
        validateNameUniquenessInCategory(product.getName(), product.getCategory().getId(), null);
    }

    /**
     * Validates product update business rules.
     */
    private void validateProductUpdate(Product existingProduct, Product updatedProduct) {
        validateBasicProductRules(updatedProduct);
        validateCategoryIsActive(updatedProduct.getCategory());
        
        // Only validate name uniqueness if name changed
        if (!existingProduct.getName().equals(updatedProduct.getName())) {
            validateNameUniquenessInCategory(
                updatedProduct.getName(), 
                updatedProduct.getCategory().getId(), 
                existingProduct.getId()
            );
        }
    }

    /**
     * Validates basic product business rules.
     */
    private void validateBasicProductRules(Product product) {
        if (product.getPrice() != null && product.getPrice().amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Product stock quantity cannot be negative");
        }
    }

    /**
     * Validates that category is active.
     */
    private void validateCategoryIsActive(Category category) {
        if (!category.isActive()) {
            throw new IllegalArgumentException(
                String.format("Cannot use inactive category: %s", category.getName())
            );
        }
    }

    /**
     * Validates that product name is unique within a category.
     */
    private void validateNameUniquenessInCategory(String productName, String categoryId, String excludeProductId) {
        // This would typically involve a repository method to check uniqueness
        // For now, we'll implement a basic check
        UUID categoryUuid = UUID.fromString(categoryId);
        List<Product> productsInCategory = productRepository.findByCategoryId(categoryUuid, 0, Integer.MAX_VALUE);
        
        boolean nameExists = productsInCategory.stream()
            .anyMatch(p -> p.getName().equalsIgnoreCase(productName) && 
                          (excludeProductId == null || !p.getId().equals(excludeProductId)));
        
        if (nameExists) {
            throw new IllegalArgumentException(
                String.format("Product name '%s' already exists in this category", productName)
            );
        }
    }

    /**
     * Validates category change business rules.
     */
    private void validateCategoryChange(Product product, Category newCategory) {
        validateCategoryIsActive(newCategory);
        validateNameUniquenessInCategory(product.getName(), newCategory.getId(), product.getId());
    }

    /**
     * Validates price update business rules.
     */
    private void validatePriceUpdate(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        
        if (newPrice.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
    }
}
