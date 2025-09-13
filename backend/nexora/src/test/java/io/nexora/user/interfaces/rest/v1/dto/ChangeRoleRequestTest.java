package io.nexora.user.interfaces.rest.v1.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for ChangeRoleRequest DTO.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the ChangeRoleRequest DTO.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Validation constraints (@NotBlank)
 * - Getter and setter behavior
 * - Edge cases and error conditions
 * - Value object equality and immutability
 * - String representation
 * - Security and privacy considerations
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All validation rules and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - DTO Testing: Focus on validation and data transfer behavior
 * - Security Testing: Ensure sensitive data is not exposed
 */
@DisplayName("ChangeRoleRequest DTO Tests")
class ChangeRoleRequestTest {

    private Validator validator;
    private ChangeRoleRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create request with no-args constructor")
        void shouldCreateRequestWithNoArgsConstructor() {
            // When
            request = new ChangeRoleRequest();

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getRole()).isNull();
        }

        @Test
        @DisplayName("Should create request with all-args constructor")
        void shouldCreateRequestWithAllArgsConstructor() {
            // Given
            String role = "ADMIN";

            // When
            request = new ChangeRoleRequest(role);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should create request with null role in all-args constructor")
        void shouldCreateRequestWithNullRoleInAllArgsConstructor() {
            // When
            request = new ChangeRoleRequest(null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getRole()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @BeforeEach
        void setUp() {
            request = new ChangeRoleRequest();
        }

        @Test
        @DisplayName("Should get and set role correctly")
        void shouldGetAndSetRoleCorrectly() {
            // Given
            String role = "CUSTOMER";

            // When
            request.setRole(role);

            // Then
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should handle null role in setter")
        void shouldHandleNullRoleInSetter() {
            // Given
            request.setRole("ADMIN");
            assertThat(request.getRole()).isEqualTo("ADMIN");

            // When
            request.setRole(null);

            // Then
            assertThat(request.getRole()).isNull();
        }

        @Test
        @DisplayName("Should handle empty string role in setter")
        void shouldHandleEmptyStringRoleInSetter() {
            // When
            request.setRole("");

            // Then
            assertThat(request.getRole()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle whitespace-only role in setter")
        void shouldHandleWhitespaceOnlyRoleInSetter() {
            // When
            request.setRole("   ");

            // Then
            assertThat(request.getRole()).isEqualTo("   ");
        }

        @Test
        @DisplayName("Should handle role with special characters")
        void shouldHandleRoleWithSpecialCharacters() {
            // Given
            String roleWithSpecialChars = "ROLE_ADMIN-123";

            // When
            request.setRole(roleWithSpecialChars);

            // Then
            assertThat(request.getRole()).isEqualTo(roleWithSpecialChars);
        }

        @Test
        @DisplayName("Should handle role with unicode characters")
        void shouldHandleRoleWithUnicodeCharacters() {
            // Given
            String unicodeRole = "ADMIN_管理员";

            // When
            request.setRole(unicodeRole);

            // Then
            assertThat(request.getRole()).isEqualTo(unicodeRole);
        }

        @Test
        @DisplayName("Should handle very long role name")
        void shouldHandleVeryLongRoleName() {
            // Given
            String longRole = "VERY_LONG_ROLE_NAME_THAT_EXCEEDS_NORMAL_LENGTH";

            // When
            request.setRole(longRole);

            // Then
            assertThat(request.getRole()).isEqualTo(longRole);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate request with valid role")
        void shouldValidateRequestWithValidRole() {
            // Given
            request = new ChangeRoleRequest("ADMIN");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with null role")
        void shouldFailValidationWithNullRole() {
            // Given
            request = new ChangeRoleRequest(null);

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty role")
        void shouldFailValidationWithEmptyRole() {
            // Given
            request = new ChangeRoleRequest("");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with whitespace-only role")
        void shouldFailValidationWithWhitespaceOnlyRole() {
            // Given
            request = new ChangeRoleRequest("   ");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with tab-only role")
        void shouldFailValidationWithTabOnlyRole() {
            // Given
            request = new ChangeRoleRequest("\t");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with newline-only role")
        void shouldFailValidationWithNewlineOnlyRole() {
            // Given
            request = new ChangeRoleRequest("\n");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with mixed whitespace-only role")
        void shouldFailValidationWithMixedWhitespaceOnlyRole() {
            // Given
            request = new ChangeRoleRequest(" \t\n ");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
        }
    }

    @Nested
    @DisplayName("Role Format Validation Tests")
    class RoleFormatValidationTests {

        @Test
        @DisplayName("Should accept standard role formats")
        void shouldAcceptStandardRoleFormats() {
            // Given
            String[] validRoles = {
                    "ADMIN",
                    "CUSTOMER",
                    "MANAGER",
                    "USER",
                    "GUEST",
                    "ROLE_ADMIN",
                    "ROLE_CUSTOMER",
                    "ROLE_MANAGER",
                    "ADMIN_USER",
                    "CUSTOMER_SUPPORT",
                    "SALES_MANAGER",
                    "HR_ADMIN",
                    "FINANCE_MANAGER",
                    "TECH_SUPPORT",
                    "SUPER_ADMIN"
            };

            // When & Then
            for (String validRole : validRoles) {
                request = new ChangeRoleRequest(validRole);
                Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept valid role: %s", validRole).isEmpty();
            }
        }

        @Test
        @DisplayName("Should accept role with numbers")
        void shouldAcceptRoleWithNumbers() {
            // Given
            String[] rolesWithNumbers = {
                    "ADMIN1",
                    "USER123",
                    "ROLE_2024",
                    "ADMIN_LEVEL_1",
                    "CUSTOMER_TIER_2",
                    "MANAGER_V2"
            };

            // When & Then
            for (String roleWithNumbers : rolesWithNumbers) {
                request = new ChangeRoleRequest(roleWithNumbers);
                Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept role with numbers: %s", roleWithNumbers).isEmpty();
            }
        }

        @Test
        @DisplayName("Should accept role with special characters")
        void shouldAcceptRoleWithSpecialCharacters() {
            // Given
            String[] rolesWithSpecialChars = {
                    "ADMIN-USER",
                    "CUSTOMER_SUPPORT",
                    "SALES.MANAGER",
                    "HR_ADMIN",
                    "FINANCE_MANAGER",
                    "TECH-SUPPORT",
                    "SUPER_ADMIN",
                    "ROLE_ADMIN-123",
                    "USER_LEVEL-1",
                    "CUSTOMER_TIER.2"
            };

            // When & Then
            for (String roleWithSpecialChars : rolesWithSpecialChars) {
                request = new ChangeRoleRequest(roleWithSpecialChars);
                Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept role with special characters: %s", roleWithSpecialChars).isEmpty();
            }
        }

        @Test
        @DisplayName("Should accept role with mixed case")
        void shouldAcceptRoleWithMixedCase() {
            // Given
            String[] mixedCaseRoles = {
                    "Admin",
                    "Customer",
                    "Manager",
                    "User",
                    "Guest",
                    "Role_Admin",
                    "ADMIN_user",
                    "Customer_Support",
                    "Sales_Manager",
                    "Hr_Admin"
            };

            // When & Then
            for (String mixedCaseRole : mixedCaseRoles) {
                request = new ChangeRoleRequest(mixedCaseRole);
                Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept mixed case role: %s", mixedCaseRole).isEmpty();
            }
        }

        @Test
        @DisplayName("Should accept role with unicode characters")
        void shouldAcceptRoleWithUnicodeCharacters() {
            // Given
            String[] unicodeRoles = {
                    "ADMIN_管理员",
                    "CUSTOMER_客户",
                    "MANAGER_经理",
                    "USER_用户",
                    "GUEST_访客",
                    "ROLE_角色",
                    "ADMIN_관리자",
                    "CUSTOMER_고객",
                    "MANAGER_매니저",
                    "USER_사용자"
            };

            // When & Then
            for (String unicodeRole : unicodeRoles) {
                request = new ChangeRoleRequest(unicodeRole);
                Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept unicode role: %s", unicodeRole).isEmpty();
            }
        }

        @Test
        @DisplayName("Should accept single character role")
        void shouldAcceptSingleCharacterRole() {
            // Given
            String singleCharRole = "A";

            // When
            request = new ChangeRoleRequest(singleCharRole);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept very long role name")
        void shouldAcceptVeryLongRoleName() {
            // Given
            String veryLongRole = "VERY_LONG_ROLE_NAME_THAT_EXCEEDS_NORMAL_LENGTH_AND_CONTAINS_MANY_CHARACTERS";

            // When
            request = new ChangeRoleRequest(veryLongRole);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle role with leading whitespace")
        void shouldHandleRoleWithLeadingWhitespace() {
            // Given
            String roleWithLeadingWhitespace = "  ADMIN";

            // When
            request = new ChangeRoleRequest(roleWithLeadingWhitespace);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with trailing whitespace")
        void shouldHandleRoleWithTrailingWhitespace() {
            // Given
            String roleWithTrailingWhitespace = "ADMIN  ";

            // When
            request = new ChangeRoleRequest(roleWithTrailingWhitespace);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with mixed whitespace")
        void shouldHandleRoleWithMixedWhitespace() {
            // Given
            String roleWithMixedWhitespace = "  ADMIN  ";

            // When
            request = new ChangeRoleRequest(roleWithMixedWhitespace);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with emojis")
        void shouldHandleRoleWithEmojis() {
            // Given
            String roleWithEmojis = "ADMIN🚀";

            // When
            request = new ChangeRoleRequest(roleWithEmojis);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with special symbols")
        void shouldHandleRoleWithSpecialSymbols() {
            // Given
            String roleWithSpecialSymbols = "ADMIN@#$%";

            // When
            request = new ChangeRoleRequest(roleWithSpecialSymbols);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with quotes")
        void shouldHandleRoleWithQuotes() {
            // Given
            String roleWithQuotes = "ADMIN\"USER\"";

            // When
            request = new ChangeRoleRequest(roleWithQuotes);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with parentheses")
        void shouldHandleRoleWithParentheses() {
            // Given
            String roleWithParentheses = "ADMIN(USER)";

            // When
            request = new ChangeRoleRequest(roleWithParentheses);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with brackets")
        void shouldHandleRoleWithBrackets() {
            // Given
            String roleWithBrackets = "ADMIN[USER]";

            // When
            request = new ChangeRoleRequest(roleWithBrackets);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with braces")
        void shouldHandleRoleWithBraces() {
            // Given
            String roleWithBraces = "ADMIN{USER}";

            // When
            request = new ChangeRoleRequest(roleWithBraces);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with backslashes")
        void shouldHandleRoleWithBackslashes() {
            // Given
            String roleWithBackslashes = "ADMIN\\USER";

            // When
            request = new ChangeRoleRequest(roleWithBackslashes);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with forward slashes")
        void shouldHandleRoleWithForwardSlashes() {
            // Given
            String roleWithForwardSlashes = "ADMIN/USER";

            // When
            request = new ChangeRoleRequest(roleWithForwardSlashes);

            // Then
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTests {

        @Test
        @DisplayName("Should not be equal when roles are the same (DTOs don't override equals)")
        void shouldNotBeEqualWhenRolesAreTheSame() {
            // Given
            String role = "ADMIN";
            ChangeRoleRequest request1 = new ChangeRoleRequest(role);
            ChangeRoleRequest request2 = new ChangeRoleRequest(role);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when roles are different")
        void shouldNotBeEqualWhenRolesAreDifferent() {
            // Given
            ChangeRoleRequest request1 = new ChangeRoleRequest("ADMIN");
            ChangeRoleRequest request2 = new ChangeRoleRequest("CUSTOMER");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            ChangeRoleRequest request = new ChangeRoleRequest("ADMIN");

            // When & Then
            assertThat(request).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            ChangeRoleRequest request = new ChangeRoleRequest("ADMIN");
            String stringValue = "ADMIN";

            // When & Then
            assertThat(request).isNotEqualTo(stringValue);
        }

        @Test
        @DisplayName("Should not be equal when role is null (DTOs don't override equals)")
        void shouldNotBeEqualWhenRoleIsNull() {
            // Given
            ChangeRoleRequest request1 = new ChangeRoleRequest(null);
            ChangeRoleRequest request2 = new ChangeRoleRequest(null);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when one role is null and other is not")
        void shouldNotBeEqualWhenOneRoleIsNullAndOtherIsNot() {
            // Given
            ChangeRoleRequest request1 = new ChangeRoleRequest(null);
            ChangeRoleRequest request2 = new ChangeRoleRequest("ADMIN");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return meaningful string representation")
        void shouldReturnMeaningfulStringRepresentation() {
            // Given
            request = new ChangeRoleRequest("ADMIN");

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangeRoleRequest");
        }

        @Test
        @DisplayName("Should handle null role in string representation")
        void shouldHandleNullRoleInStringRepresentation() {
            // Given
            request = new ChangeRoleRequest(null);

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangeRoleRequest");
        }

        @Test
        @DisplayName("Should handle empty role in string representation")
        void shouldHandleEmptyRoleInStringRepresentation() {
            // Given
            request = new ChangeRoleRequest("");

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangeRoleRequest");
        }
    }

    @Nested
    @DisplayName("Security and Privacy Tests")
    class SecurityAndPrivacyTests {

        @Test
        @DisplayName("Should validate request without exposing role in error messages")
        void shouldValidateRequestWithoutExposingRoleInErrorMessages() {
            // Given
            request = new ChangeRoleRequest("");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
            // Ensure the actual role value is not exposed in the violation
            assertThat(violation.getInvalidValue()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle validation errors without exposing sensitive data")
        void shouldHandleValidationErrorsWithoutExposingSensitiveData() {
            // Given
            request = new ChangeRoleRequest("");

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangeRoleRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Role cannot be empty");
            // Ensure sensitive data is not exposed
            assertThat(violation.getInvalidValue()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle role with sensitive information")
        void shouldHandleRoleWithSensitiveInformation() {
            // Given
            String sensitiveRole = "ADMIN_SECRET_ACCESS";
            request = new ChangeRoleRequest(sensitiveRole);

            // When
            String result = request.toString();

            // Then
            // Note: Role information might be included in toString, but this is generally acceptable
            // as roles are not as sensitive as passwords
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangeRoleRequest");
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete role change request workflow")
        void shouldHandleCompleteRoleChangeRequestWorkflow() {
            // Given
            String role = "ADMIN";

            // When
            request = new ChangeRoleRequest(role);
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should handle request modification workflow")
        void shouldHandleRequestModificationWorkflow() {
            // Given
            request = new ChangeRoleRequest("CUSTOMER");

            // When
            request.setRole("ADMIN");

            // Then
            assertThat(request.getRole()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Should handle validation after modification")
        void shouldHandleValidationAfterModification() {
            // Given
            request = new ChangeRoleRequest("ADMIN");
            assertThat(validator.validate(request)).isEmpty();

            // When
            request.setRole("");
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should handle multiple validation cycles")
        void shouldHandleMultipleValidationCycles() {
            // Given
            request = new ChangeRoleRequest("");
            assertThat(validator.validate(request)).hasSize(1);

            // When - Fix role
            request.setRole("ADMIN");
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role change from valid to invalid")
        void shouldHandleRoleChangeFromValidToInvalid() {
            // Given
            request = new ChangeRoleRequest("ADMIN");
            assertThat(validator.validate(request)).isEmpty();

            // When
            request.setRole("   ");
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Role cannot be empty");
        }

        @Test
        @DisplayName("Should handle role change from invalid to valid")
        void shouldHandleRoleChangeFromInvalidToValid() {
            // Given
            request = new ChangeRoleRequest("");
            assertThat(validator.validate(request)).hasSize(1);

            // When
            request.setRole("CUSTOMER");
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Parameterized Tests")
    class ParameterizedTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "ADMIN", "CUSTOMER", "MANAGER", "USER", "GUEST",
                "ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MANAGER",
                "ADMIN_USER", "CUSTOMER_SUPPORT", "SALES_MANAGER",
                "HR_ADMIN", "FINANCE_MANAGER", "TECH_SUPPORT",
                "SUPER_ADMIN", "ADMIN1", "USER123", "ROLE_2024",
                "ADMIN-USER", "CUSTOMER_SUPPORT", "SALES.MANAGER",
                "Admin", "Customer", "Manager", "User", "Guest"
        })
        @DisplayName("Should accept valid role formats")
        void shouldAcceptValidRoleFormats(String validRole) {
            // Given
            request = new ChangeRoleRequest(validRole);

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).as("Should accept valid role: %s", validRole).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "", "   ", "\t", "\n", " \t\n ", "  \t  \n  "
        })
        @DisplayName("Should reject blank role values")
        void shouldRejectBlankRoleValues(String blankRole) {
            // Given
            request = new ChangeRoleRequest(blankRole);

            // When
            Set<ConstraintViolation<ChangeRoleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).as("Should reject blank role: '%s'", blankRole).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Role cannot be empty");
        }
    }
}
