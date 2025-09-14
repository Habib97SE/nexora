package io.nexora.catalog.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Domain Tests")
class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create category with default constructor")
        void shouldCreateCategoryWithDefaultConstructor() {
            // When
            Category newCategory = new Category();

            // Then
            assertNotNull(newCategory);
            assertNull(newCategory.getId());
            assertNull(newCategory.getName());
            assertNull(newCategory.getDescription());
            assertFalse(newCategory.isActive()); // Default value is false
            assertNull(newCategory.getCreatedAt());
            assertNull(newCategory.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create category with all-args constructor")
        void shouldCreateCategoryWithAllArgsConstructor() {
            // Given
            String id = "cat-123";
            String name = "Test Category";
            String description = "A test category";
            boolean active = true;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            Category newCategory = new Category(id, name, description, active, createdAt, updatedAt);

            // Then
            assertNotNull(newCategory);
            assertEquals(id, newCategory.getId());
            assertEquals(name, newCategory.getName());
            assertEquals(description, newCategory.getDescription());
            assertTrue(newCategory.isActive());
            assertEquals(createdAt, newCategory.getCreatedAt());
            assertEquals(updatedAt, newCategory.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create category using builder pattern")
        void shouldCreateCategoryUsingBuilderPattern() {
            // Given
            String id = "cat-456";
            String name = "Builder Category";
            String description = "A category created with builder";
            boolean active = true;

            // When
            Category builtCategory = Category.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .active(active)
                    .build();

            // Then
            assertNotNull(builtCategory);
            assertEquals(id, builtCategory.getId());
            assertEquals(name, builtCategory.getName());
            assertEquals(description, builtCategory.getDescription());
            assertTrue(builtCategory.isActive());
        }

        @Test
        @DisplayName("Should create category with partial builder data")
        void shouldCreateCategoryWithPartialBuilderData() {
            // When
            Category partialCategory = Category.builder()
                    .name("Partial Category")
                    .active(true)
                    .build();

            // Then
            assertNotNull(partialCategory);
            assertNull(partialCategory.getId());
            assertEquals("Partial Category", partialCategory.getName());
            assertNull(partialCategory.getDescription());
            assertTrue(partialCategory.isActive());
            assertNull(partialCategory.getCreatedAt());
            assertNull(partialCategory.getUpdatedAt());
        }

        @Test
        @DisplayName("Should create inactive category by default")
        void shouldCreateInactiveCategoryByDefault() {
            // When
            Category defaultCategory = Category.builder()
                    .name("Default Category")
                    .build();

            // Then
            assertFalse(defaultCategory.isActive());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set ID correctly")
        void shouldGetAndSetIdCorrectly() {
            // Given
            String testId = "test-cat-id-123";

            // When
            category.setId(testId);

            // Then
            assertEquals(testId, category.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            String testName = "Test Category Name";

            // When
            category.setName(testName);

            // Then
            assertEquals(testName, category.getName());
        }

        @Test
        @DisplayName("Should get and set description correctly")
        void shouldGetAndSetDescriptionCorrectly() {
            // Given
            String testDescription = "This is a test category description";

            // When
            category.setDescription(testDescription);

            // Then
            assertEquals(testDescription, category.getDescription());
        }

        @Test
        @DisplayName("Should get and set active status correctly")
        void shouldGetAndSetActiveStatusCorrectly() {
            // Test setting to true
            category.setActive(true);
            assertTrue(category.isActive());

            // Test setting to false
            category.setActive(false);
            assertFalse(category.isActive());
        }

        @Test
        @DisplayName("Should get and set created at timestamp correctly")
        void shouldGetAndSetCreatedAtCorrectly() {
            // Given
            LocalDateTime testCreatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

            // When
            category.setCreatedAt(testCreatedAt);

            // Then
            assertEquals(testCreatedAt, category.getCreatedAt());
        }

        @Test
        @DisplayName("Should get and set updated at timestamp correctly")
        void shouldGetAndSetUpdatedAtCorrectly() {
            // Given
            LocalDateTime testUpdatedAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);

            // When
            category.setUpdatedAt(testUpdatedAt);

            // Then
            assertEquals(testUpdatedAt, category.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Business Behavior Tests")
    class BusinessBehaviorTests {

        @Test
        @DisplayName("Should be able to update category information")
        void shouldBeAbleToUpdateCategoryInformation() {
            // Given
            category.setName("Original Name");
            category.setDescription("Original Description");
            category.setActive(false);

            // When
            category.setName("Updated Name");
            category.setDescription("Updated Description");
            category.setActive(true);

            // Then
            assertEquals("Updated Name", category.getName());
            assertEquals("Updated Description", category.getDescription());
            assertTrue(category.isActive());
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // When & Then
            assertDoesNotThrow(() -> {
                category.setId(null);
                category.setName(null);
                category.setDescription(null);
                category.setCreatedAt(null);
                category.setUpdatedAt(null);
            });

            assertNull(category.getId());
            assertNull(category.getName());
            assertNull(category.getDescription());
            assertNull(category.getCreatedAt());
            assertNull(category.getUpdatedAt());
        }

        @Test
        @DisplayName("Should toggle active status correctly")
        void shouldToggleActiveStatusCorrectly() {
            // Start with inactive
            category.setActive(false);
            assertFalse(category.isActive());

            // Activate
            category.setActive(true);
            assertTrue(category.isActive());

            // Deactivate again
            category.setActive(false);
            assertFalse(category.isActive());
        }

        @Test
        @DisplayName("Should maintain state consistency during updates")
        void shouldMaintainStateConsistencyDuringUpdates() {
            // Given
            String originalId = "original-id";
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(5);
            
            category.setId(originalId);
            category.setName("Original Name");
            category.setDescription("Original Description");
            category.setActive(true);
            category.setCreatedAt(originalCreatedAt);

            // When - Update only some fields
            category.setName("New Name");
            category.setDescription("New Description");
            category.setActive(false);

            // Then - Verify unchanged fields remain the same
            assertEquals(originalId, category.getId());
            assertEquals(originalCreatedAt, category.getCreatedAt());
            
            // Verify changed fields are updated
            assertEquals("New Name", category.getName());
            assertEquals("New Description", category.getDescription());
            assertFalse(category.isActive());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() {
            // When
            category.setName("");
            category.setDescription("");

            // Then
            assertEquals("", category.getName());
            assertEquals("", category.getDescription());
        }

        @Test
        @DisplayName("Should handle very long string values")
        void shouldHandleVeryLongStringValues() {
            // Given
            String longName = "A".repeat(1000);
            String longDescription = "B".repeat(5000);

            // When
            category.setName(longName);
            category.setDescription(longDescription);

            // Then
            assertEquals(longName, category.getName());
            assertEquals(longDescription, category.getDescription());
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void shouldHandleSpecialCharactersInStrings() {
            // Given
            String nameWithSpecialChars = "Category™ with émojis 🚀 and spëcial chars";
            String descriptionWithSpecialChars = "Description with unicode: αβγδε and symbols: @#$%^&*()";

            // When
            category.setName(nameWithSpecialChars);
            category.setDescription(descriptionWithSpecialChars);

            // Then
            assertEquals(nameWithSpecialChars, category.getName());
            assertEquals(descriptionWithSpecialChars, category.getDescription());
        }

        @Test
        @DisplayName("Should handle whitespace-only strings")
        void shouldHandleWhitespaceOnlyStrings() {
            // Given
            String whitespaceName = "   \t\n   ";
            String whitespaceDescription = "   \t\n   ";

            // When
            category.setName(whitespaceName);
            category.setDescription(whitespaceDescription);

            // Then
            assertEquals(whitespaceName, category.getName());
            assertEquals(whitespaceDescription, category.getDescription());
        }

        @Test
        @DisplayName("Should handle numeric strings")
        void shouldHandleNumericStrings() {
            // Given
            String numericName = "12345";
            String numericDescription = "67890";

            // When
            category.setName(numericName);
            category.setDescription(numericDescription);

            // Then
            assertEquals(numericName, category.getName());
            assertEquals(numericDescription, category.getDescription());
        }

        @Test
        @DisplayName("Should handle mixed case strings")
        void shouldHandleMixedCaseStrings() {
            // Given
            String mixedCaseName = "MiXeD cAsE cAtEgOrY";
            String mixedCaseDescription = "ThIs Is A mIxEd CaSe DeScRiPtIoN";

            // When
            category.setName(mixedCaseName);
            category.setDescription(mixedCaseDescription);

            // Then
            assertEquals(mixedCaseName, category.getName());
            assertEquals(mixedCaseDescription, category.getDescription());
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
            category.setCreatedAt(createdAt);
            category.setUpdatedAt(updatedAt);

            // Then
            assertTrue(category.getUpdatedAt().isAfter(category.getCreatedAt()));
        }

        @Test
        @DisplayName("Should handle same timestamp for created and updated")
        void shouldHandleSameTimestampForCreatedAndUpdated() {
            // Given
            LocalDateTime sameTime = LocalDateTime.of(2023, 6, 15, 14, 30, 0);

            // When
            category.setCreatedAt(sameTime);
            category.setUpdatedAt(sameTime);

            // Then
            assertEquals(sameTime, category.getCreatedAt());
            assertEquals(sameTime, category.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle future timestamps")
        void shouldHandleFutureTimestamps() {
            // Given
            LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

            // When
            category.setCreatedAt(futureTime);
            category.setUpdatedAt(futureTime);

            // Then
            assertEquals(futureTime, category.getCreatedAt());
            assertEquals(futureTime, category.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle very old timestamps")
        void shouldHandleVeryOldTimestamps() {
            // Given
            LocalDateTime oldTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0);

            // When
            category.setCreatedAt(oldTime);
            category.setUpdatedAt(oldTime);

            // Then
            assertEquals(oldTime, category.getCreatedAt());
            assertEquals(oldTime, category.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Active Status Behavior Tests")
    class ActiveStatusBehaviorTests {

        @Test
        @DisplayName("Should default to inactive status")
        void shouldDefaultToInactiveStatus() {
            // When
            Category newCategory = new Category();

            // Then
            assertFalse(newCategory.isActive());
        }

        @Test
        @DisplayName("Should allow explicit active status setting")
        void shouldAllowExplicitActiveStatusSetting() {
            // Test setting to active
            category.setActive(true);
            assertTrue(category.isActive());

            // Test setting to inactive
            category.setActive(false);
            assertFalse(category.isActive());
        }

        @Test
        @DisplayName("Should maintain active status through updates")
        void shouldMaintainActiveStatusThroughUpdates() {
            // Given
            category.setActive(true);
            assertTrue(category.isActive());

            // When - Update other fields
            category.setName("Updated Name");
            category.setDescription("Updated Description");

            // Then - Active status should remain unchanged
            assertTrue(category.isActive());
        }

        @Test
        @DisplayName("Should handle rapid active status changes")
        void shouldHandleRapidActiveStatusChanges() {
            // When & Then
            for (int i = 0; i < 10; i++) {
                category.setActive(i % 2 == 0);
                assertEquals(i % 2 == 0, category.isActive());
            }
        }
    }

    @Nested
    @DisplayName("Parameterized Tests")
    class ParameterizedTests {

        @ParameterizedTest
        @ValueSource(strings = {"A", "AB", "ABC", "Category Name", "Very Long Category Name That Exceeds Normal Length"})
        @DisplayName("Should handle various name lengths")
        void shouldHandleVariousNameLengths(String name) {
            // When
            category.setName(name);

            // Then
            assertEquals(name, category.getName());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("Should handle both active status values")
        void shouldHandleBothActiveStatusValues(boolean active) {
            // When
            category.setActive(active);

            // Then
            assertEquals(active, category.isActive());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r\n", "   \t\n   "})
        @DisplayName("Should handle various whitespace strings")
        void shouldHandleVariousWhitespaceStrings(String whitespaceString) {
            // When
            category.setName(whitespaceString);
            category.setDescription(whitespaceString);

            // Then
            assertEquals(whitespaceString, category.getName());
            assertEquals(whitespaceString, category.getDescription());
        }
    }

    @Nested
    @DisplayName("Integration Behavior Tests")
    class IntegrationBehaviorTests {

        @Test
        @DisplayName("Should create complete category with all fields")
        void shouldCreateCompleteCategoryWithAllFields() {
            // Given
            String id = "complete-category-123";
            String name = "Complete Test Category";
            String description = "A complete category for integration testing";
            boolean active = true;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
            LocalDateTime updatedAt = LocalDateTime.now().minusHours(2);

            // When
            Category completeCategory = Category.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .active(active)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            // Then
            assertNotNull(completeCategory);
            assertEquals(id, completeCategory.getId());
            assertEquals(name, completeCategory.getName());
            assertEquals(description, completeCategory.getDescription());
            assertTrue(completeCategory.isActive());
            assertEquals(createdAt, completeCategory.getCreatedAt());
            assertEquals(updatedAt, completeCategory.getUpdatedAt());
        }

        @Test
        @DisplayName("Should support category lifecycle operations")
        void shouldSupportCategoryLifecycleOperations() {
            // Create
            Category lifecycleCategory = Category.builder()
                    .name("Lifecycle Category")
                    .description("A category for lifecycle testing")
                    .active(false)
                    .build();

            assertNotNull(lifecycleCategory);
            assertEquals("Lifecycle Category", lifecycleCategory.getName());
            assertFalse(lifecycleCategory.isActive());

            // Update
            lifecycleCategory.setName("Updated Lifecycle Category");
            lifecycleCategory.setDescription("Updated description");
            lifecycleCategory.setActive(true);

            assertEquals("Updated Lifecycle Category", lifecycleCategory.getName());
            assertEquals("Updated description", lifecycleCategory.getDescription());
            assertTrue(lifecycleCategory.isActive());

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            lifecycleCategory.setCreatedAt(now.minusDays(1));
            lifecycleCategory.setUpdatedAt(now);

            assertTrue(lifecycleCategory.getUpdatedAt().isAfter(lifecycleCategory.getCreatedAt()));
        }

        @Test
        @DisplayName("Should handle category state transitions")
        void shouldHandleCategoryStateTransitions() {
            // Given - Create active category
            Category stateCategory = Category.builder()
                    .name("State Category")
                    .active(true)
                    .build();

            assertTrue(stateCategory.isActive());

            // When - Deactivate
            stateCategory.setActive(false);
            assertFalse(stateCategory.isActive());

            // When - Reactivate
            stateCategory.setActive(true);
            assertTrue(stateCategory.isActive());

            // When - Update while active
            stateCategory.setName("Updated State Category");
            stateCategory.setDescription("Updated while active");

            // Then - Should remain active
            assertTrue(stateCategory.isActive());
            assertEquals("Updated State Category", stateCategory.getName());
            assertEquals("Updated while active", stateCategory.getDescription());
        }

        @Test
        @DisplayName("Should maintain data integrity during complex operations")
        void shouldMaintainDataIntegrityDuringComplexOperations() {
            // Given
            String originalId = "integrity-test-id";
            LocalDateTime originalCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0);

            Category integrityCategory = Category.builder()
                    .id(originalId)
                    .name("Original Name")
                    .description("Original Description")
                    .active(true)
                    .createdAt(originalCreatedAt)
                    .build();

            // When - Perform multiple operations
            integrityCategory.setName("New Name");
            integrityCategory.setDescription("New Description");
            integrityCategory.setActive(false);
            LocalDateTime newUpdatedAt = LocalDateTime.now();
            integrityCategory.setUpdatedAt(newUpdatedAt);

            // Then - Verify data integrity
            assertEquals(originalId, integrityCategory.getId());
            assertEquals(originalCreatedAt, integrityCategory.getCreatedAt());
            assertEquals("New Name", integrityCategory.getName());
            assertEquals("New Description", integrityCategory.getDescription());
            assertFalse(integrityCategory.isActive());
            assertEquals(newUpdatedAt, integrityCategory.getUpdatedAt());

            // Verify timestamps are ordered correctly
            assertTrue(integrityCategory.getUpdatedAt().isAfter(integrityCategory.getCreatedAt()));
        }
    }
}
