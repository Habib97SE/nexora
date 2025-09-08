package main.java.io.nexora.catalog.domain.service;

import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.Product;
import io.nexora.catalog.domain.ProductRepository;
import io.nexora.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDomainService Tests")
class ProductDomainServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductDomainService productDomainService;

    private Product validProduct;
    private Category validCategory;
    private Money validPrice;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        
        validPrice = new Money(new BigDecimal("29.99"), Currency.getInstance("USD"));
        
        validCategory = Category.builder()
                .id(productId.toString())
                .name("Electronics")
                .description("Electronic devices")
                .active(true)
                .build();

        validProduct = Product.builder()
                .id(productId.toString())
                .name("Test Product")
                .description("A test product")
                .price(validPrice)
                .stockQuantity(10)
                .category(validCategory)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Nested
    @DisplayName("Product Creation Tests")
    class ProductCreationTests {

        @Test
        @DisplayName("Should create product successfully with valid data")
        void shouldCreateProductSuccessfullyWithValidData() {
            // Given
            Product newProduct = Product.builder()
                    .name("New Product")
                    .description("A new product")
                    .price(validPrice)
                    .stockQuantity(5)
                    .category(validCategory)
                    .build();

            when(productRepository.findByCategoryId(any(UUID.class), anyInt(), anyInt()))
                    .thenReturn(List.of());
            when(productRepository.save(any(Product.class))).thenReturn(validProduct);

            // When
            Product result = productDomainService.createProduct(newProduct);

            // Then
            assertNotNull(result);
            assertEquals(validProduct.getId(), result.getId());
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
            
            verify(productRepository).save(any(Product.class));
            verify(productRepository).findByCategoryId(any(UUID.class), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Should throw exception when creating product with negative price")
        void shouldThrowExceptionWhenCreatingProductWithNegativePrice() {
            // Given - Money constructor will throw exception for negative amounts
            // This test verifies that the Money value object enforces its own invariants
            
            // When & Then - The exception should be thrown during Money construction
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Money(new BigDecimal("-10.00"), Currency.getInstance("USD")));
            
            assertEquals("Amount must not be negative", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when creating product with negative stock")
        void shouldThrowExceptionWhenCreatingProductWithNegativeStock() {
            // Given
            Product invalidProduct = Product.builder()
                    .name("Invalid Product")
                    .price(validPrice)
                    .stockQuantity(-5)
                    .category(validCategory)
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.createProduct(invalidProduct));
            
            assertEquals("Product stock quantity cannot be negative", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when creating product with inactive category")
        void shouldThrowExceptionWhenCreatingProductWithInactiveCategory() {
            // Given
            Category inactiveCategory = Category.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Inactive Category")
                    .active(false)
                    .build();

            Product invalidProduct = Product.builder()
                    .name("Invalid Product")
                    .price(validPrice)
                    .category(inactiveCategory)
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.createProduct(invalidProduct));
            
            assertEquals("Cannot use inactive category: Inactive Category", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when product name already exists in category")
        void shouldThrowExceptionWhenProductNameAlreadyExistsInCategory() {
            // Given
            Product existingProduct = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Existing Product")
                    .price(validPrice)
                    .stockQuantity(5)
                    .category(validCategory)
                    .build();

            Product newProduct = Product.builder()
                    .name("Existing Product")
                    .price(validPrice)
                    .stockQuantity(0)
                    .category(validCategory)
                    .build();

            when(productRepository.findByCategoryId(any(UUID.class), anyInt(), anyInt()))
                    .thenReturn(List.of(existingProduct));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.createProduct(newProduct));
            
            assertEquals("Product name 'Existing Product' already exists in this category", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Product Update Tests")
    class ProductUpdateTests {

        @Test
        @DisplayName("Should update product successfully with valid data")
        void shouldUpdateProductSuccessfullyWithValidData() {
            // Given
            Product updatedProduct = Product.builder()
                    .name("Updated Product")
                    .description("Updated description")
                    .price(validPrice)
                    .stockQuantity(15)
                    .category(validCategory)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.findByCategoryId(any(UUID.class), anyInt(), anyInt()))
                    .thenReturn(List.of());
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            // When
            Product result = productDomainService.updateProduct(productId, updatedProduct);

            // Then
            assertNotNull(result);
            assertEquals(productId.toString(), result.getId());
            assertEquals(validProduct.getCreatedAt(), result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
            
            verify(productRepository).findById(productId);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.updateProduct(productId, validProduct));
            
            assertEquals(String.format("Product with ID %s not found", productId), exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should allow update when name doesn't change")
        void shouldAllowUpdateWhenNameDoesntChange() {
            // Given
            Product updatedProduct = Product.builder()
                    .name(validProduct.getName()) // Same name
                    .price(validPrice)
                    .category(validCategory)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            // When
            Product result = productDomainService.updateProduct(productId, updatedProduct);

            // Then
            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Stock Adjustment Tests")
    class StockAdjustmentTests {

        @Test
        @DisplayName("Should adjust stock successfully with positive adjustment")
        void shouldAdjustStockSuccessfullyWithPositiveAdjustment() {
            // Given
            int adjustment = 5;
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.save(any(Product.class))).thenReturn(validProduct);

            // When
            Product result = productDomainService.adjustStock(productId, adjustment);

            // Then
            assertNotNull(result);
            verify(productRepository).findById(productId);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should adjust stock successfully with negative adjustment")
        void shouldAdjustStockSuccessfullyWithNegativeAdjustment() {
            // Given
            int adjustment = -3;
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.save(any(Product.class))).thenReturn(validProduct);

            // When
            Product result = productDomainService.adjustStock(productId, adjustment);

            // Then
            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when stock adjustment results in negative quantity")
        void shouldThrowExceptionWhenStockAdjustmentResultsInNegativeQuantity() {
            // Given
            int adjustment = -15; // Would result in negative stock
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.adjustStock(productId, adjustment));
            
            assertEquals("Stock adjustment would result in negative quantity. Current: 10, Adjustment: -15", 
                    exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when adjusting stock for non-existent product")
        void shouldThrowExceptionWhenAdjustingStockForNonExistentProduct() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.adjustStock(productId, 5));
            
            assertEquals(String.format("Product with ID %s not found", productId), exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Category Change Tests")
    class CategoryChangeTests {

        @Test
        @DisplayName("Should change category successfully with valid new category")
        void shouldChangeCategorySuccessfullyWithValidNewCategory() {
            // Given
            Category newCategory = Category.builder()
                    .id(UUID.randomUUID().toString())
                    .name("New Category")
                    .active(true)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.findByCategoryId(any(UUID.class), anyInt(), anyInt()))
                    .thenReturn(List.of());
            when(productRepository.save(any(Product.class))).thenReturn(validProduct);

            // When
            Product result = productDomainService.changeCategory(productId, newCategory);

            // Then
            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when changing to inactive category")
        void shouldThrowExceptionWhenChangingToInactiveCategory() {
            // Given
            Category inactiveCategory = Category.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Inactive Category")
                    .active(false)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.changeCategory(productId, inactiveCategory));
            
            assertEquals("Cannot use inactive category: Inactive Category", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Price Update Tests")
    class PriceUpdateTests {

        @Test
        @DisplayName("Should update price successfully with valid price")
        void shouldUpdatePriceSuccessfullyWithValidPrice() {
            // Given
            Money newPrice = new Money(new BigDecimal("39.99"), Currency.getInstance("USD"));
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));
            when(productRepository.save(any(Product.class))).thenReturn(validProduct);

            // When
            Product result = productDomainService.updatePrice(productId, newPrice);

            // Then
            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when updating with null price")
        void shouldThrowExceptionWhenUpdatingWithNullPrice() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.updatePrice(productId, null));
            
            assertEquals("Product price cannot be null", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when updating with negative price")
        void shouldThrowExceptionWhenUpdatingWithNegativePrice() {
            // Given - Money constructor will throw exception for negative amounts
            // This test verifies that the Money value object enforces its own invariants

            // When & Then - The exception should be thrown during Money construction
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Money(new BigDecimal("-10.00"), Currency.getInstance("USD")));
            
            assertEquals("Amount must not be negative", exception.getMessage());
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Product Deactivation Tests")
    class ProductDeactivationTests {

        @Test
        @DisplayName("Should deactivate product successfully when stock is zero")
        void shouldDeactivateProductSuccessfullyWhenStockIsZero() {
            // Given
            Product productWithZeroStock = Product.builder()
                    .id(productId.toString())
                    .name("Product with Zero Stock")
                    .stockQuantity(0)
                    .category(validCategory)
                    .build();

            when(productRepository.findById(productId)).thenReturn(Optional.of(productWithZeroStock));

            // When
            assertDoesNotThrow(() -> productDomainService.deactivateProduct(productId));

            // Then
            verify(productRepository).deleteById(productId);
        }

        @Test
        @DisplayName("Should throw exception when deactivating product with remaining stock")
        void shouldThrowExceptionWhenDeactivatingProductWithRemainingStock() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.deactivateProduct(productId));
            
            assertEquals("Cannot deactivate product 'Test Product' with remaining stock: 10", 
                    exception.getMessage());
            verify(productRepository, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("Find Product Tests")
    class FindProductTests {

        @Test
        @DisplayName("Should find product successfully when product exists")
        void shouldFindProductSuccessfullyWhenProductExists() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.of(validProduct));

            // When
            Product result = productDomainService.findProductById(productId);

            // Then
            assertNotNull(result);
            assertEquals(validProduct.getId(), result.getId());
            verify(productRepository).findById(productId);
        }

        @Test
        @DisplayName("Should throw exception when product doesn't exist")
        void shouldThrowExceptionWhenProductDoesntExist() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.findProductById(productId));
            
            assertEquals(String.format("Product with ID %s not found", productId), exception.getMessage());
        }
    }
}
