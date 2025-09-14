package io.nexora.catalog.interfaces;

import io.nexora.catalog.application.ProductApplicationService;
import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.Product;
import io.nexora.catalog.interfaces.rest.v1.ProductController;
import io.nexora.catalog.interfaces.rest.v1.dto.CreateProductRequest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Mock
    private ProductApplicationService productApplicationService;

    @InjectMocks
    private ProductController productController;

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
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Nested
        @DisplayName("Create Product Tests")
        class CreateProductTests {

            @Test
            @DisplayName("Should create product successfully with valid request")
            void shouldCreateProductSuccessfullyWithValidRequest() {
                // Given
                CreateProductRequest request = new CreateProductRequest(
                        "New Product",
                        "A new product description",
                        new BigDecimal("39.99"),
                        Currency.getInstance("USD"),
                        5,
                        categoryId
                );

                when(productApplicationService.createProduct(any(ProductApplicationService.CreateProductCommand.class)))
                        .thenReturn(validProduct);

                // When
                var response = productController.createProduct(request);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());
                assertEquals(validProduct.getDescription(), response.getBody().getDescription());
                assertEquals(validProduct.getPrice().amount(), response.getBody().getPrice());
                assertEquals(validProduct.getPrice().currency().getCurrencyCode(), response.getBody().getCurrency());
                assertEquals(validProduct.getStockQuantity(), response.getBody().getStockQuantity());
                assertEquals(validProduct.getCategory().getId(), response.getBody().getCategoryId());
                assertEquals(validProduct.getCategory().getName(), response.getBody().getCategoryName());

                verify(productApplicationService).createProduct(any(ProductApplicationService.CreateProductCommand.class));
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                CreateProductRequest request = new CreateProductRequest(
                        "New Product",
                        "A new product description",
                        new BigDecimal("39.99"),
                        Currency.getInstance("USD"),
                        5,
                        categoryId
                );

                when(productApplicationService.createProduct(any(ProductApplicationService.CreateProductCommand.class)))
                        .thenThrow(new RuntimeException("Application service error"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.createProduct(request));
                
                assertTrue(exception.getMessage().contains("Failed to create product"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).createProduct(any(ProductApplicationService.CreateProductCommand.class));
            }
        }

        @Nested
        @DisplayName("Get Product Tests")
        class GetProductTests {

            @Test
            @DisplayName("Should get product successfully with valid ID")
            void shouldGetProductSuccessfullyWithValidId() {
                // Given
                when(productApplicationService.findProductById(productId))
                        .thenReturn(validProduct);

                // When
                var response = productController.getProduct(productId);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());
                assertEquals(validProduct.getDescription(), response.getBody().getDescription());
                assertEquals(validProduct.getPrice().amount(), response.getBody().getPrice());
                assertEquals(validProduct.getPrice().currency().getCurrencyCode(), response.getBody().getCurrency());
                assertEquals(validProduct.getStockQuantity(), response.getBody().getStockQuantity());
                assertEquals(validProduct.getCategory().getId(), response.getBody().getCategoryId());
                assertEquals(validProduct.getCategory().getName(), response.getBody().getCategoryName());

                verify(productApplicationService).findProductById(productId);
            }

            @Test
            @DisplayName("Should throw exception when product not found")
            void shouldThrowExceptionWhenProductNotFound() {
                // Given
                when(productApplicationService.findProductById(productId))
                        .thenThrow(new RuntimeException("Product not found"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.getProduct(productId));
                
                assertTrue(exception.getMessage().contains("Failed to retrieve product"));
                assertTrue(exception.getMessage().contains("Product not found"));

                verify(productApplicationService).findProductById(productId);
            }
        }

        @Nested
        @DisplayName("Update Product Tests")
        class UpdateProductTests {

            @Test
            @DisplayName("Should update product successfully with valid request")
            void shouldUpdateProductSuccessfullyWithValidRequest() {
                // Given
                ProductController.UpdateProductRequest request = new ProductController.UpdateProductRequest(
                        "Updated Product",
                        "An updated product description",
                        new BigDecimal("49.99"),
                        "USD",
                        15,
                        categoryId
                );

                when(productApplicationService.updateProduct(eq(productId), any(ProductApplicationService.UpdateProductCommand.class)))
                        .thenReturn(validProduct);

                // When
                var response = productController.updateProduct(productId, request);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());

                verify(productApplicationService).updateProduct(eq(productId), any(ProductApplicationService.UpdateProductCommand.class));
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                ProductController.UpdateProductRequest request = new ProductController.UpdateProductRequest(
                        "Updated Product",
                        "An updated product description",
                        new BigDecimal("49.99"),
                        "USD",
                        15,
                        categoryId
                );

                when(productApplicationService.updateProduct(eq(productId), any(ProductApplicationService.UpdateProductCommand.class)))
                        .thenThrow(new RuntimeException("Application service error"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.updateProduct(productId, request));
                
                assertTrue(exception.getMessage().contains("Failed to update product"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).updateProduct(eq(productId), any(ProductApplicationService.UpdateProductCommand.class));
            }
        }

        @Nested
        @DisplayName("Delete Product Tests")
        class DeleteProductTests {

            @Test
            @DisplayName("Should deactivate product successfully")
            void shouldDeactivateProductSuccessfully() {
                // Given
                doNothing().when(productApplicationService).deactivateProduct(productId);

                // When
                var response = productController.deactivateProduct(productId);

                // Then
                assertNotNull(response);
                // No content response should be returned

                verify(productApplicationService).deactivateProduct(productId);
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                doThrow(new RuntimeException("Application service error"))
                        .when(productApplicationService).deactivateProduct(productId);

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.deactivateProduct(productId));
                
                assertTrue(exception.getMessage().contains("Failed to deactivate product"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).deactivateProduct(productId);
            }
        }
    }

    @Nested
    @DisplayName("Management Operations Tests")
    class ManagementOperationsTests {

        @Nested
        @DisplayName("Adjust Stock Tests")
        class AdjustStockTests {

            @Test
            @DisplayName("Should adjust stock successfully with valid request")
            void shouldAdjustStockSuccessfullyWithValidRequest() {
                // Given
                ProductController.StockAdjustmentRequest request = new ProductController.StockAdjustmentRequest(5);

                when(productApplicationService.adjustStock(eq(productId), any(ProductApplicationService.StockAdjustmentCommand.class)))
                        .thenReturn(validProduct);

                // When
                var response = productController.adjustStock(productId, request);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());

                verify(productApplicationService).adjustStock(eq(productId), any(ProductApplicationService.StockAdjustmentCommand.class));
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                ProductController.StockAdjustmentRequest request = new ProductController.StockAdjustmentRequest(5);

                when(productApplicationService.adjustStock(eq(productId), any(ProductApplicationService.StockAdjustmentCommand.class)))
                        .thenThrow(new RuntimeException("Application service error"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.adjustStock(productId, request));
                
                assertTrue(exception.getMessage().contains("Failed to adjust stock"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).adjustStock(eq(productId), any(ProductApplicationService.StockAdjustmentCommand.class));
            }
        }

        @Nested
        @DisplayName("Change Category Tests")
        class ChangeCategoryTests {

            @Test
            @DisplayName("Should change category successfully with valid request")
            void shouldChangeCategorySuccessfullyWithValidRequest() {
                // Given
                ProductController.ChangeCategoryRequest request = new ProductController.ChangeCategoryRequest(categoryId);

                when(productApplicationService.changeCategory(eq(productId), any(ProductApplicationService.ChangeCategoryCommand.class)))
                        .thenReturn(validProduct);

                // When
                var response = productController.changeCategory(productId, request);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());

                verify(productApplicationService).changeCategory(eq(productId), any(ProductApplicationService.ChangeCategoryCommand.class));
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                ProductController.ChangeCategoryRequest request = new ProductController.ChangeCategoryRequest(categoryId);

                when(productApplicationService.changeCategory(eq(productId), any(ProductApplicationService.ChangeCategoryCommand.class)))
                        .thenThrow(new RuntimeException("Application service error"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.changeCategory(productId, request));
                
                assertTrue(exception.getMessage().contains("Failed to change category"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).changeCategory(eq(productId), any(ProductApplicationService.ChangeCategoryCommand.class));
            }
        }

        @Nested
        @DisplayName("Update Price Tests")
        class UpdatePriceTests {

            @Test
            @DisplayName("Should update price successfully with valid request")
            void shouldUpdatePriceSuccessfullyWithValidRequest() {
                // Given
                ProductController.UpdatePriceRequest request = new ProductController.UpdatePriceRequest(
                        new BigDecimal("59.99"),
                        "USD"
                );

                when(productApplicationService.updatePrice(eq(productId), any(ProductApplicationService.UpdatePriceCommand.class)))
                        .thenReturn(validProduct);

                // When
                var response = productController.updatePrice(productId, request);

                // Then
                assertNotNull(response);
                assertEquals(validProduct.getId(), response.getBody().getId());
                assertEquals(validProduct.getName(), response.getBody().getName());

                verify(productApplicationService).updatePrice(eq(productId), any(ProductApplicationService.UpdatePriceCommand.class));
            }

            @Test
            @DisplayName("Should throw exception when application service fails")
            void shouldThrowExceptionWhenApplicationServiceFails() {
                // Given
                ProductController.UpdatePriceRequest request = new ProductController.UpdatePriceRequest(
                        new BigDecimal("59.99"),
                        "USD"
                );

                when(productApplicationService.updatePrice(eq(productId), any(ProductApplicationService.UpdatePriceCommand.class)))
                        .thenThrow(new RuntimeException("Application service error"));

                // When & Then
                ProductController.ProductControllerException exception = 
                        assertThrows(ProductController.ProductControllerException.class,
                                () -> productController.updatePrice(productId, request));
                
                assertTrue(exception.getMessage().contains("Failed to update price"));
                assertTrue(exception.getMessage().contains("Application service error"));

                verify(productApplicationService).updatePrice(eq(productId), any(ProductApplicationService.UpdatePriceCommand.class));
            }
        }
    }

    @Nested
    @DisplayName("DTO Classes Tests")
    class DtoClassesTests {

        @Test
        @DisplayName("Should create CreateProductRequest with all fields")
        void shouldCreateCreateProductRequestWithAllFields() {
            // Given
            CreateProductRequest request = new CreateProductRequest(
                    "Test Product",
                    "Test Description",
                    new BigDecimal("29.99"),
                    Currency.getInstance("USD"),
                    10,
                    categoryId
            );

            // Then
            assertEquals("Test Product", request.getName());
            assertEquals("Test Description", request.getDescription());
            assertEquals(new BigDecimal("29.99"), request.getAmount());
            assertEquals("USD", request.getCurrency());
            assertEquals(10, request.getStockQuantity());
            assertEquals(categoryId, request.getCategoryId());
        }

        @Test
        @DisplayName("Should create UpdateProductRequest with all fields")
        void shouldCreateUpdateProductRequestWithAllFields() {
            // Given
            ProductController.UpdateProductRequest request = new ProductController.UpdateProductRequest(
                    "Updated Product",
                    "Updated Description",
                    new BigDecimal("39.99"),
                    "USD",
                    15,
                    categoryId
            );

            // Then
            assertEquals("Updated Product", request.getName());
            assertEquals("Updated Description", request.getDescription());
            assertEquals(new BigDecimal("39.99"), request.getPrice());
            assertEquals("USD", request.getCurrency());
            assertEquals(15, request.getStockQuantity());
            assertEquals(categoryId, request.getCategoryId());
        }

        @Test
        @DisplayName("Should create StockAdjustmentRequest with quantity")
        void shouldCreateStockAdjustmentRequestWithQuantity() {
            // Given
            ProductController.StockAdjustmentRequest request = new ProductController.StockAdjustmentRequest(5);

            // Then
            assertEquals(5, request.getQuantity());
        }

        @Test
        @DisplayName("Should create ChangeCategoryRequest with category ID")
        void shouldCreateChangeCategoryRequestWithCategoryId() {
            // Given
            ProductController.ChangeCategoryRequest request = new ProductController.ChangeCategoryRequest(categoryId);

            // Then
            assertEquals(categoryId, request.getCategoryId());
        }

        @Test
        @DisplayName("Should create UpdatePriceRequest with price and currency")
        void shouldCreateUpdatePriceRequestWithPriceAndCurrency() {
            // Given
            ProductController.UpdatePriceRequest request = new ProductController.UpdatePriceRequest(
                    new BigDecimal("49.99"),
                    "USD"
            );

            // Then
            assertEquals(new BigDecimal("49.99"), request.getPrice());
            assertEquals("USD", request.getCurrency());
        }

        @Test
        @DisplayName("Should create ProductResponse from domain object")
        void shouldCreateProductResponseFromDomainObject() {
            // Given
            ProductController.ProductResponse response = ProductController.ProductResponse.fromDomain(validProduct);

            // Then
            assertEquals(validProduct.getId(), response.getId());
            assertEquals(validProduct.getName(), response.getName());
            assertEquals(validProduct.getDescription(), response.getDescription());
            assertEquals(validProduct.getPrice().amount(), response.getPrice());
            assertEquals(validProduct.getPrice().currency().getCurrencyCode(), response.getCurrency());
            assertEquals(validProduct.getStockQuantity(), response.getStockQuantity());
            assertEquals(validProduct.getCategory().getId(), response.getCategoryId());
            assertEquals(validProduct.getCategory().getName(), response.getCategoryName());
        }

        @Test
        @DisplayName("Should create ProductStatisticsResponse from domain object")
        void shouldCreateProductStatisticsResponseFromDomainObject() {
            // Given
            ProductApplicationService.ProductStatistics statistics = 
                    new ProductApplicationService.ProductStatistics(100L);
            ProductController.ProductStatisticsResponse response = 
                    ProductController.ProductStatisticsResponse.fromDomain(statistics);

            // Then
            assertEquals(100L, response.getTotalProducts());
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Should create ProductControllerException with message")
        void shouldCreateProductControllerExceptionWithMessage() {
            // Given
            String message = "Test error message";

            // When
            ProductController.ProductControllerException exception = 
                    new ProductController.ProductControllerException(message);

            // Then
            assertEquals(message, exception.getMessage());
        }

        @Test
        @DisplayName("Should create ProductControllerException with message and cause")
        void shouldCreateProductControllerExceptionWithMessageAndCause() {
            // Given
            String message = "Test error message";
            Throwable cause = new RuntimeException("Root cause");

            // When
            ProductController.ProductControllerException exception = 
                    new ProductController.ProductControllerException(message, cause);

            // Then
            assertEquals(message, exception.getMessage());
            assertEquals(cause, exception.getCause());
        }
    }
}