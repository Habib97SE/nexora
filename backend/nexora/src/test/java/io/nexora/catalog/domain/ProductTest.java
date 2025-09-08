package io.nexora.catalog.domain;

import io.nexora.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Tests")
class ProductTest {

    private Product product;
    private Money validPrice;
    private Category validCategory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        product = new Product();
        validPrice = new Money(new BigDecimal("29.99"), Currency.getInstance("USD"));
        validCategory = Category.builder()
                .id("cat-123")
                .name("Electronics")
                .description("Electronic devices")
                .active(true)
                .build();
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create product with default constructor")
        void shouldCreateProductWithDefaultConstructor() {
            // When
            Product newProduct = new Product();

            // Then
            assertNotNull(newProduct);
            assertNull(newProduct.getId());
            assertNull(newProduct.getName());
            assertNull(newProduct.getDescription());
            assertNull(newProduct.getPrice());
            assertEquals(0, newProduct.getStockQuantity());
            assertNull(newProduct.getCategory());
            assertNull(newProduct.getCreatedAt());
            assertNull(newProduct.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create product with all-args constructor")
        void shouldCreateProductWithAllArgsConstructor() {
            // Given
            String id = "prod-123";
            String name = "Test Product";
            String description = "A test product";
            Money price = validPrice;
            int stockQuantity = 100;
            Category category = validCategory;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            Product newProduct = new Product(id, name, description, price, stockQuantity, category, createdAt, updatedAt);

            // Then
            assertNotNull(newProduct);
            assertEquals(id, newProduct.getId());
            assertEquals(name, newProduct.getName());
            assertEquals(description, newProduct.getDescription());
            assertEquals(price, newProduct.getPrice());
            assertEquals(stockQuantity, newProduct.getStockQuantity());
            assertEquals(category, newProduct.getCategory());
            assertEquals(createdAt, newProduct.getCreatedAt());
            assertEquals(updatedAt, newProduct.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create product using builder pattern")
        void shouldCreateProductUsingBuilderPattern() {
            // Given
            String id = "prod-456";
            String name = "Builder Product";
            String description = "A product created with builder";
            Money price = validPrice;
            int stockQuantity = 50;
            Category category = validCategory;

            // When
            Product builtProduct = Product.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price)
                    .stockQuantity(stockQuantity)
                    .category(category)
                    .build();

            // Then
            assertNotNull(builtProduct);
            assertEquals(id, builtProduct.getId());
            assertEquals(name, builtProduct.getName());
            assertEquals(description, builtProduct.getDescription());
            assertEquals(price, builtProduct.getPrice());
            assertEquals(stockQuantity, builtProduct.getStockQuantity());
            assertEquals(category, builtProduct.getCategory());
        }

        @Test
        @DisplayName("Should create product with partial builder data")
        void shouldCreateProductWithPartialBuilderData() {
            // When
            Product partialProduct = Product.builder()
                    .name("Partial Product")
                    .price(validPrice)
                    .build();

            // Then
            assertNotNull(partialProduct);
            assertNull(partialProduct.getId());
            assertEquals("Partial Product", partialProduct.getName());
            assertNull(partialProduct.getDescription());
            assertEquals(validPrice, partialProduct.getPrice());
            assertEquals(0, partialProduct.getStockQuantity());
            assertNull(partialProduct.getCategory());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set ID correctly")
        void shouldGetAndSetIdCorrectly() {
            // Given
            String testId = "test-id-123";

            // When
            product.setId(testId);

            // Then
            assertEquals(testId, product.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            String testName = "Test Product Name";

            // When
            product.setName(testName);

            // Then
            assertEquals(testName, product.getName());
        }

        @Test
        @DisplayName("Should get and set description correctly")
        void shouldGetAndSetDescriptionCorrectly() {
            // Given
            String testDescription = "This is a test product description";

            // When
            product.setDescription(testDescription);

            // Then
            assertEquals(testDescription, product.getDescription());
        }

        @Test
        @DisplayName("Should get and set price correctly")
        void shouldGetAndSetPriceCorrectly() {
            // Given
            Money testPrice = new Money(new BigDecimal("99.99"), Currency.getInstance("EUR"));

            // When
            product.setPrice(testPrice);

            // Then
            assertEquals(testPrice, product.getPrice());
            assertEquals(new BigDecimal("99.99"), product.getPrice().amount());
            assertEquals(Currency.getInstance("EUR"), product.getPrice().currency());
        }

        @Test
        @DisplayName("Should get and set stock quantity correctly")
        void shouldGetAndSetStockQuantityCorrectly() {
            // Given
            int testStock = 150;

            // When
            product.setStockQuantity(testStock);

            // Then
            assertEquals(testStock, product.getStockQuantity());
        }

        @Test
        @DisplayName("Should get and set category correctly")
        void shouldGetAndSetCategoryCorrectly() {
            // Given
            Category testCategory = Category.builder()
                    .id("test-cat")
                    .name("Test Category")
                    .build();

            // When
            product.setCategory(testCategory);

            // Then
            assertEquals(testCategory, product.getCategory());
            assertEquals("test-cat", product.getCategory().getId());
            assertEquals("Test Category", product.getCategory().getName());
        }

        @Test
        @DisplayName("Should get and set created at timestamp correctly")
        void shouldGetAndSetCreatedAtCorrectly() {
            // Given
            LocalDateTime testCreatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

            // When
            product.setCreatedAt(testCreatedAt);

            // Then
            assertEquals(testCreatedAt, product.getCreatedAt());
        }

        @Test
        @DisplayName("Should get and set updated at timestamp correctly")
        void shouldGetAndSetUpdatedAtCorrectly() {
            // Given
            LocalDateTime testUpdatedAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);

            // When
            product.setUpdatedAt(testUpdatedAt);

            // Then
            assertEquals(testUpdatedAt, product.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Business Behavior Tests")
    class BusinessBehaviorTests {

        @Test
        @DisplayName("Should be able to update product information")
        void shouldBeAbleToUpdateProductInformation() {
            // Given
            product.setName("Original Name");
            product.setPrice(validPrice);
            product.setStockQuantity(10);

            // When
            product.setName("Updated Name");
            Money newPrice = new Money(new BigDecimal("39.99"), Currency.getInstance("USD"));
            product.setPrice(newPrice);
            product.setStockQuantity(25);

            // Then
            assertEquals("Updated Name", product.getName());
            assertEquals(newPrice, product.getPrice());
            assertEquals(25, product.getStockQuantity());
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // When & Then
            assertDoesNotThrow(() -> {
                product.setId(null);
                product.setName(null);
                product.setDescription(null);
                product.setPrice(null);
                product.setCategory(null);
                product.setCreatedAt(null);
                product.setUpdatedAt(null);
            });

            assertNull(product.getId());
            assertNull(product.getName());
            assertNull(product.getDescription());
            assertNull(product.getPrice());
            assertNull(product.getCategory());
            assertNull(product.getCreatedAt());
            assertNull(product.getUpdatedAt());
        }

        @Test
        @DisplayName("Should maintain referential integrity with category")
        void shouldMaintainReferentialIntegrityWithCategory() {
            // Given
            Category category = Category.builder()
                    .id("cat-789")
                    .name("Books")
                    .description("Book category")
                    .active(true)
                    .build();

            // When
            product.setCategory(category);

            // Then
            assertSame(category, product.getCategory());
            assertEquals("cat-789", product.getCategory().getId());
            assertEquals("Books", product.getCategory().getName());
        }

        @Test
        @DisplayName("Should handle stock quantity edge cases")
        void shouldHandleStockQuantityEdgeCases() {
            // Test zero stock
            product.setStockQuantity(0);
            assertEquals(0, product.getStockQuantity());

            // Test negative stock (business rule might allow this for backorders)
            product.setStockQuantity(-5);
            assertEquals(-5, product.getStockQuantity());

            // Test large stock quantity
            product.setStockQuantity(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, product.getStockQuantity());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() {
            // When
            product.setName("");
            product.setDescription("");

            // Then
            assertEquals("", product.getName());
            assertEquals("", product.getDescription());
        }

        @Test
        @DisplayName("Should handle very long string values")
        void shouldHandleVeryLongStringValues() {
            // Given
            String longName = "A".repeat(1000);
            String longDescription = "B".repeat(5000);

            // When
            product.setName(longName);
            product.setDescription(longDescription);

            // Then
            assertEquals(longName, product.getName());
            assertEquals(longDescription, product.getDescription());
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            // Given
            String nameWithSpecialChars = "Product™ with émojis 🚀 and spëcial chars";
            String descriptionWithSpecialChars = "Description with unicode: αβγδε and symbols: @#$%^&*()";

            // When
            product.setName(nameWithSpecialChars);
            product.setDescription(descriptionWithSpecialChars);

            // Then
            assertEquals(nameWithSpecialChars, product.getName());
            assertEquals(descriptionWithSpecialChars, product.getDescription());
        }

        @Test
        @DisplayName("Should handle different currency types")
        void shouldHandleDifferentCurrencyTypes() {
            // Given
            Money usdPrice = new Money(new BigDecimal("10.00"), Currency.getInstance("USD"));
            Money eurPrice = new Money(new BigDecimal("8.50"), Currency.getInstance("EUR"));
            Money jpyPrice = new Money(new BigDecimal("1000"), Currency.getInstance("JPY"));

            // When & Then
            product.setPrice(usdPrice);
            assertEquals(usdPrice, product.getPrice());

            product.setPrice(eurPrice);
            assertEquals(eurPrice, product.getPrice());

            product.setPrice(jpyPrice);
            assertEquals(jpyPrice, product.getPrice());
        }

        @Test
        @DisplayName("Should handle zero and very small price amounts")
        void shouldHandleZeroAndVerySmallPriceAmounts() {
            // Given
            Money zeroPrice = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
            Money smallPrice = new Money(new BigDecimal("0.01"), Currency.getInstance("USD"));

            // When & Then
            product.setPrice(zeroPrice);
            assertEquals(zeroPrice, product.getPrice());

            product.setPrice(smallPrice);
            assertEquals(smallPrice, product.getPrice());
        }

        @Test
        @DisplayName("Should handle very large price amounts")
        void shouldHandleVeryLargePriceAmounts() {
            // Given
            Money largePrice = new Money(new BigDecimal("999999.99"), Currency.getInstance("USD"));

            // When
            product.setPrice(largePrice);

            // Then
            assertEquals(largePrice, product.getPrice());
        }
    }

    @Nested
    @DisplayName("Timestamp Behavior Tests")
    class TimestampBehaviorTests {

        @Test
        @DisplayName("Should handle timestamp ordering correctly")
        void shouldHandleTimestampOrderingCorrectly() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 1, 11, 0, 0);

            // When
            product.setCreatedAt(createdAt);
            product.setUpdatedAt(updatedAt);

            // Then
            assertTrue(product.getUpdatedAt().isAfter(product.getCreatedAt()));
        }

        @Test
        @DisplayName("Should handle same timestamp for created and updated")
        void shouldHandleSameTimestampForCreatedAndUpdated() {
            // Given
            LocalDateTime sameTime = LocalDateTime.of(2023, 6, 15, 14, 30, 0);

            // When
            product.setCreatedAt(sameTime);
            product.setUpdatedAt(sameTime);

            // Then
            assertEquals(sameTime, product.getCreatedAt());
            assertEquals(sameTime, product.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Product-Category Relationship Tests")
    class ProductCategoryRelationshipTests {

        @Test
        @DisplayName("Should establish bidirectional relationship with category")
        void shouldEstablishBidirectionalRelationshipWithCategory() {
            // Given
            Category category = Category.builder()
                    .id("cat-relationship")
                    .name("Relationship Test")
                    .build();

            // When
            product.setCategory(category);

            // Then
            assertEquals(category, product.getCategory());
            assertSame(category, product.getCategory());
        }

        @Test
        @DisplayName("Should handle category replacement")
        void shouldHandleCategoryReplacement() {
            // Given
            Category originalCategory = Category.builder()
                    .id("original-cat")
                    .name("Original Category")
                    .build();

            Category newCategory = Category.builder()
                    .id("new-cat")
                    .name("New Category")
                    .build();

            // When
            product.setCategory(originalCategory);
            assertEquals(originalCategory, product.getCategory());

            product.setCategory(newCategory);

            // Then
            assertEquals(newCategory, product.getCategory());
            assertNotEquals(originalCategory, product.getCategory());
        }

        @Test
        @DisplayName("Should handle null category assignment")
        void shouldHandleNullCategoryAssignment() {
            // Given
            product.setCategory(validCategory);
            assertNotNull(product.getCategory());

            // When
            product.setCategory(null);

            // Then
            assertNull(product.getCategory());
        }
    }

    @Nested
    @DisplayName("Parameterized Tests")
    class ParameterizedTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, 100, 1000, Integer.MAX_VALUE})
        @DisplayName("Should handle various stock quantities")
        void shouldHandleVariousStockQuantities(int stockQuantity) {
            // When
            product.setStockQuantity(stockQuantity);

            // Then
            assertEquals(stockQuantity, product.getStockQuantity());
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "AB", "ABC", "Product Name", "Very Long Product Name That Exceeds Normal Length"})
        @DisplayName("Should handle various name lengths")
        void shouldHandleVariousNameLengths(String name) {
            // When
            product.setName(name);

            // Then
            assertEquals(name, product.getName());
        }
    }

    @Nested
    @DisplayName("Integration Behavior Tests")
    class IntegrationBehaviorTests {

        @Test
        @DisplayName("Should create complete product with all fields")
        void shouldCreateCompleteProductWithAllFields() {
            // Given
            String id = "complete-product-123";
            String name = "Complete Test Product";
            String description = "A complete product for integration testing";
            Money price = new Money(new BigDecimal("149.99"), Currency.getInstance("USD"));
            int stockQuantity = 75;
            Category category = Category.builder()
                    .id("integration-cat")
                    .name("Integration Category")
                    .description("Category for integration tests")
                    .active(true)
                    .build();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
            LocalDateTime updatedAt = LocalDateTime.now().minusHours(2);

            // When
            Product completeProduct = Product.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price)
                    .stockQuantity(stockQuantity)
                    .category(category)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            // Then
            assertNotNull(completeProduct);
            assertEquals(id, completeProduct.getId());
            assertEquals(name, completeProduct.getName());
            assertEquals(description, completeProduct.getDescription());
            assertEquals(price, completeProduct.getPrice());
            assertEquals(stockQuantity, completeProduct.getStockQuantity());
            assertEquals(category, completeProduct.getCategory());
            assertEquals(createdAt, completeProduct.getCreatedAt());
            assertEquals(updatedAt, completeProduct.getUpdatedAt());

            // Verify price details
            assertEquals(new BigDecimal("149.99"), completeProduct.getPrice().amount());
            assertEquals(Currency.getInstance("USD"), completeProduct.getPrice().currency());

            // Verify category details
            assertEquals("integration-cat", completeProduct.getCategory().getId());
            assertEquals("Integration Category", completeProduct.getCategory().getName());
            assertTrue(completeProduct.getCategory().isActive());
        }

        @Test
        @DisplayName("Should support product lifecycle operations")
        void shouldSupportProductLifecycleOperations() {
            // Create
            Product lifecycleProduct = Product.builder()
                    .name("Lifecycle Product")
                    .price(validPrice)
                    .stockQuantity(100)
                    .build();

            assertNotNull(lifecycleProduct);
            assertEquals("Lifecycle Product", lifecycleProduct.getName());

            // Update
            lifecycleProduct.setName("Updated Lifecycle Product");
            lifecycleProduct.setStockQuantity(50);
            Money newPrice = new Money(new BigDecimal("199.99"), Currency.getInstance("USD"));
            lifecycleProduct.setPrice(newPrice);

            assertEquals("Updated Lifecycle Product", lifecycleProduct.getName());
            assertEquals(50, lifecycleProduct.getStockQuantity());
            assertEquals(newPrice, lifecycleProduct.getPrice());

            // Associate with category
            lifecycleProduct.setCategory(validCategory);
            assertEquals(validCategory, lifecycleProduct.getCategory());

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            lifecycleProduct.setCreatedAt(now.minusDays(1));
            lifecycleProduct.setUpdatedAt(now);

            assertTrue(lifecycleProduct.getUpdatedAt().isAfter(lifecycleProduct.getCreatedAt()));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate product with valid data")
        void shouldValidateProductWithValidData() {
            // Given
            Product validProduct = Product.builder()
                    .name("Valid Product")
                    .description("A valid product description")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(validProduct);

            // Then
            assertTrue(violations.isEmpty(), "Valid product should have no validation violations");
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void shouldFailValidationWhenNameIsNull() {
            // Given
            Product invalidProduct = Product.builder()
                    .name(null)
                    .description("A product with null name")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

            // Then
            assertFalse(violations.isEmpty(), "Product with null name should have validation violations");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name") 
                    && v.getMessage().contains("must not be null")));
        }

        @Test
        @DisplayName("Should fail validation when name is too short")
        void shouldFailValidationWhenNameIsTooShort() {
            // Given
            Product invalidProduct = Product.builder()
                    .name("A") // Only 1 character, should be at least 2
                    .description("A product with short name")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

            // Then
            assertFalse(violations.isEmpty(), "Product with name too short should have validation violations");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name") 
                    && (v.getMessage().contains("size must be between 2 and") || v.getMessage().contains("must be greater than or equal to 2"))));
        }

        @Test
        @DisplayName("Should pass validation when name is exactly 2 characters")
        void shouldPassValidationWhenNameIsExactly2Characters() {
            // Given
            Product validProduct = Product.builder()
                    .name("AB") // Exactly 2 characters, should be valid
                    .description("A product with 2-character name")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(validProduct);

            // Then
            assertTrue(violations.isEmpty(), "Product with 2-character name should be valid");
        }

        @Test
        @DisplayName("Should fail validation when category is null")
        void shouldFailValidationWhenCategoryIsNull() {
            // Given
            Product invalidProduct = Product.builder()
                    .name("Valid Product Name")
                    .description("A product with null category")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(null) // Category is null, should fail validation
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

            // Then
            assertFalse(violations.isEmpty(), "Product with null category should have validation violations");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category") 
                    && v.getMessage().contains("must not be null")));
        }

