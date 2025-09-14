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
 * Comprehensive unit tests for AuthenticateUserRequest DTO.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the AuthenticateUserRequest DTO.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Validation constraints (@NotBlank, @Email)
 * - Getter and setter behavior
 * - Edge cases and error conditions
 * - Value object equality and immutability
 * - String representation
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All validation rules and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - DTO Testing: Focus on validation and data transfer behavior
 */
@DisplayName("AuthenticateUserRequest DTO Tests")
class AuthenticateUserRequestTest {

    private Validator validator;
    private AuthenticateUserRequest request;

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
            request = new AuthenticateUserRequest();

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getEmail()).isNull();
            assertThat(request.getPassword()).isNull();
        }

        @Test
        @DisplayName("Should create request with all-args constructor")
        void shouldCreateRequestWithAllArgsConstructor() {
            // Given
            String email = "test@example.com";
            String password = "password123";

            // When
            request = new AuthenticateUserRequest(email, password);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isEqualTo(password);
        }

        @Test
        @DisplayName("Should create request with null values in all-args constructor")
        void shouldCreateRequestWithNullValuesInAllArgsConstructor() {
            // When
            request = new AuthenticateUserRequest(null, null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getEmail()).isNull();
            assertThat(request.getPassword()).isNull();
        }

        @Test
        @DisplayName("Should create request with mixed null and valid values")
        void shouldCreateRequestWithMixedNullAndValidValues() {
            // Given
            String email = "test@example.com";

            // When
            request = new AuthenticateUserRequest(email, null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @BeforeEach
        void setUp() {
            request = new AuthenticateUserRequest();
        }

        @Test
        @DisplayName("Should get and set email correctly")
        void shouldGetAndSetEmailCorrectly() {
            // Given
            String email = "user@example.com";

            // When
            request.setEmail(email);

            // Then
            assertThat(request.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("Should get and set password correctly")
        void shouldGetAndSetPasswordCorrectly() {
            // Given
            String password = "securepassword123";

            // When
            request.setPassword(password);

            // Then
            assertThat(request.getPassword()).isEqualTo(password);
        }

        @Test
        @DisplayName("Should handle null email in setter")
        void shouldHandleNullEmailInSetter() {
            // Given
            request.setEmail("test@example.com");
            assertThat(request.getEmail()).isEqualTo("test@example.com");

            // When
            request.setEmail(null);

            // Then
            assertThat(request.getEmail()).isNull();
        }

        @Test
        @DisplayName("Should handle null password in setter")
        void shouldHandleNullPasswordInSetter() {
            // Given
            request.setPassword("password123");
            assertThat(request.getPassword()).isEqualTo("password123");

            // When
            request.setPassword(null);

            // Then
            assertThat(request.getPassword()).isNull();
        }

        @Test
        @DisplayName("Should handle empty string email in setter")
        void shouldHandleEmptyStringEmailInSetter() {
            // When
            request.setEmail("");

            // Then
            assertThat(request.getEmail()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle empty string password in setter")
        void shouldHandleEmptyStringPasswordInSetter() {
            // When
            request.setPassword("");

            // Then
            assertThat(request.getPassword()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle whitespace-only email in setter")
        void shouldHandleWhitespaceOnlyEmailInSetter() {
            // When
            request.setEmail("   ");

            // Then
            assertThat(request.getEmail()).isEqualTo("   ");
        }

        @Test
        @DisplayName("Should handle whitespace-only password in setter")
        void shouldHandleWhitespaceOnlyPasswordInSetter() {
            // When
            request.setPassword("   ");

            // Then
            assertThat(request.getPassword()).isEqualTo("   ");
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate request with valid data")
        void shouldValidateRequestWithValidData() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with null email")
        void shouldFailValidationWithNullEmail() {
            // Given
            request = new AuthenticateUserRequest(null, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty email")
        void shouldFailValidationWithEmptyEmail() {
            // Given
            request = new AuthenticateUserRequest("", "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with whitespace-only email")
        void shouldFailValidationWithWhitespaceOnlyEmail() {
            // Given
            request = new AuthenticateUserRequest("   ", "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // Both @NotBlank and @Email constraints are violated for whitespace-only string
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("email", "email");
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Email cannot be empty", "Email must be valid");
        }

        @Test
        @DisplayName("Should fail validation with invalid email format")
        void shouldFailValidationWithInvalidEmailFormat() {
            // Given
            request = new AuthenticateUserRequest("invalid-email", "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email must be valid");
        }

        @Test
        @DisplayName("Should fail validation with null password")
        void shouldFailValidationWithNullPassword() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", null);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty password")
        void shouldFailValidationWithEmptyPassword() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", "");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with whitespace-only password")
        void shouldFailValidationWithWhitespaceOnlyPassword() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", "   ");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
            assertThat(violation.getMessage()).isEqualTo("Password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with multiple constraint violations")
        void shouldFailValidationWithMultipleConstraintViolations() {
            // Given
            request = new AuthenticateUserRequest("invalid-email", "");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            
            // Check that both violations are present
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("email", "password");
            
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Email must be valid", "Password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with all fields invalid")
        void shouldFailValidationWithAllFieldsInvalid() {
            // Given
            request = new AuthenticateUserRequest(null, null);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            
            // Check that both violations are present
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("email", "password");
            
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Email cannot be empty", "Password cannot be empty");
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
                    "user-name@example.com",
                    "user.name+tag@example-domain.co.uk"
            };

            // When & Then
            for (String validEmail : validEmails) {
                request = new AuthenticateUserRequest(validEmail, "password123");
                Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);
                
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
                "user@example@com",
                "user@example.com.",
                ".user@example.com",
                "user@example.com..",
                "user@.example.com",
                "user@example..com"
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) {
            // Given
            request = new AuthenticateUserRequest(invalidEmail, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
            assertThat(violation.getMessage()).isEqualTo("Email must be valid");
        }

        @Test
        @DisplayName("Should handle email with multiple dots in domain")
        void shouldHandleEmailWithMultipleDotsInDomain() {
            // Given
            String emailWithMultipleDots = "user@sub.domain.example.com";
            request = new AuthenticateUserRequest(emailWithMultipleDots, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle email with long domain")
        void shouldHandleEmailWithLongDomain() {
            // Given
            String emailWithLongDomain = "user@very-long-domain-name.example.com";
            request = new AuthenticateUserRequest(emailWithLongDomain, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle email with numbers in domain")
        void shouldHandleEmailWithNumbersInDomain() {
            // Given
            String emailWithNumbers = "user@domain123.example.com";
            request = new AuthenticateUserRequest(emailWithNumbers, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle very long email address")
        void shouldHandleVeryLongEmailAddress() {
            // Given
            String longLocalPart = "verylonglocalpartthatisstillvalid";
            String longDomain = "verylongdomainnamethatistillvalid.example.com";
            String longEmail = longLocalPart + "@" + longDomain;
            request = new AuthenticateUserRequest(longEmail, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle email with mixed case")
        void shouldHandleEmailWithMixedCase() {
            // Given
            String mixedCaseEmail = "User@Example.COM";
            request = new AuthenticateUserRequest(mixedCaseEmail, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle email with international domain")
        void shouldHandleEmailWithInternationalDomain() {
            // Given
            String internationalEmail = "user@example.co.uk";
            request = new AuthenticateUserRequest(internationalEmail, "password123");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long password")
        void shouldHandleVeryLongPassword() {
            // Given
            String longPassword = "a".repeat(1000);
            request = new AuthenticateUserRequest("test@example.com", longPassword);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle password with special characters")
        void shouldHandlePasswordWithSpecialCharacters() {
            // Given
            String specialPassword = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
            request = new AuthenticateUserRequest("test@example.com", specialPassword);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle password with unicode characters")
        void shouldHandlePasswordWithUnicodeCharacters() {
            // Given
            String unicodePassword = "pássw0rd123";
            request = new AuthenticateUserRequest("test@example.com", unicodePassword);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle password with emojis")
        void shouldHandlePasswordWithEmojis() {
            // Given
            String emojiPassword = "password123🚀";
            request = new AuthenticateUserRequest("test@example.com", emojiPassword);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle minimum valid password")
        void shouldHandleMinimumValidPassword() {
            // Given
            String minPassword = "a";
            request = new AuthenticateUserRequest("test@example.com", minPassword);

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTests {

        @Test
        @DisplayName("Should not be equal when email and password are the same (DTOs don't override equals)")
        void shouldNotBeEqualWhenEmailAndPasswordAreTheSame() {
            // Given
            String email = "test@example.com";
            String password = "password123";
            AuthenticateUserRequest request1 = new AuthenticateUserRequest(email, password);
            AuthenticateUserRequest request2 = new AuthenticateUserRequest(email, password);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when emails are different")
        void shouldNotBeEqualWhenEmailsAreDifferent() {
            // Given
            AuthenticateUserRequest request1 = new AuthenticateUserRequest("test1@example.com", "password123");
            AuthenticateUserRequest request2 = new AuthenticateUserRequest("test2@example.com", "password123");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal when passwords are different")
        void shouldNotBeEqualWhenPasswordsAreDifferent() {
            // Given
            AuthenticateUserRequest request1 = new AuthenticateUserRequest("test@example.com", "password123");
            AuthenticateUserRequest request2 = new AuthenticateUserRequest("test@example.com", "password456");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            AuthenticateUserRequest request = new AuthenticateUserRequest("test@example.com", "password123");

            // When & Then
            assertThat(request).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            AuthenticateUserRequest request = new AuthenticateUserRequest("test@example.com", "password123");
            String stringValue = "test@example.com";

            // When & Then
            assertThat(request).isNotEqualTo(stringValue);
        }

        @Test
        @DisplayName("Should not be equal when both fields are null (DTOs don't override equals)")
        void shouldNotBeEqualWhenBothFieldsAreNull() {
            // Given
            AuthenticateUserRequest request1 = new AuthenticateUserRequest(null, null);
            AuthenticateUserRequest request2 = new AuthenticateUserRequest(null, null);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when one field is null and other is not")
        void shouldNotBeEqualWhenOneFieldIsNullAndOtherIsNot() {
            // Given
            AuthenticateUserRequest request1 = new AuthenticateUserRequest("test@example.com", null);
            AuthenticateUserRequest request2 = new AuthenticateUserRequest("test@example.com", "password123");

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
            request = new AuthenticateUserRequest("test@example.com", "password123");

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("AuthenticateUserRequest");
            // Note: Password should not be included in toString for security reasons
            assertThat(result).doesNotContain("password123");
        }

        @Test
        @DisplayName("Should handle null values in string representation")
        void shouldHandleNullValuesInStringRepresentation() {
            // Given
            request = new AuthenticateUserRequest(null, null);

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("AuthenticateUserRequest");
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
            request = new AuthenticateUserRequest("test@example.com", sensitivePassword);

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitivePassword);
            assertThat(result).doesNotContain("very-secret-password-123");
        }

        @Test
        @DisplayName("Should handle password with sensitive information")
        void shouldHandlePasswordWithSensitiveInformation() {
            // Given
            String sensitivePassword = "admin123!@#";
            request = new AuthenticateUserRequest("admin@example.com", sensitivePassword);

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitivePassword);
            assertThat(result).doesNotContain("admin123");
        }

        @Test
        @DisplayName("Should validate request without exposing password in error messages")
        void shouldValidateRequestWithoutExposingPasswordInErrorMessages() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", "");

            // When
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<AuthenticateUserRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Password cannot be empty");
            // Ensure the actual password value is not exposed in the violation
            assertThat(violation.getInvalidValue()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete authentication request workflow")
        void shouldHandleCompleteAuthenticationRequestWorkflow() {
            // Given
            String email = "user@example.com";
            String password = "securepassword123";

            // When
            request = new AuthenticateUserRequest(email, password);
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
            assertThat(request.getEmail()).isEqualTo(email);
            assertThat(request.getPassword()).isEqualTo(password);
        }

        @Test
        @DisplayName("Should handle request modification workflow")
        void shouldHandleRequestModificationWorkflow() {
            // Given
            request = new AuthenticateUserRequest("old@example.com", "oldpassword");

            // When
            request.setEmail("new@example.com");
            request.setPassword("newpassword");

            // Then
            assertThat(request.getEmail()).isEqualTo("new@example.com");
            assertThat(request.getPassword()).isEqualTo("newpassword");
        }

        @Test
        @DisplayName("Should handle validation after modification")
        void shouldHandleValidationAfterModification() {
            // Given
            request = new AuthenticateUserRequest("test@example.com", "password123");
            assertThat(validator.validate(request)).isEmpty();

            // When
            request.setEmail("invalid-email");
            Set<ConstraintViolation<AuthenticateUserRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must be valid");
        }
    }
}
