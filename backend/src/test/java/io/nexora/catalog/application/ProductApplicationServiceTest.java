package io.nexora.catalog.application;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductApplicationService Tests")
class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private Product validProduct;
    private Category validCategory;
    private Money validPrice;
    private UUID productId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        
        validPrice = new Money(new BigDecimal("29.99"), Currency.getInstance("USD"));
        
        validCategory = Category.builder()
                .id(categoryId.toString())
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
    @DisplayName("Command Operations Tests")
    class CommandOperationsTests {

        @Nested
        @DisplayName("Create Product Tests")
        class CreateProductTests {

            @Test
            @DisplayName("Should create product successfully with valid command")
            void shouldCreateProductSuccessfullyWithValidCommand() {
                // Given
                ProductApplicationService.CreateProductCommand command = 
                        new ProductApplicationService.CreateProductCommand(
                                "New Product",
                                "A new product description",
                                new BigDecimal("39.99"),
                                "USD",
                                5,
                                categoryId
                        );

                when(productRepository.save(any(Product.class))).thenReturn(validProduct);

                // When
                Product result = productApplicationService.createProduct(command);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).save(any(Product.class));
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                ProductApplicationService.CreateProductCommand command = 
                        new ProductApplicationService.CreateProductCommand(
                                "Invalid Product",
                                "An invalid product",
                                new BigDecimal("39.99"),
                                "USD",
                                5,
                                categoryId
                        );

                when(productRepository.save(any(Product.class)))
                        .thenThrow(new IllegalArgumentException("Domain validation failed"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.createProduct(command));
                
                assertTrue(exception.getMessage().contains("Failed to create product"));
                assertTrue(exception.getMessage().contains("Domain validation failed"));
            }
        }

        @Nested
        @DisplayName("Update Product Tests")
        class UpdateProductTests {

            @Test
            @DisplayName("Should update product successfully with valid command")
            void shouldUpdateProductSuccessfullyWithValidCommand() {
                // Given
                ProductApplicationService.UpdateProductCommand command = 
                        new ProductApplicationService.UpdateProductCommand(
                                "Updated Product",
                                "An updated product description",
                                new BigDecimal("49.99"),
                                "USD",
                                15,
                                categoryId
                        );

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));
                when(productRepository.save(any(Product.class))).thenReturn(validProduct);

                // When
                Product result = productApplicationService.updateProduct(productId, command);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).save(any(Product.class));
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                ProductApplicationService.UpdateProductCommand command = 
                        new ProductApplicationService.UpdateProductCommand(
                                "Invalid Product",
                                "An invalid product",
                                new BigDecimal("49.99"),
                                "USD",
                                15,
                                categoryId
                        );

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.updateProduct(productId, command));
                
                assertTrue(exception.getMessage().contains("Failed to update product"));
                assertTrue(exception.getMessage().contains("Product not found"));
            }
        }

        @Nested
        @DisplayName("Adjust Stock Tests")
        class AdjustStockTests {

            @Test
            @DisplayName("Should adjust stock successfully with valid command")
            void shouldAdjustStockSuccessfullyWithValidCommand() {
                // Given
                ProductApplicationService.StockAdjustmentCommand command = 
                        new ProductApplicationService.StockAdjustmentCommand(5);

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));
                when(productRepository.save(any(Product.class))).thenReturn(validProduct);

                // When
                Product result = productApplicationService.adjustStock(productId, command);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).save(any(Product.class));
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                ProductApplicationService.StockAdjustmentCommand command = 
                        new ProductApplicationService.StockAdjustmentCommand(-20);

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.adjustStock(productId, command));
                
                assertTrue(exception.getMessage().contains("Failed to adjust stock"));
                assertTrue(exception.getMessage().contains("Insufficient stock"));
            }
        }

        @Nested
        @DisplayName("Change Category Tests")
        class ChangeCategoryTests {

            @Test
            @DisplayName("Should change category successfully with valid command")
            void shouldChangeCategorySuccessfullyWithValidCommand() {
                // Given
                ProductApplicationService.ChangeCategoryCommand command = 
                        new ProductApplicationService.ChangeCategoryCommand(categoryId);

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));
                when(productRepository.save(any(Product.class))).thenReturn(validProduct);

                // When
                Product result = productApplicationService.changeCategory(productId, command);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).save(any(Product.class));
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                ProductApplicationService.ChangeCategoryCommand command = 
                        new ProductApplicationService.ChangeCategoryCommand(categoryId);

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.changeCategory(productId, command));
                
                assertTrue(exception.getMessage().contains("Failed to change category"));
                assertTrue(exception.getMessage().contains("Category not found"));
            }
        }

        @Nested
        @DisplayName("Update Price Tests")
        class UpdatePriceTests {

            @Test
            @DisplayName("Should update price successfully with valid command")
            void shouldUpdatePriceSuccessfullyWithValidCommand() {
                // Given
                ProductApplicationService.UpdatePriceCommand command = 
                        new ProductApplicationService.UpdatePriceCommand(
                                new BigDecimal("59.99"),
                                "USD"
                        );

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));
                when(productRepository.save(any(Product.class))).thenReturn(validProduct);

                // When
                Product result = productApplicationService.updatePrice(productId, command);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).save(any(Product.class));
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                ProductApplicationService.UpdatePriceCommand command = 
                        new ProductApplicationService.UpdatePriceCommand(
                                new BigDecimal("59.99"),
                                "USD"
                        );

                when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.updatePrice(productId, command));
                
                assertTrue(exception.getMessage().contains("Failed to update price"));
                assertTrue(exception.getMessage().contains("Invalid price"));
            }
        }

        @Nested
        @DisplayName("Deactivate Product Tests")
        class DeactivateProductTests {

            @Test
            @DisplayName("Should deactivate product successfully")
            void shouldDeactivateProductSuccessfully() {
                // Given
                doNothing().when(productRepository).deleteById(productId);

                // When
                assertDoesNotThrow(() -> productApplicationService.deactivateProduct(productId));

                // Then
                verify(productRepository).deleteById(productId);
            }

            @Test
            @DisplayName("Should throw exception when domain service fails")
            void shouldThrowExceptionWhenDomainServiceFails() {
                // Given
                doThrow(new IllegalArgumentException("Product has remaining stock"))
                        .when(productRepository).deleteById(productId);

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.deactivateProduct(productId));
                
                assertTrue(exception.getMessage().contains("Failed to deactivate product"));
                assertTrue(exception.getMessage().contains("Product has remaining stock"));
            }
        }
    }

    @Nested
    @DisplayName("Query Operations Tests")
    class QueryOperationsTests {

        @Nested
        @DisplayName("Find Product Tests")
        class FindProductTests {

            @Test
            @DisplayName("Should find product successfully by ID")
            void shouldFindProductSuccessfullyById() {
                // Given
                when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(validProduct));

                // When
                Product result = productApplicationService.findProductById(productId);

                // Then
                assertNotNull(result);
                assertEquals(validProduct.getId(), result.getId());
                verify(productRepository).findById(productId);
            }

            @Test
            @DisplayName("Should throw exception when product not found")
            void shouldThrowExceptionWhenProductNotFound() {
                // Given
                when(productRepository.findById(productId))
                        .thenThrow(new IllegalArgumentException("Product not found"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.findProductById(productId));
                
                assertTrue(exception.getMessage().contains("Failed to find product"));
                assertTrue(exception.getMessage().contains("Product not found"));
            }
        }

        @Nested
        @DisplayName("Find Products by Category Tests")
        class FindProductsByCategoryTests {

            @Test
            @DisplayName("Should find products by category successfully")
            void shouldFindProductsByCategorySuccessfully() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                List<Product> products = List.of(validProduct);
                
                when(productRepository.findByCategoryId(categoryId, 0, 10))
                        .thenReturn(products);

                // When
                Page<Product> result = productApplicationService.findProductsByCategory(categoryId, pageable);

                // Then
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(validProduct.getId(), result.getContent().get(0).getId());
                verify(productRepository).findByCategoryId(categoryId, 0, 10);
            }

            @Test
            @DisplayName("Should throw exception when repository fails")
            void shouldThrowExceptionWhenRepositoryFails() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                
                when(productRepository.findByCategoryId(categoryId, 0, 10))
                        .thenThrow(new RuntimeException("Database error"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.findProductsByCategory(categoryId, pageable));
                
                assertTrue(exception.getMessage().contains("Failed to find products by category"));
                assertTrue(exception.getMessage().contains("Database error"));
            }
        }

        @Nested
        @DisplayName("Search Products Tests")
        class SearchProductsTests {

            @Test
            @DisplayName("Should search products successfully")
            void shouldSearchProductsSuccessfully() {
                // Given
                String searchText = "test";
                Pageable pageable = PageRequest.of(0, 10);
                List<Product> products = List.of(validProduct);
                
                when(productRepository.searchByText(searchText, 0, 10))
                        .thenReturn(products);

                // When
                Page<Product> result = productApplicationService.searchProducts(searchText, pageable);

                // Then
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(validProduct.getId(), result.getContent().get(0).getId());
                verify(productRepository).searchByText(searchText, 0, 10);
            }

            @Test
            @DisplayName("Should throw exception when repository fails")
            void shouldThrowExceptionWhenRepositoryFails() {
                // Given
                String searchText = "test";
                Pageable pageable = PageRequest.of(0, 10);
                
                when(productRepository.searchByText(searchText, 0, 10))
                        .thenThrow(new RuntimeException("Search error"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.searchProducts(searchText, pageable));
                
                assertTrue(exception.getMessage().contains("Failed to search products"));
                assertTrue(exception.getMessage().contains("Search error"));
            }
        }

        @Nested
        @DisplayName("Find All Products Tests")
        class FindAllProductsTests {

            @Test
            @DisplayName("Should find all products successfully")
            void shouldFindAllProductsSuccessfully() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                List<Product> products = List.of(validProduct);
                
                when(productRepository.findAll(0, 10)).thenReturn(products);
                when(productRepository.count()).thenReturn(1L);

                // When
                Page<Product> result = productApplicationService.findAllProducts(pageable);

                // Then
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(1L, result.getTotalElements());
                assertEquals(validProduct.getId(), result.getContent().get(0).getId());
                verify(productRepository).findAll(0, 10);
                verify(productRepository).count();
            }

            @Test
            @DisplayName("Should throw exception when repository fails")
            void shouldThrowExceptionWhenRepositoryFails() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                
                when(productRepository.findAll(0, 10))
                        .thenThrow(new RuntimeException("Database error"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.findAllProducts(pageable));
                
                assertTrue(exception.getMessage().contains("Failed to find all products"));
                assertTrue(exception.getMessage().contains("Database error"));
            }
        }

        @Nested
        @DisplayName("Get Product Statistics Tests")
        class GetProductStatisticsTests {

            @Test
            @DisplayName("Should get product statistics successfully")
            void shouldGetProductStatisticsSuccessfully() {
                // Given
                when(productRepository.count()).thenReturn(100L);

                // When
                ProductApplicationService.ProductStatistics result = 
                        productApplicationService.getProductStatistics();

                // Then
                assertNotNull(result);
                assertEquals(100L, result.getTotalProducts());
                verify(productRepository).count();
            }

            @Test
            @DisplayName("Should throw exception when repository fails")
            void shouldThrowExceptionWhenRepositoryFails() {
                // Given
                when(productRepository.count())
                        .thenThrow(new RuntimeException("Database error"));

                // When & Then
                ProductApplicationService.ProductApplicationException exception = 
                        assertThrows(ProductApplicationService.ProductApplicationException.class,
                                () -> productApplicationService.getProductStatistics());
                
                assertTrue(exception.getMessage().contains("Failed to get product statistics"));
                assertTrue(exception.getMessage().contains("Database error"));
            }
        }
    }

    @Nested
    @DisplayName("Command Classes Tests")
    class CommandClassesTests {

        @Test
        @DisplayName("Should create CreateProductCommand with all fields")
        void shouldCreateCreateProductCommandWithAllFields() {
            // Given
            ProductApplicationService.CreateProductCommand command = 
                    new ProductApplicationService.CreateProductCommand(
                            "Test Product",
                            "Test Description",
                            new BigDecimal("29.99"),
                            "USD",
                            10,
                            categoryId
                    );

            // Then
            assertEquals("Test Product", command.getName());
            assertEquals("Test Description", command.getDescription());
            assertEquals(new BigDecimal("29.99"), command.getPrice());
            assertEquals("USD", command.getCurrency());
            assertEquals(10, command.getStockQuantity());
            assertEquals(categoryId, command.getCategoryId());
        }

        @Test
        @DisplayName("Should create UpdateProductCommand with all fields")
        void shouldCreateUpdateProductCommandWithAllFields() {
            // Given
            ProductApplicationService.UpdateProductCommand command = 
                    new ProductApplicationService.UpdateProductCommand(
                            "Updated Product",
                            "Updated Description",
                            new BigDecimal("39.99"),
                            "USD",
                            15,
                            categoryId
                    );

            // Then
            assertEquals("Updated Product", command.getName());
            assertEquals("Updated Description", command.getDescription());
            assertEquals(new BigDecimal("39.99"), command.getPrice());
            assertEquals("USD", command.getCurrency());
            assertEquals(15, command.getStockQuantity());
            assertEquals(categoryId, command.getCategoryId());
        }

        @Test
            @DisplayName("Should create StockAdjustmentCommand with quantity")
        void shouldCreateStockAdjustmentCommandWithQuantity() {
            // Given
            ProductApplicationService.StockAdjustmentCommand command = 
                    new ProductApplicationService.StockAdjustmentCommand(5);

            // Then
            assertEquals(5, command.getQuantity());
        }

        @Test
        @DisplayName("Should create ChangeCategoryCommand with category ID")
        void shouldCreateChangeCategoryCommandWithCategoryId() {
            // Given
            ProductApplicationService.ChangeCategoryCommand command = 
                    new ProductApplicationService.ChangeCategoryCommand(categoryId);

            // Then
            assertEquals(categoryId, command.getCategoryId());
        }

        @Test
        @DisplayName("Should create UpdatePriceCommand with price and currency")
        void shouldCreateUpdatePriceCommandWithPriceAndCurrency() {
            // Given
            ProductApplicationService.UpdatePriceCommand command = 
                    new ProductApplicationService.UpdatePriceCommand(
                            new BigDecimal("49.99"),
                            "USD"
                    );

            // Then
            assertEquals(new BigDecimal("49.99"), command.getPrice());
            assertEquals("USD", command.getCurrency());
        }

        @Test
        @DisplayName("Should create ProductStatistics with total products")
        void shouldCreateProductStatisticsWithTotalProducts() {
            // Given
            ProductApplicationService.ProductStatistics statistics = 
                    ProductApplicationService.ProductStatistics.builder()
                            .totalProducts(100L)
                            .build();

            // Then
            assertEquals(100L, statistics.getTotalProducts());
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Should create ProductApplicationException with message")
        void shouldCreateProductApplicationExceptionWithMessage() {
            // Given
            String message = "Test error message";

            // When
            ProductApplicationService.ProductApplicationException exception = 
                    new ProductApplicationService.ProductApplicationException(message);

            // Then
            assertEquals(message, exception.getMessage());
        }

        @Test
        @DisplayName("Should create ProductApplicationException with message and cause")
        void shouldCreateProductApplicationExceptionWithMessageAndCause() {
            // Given
            String message = "Test error message";
            Throwable cause = new RuntimeException("Root cause");

            // When
            ProductApplicationService.ProductApplicationException exception = 
                    new ProductApplicationService.ProductApplicationException(message, cause);

            // Then
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }
}
