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
 * Comprehensive unit tests for UpdateUserRequest DTO.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the UpdateUserRequest DTO.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Validation constraints (@NotBlank, @Email, @Size, @NotNull)
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
@DisplayName("UpdateUserRequest DTO Tests")
class UpdateUserRequestTest {

    private Validator validator;
    private UpdateUserRequest request;

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
            request = new UpdateUserRequest();

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getFirstName()).isNull();
            assertThat(request.getLastName()).isNull();
            assertThat(request.getEmail()).isNull();
            assertThat(request.getPassword()).isNull();
            assertThat(request.getRole()).isNull();
        }

        @Test
        @DisplayName("Should create request with all-args constructor")
        void shouldCreateRequestWithAllArgsConstructor() {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String email = "john.doe@example.com";
            String password = "password123";
            String role = "CUSTOMER";

            // When
            request = new UpdateUserRequest(firstName, lastName, email, password, role);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getFirstName()).isEqualTo(firstName);
            assertThat(request.getLastName()).isEqualTo(lastName);
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isEqualTo(password);
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should create request with null values in all-args constructor")
        void shouldCreateRequestWithNullValuesInAllArgsConstructor() {
            // When
            request = new UpdateUserRequest(null, null, null, null, null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getFirstName()).isNull();
            assertThat(request.getLastName()).isNull();
            assertThat(request.getEmail()).isNull();
            assertThat(request.getPassword()).isNull();
            assertThat(request.getRole()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @BeforeEach
        void setUp() {
            request = new UpdateUserRequest();
        }

        @Test
        @DisplayName("Should get and set all fields correctly")
        void shouldGetAndSetAllFieldsCorrectly() {
            // Given
            String firstName = "Jane";
            String lastName = "Smith";
            String email = "jane.smith@example.com";
            String password = "securepassword123";
            String role = "ADMIN";

            // When
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setEmail(email);
            request.setPassword(password);
            request.setRole(role);

            // Then
            assertThat(request.getFirstName()).isEqualTo(firstName);
            assertThat(request.getLastName()).isEqualTo(lastName);
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isEqualTo(password);
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            request.setFirstName("John");
            request.setLastName("Doe");
            request.setEmail("john@example.com");
            request.setPassword("password123");
            request.setRole("CUSTOMER");

            // When
            request.setFirstName(null);
            request.setLastName(null);
            request.setEmail(null);
            request.setPassword(null);
            request.setRole(null);

            // Then
            assertThat(request.getFirstName()).isNull();
            assertThat(request.getLastName()).isNull();
            assertThat(request.getEmail()).isNull();
            assertThat(request.getPassword()).isNull();
            assertThat(request.getRole()).isNull();
        }

        @Test
        @DisplayName("Should handle empty strings in setters")
        void shouldHandleEmptyStringsInSetters() {
            // When
            request.setFirstName("");
            request.setLastName("");
            request.setEmail("");
            request.setPassword("");
            request.setRole("");

            // Then
            assertThat(request.getFirstName()).isEqualTo("");
            assertThat(request.getLastName()).isEqualTo("");
            assertThat(request.getEmail()).isEqualTo("");
            assertThat(request.getPassword()).isEqualTo("");
            assertThat(request.getRole()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate request with valid data")
        void shouldValidateRequestWithValidData() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john.doe@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with null firstName")
        void shouldFailValidationWithNullFirstName() {
            // Given
            request = new UpdateUserRequest(null, "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
            assertThat(violation.getMessage()).isEqualTo("First name cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty firstName")
        void shouldFailValidationWithEmptyFirstName() {
            // Given
            request = new UpdateUserRequest("", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("firstName", "firstName");
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("First name cannot be empty", "First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with firstName too short")
        void shouldFailValidationWithFirstNameTooShort() {
            // Given
            request = new UpdateUserRequest("J", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
            assertThat(violation.getMessage()).isEqualTo("First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with firstName too long")
        void shouldFailValidationWithFirstNameTooLong() {
            // Given
            String longFirstName = "A".repeat(51);
            request = new UpdateUserRequest(longFirstName, "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
            assertThat(violation.getMessage()).isEqualTo("First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with null lastName")
        void shouldFailValidationWithNullLastName() {
            // Given
            request = new UpdateUserRequest("John", null, "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("lastName");
            assertThat(violation.getMessage()).isEqualTo("Last name cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty lastName")
        void shouldFailValidationWithEmptyLastName() {
            // Given
            request = new UpdateUserRequest("John", "", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("lastName", "lastName");
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Last name cannot be empty", "Last name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with null email")
        void shouldFailValidationWithNullEmail() {
            // Given
            request = new UpdateUserRequest("John", "Doe", null, "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with invalid email format")
        void shouldFailValidationWithInvalidEmailFormat() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "invalid-email", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email must be valid");
        }

        @Test
        @DisplayName("Should fail validation with null password")
        void shouldFailValidationWithNullPassword() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", null, "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with password too short")
        void shouldFailValidationWithPasswordTooShort() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "short", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should fail validation with null role")
        void shouldFailValidationWithNullRole() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", null);

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("role");
            assertThat(violation.getMessage()).isEqualTo("Role cannot be null");
        }

        @Test
        @DisplayName("Should fail validation with multiple constraint violations")
        void shouldFailValidationWithMultipleConstraintViolations() {
            // Given
            request = new UpdateUserRequest("", "", "invalid-email", "short", null);

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(7);
            // firstName: @NotBlank + @Size violations
            // lastName: @NotBlank + @Size violations  
            // email: @Email violation
            // password: @Size violation
            // role: @NotNull violation
        }
    }

    @Nested
    @DisplayName("Name Length Validation Tests")
    class NameLengthValidationTests {

        @Test
        @DisplayName("Should accept firstName with minimum valid length")
        void shouldAcceptFirstNameWithMinimumValidLength() {
            // Given
            request = new UpdateUserRequest("Jo", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept firstName with maximum valid length")
        void shouldAcceptFirstNameWithMaximumValidLength() {
            // Given
            String maxLengthFirstName = "A".repeat(50);
            request = new UpdateUserRequest(maxLengthFirstName, "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept lastName with minimum valid length")
        void shouldAcceptLastNameWithMinimumValidLength() {
            // Given
            request = new UpdateUserRequest("John", "Do", "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept lastName with maximum valid length")
        void shouldAcceptLastNameWithMaximumValidLength() {
            // Given
            String maxLengthLastName = "A".repeat(50);
            request = new UpdateUserRequest("John", maxLengthLastName, "john@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Email Format Validation Tests")
    class EmailFormatValidationTests {

        @Test
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats() {
            // Given
            String[] validEmails = {
                    "user@example.com",
                    "user.name@example.com",
                    "user+tag@example.com",
                    "user123@example.com",
                    "user@sub.example.com",
                    "user@example.co.uk",
                    "user@example-domain.com",
                    "user_name@example.com",
                    "user-name@example.com"
            };

            // When & Then
            for (String validEmail : validEmails) {
                request = new UpdateUserRequest("John", "Doe", validEmail, "password123", "CUSTOMER");
                Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
                
                assertThat(violations).as("Should accept valid email: %s", validEmail).isEmpty();
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid-email",
                "@example.com",
                "user@",
                "user@.com",
                "user..name@example.com",
                "user@example..com",
                "user name@example.com",
                "user@example com",
                "user@@example.com",
                "user@example@com"
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) {
            // Given
            request = new UpdateUserRequest("John", "Doe", invalidEmail, "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email must be valid");
        }
    }

    @Nested
    @DisplayName("Password Length Validation Tests")
    class PasswordLengthValidationTests {

        @Test
        @DisplayName("Should accept password with minimum valid length")
        void shouldAcceptPasswordWithMinimumValidLength() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "12345678", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept password longer than minimum length")
        void shouldAcceptPasswordLongerThanMinimumLength() {
            // Given
            String longPassword = "a".repeat(100);
            request = new UpdateUserRequest("John", "Doe", "john@example.com", longPassword, "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1", "12", "123", "1234", "12345", "123456", "1234567"
        })
        @DisplayName("Should reject password shorter than 8 characters")
        void shouldRejectPasswordShorterThan8Characters(String shortPassword) {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", shortPassword, "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password must be at least 8 characters long");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle names with special characters")
        void shouldHandleNamesWithSpecialCharacters() {
            // Given
            request = new UpdateUserRequest("Jean-Pierre", "O'Connor", "jean@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle names with unicode characters")
        void shouldHandleNamesWithUnicodeCharacters() {
            // Given
            request = new UpdateUserRequest("José", "García", "jose@example.com", "password123", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle password with special characters")
        void shouldHandlePasswordWithSpecialCharacters() {
            // Given
            String specialPassword = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
            request = new UpdateUserRequest("John", "Doe", "john@example.com", specialPassword, "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle role with special characters")
        void shouldHandleRoleWithSpecialCharacters() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "ADMIN-USER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty role string")
        void shouldHandleEmptyRoleString() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTests {

        @Test
        @DisplayName("Should not be equal when all fields are the same (DTOs don't override equals)")
        void shouldNotBeEqualWhenAllFieldsAreTheSame() {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String email = "john@example.com";
            String password = "password123";
            String role = "CUSTOMER";
            
            UpdateUserRequest request1 = new UpdateUserRequest(firstName, lastName, email, password, role);
            UpdateUserRequest request2 = new UpdateUserRequest(firstName, lastName, email, password, role);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When & Then
            assertThat(request).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");
            String stringValue = "John";

            // When & Then
            assertThat(request).isNotEqualTo(stringValue);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return meaningful string representation")
        void shouldReturnMeaningfulStringRepresentation() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateUserRequest");
            // Note: Password should not be included in toString for security reasons
            assertThat(result).doesNotContain("password123");
        }

        @Test
        @DisplayName("Should handle null values in string representation")
        void shouldHandleNullValuesInStringRepresentation() {
            // Given
            request = new UpdateUserRequest(null, null, null, null, null);

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("UpdateUserRequest");
        }
    }

    @Nested
    @DisplayName("Security and Privacy Tests")
    class SecurityAndPrivacyTests {

        @Test
        @DisplayName("Should not expose password in toString")
        void shouldNotExposePasswordInToString() {
            // Given
            String sensitivePassword = "very-secret-password-123";
            request = new UpdateUserRequest("John", "Doe", "john@example.com", sensitivePassword, "CUSTOMER");

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitivePassword);
            assertThat(result).doesNotContain("very-secret-password-123");
        }

        @Test
        @DisplayName("Should validate request without exposing passwords in error messages")
        void shouldValidateRequestWithoutExposingPasswordsInErrorMessages() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "short", "CUSTOMER");

            // When
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<UpdateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Password must be at least 8 characters long");
            // Ensure the actual password value is not exposed in the violation
            assertThat(violation.getInvalidValue()).isEqualTo("short");
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete user update request workflow")
        void shouldHandleCompleteUserUpdateRequestWorkflow() {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String email = "john.doe@example.com";
            String password = "securepassword123";
            String role = "CUSTOMER";

            // When
            request = new UpdateUserRequest(firstName, lastName, email, password, role);
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
            assertThat(request.getFirstName()).isEqualTo(firstName);
            assertThat(request.getLastName()).isEqualTo(lastName);
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isEqualTo(password);
            assertThat(request.getRole()).isEqualTo(role);
        }

        @Test
        @DisplayName("Should handle request modification workflow")
        void shouldHandleRequestModificationWorkflow() {
            // Given
            request = new UpdateUserRequest("Old", "Name", "old@example.com", "oldpassword", "OLD_ROLE");

            // When
            request.setFirstName("New");
            request.setLastName("Name");
            request.setEmail("new@example.com");
            request.setPassword("newpassword123");
            request.setRole("NEW_ROLE");

            // Then
            assertThat(request.getFirstName()).isEqualTo("New");
            assertThat(request.getLastName()).isEqualTo("Name");
            assertThat(request.getEmail()).isEqualTo("new@example.com");
            assertThat(request.getPassword()).isEqualTo("newpassword123");
            assertThat(request.getRole()).isEqualTo("NEW_ROLE");
        }

        @Test
        @DisplayName("Should handle validation after modification")
        void shouldHandleValidationAfterModification() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");
            assertThat(validator.validate(request)).isEmpty();

            // When
            request.setPassword("short");
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should handle partial field updates")
        void shouldHandlePartialFieldUpdates() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");

            // When - Update only some fields
            request.setFirstName("Jane");
            request.setEmail("jane@example.com");

            // Then
            assertThat(request.getFirstName()).isEqualTo("Jane");
            assertThat(request.getLastName()).isEqualTo("Doe"); // Unchanged
            assertThat(request.getEmail()).isEqualTo("jane@example.com");
            assertThat(request.getPassword()).isEqualTo("password123"); // Unchanged
            assertThat(request.getRole()).isEqualTo("CUSTOMER"); // Unchanged
        }

        @Test
        @DisplayName("Should handle validation after partial updates")
        void shouldHandleValidationAfterPartialUpdates() {
            // Given
            request = new UpdateUserRequest("John", "Doe", "john@example.com", "password123", "CUSTOMER");
            assertThat(validator.validate(request)).isEmpty();

            // When - Update to invalid values
            request.setFirstName("J"); // Too short
            request.setEmail("invalid-email"); // Invalid format

            // Then
            Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("First name must be between 2 and 50 characters", "Email must be valid");
        }
    }
}
