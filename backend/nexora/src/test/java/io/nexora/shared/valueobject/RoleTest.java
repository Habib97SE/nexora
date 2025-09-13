package io.nexora.shared.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Role value object.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the Role value object.
 * 
 * Test Coverage:
 * - Constructor validation and behavior
 * - Role validation and predefined roles
 * - Role privilege methods (isAdmin, isCustomer, isManager)
 * - Edge cases and error conditions
 * - Value object equality and immutability
 * - String representation
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All validation rules and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - Value Object Testing: Focus on validation and immutability
 */
@DisplayName("Role Value Object Tests")
class RoleTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create role successfully with valid role value")
        void shouldCreateRoleSuccessfullyWithValidRoleValue() {
            // Given
            String validRole = "CUSTOMER";

            // When
            Role role = new Role(validRole);

            // Then
            assertThat(role).isNotNull();
            assertThat(role.value()).isEqualTo(validRole);
        }

        @Test
        @DisplayName("Should normalize role value to uppercase")
        void shouldNormalizeRoleValueToUppercase() {
            // Given
            String roleValue = "customer";
            String expectedValue = "CUSTOMER";

            // When
            Role role = new Role(roleValue);

            // Then
            assertThat(role.value()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should trim whitespace from role value")
        void shouldTrimWhitespaceFromRoleValue() {
            // Given
            String roleWithWhitespace = "  ADMIN  ";
            String expectedValue = "ADMIN";

            // When
            Role role = new Role(roleWithWhitespace);

            // Then
            assertThat(role.value()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should throw exception when role is null")
        void shouldThrowExceptionWhenRoleIsNull() {
            // When & Then
            assertThatThrownBy(() -> new Role(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Role cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when role is empty")
        void shouldThrowExceptionWhenRoleIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> new Role(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Role cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when role is only whitespace")
        void shouldThrowExceptionWhenRoleIsOnlyWhitespace() {
            // When & Then
            assertThatThrownBy(() -> new Role("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Role cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("Role Validation Tests")
    class RoleValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"CUSTOMER", "ADMIN", "MANAGER"})
        @DisplayName("Should accept valid role values")
        void shouldAcceptValidRoleValues(String validRole) {
            // When & Then
            assertThatCode(() -> new Role(validRole))
                    .as("Should accept valid role: %s", validRole)
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"customer", "admin", "manager"})
        @DisplayName("Should accept valid role values in lowercase")
        void shouldAcceptValidRoleValuesInLowercase(String validRole) {
            // When & Then
            assertThatCode(() -> new Role(validRole))
                    .as("Should accept valid role in lowercase: %s", validRole)
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Customer", "Admin", "Manager"})
        @DisplayName("Should accept valid role values in mixed case")
        void shouldAcceptValidRoleValuesInMixedCase(String validRole) {
            // When & Then
            assertThatCode(() -> new Role(validRole))
                    .as("Should accept valid role in mixed case: %s", validRole)
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "INVALID", "USER", "GUEST", "MODERATOR", "SUPER_ADMIN",
                "CUSTOMER_ADMIN", "ADMIN_USER", "MANAGER_ADMIN"
        })
        @DisplayName("Should reject invalid role values")
        void shouldRejectInvalidRoleValues(String invalidRole) {
            // When & Then
            assertThatThrownBy(() -> new Role(invalidRole))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid role: " + invalidRole)
                    .hasMessageContaining("Valid roles are: CUSTOMER, ADMIN, MANAGER");
        }
    }

    @Nested
    @DisplayName("Predefined Role Constants Tests")
    class PredefinedRoleConstantsTests {

        @Test
        @DisplayName("Should have correct CUSTOMER constant")
        void shouldHaveCorrectCustomerConstant() {
            // When & Then
            assertThat(Role.CUSTOMER).isNotNull();
            assertThat(Role.CUSTOMER.value()).isEqualTo("CUSTOMER");
        }

        @Test
        @DisplayName("Should have correct ADMIN constant")
        void shouldHaveCorrectAdminConstant() {
            // When & Then
            assertThat(Role.ADMIN).isNotNull();
            assertThat(Role.ADMIN.value()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Should have correct MANAGER constant")
        void shouldHaveCorrectManagerConstant() {
            // When & Then
            assertThat(Role.MANAGER).isNotNull();
            assertThat(Role.MANAGER.value()).isEqualTo("MANAGER");
        }

        @Test
        @DisplayName("Should be equal when using predefined constants")
        void shouldBeEqualWhenUsingPredefinedConstants() {
            // Given
            Role customer1 = Role.CUSTOMER;
            Role customer2 = new Role("CUSTOMER");

            // When & Then
            assertThat(customer1).isEqualTo(customer2);
            assertThat(customer1.hashCode()).isEqualTo(customer2.hashCode());
        }
    }

    @Nested
    @DisplayName("Role Privilege Tests")
    class RolePrivilegeTests {

        @Test
        @DisplayName("Should return true for isAdmin when role is ADMIN")
        void shouldReturnTrueForIsAdminWhenRoleIsAdmin() {
            // Given
            Role adminRole = new Role("ADMIN");

            // When
            boolean result = adminRole.isAdmin();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true for isAdmin when role is MANAGER")
        void shouldReturnTrueForIsAdminWhenRoleIsManager() {
            // Given
            Role managerRole = new Role("MANAGER");

            // When
            boolean result = managerRole.isAdmin();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for isAdmin when role is CUSTOMER")
        void shouldReturnFalseForIsAdminWhenRoleIsCustomer() {
            // Given
            Role customerRole = new Role("CUSTOMER");

            // When
            boolean result = customerRole.isAdmin();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for isCustomer when role is CUSTOMER")
        void shouldReturnTrueForIsCustomerWhenRoleIsCustomer() {
            // Given
            Role customerRole = new Role("CUSTOMER");

            // When
            boolean result = customerRole.isCustomer();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for isCustomer when role is ADMIN")
        void shouldReturnFalseForIsCustomerWhenRoleIsAdmin() {
            // Given
            Role adminRole = new Role("ADMIN");

            // When
            boolean result = adminRole.isCustomer();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for isCustomer when role is MANAGER")
        void shouldReturnFalseForIsCustomerWhenRoleIsManager() {
            // Given
            Role managerRole = new Role("MANAGER");

            // When
            boolean result = managerRole.isCustomer();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return true for isManager when role is MANAGER")
        void shouldReturnTrueForIsManagerWhenRoleIsManager() {
            // Given
            Role managerRole = new Role("MANAGER");

            // When
            boolean result = managerRole.isManager();

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false for isManager when role is ADMIN")
        void shouldReturnFalseForIsManagerWhenRoleIsAdmin() {
            // Given
            Role adminRole = new Role("ADMIN");

            // When
            boolean result = adminRole.isManager();

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for isManager when role is CUSTOMER")
        void shouldReturnFalseForIsManagerWhenRoleIsCustomer() {
            // Given
            Role customerRole = new Role("CUSTOMER");

            // When
            boolean result = customerRole.isManager();

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Value Object Behavior Tests")
    class ValueObjectBehaviorTests {

        @Test
        @DisplayName("Should be equal when role values are the same")
        void shouldBeEqualWhenRoleValuesAreTheSame() {
            // Given
            String roleValue = "CUSTOMER";
            Role role1 = new Role(roleValue);
            Role role2 = new Role(roleValue);

            // When & Then
            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when role values are different")
        void shouldNotBeEqualWhenRoleValuesAreDifferent() {
            // Given
            Role role1 = new Role("CUSTOMER");
            Role role2 = new Role("ADMIN");

            // When & Then
            assertThat(role1).isNotEqualTo(role2);
        }

        @Test
        @DisplayName("Should be equal when role values are same but with different case")
        void shouldBeEqualWhenRoleValuesAreSameButWithDifferentCase() {
            // Given
            Role role1 = new Role("CUSTOMER");
            Role role2 = new Role("customer");

            // When & Then
            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("Should be equal when role values are same but with different whitespace")
        void shouldBeEqualWhenRoleValuesAreSameButWithDifferentWhitespace() {
            // Given
            Role role1 = new Role("CUSTOMER");
            Role role2 = new Role("  CUSTOMER  ");

            // When & Then
            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Role role = new Role("CUSTOMER");

            // When & Then
            assertThat(role).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Role role = new Role("CUSTOMER");
            String stringValue = "CUSTOMER";

            // When & Then
            assertThat(role).isNotEqualTo(stringValue);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return role value in toString")
        void shouldReturnRoleValueInToString() {
            // Given
            String roleValue = "CUSTOMER";
            Role role = new Role(roleValue);

            // When
            String result = role.toString();

            // Then
            assertThat(result).isEqualTo(roleValue);
        }

        @Test
        @DisplayName("Should return normalized role value in toString")
        void shouldReturnNormalizedRoleValueInToString() {
            // Given
            String roleValue = "customer";
            String expectedValue = "CUSTOMER";
            Role role = new Role(roleValue);

            // When
            String result = role.toString();

            // Then
            assertThat(result).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should return trimmed role value in toString")
        void shouldReturnTrimmedRoleValueInToString() {
            // Given
            String roleValue = "  ADMIN  ";
            String expectedValue = "ADMIN";
            Role role = new Role(roleValue);

            // When
            String result = role.toString();

            // Then
            assertThat(result).isEqualTo(expectedValue);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle role with extra whitespace")
        void shouldHandleRoleWithExtraWhitespace() {
            // Given
            String roleWithWhitespace = "  \t  CUSTOMER  \n  ";
            String expectedValue = "CUSTOMER";

            // When
            Role role = new Role(roleWithWhitespace);

            // Then
            assertThat(role.value()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should handle role with mixed case and whitespace")
        void shouldHandleRoleWithMixedCaseAndWhitespace() {
            // Given
            String roleWithMixedCase = "  admin  ";
            String expectedValue = "ADMIN";

            // When
            Role role = new Role(roleWithMixedCase);

            // Then
            assertThat(role.value()).isEqualTo(expectedValue);
        }

        @Test
        @DisplayName("Should handle very long invalid role")
        void shouldHandleVeryLongInvalidRole() {
            // Given
            String longInvalidRole = "VERY_LONG_INVALID_ROLE_NAME_THAT_SHOULD_BE_REJECTED";

            // When & Then
            assertThatThrownBy(() -> new Role(longInvalidRole))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid role: " + longInvalidRole);
        }

        @Test
        @DisplayName("Should handle role with special characters")
        void shouldHandleRoleWithSpecialCharacters() {
            // Given
            String roleWithSpecialChars = "CUSTOMER@ADMIN";

            // When & Then
            assertThatThrownBy(() -> new Role(roleWithSpecialChars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid role: " + roleWithSpecialChars);
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - value cannot be changed after creation")
        void shouldBeImmutable() {
            // Given
            String originalValue = "CUSTOMER";
            Role role = new Role(originalValue);

            // When & Then
            // The record is immutable by design, so we can't modify the value
            // This test verifies that the value object behaves as expected
            assertThat(role.value()).isEqualTo(originalValue);
        }

        @Test
        @DisplayName("Should create new instance when value is different")
        void shouldCreateNewInstanceWhenValueIsDifferent() {
            // Given
            Role role1 = new Role("CUSTOMER");
            Role role2 = new Role("ADMIN");

            // When & Then
            assertThat(role1).isNotSameAs(role2);
            assertThat(role1).isNotEqualTo(role2);
        }
    }
}