        @Test
        @DisplayName("Should fail validation with multiple constraint violations")
        void shouldFailValidationWithMultipleConstraintViolations() {
            // Given
            Product invalidProduct = Product.builder()
                    .name("A") // Too short
                    .description("A product with multiple violations")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(null) // Null category
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

            // Then
            assertFalse(violations.isEmpty(), "Product with multiple violations should have validation violations");
            assertEquals(2, violations.size(), "Should have exactly 2 validation violations");
            
            // Check that both violations are present
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "", " "})
        @DisplayName("Should fail validation for invalid name lengths")
        void shouldFailValidationForInvalidNameLengths(String invalidName) {
            // Given
            Product invalidProduct = Product.builder()
                    .name(invalidName)
                    .description("A product with invalid name")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

            // Then
            assertFalse(violations.isEmpty(), "Product with invalid name '" + invalidName + "' should have validation violations");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB", "ABC", "Valid Product Name", "Very Long Product Name That Exceeds Normal Length"})
        @DisplayName("Should pass validation for valid name lengths")
        void shouldPassValidationForValidNameLengths(String validName) {
            // Given
            Product validProduct = Product.builder()
                    .name(validName)
                    .description("A product with valid name")
                    .price(validPrice)
                    .stockQuantity(10)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(validProduct);

            // Then
            assertTrue(violations.isEmpty(), "Product with valid name '" + validName + "' should have no validation violations");
        }

        @Test
        @DisplayName("Should validate product creation with all required fields")
        void shouldValidateProductCreationWithAllRequiredFields() {
            // Given
            Product completeProduct = Product.builder()
                    .name("Complete Product")
                    .description("A complete product with all fields")
                    .price(validPrice)
                    .stockQuantity(100)
                    .category(validCategory)
                    .build();

            // When
            Set<ConstraintViolation<Product>> violations = validator.validate(completeProduct);

            // Then
            assertTrue(violations.isEmpty(), "Complete product should have no validation violations");
        }

        @Test
        @DisplayName("Should validate product update with all required fields")
        void shouldValidateProductUpdateWithAllRequiredFields() {
            // Given
            Product productToUpdate = Product.builder()
                    .name("Updated Product")
                    .description("An updated product")
                    .price(validPrice)
                    .stockQuantity(50)
                    .category(validCategory)
                    .build();

            // When - Simulate update by validating again
            Set<ConstraintViolation<Product>> violations = validator.validate(productToUpdate);

            // Then
            assertTrue(violations.isEmpty(), "Updated product should have no validation violations");
        }
    }
}
