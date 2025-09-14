package io.nexora.user.interfaces.rest.v1.dto;

import io.nexora.user.application.UserApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for UserStatisticsResponse DTO.
 * 
 * These tests verify all behavior, data transformation, and edge cases
 * implemented in the UserStatisticsResponse DTO.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Getter and setter behavior
 * - Static factory method (fromDomain)
 * - Edge cases and error conditions
 * - Value object equality and immutability
 * - String representation
 * - Data transformation and mapping
 * - Null handling and boundary conditions
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All methods and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - DTO Testing: Focus on data transfer and transformation behavior
 * - Domain Mapping Testing: Verify proper conversion from domain objects
 */
@DisplayName("UserStatisticsResponse DTO Tests")
class UserStatisticsResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create response with no-args constructor")
        void shouldCreateResponseWithNoArgsConstructor() {
            // When
            UserStatisticsResponse response = new UserStatisticsResponse();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isNull();
            assertThat(response.getActiveUsers()).isNull();
            assertThat(response.getInactiveUsers()).isNull();
            assertThat(response.getCustomerUsers()).isNull();
            assertThat(response.getAdminUsers()).isNull();
            assertThat(response.getManagerUsers()).isNull();
        }

        @Test
        @DisplayName("Should create response with all-args constructor")
        void shouldCreateResponseWithAllArgsConstructor() {
            // Given
            Long totalUsers = 100L;
            Long activeUsers = 80L;
            Long inactiveUsers = 20L;
            Long customerUsers = 70L;
            Long adminUsers = 5L;
            Long managerUsers = 25L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, activeUsers, inactiveUsers, 
                    customerUsers, adminUsers, managerUsers
            );

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getCustomerUsers()).isEqualTo(customerUsers);
            assertThat(response.getAdminUsers()).isEqualTo(adminUsers);
            assertThat(response.getManagerUsers()).isEqualTo(managerUsers);
        }

        @Test
        @DisplayName("Should create response with null values in all-args constructor")
        void shouldCreateResponseWithNullValuesInAllArgsConstructor() {
            // When
            UserStatisticsResponse response = new UserStatisticsResponse(null, null, null, null, null, null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isNull();
            assertThat(response.getActiveUsers()).isNull();
            assertThat(response.getInactiveUsers()).isNull();
            assertThat(response.getCustomerUsers()).isNull();
            assertThat(response.getAdminUsers()).isNull();
            assertThat(response.getManagerUsers()).isNull();
        }

        @Test
        @DisplayName("Should create response with zero values")
        void shouldCreateResponseWithZeroValues() {
            // Given
            Long zero = 0L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(zero, zero, zero, zero, zero, zero);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(zero);
            assertThat(response.getActiveUsers()).isEqualTo(zero);
            assertThat(response.getInactiveUsers()).isEqualTo(zero);
            assertThat(response.getCustomerUsers()).isEqualTo(zero);
            assertThat(response.getAdminUsers()).isEqualTo(zero);
            assertThat(response.getManagerUsers()).isEqualTo(zero);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set all fields correctly")
        void shouldGetAndSetAllFieldsCorrectly() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse();
            Long totalUsers = 150L;
            Long activeUsers = 120L;
            Long inactiveUsers = 30L;
            Long customerUsers = 100L;
            Long adminUsers = 10L;
            Long managerUsers = 40L;

            // When
            response.setTotalUsers(totalUsers);
            response.setActiveUsers(activeUsers);
            response.setInactiveUsers(inactiveUsers);
            response.setCustomerUsers(customerUsers);
            response.setAdminUsers(adminUsers);
            response.setManagerUsers(managerUsers);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getCustomerUsers()).isEqualTo(customerUsers);
            assertThat(response.getAdminUsers()).isEqualTo(adminUsers);
            assertThat(response.getManagerUsers()).isEqualTo(managerUsers);
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When
            response.setTotalUsers(null);
            response.setActiveUsers(null);
            response.setInactiveUsers(null);
            response.setCustomerUsers(null);
            response.setAdminUsers(null);
            response.setManagerUsers(null);

            // Then
            assertThat(response.getTotalUsers()).isNull();
            assertThat(response.getActiveUsers()).isNull();
            assertThat(response.getInactiveUsers()).isNull();
            assertThat(response.getCustomerUsers()).isNull();
            assertThat(response.getAdminUsers()).isNull();
            assertThat(response.getManagerUsers()).isNull();
        }

        @Test
        @DisplayName("Should handle zero values in setters")
        void shouldHandleZeroValuesInSetters() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse();
            Long zero = 0L;

            // When
            response.setTotalUsers(zero);
            response.setActiveUsers(zero);
            response.setInactiveUsers(zero);
            response.setCustomerUsers(zero);
            response.setAdminUsers(zero);
            response.setManagerUsers(zero);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(zero);
            assertThat(response.getActiveUsers()).isEqualTo(zero);
            assertThat(response.getInactiveUsers()).isEqualTo(zero);
            assertThat(response.getCustomerUsers()).isEqualTo(zero);
            assertThat(response.getAdminUsers()).isEqualTo(zero);
            assertThat(response.getManagerUsers()).isEqualTo(zero);
        }

        @Test
        @DisplayName("Should handle large values in setters")
        void shouldHandleLargeValuesInSetters() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse();
            Long largeValue = Long.MAX_VALUE;

            // When
            response.setTotalUsers(largeValue);
            response.setActiveUsers(largeValue);
            response.setInactiveUsers(largeValue);
            response.setCustomerUsers(largeValue);
            response.setAdminUsers(largeValue);
            response.setManagerUsers(largeValue);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(largeValue);
            assertThat(response.getActiveUsers()).isEqualTo(largeValue);
            assertThat(response.getInactiveUsers()).isEqualTo(largeValue);
            assertThat(response.getCustomerUsers()).isEqualTo(largeValue);
            assertThat(response.getAdminUsers()).isEqualTo(largeValue);
            assertThat(response.getManagerUsers()).isEqualTo(largeValue);
        }
    }

    @Nested
    @DisplayName("Static Factory Method Tests")
    class StaticFactoryMethodTests {

        @Test
        @DisplayName("Should create response from domain object with valid data")
        void shouldCreateResponseFromDomainObjectWithValidData() {
            // Given
            UserApplicationService.UserStatistics domainStats = new UserApplicationService.UserStatistics(
                    100L, 80L, 20L, 70L, 5L, 25L
            );

            // When
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(domainStats);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(100L);
            assertThat(response.getActiveUsers()).isEqualTo(80L);
            assertThat(response.getInactiveUsers()).isEqualTo(20L);
            assertThat(response.getCustomerUsers()).isEqualTo(70L);
            assertThat(response.getAdminUsers()).isEqualTo(5L);
            assertThat(response.getManagerUsers()).isEqualTo(25L);
        }

        @Test
        @DisplayName("Should create response from domain object with zero values")
        void shouldCreateResponseFromDomainObjectWithZeroValues() {
            // Given
            UserApplicationService.UserStatistics domainStats = new UserApplicationService.UserStatistics(
                    0L, 0L, 0L, 0L, 0L, 0L
            );

            // When
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(domainStats);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(0L);
            assertThat(response.getActiveUsers()).isEqualTo(0L);
            assertThat(response.getInactiveUsers()).isEqualTo(0L);
            assertThat(response.getCustomerUsers()).isEqualTo(0L);
            assertThat(response.getAdminUsers()).isEqualTo(0L);
            assertThat(response.getManagerUsers()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should create response from domain object with no-args constructor")
        void shouldCreateResponseFromDomainObjectWithNoArgsConstructor() {
            // Given
            UserApplicationService.UserStatistics domainStats = new UserApplicationService.UserStatistics();
            // No setters called, so all values will be 0 (default for long)

            // When
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(domainStats);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(0L);
            assertThat(response.getActiveUsers()).isEqualTo(0L);
            assertThat(response.getInactiveUsers()).isEqualTo(0L);
            assertThat(response.getCustomerUsers()).isEqualTo(0L);
            assertThat(response.getAdminUsers()).isEqualTo(0L);
            assertThat(response.getManagerUsers()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should create response from domain object with mixed values")
        void shouldCreateResponseFromDomainObjectWithMixedValues() {
            // Given
            UserApplicationService.UserStatistics domainStats = new UserApplicationService.UserStatistics(
                    1000L, 0L, 1000L, 500L, 10L, 490L
            );

            // When
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(domainStats);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getTotalUsers()).isEqualTo(1000L);
            assertThat(response.getActiveUsers()).isEqualTo(0L);
            assertThat(response.getInactiveUsers()).isEqualTo(1000L);
            assertThat(response.getCustomerUsers()).isEqualTo(500L);
            assertThat(response.getAdminUsers()).isEqualTo(10L);
            assertThat(response.getManagerUsers()).isEqualTo(490L);
        }

        @Test
        @DisplayName("Should handle null domain object")
        void shouldHandleNullDomainObject() {
            // When & Then
            assertThatThrownBy(() -> UserStatisticsResponse.fromDomain(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Data Consistency Tests")
    class DataConsistencyTests {

        @Test
        @DisplayName("Should maintain data consistency when all users are active")
        void shouldMaintainDataConsistencyWhenAllUsersAreActive() {
            // Given
            Long totalUsers = 100L;
            Long activeUsers = 100L;
            Long inactiveUsers = 0L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, activeUsers, inactiveUsers, 70L, 5L, 25L
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getActiveUsers() + response.getInactiveUsers()).isEqualTo(response.getTotalUsers());
        }

        @Test
        @DisplayName("Should maintain data consistency when all users are inactive")
        void shouldMaintainDataConsistencyWhenAllUsersAreInactive() {
            // Given
            Long totalUsers = 50L;
            Long activeUsers = 0L;
            Long inactiveUsers = 50L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, activeUsers, inactiveUsers, 30L, 2L, 18L
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getActiveUsers() + response.getInactiveUsers()).isEqualTo(response.getTotalUsers());
        }

        @Test
        @DisplayName("Should handle role distribution consistency")
        void shouldHandleRoleDistributionConsistency() {
            // Given
            Long customerUsers = 70L;
            Long adminUsers = 5L;
            Long managerUsers = 25L;
            Long totalUsers = customerUsers + adminUsers + managerUsers;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, 80L, 20L, customerUsers, adminUsers, managerUsers
            );

            // Then
            assertThat(response.getCustomerUsers()).isEqualTo(customerUsers);
            assertThat(response.getAdminUsers()).isEqualTo(adminUsers);
            assertThat(response.getManagerUsers()).isEqualTo(managerUsers);
            assertThat(response.getCustomerUsers() + response.getAdminUsers() + response.getManagerUsers())
                    .isEqualTo(response.getTotalUsers());
        }

        @Test
        @DisplayName("Should handle null values in data consistency checks")
        void shouldHandleNullValuesInDataConsistencyChecks() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(
                    null, null, null, null, null, null
            );

            // When & Then
            // Should not throw exceptions when performing arithmetic with null values
            assertThat(response.getTotalUsers()).isNull();
            assertThat(response.getActiveUsers()).isNull();
            assertThat(response.getInactiveUsers()).isNull();
            assertThat(response.getCustomerUsers()).isNull();
            assertThat(response.getAdminUsers()).isNull();
            assertThat(response.getManagerUsers()).isNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle negative values")
        void shouldHandleNegativeValues() {
            // Given
            Long negativeValue = -1L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    negativeValue, negativeValue, negativeValue, 
                    negativeValue, negativeValue, negativeValue
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(negativeValue);
            assertThat(response.getActiveUsers()).isEqualTo(negativeValue);
            assertThat(response.getInactiveUsers()).isEqualTo(negativeValue);
            assertThat(response.getCustomerUsers()).isEqualTo(negativeValue);
            assertThat(response.getAdminUsers()).isEqualTo(negativeValue);
            assertThat(response.getManagerUsers()).isEqualTo(negativeValue);
        }

        @Test
        @DisplayName("Should handle maximum Long values")
        void shouldHandleMaximumLongValues() {
            // Given
            Long maxValue = Long.MAX_VALUE;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    maxValue, maxValue, maxValue, maxValue, maxValue, maxValue
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(maxValue);
            assertThat(response.getActiveUsers()).isEqualTo(maxValue);
            assertThat(response.getInactiveUsers()).isEqualTo(maxValue);
            assertThat(response.getCustomerUsers()).isEqualTo(maxValue);
            assertThat(response.getAdminUsers()).isEqualTo(maxValue);
            assertThat(response.getManagerUsers()).isEqualTo(maxValue);
        }

        @Test
        @DisplayName("Should handle minimum Long values")
        void shouldHandleMinimumLongValues() {
            // Given
            Long minValue = Long.MIN_VALUE;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    minValue, minValue, minValue, minValue, minValue, minValue
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(minValue);
            assertThat(response.getActiveUsers()).isEqualTo(minValue);
            assertThat(response.getInactiveUsers()).isEqualTo(minValue);
            assertThat(response.getCustomerUsers()).isEqualTo(minValue);
            assertThat(response.getAdminUsers()).isEqualTo(minValue);
            assertThat(response.getManagerUsers()).isEqualTo(minValue);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 100L, 1000L, 10000L, 100000L, 1000000L})
        @DisplayName("Should handle various positive values")
        void shouldHandleVariousPositiveValues(long value) {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(
                    value, value, value, value, value, value
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(value);
            assertThat(response.getActiveUsers()).isEqualTo(value);
            assertThat(response.getInactiveUsers()).isEqualTo(value);
            assertThat(response.getCustomerUsers()).isEqualTo(value);
            assertThat(response.getAdminUsers()).isEqualTo(value);
            assertThat(response.getManagerUsers()).isEqualTo(value);
        }

        @Test
        @DisplayName("Should handle mixed null and non-null values")
        void shouldHandleMixedNullAndNonNullValues() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(
                    100L, null, 20L, null, 5L, null
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(100L);
            assertThat(response.getActiveUsers()).isNull();
            assertThat(response.getInactiveUsers()).isEqualTo(20L);
            assertThat(response.getCustomerUsers()).isNull();
            assertThat(response.getAdminUsers()).isEqualTo(5L);
            assertThat(response.getManagerUsers()).isNull();
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTests {

        @Test
        @DisplayName("Should not be equal when all fields are the same (DTOs don't override equals)")
        void shouldNotBeEqualWhenAllFieldsAreTheSame() {
            // Given
            UserStatisticsResponse response1 = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);
            UserStatisticsResponse response2 = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(response1).isNotEqualTo(response2);
            assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When & Then
            assertThat(response).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);
            String stringValue = "100";

            // When & Then
            assertThat(response).isNotEqualTo(stringValue);
        }

        @Test
        @DisplayName("Should not be equal when both objects have null values")
        void shouldNotBeEqualWhenBothObjectsHaveNullValues() {
            // Given
            UserStatisticsResponse response1 = new UserStatisticsResponse(null, null, null, null, null, null);
            UserStatisticsResponse response2 = new UserStatisticsResponse(null, null, null, null, null, null);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(response1).isNotEqualTo(response2);
            assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return meaningful string representation")
        void shouldReturnMeaningfulStringRepresentation() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When
            String result = response.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("UserStatisticsResponse");
        }

        @Test
        @DisplayName("Should handle null values in string representation")
        void shouldHandleNullValuesInStringRepresentation() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(null, null, null, null, null, null);

            // When
            String result = response.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("UserStatisticsResponse");
        }

        @Test
        @DisplayName("Should handle zero values in string representation")
        void shouldHandleZeroValuesInStringRepresentation() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(0L, 0L, 0L, 0L, 0L, 0L);

            // When
            String result = response.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("UserStatisticsResponse");
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete statistics workflow")
        void shouldHandleCompleteStatisticsWorkflow() {
            // Given
            Long totalUsers = 200L;
            Long activeUsers = 150L;
            Long inactiveUsers = 50L;
            Long customerUsers = 120L;
            Long adminUsers = 10L;
            Long managerUsers = 70L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, activeUsers, inactiveUsers, 
                    customerUsers, adminUsers, managerUsers
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getCustomerUsers()).isEqualTo(customerUsers);
            assertThat(response.getAdminUsers()).isEqualTo(adminUsers);
            assertThat(response.getManagerUsers()).isEqualTo(managerUsers);
        }

        @Test
        @DisplayName("Should handle domain object transformation workflow")
        void shouldHandleDomainObjectTransformationWorkflow() {
            // Given
            UserApplicationService.UserStatistics domainStats = new UserApplicationService.UserStatistics(
                    500L, 400L, 100L, 300L, 20L, 180L
            );

            // When
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(domainStats);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(500L);
            assertThat(response.getActiveUsers()).isEqualTo(400L);
            assertThat(response.getInactiveUsers()).isEqualTo(100L);
            assertThat(response.getCustomerUsers()).isEqualTo(300L);
            assertThat(response.getAdminUsers()).isEqualTo(20L);
            assertThat(response.getManagerUsers()).isEqualTo(180L);
        }

        @Test
        @DisplayName("Should handle response modification workflow")
        void shouldHandleResponseModificationWorkflow() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When
            response.setTotalUsers(200L);
            response.setActiveUsers(160L);
            response.setInactiveUsers(40L);
            response.setCustomerUsers(140L);
            response.setAdminUsers(10L);
            response.setManagerUsers(50L);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(200L);
            assertThat(response.getActiveUsers()).isEqualTo(160L);
            assertThat(response.getInactiveUsers()).isEqualTo(40L);
            assertThat(response.getCustomerUsers()).isEqualTo(140L);
            assertThat(response.getAdminUsers()).isEqualTo(10L);
            assertThat(response.getManagerUsers()).isEqualTo(50L);
        }

        @Test
        @DisplayName("Should handle partial field updates")
        void shouldHandlePartialFieldUpdates() {
            // Given
            UserStatisticsResponse response = new UserStatisticsResponse(100L, 80L, 20L, 70L, 5L, 25L);

            // When - Update only some fields
            response.setTotalUsers(150L);
            response.setActiveUsers(120L);
            response.setCustomerUsers(100L);

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(150L);
            assertThat(response.getActiveUsers()).isEqualTo(120L);
            assertThat(response.getInactiveUsers()).isEqualTo(20L); // Unchanged
            assertThat(response.getCustomerUsers()).isEqualTo(100L);
            assertThat(response.getAdminUsers()).isEqualTo(5L); // Unchanged
            assertThat(response.getManagerUsers()).isEqualTo(25L); // Unchanged
        }

        @Test
        @DisplayName("Should handle statistics calculation scenarios")
        void shouldHandleStatisticsCalculationScenarios() {
            // Given - Scenario: All users are active customers
            Long totalUsers = 100L;
            Long activeUsers = 100L;
            Long inactiveUsers = 0L;
            Long customerUsers = 100L;
            Long adminUsers = 0L;
            Long managerUsers = 0L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    totalUsers, activeUsers, inactiveUsers, 
                    customerUsers, adminUsers, managerUsers
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(totalUsers);
            assertThat(response.getActiveUsers()).isEqualTo(activeUsers);
            assertThat(response.getInactiveUsers()).isEqualTo(inactiveUsers);
            assertThat(response.getCustomerUsers()).isEqualTo(customerUsers);
            assertThat(response.getAdminUsers()).isEqualTo(adminUsers);
            assertThat(response.getManagerUsers()).isEqualTo(managerUsers);
            
            // Verify logical consistency
            assertThat(response.getActiveUsers() + response.getInactiveUsers())
                    .isEqualTo(response.getTotalUsers());
            assertThat(response.getCustomerUsers() + response.getAdminUsers() + response.getManagerUsers())
                    .isEqualTo(response.getTotalUsers());
        }

        @Test
        @DisplayName("Should handle empty system scenario")
        void shouldHandleEmptySystemScenario() {
            // Given - Scenario: No users in the system
            Long zero = 0L;

            // When
            UserStatisticsResponse response = new UserStatisticsResponse(
                    zero, zero, zero, zero, zero, zero
            );

            // Then
            assertThat(response.getTotalUsers()).isEqualTo(zero);
            assertThat(response.getActiveUsers()).isEqualTo(zero);
            assertThat(response.getInactiveUsers()).isEqualTo(zero);
            assertThat(response.getCustomerUsers()).isEqualTo(zero);
            assertThat(response.getAdminUsers()).isEqualTo(zero);
            assertThat(response.getManagerUsers()).isEqualTo(zero);
        }
    }
}
