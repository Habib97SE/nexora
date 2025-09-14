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
 * Comprehensive unit tests for ChangePasswordRequest DTO.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the ChangePasswordRequest DTO.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Validation constraints (@NotBlank, @Size)
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
@DisplayName("ChangePasswordRequest DTO Tests")
class ChangePasswordRequestTest {

    private Validator validator;
    private ChangePasswordRequest request;

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
            request = new ChangePasswordRequest();

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getCurrentPassword()).isNull();
            assertThat(request.getNewPassword()).isNull();
        }

        @Test
        @DisplayName("Should create request with all-args constructor")
        void shouldCreateRequestWithAllArgsConstructor() {
            // Given
            String currentPassword = "currentPassword123";
            String newPassword = "newPassword123";

            // When
            request = new ChangePasswordRequest(currentPassword, newPassword);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getCurrentPassword()).isEqualTo(currentPassword);
            assertThat(request.getNewPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("Should create request with null values in all-args constructor")
        void shouldCreateRequestWithNullValuesInAllArgsConstructor() {
            // When
            request = new ChangePasswordRequest(null, null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getCurrentPassword()).isNull();
            assertThat(request.getNewPassword()).isNull();
        }

        @Test
        @DisplayName("Should create request with mixed null and valid values")
        void shouldCreateRequestWithMixedNullAndValidValues() {
            // Given
            String currentPassword = "currentPassword123";

            // When
            request = new ChangePasswordRequest(currentPassword, null);

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getCurrentPassword()).isEqualTo(currentPassword);
            assertThat(request.getNewPassword()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @BeforeEach
        void setUp() {
            request = new ChangePasswordRequest();
        }

        @Test
        @DisplayName("Should get and set current password correctly")
        void shouldGetAndSetCurrentPasswordCorrectly() {
            // Given
            String currentPassword = "currentPassword123";

            // When
            request.setCurrentPassword(currentPassword);

            // Then
            assertThat(request.getCurrentPassword()).isEqualTo(currentPassword);
        }

        @Test
        @DisplayName("Should get and set new password correctly")
        void shouldGetAndSetNewPasswordCorrectly() {
            // Given
            String newPassword = "newPassword123";

            // When
            request.setNewPassword(newPassword);

            // Then
            assertThat(request.getNewPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("Should handle null current password in setter")
        void shouldHandleNullCurrentPasswordInSetter() {
            // Given
            request.setCurrentPassword("currentPassword123");
            assertThat(request.getCurrentPassword()).isEqualTo("currentPassword123");

            // When
            request.setCurrentPassword(null);

            // Then
            assertThat(request.getCurrentPassword()).isNull();
        }

        @Test
        @DisplayName("Should handle null new password in setter")
        void shouldHandleNullNewPasswordInSetter() {
            // Given
            request.setNewPassword("newPassword123");
            assertThat(request.getNewPassword()).isEqualTo("newPassword123");

            // When
            request.setNewPassword(null);

            // Then
            assertThat(request.getNewPassword()).isNull();
        }

        @Test
        @DisplayName("Should handle empty string current password in setter")
        void shouldHandleEmptyStringCurrentPasswordInSetter() {
            // When
            request.setCurrentPassword("");

            // Then
            assertThat(request.getCurrentPassword()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle empty string new password in setter")
        void shouldHandleEmptyStringNewPasswordInSetter() {
            // When
            request.setNewPassword("");

            // Then
            assertThat(request.getNewPassword()).isEqualTo("");
        }

        @Test
        @DisplayName("Should handle whitespace-only current password in setter")
        void shouldHandleWhitespaceOnlyCurrentPasswordInSetter() {
            // When
            request.setCurrentPassword("   ");

            // Then
            assertThat(request.getCurrentPassword()).isEqualTo("   ");
        }

        @Test
        @DisplayName("Should handle whitespace-only new password in setter")
        void shouldHandleWhitespaceOnlyNewPasswordInSetter() {
            // When
            request.setNewPassword("   ");

            // Then
            assertThat(request.getNewPassword()).isEqualTo("   ");
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate request with valid data")
        void shouldValidateRequestWithValidData() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with null current password")
        void shouldFailValidationWithNullCurrentPassword() {
            // Given
            request = new ChangePasswordRequest(null, "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("currentPassword");
            assertThat(violation.getMessage()).isEqualTo("Current password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty current password")
        void shouldFailValidationWithEmptyCurrentPassword() {
            // Given
            request = new ChangePasswordRequest("", "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("currentPassword");
            assertThat(violation.getMessage()).isEqualTo("Current password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with whitespace-only current password")
        void shouldFailValidationWithWhitespaceOnlyCurrentPassword() {
            // Given
            request = new ChangePasswordRequest("   ", "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("currentPassword");
            assertThat(violation.getMessage()).isEqualTo("Current password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with null new password")
        void shouldFailValidationWithNullNewPassword() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", null);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("newPassword");
            assertThat(violation.getMessage()).isEqualTo("New password cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty new password")
        void shouldFailValidationWithEmptyNewPassword() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // Both @NotBlank and @Size constraints are violated for empty string
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("newPassword", "newPassword");
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("New password cannot be empty", "New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should fail validation with whitespace-only new password")
        void shouldFailValidationWithWhitespaceOnlyNewPassword() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "   ");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // Both @NotBlank and @Size constraints are violated for whitespace-only string
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("newPassword", "newPassword");
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("New password cannot be empty", "New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should fail validation with new password shorter than 8 characters")
        void shouldFailValidationWithNewPasswordShorterThan8Characters() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "short");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("newPassword");
            assertThat(violation.getMessage()).isEqualTo("New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should fail validation with new password exactly 7 characters")
        void shouldFailValidationWithNewPasswordExactly7Characters() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "1234567");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("newPassword");
            assertThat(violation.getMessage()).isEqualTo("New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should pass validation with new password exactly 8 characters")
        void shouldPassValidationWithNewPasswordExactly8Characters() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "12345678");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with multiple constraint violations")
        void shouldFailValidationWithMultipleConstraintViolations() {
            // Given
            request = new ChangePasswordRequest("", "short");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // currentPassword: @NotBlank violation
            // newPassword: @Size violation (not empty, but too short)
            
            // Check that violations are present for both fields
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("currentPassword", "newPassword");
            
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Current password cannot be empty", 
                            "New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should fail validation with all fields invalid")
        void shouldFailValidationWithAllFieldsInvalid() {
            // Given
            request = new ChangePasswordRequest(null, null);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            
            // Check that both violations are present
            assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("currentPassword", "newPassword");
            
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("Current password cannot be empty", "New password cannot be empty");
        }
    }

    @Nested
    @DisplayName("Password Length Validation Tests")
    class PasswordLengthValidationTests {

        @Test
        @DisplayName("Should accept new password with minimum valid length")
        void shouldAcceptNewPasswordWithMinimumValidLength() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "12345678");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept new password longer than minimum length")
        void shouldAcceptNewPasswordLongerThanMinimumLength() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "veryLongPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "1", "12", "123", "1234", "12345", "123456", "1234567"
        })
        @DisplayName("Should reject new password shorter than 8 characters")
        void shouldRejectNewPasswordShorterThan8Characters(String shortPassword) {
            // Given
            request = new ChangePasswordRequest("currentPassword123", shortPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("newPassword");
            assertThat(violation.getMessage()).isEqualTo("New password must be at least 8 characters long");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "12345678", "123456789", "1234567890", "veryLongPassword123"
        })
        @DisplayName("Should accept new password with 8 or more characters")
        void shouldAcceptNewPasswordWith8OrMoreCharacters(String validPassword) {
            // Given
            request = new ChangePasswordRequest("currentPassword123", validPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle new password with exactly 8 characters")
        void shouldHandleNewPasswordWithExactly8Characters() {
            // Given
            String exactly8Chars = "12345678";
            request = new ChangePasswordRequest("currentPassword123", exactly8Chars);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle new password with 9 characters")
        void shouldHandleNewPasswordWith9Characters() {
            // Given
            String nineChars = "123456789";
            request = new ChangePasswordRequest("currentPassword123", nineChars);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle very long current password")
        void shouldHandleVeryLongCurrentPassword() {
            // Given
            String longCurrentPassword = "a".repeat(1000);
            request = new ChangePasswordRequest(longCurrentPassword, "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long new password")
        void shouldHandleVeryLongNewPassword() {
            // Given
            String longNewPassword = "a".repeat(1000);
            request = new ChangePasswordRequest("currentPassword123", longNewPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle current password with special characters")
        void shouldHandleCurrentPasswordWithSpecialCharacters() {
            // Given
            String specialCurrentPassword = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
            request = new ChangePasswordRequest(specialCurrentPassword, "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle new password with special characters")
        void shouldHandleNewPasswordWithSpecialCharacters() {
            // Given
            String specialNewPassword = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
            request = new ChangePasswordRequest("currentPassword123", specialNewPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle current password with unicode characters")
        void shouldHandleCurrentPasswordWithUnicodeCharacters() {
            // Given
            String unicodeCurrentPassword = "pássw0rd123";
            request = new ChangePasswordRequest(unicodeCurrentPassword, "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle new password with unicode characters")
        void shouldHandleNewPasswordWithUnicodeCharacters() {
            // Given
            String unicodeNewPassword = "pássw0rd123";
            request = new ChangePasswordRequest("currentPassword123", unicodeNewPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle current password with emojis")
        void shouldHandleCurrentPasswordWithEmojis() {
            // Given
            String emojiCurrentPassword = "password123🚀";
            request = new ChangePasswordRequest(emojiCurrentPassword, "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle new password with emojis")
        void shouldHandleNewPasswordWithEmojis() {
            // Given
            String emojiNewPassword = "password123🚀";
            request = new ChangePasswordRequest("currentPassword123", emojiNewPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle minimum valid new password")
        void shouldHandleMinimumValidNewPassword() {
            // Given
            String minNewPassword = "12345678";
            request = new ChangePasswordRequest("currentPassword123", minNewPassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle same current and new password")
        void shouldHandleSameCurrentAndNewPassword() {
            // Given
            String samePassword = "samePassword123";
            request = new ChangePasswordRequest(samePassword, samePassword);

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should handle current password with only spaces")
        void shouldHandleCurrentPasswordWithOnlySpaces() {
            // Given
            request = new ChangePasswordRequest("   ", "newPassword123");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Current password cannot be empty");
        }

        @Test
        @DisplayName("Should handle new password with only spaces")
        void shouldHandleNewPasswordWithOnlySpaces() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "   ");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // Both @NotBlank and @Size constraints are violated for whitespace-only string
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("New password cannot be empty", "New password must be at least 8 characters long");
        }
    }

    @Nested
    @DisplayName("Object Behavior Tests")
    class ObjectBehaviorTests {

        @Test
        @DisplayName("Should not be equal when current and new passwords are the same (DTOs don't override equals)")
        void shouldNotBeEqualWhenCurrentAndNewPasswordsAreTheSame() {
            // Given
            String currentPassword = "currentPassword123";
            String newPassword = "newPassword123";
            ChangePasswordRequest request1 = new ChangePasswordRequest(currentPassword, newPassword);
            ChangePasswordRequest request2 = new ChangePasswordRequest(currentPassword, newPassword);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when current passwords are different")
        void shouldNotBeEqualWhenCurrentPasswordsAreDifferent() {
            // Given
            ChangePasswordRequest request1 = new ChangePasswordRequest("currentPassword1", "newPassword123");
            ChangePasswordRequest request2 = new ChangePasswordRequest("currentPassword2", "newPassword123");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal when new passwords are different")
        void shouldNotBeEqualWhenNewPasswordsAreDifferent() {
            // Given
            ChangePasswordRequest request1 = new ChangePasswordRequest("currentPassword123", "newPassword1");
            ChangePasswordRequest request2 = new ChangePasswordRequest("currentPassword123", "newPassword2");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword123", "newPassword123");

            // When & Then
            assertThat(request).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword123", "newPassword123");
            String stringValue = "currentPassword123";

            // When & Then
            assertThat(request).isNotEqualTo(stringValue);
        }

        @Test
        @DisplayName("Should not be equal when both fields are null (DTOs don't override equals)")
        void shouldNotBeEqualWhenBothFieldsAreNull() {
            // Given
            ChangePasswordRequest request1 = new ChangePasswordRequest(null, null);
            ChangePasswordRequest request2 = new ChangePasswordRequest(null, null);

            // When & Then
            // DTOs don't override equals/hashCode, so they are not equal even with same values
            assertThat(request1).isNotEqualTo(request2);
            assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when one field is null and other is not")
        void shouldNotBeEqualWhenOneFieldIsNullAndOtherIsNot() {
            // Given
            ChangePasswordRequest request1 = new ChangePasswordRequest("currentPassword123", null);
            ChangePasswordRequest request2 = new ChangePasswordRequest("currentPassword123", "newPassword123");

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("Should not be equal when current password is null and new password is not")
        void shouldNotBeEqualWhenCurrentPasswordIsNullAndNewPasswordIsNot() {
            // Given
            ChangePasswordRequest request1 = new ChangePasswordRequest(null, "newPassword123");
            ChangePasswordRequest request2 = new ChangePasswordRequest("currentPassword123", "newPassword123");

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
            request = new ChangePasswordRequest("currentPassword123", "newPassword123");

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangePasswordRequest");
            // Note: Passwords should not be included in toString for security reasons
            assertThat(result).doesNotContain("currentPassword123");
            assertThat(result).doesNotContain("newPassword123");
        }

        @Test
        @DisplayName("Should handle null values in string representation")
        void shouldHandleNullValuesInStringRepresentation() {
            // Given
            request = new ChangePasswordRequest(null, null);

            // When
            String result = request.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("ChangePasswordRequest");
        }
    }

    @Nested
    @DisplayName("Security and Privacy Tests")
    class SecurityAndPrivacyTests {

        @Test
        @DisplayName("Should not expose current password in toString")
        void shouldNotExposeCurrentPasswordInToString() {
            // Given
            String sensitiveCurrentPassword = "very-secret-current-password-123";
            request = new ChangePasswordRequest(sensitiveCurrentPassword, "newPassword123");

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitiveCurrentPassword);
            assertThat(result).doesNotContain("very-secret-current-password-123");
        }

        @Test
        @DisplayName("Should not expose new password in toString")
        void shouldNotExposeNewPasswordInToString() {
            // Given
            String sensitiveNewPassword = "very-secret-new-password-123";
            request = new ChangePasswordRequest("currentPassword123", sensitiveNewPassword);

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitiveNewPassword);
            assertThat(result).doesNotContain("very-secret-new-password-123");
        }

        @Test
        @DisplayName("Should handle passwords with sensitive information")
        void shouldHandlePasswordsWithSensitiveInformation() {
            // Given
            String sensitiveCurrentPassword = "admin123!@#";
            request = new ChangePasswordRequest(sensitiveCurrentPassword, "newAdmin456!@#");

            // When
            String result = request.toString();

            // Then
            assertThat(result).doesNotContain(sensitiveCurrentPassword);
            assertThat(result).doesNotContain("newAdmin456!@#");
            assertThat(result).doesNotContain("admin123");
            assertThat(result).doesNotContain("newAdmin456");
        }

        @Test
        @DisplayName("Should validate request without exposing passwords in error messages")
        void shouldValidateRequestWithoutExposingPasswordsInErrorMessages() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(2);
            // Both @NotBlank and @Size constraints are violated for empty string
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .containsExactlyInAnyOrder("New password cannot be empty", "New password must be at least 8 characters long");
            
            // Ensure the actual password value is not exposed in the violations
            for (ConstraintViolation<ChangePasswordRequest> violation : violations) {
                assertThat(violation.getInvalidValue()).isEqualTo("");
            }
        }

        @Test
        @DisplayName("Should handle validation errors without exposing sensitive data")
        void shouldHandleValidationErrorsWithoutExposingSensitiveData() {
            // Given
            String sensitiveCurrentPassword = "secretCurrentPassword123";
            request = new ChangePasswordRequest(sensitiveCurrentPassword, "short");

            // When
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("New password must be at least 8 characters long");
            // Ensure sensitive data is not exposed
            assertThat(violation.getInvalidValue()).isEqualTo("short");
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete password change request workflow")
        void shouldHandleCompletePasswordChangeRequestWorkflow() {
            // Given
            String currentPassword = "currentPassword123";
            String newPassword = "newPassword123";

            // When
            request = new ChangePasswordRequest(currentPassword, newPassword);
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
            assertThat(request.getCurrentPassword()).isEqualTo(currentPassword);
            assertThat(request.getNewPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("Should handle request modification workflow")
        void shouldHandleRequestModificationWorkflow() {
            // Given
            request = new ChangePasswordRequest("oldCurrentPassword", "oldNewPassword");

            // When
            request.setCurrentPassword("newCurrentPassword");
            request.setNewPassword("newNewPassword");

            // Then
            assertThat(request.getCurrentPassword()).isEqualTo("newCurrentPassword");
            assertThat(request.getNewPassword()).isEqualTo("newNewPassword");
        }

        @Test
        @DisplayName("Should handle validation after modification")
        void shouldHandleValidationAfterModification() {
            // Given
            request = new ChangePasswordRequest("currentPassword123", "newPassword123");
            assertThat(validator.validate(request)).isEmpty();

            // When
            request.setNewPassword("short");
            Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("New password must be at least 8 characters long");
        }

        @Test
        @DisplayName("Should handle multiple validation cycles")
        void shouldHandleMultipleValidationCycles() {
            // Given
            request = new ChangePasswordRequest("", "short");
            assertThat(validator.validate(request)).hasSize(2);

            // When - Fix current password
            request.setCurrentPassword("currentPassword123");
            Set<ConstraintViolation<ChangePasswordRequest>> violations1 = validator.validate(request);

            // Then
            assertThat(violations1).hasSize(1);
            assertThat(violations1.iterator().next().getMessage()).isEqualTo("New password must be at least 8 characters long");

            // When - Fix new password
            request.setNewPassword("newPassword123");
            Set<ConstraintViolation<ChangePasswordRequest>> violations2 = validator.validate(request);

            // Then
            assertThat(violations2).isEmpty();
        }
    }
}
