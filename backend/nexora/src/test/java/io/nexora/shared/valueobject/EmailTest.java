package io.nexora.shared.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Email value object.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the Email value object.
 * 
 * Test Coverage:
 * - Constructor validation and behavior
 * - Email format validation
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
@DisplayName("Email Value Object Tests")
class EmailTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create email successfully with valid email address")
        void shouldCreateEmailSuccessfullyWithValidEmailAddress() {
            // Given
            String validEmail = "test@example.com";

            // When
            Email email = new Email(validEmail);

            // Then
            assertThat(email).isNotNull();
            assertThat(email.value()).isEqualTo(validEmail);
        }

        @Test
        @DisplayName("Should trim whitespace from email address")
        void shouldTrimWhitespaceFromEmailAddress() {
            // Given
            String emailWithWhitespace = "  test@example.com  ";
            String expectedEmail = "test@example.com";

            // When
            Email email = new Email(emailWithWhitespace);

            // Then
            assertThat(email.value()).isEqualTo(expectedEmail);
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // When & Then
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when email is empty")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> new Email(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when email is only whitespace")
        void shouldThrowExceptionWhenEmailIsOnlyWhitespace() {
            // When & Then
            assertThatThrownBy(() -> new Email("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email cannot be null or empty");
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
                assertThatCode(() -> new Email(validEmail))
                        .as("Should accept valid email: %s", validEmail)
                        .doesNotThrowAnyException();
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
                "user@example",
                "user@.example.com",
                "user@example.",
                "user@example.c",
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
            // When & Then
            assertThatThrownBy(() -> new Email(invalidEmail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid email format: " + invalidEmail);
        }

        @Test
        @DisplayName("Should handle email with multiple dots in domain")
        void shouldHandleEmailWithMultipleDotsInDomain() {
            // Given
            String emailWithMultipleDots = "user@sub.domain.example.com";

            // When
            Email email = new Email(emailWithMultipleDots);

            // Then
            assertThat(email.value()).isEqualTo(emailWithMultipleDots);
        }

        @Test
        @DisplayName("Should handle email with long domain")
        void shouldHandleEmailWithLongDomain() {
            // Given
            String emailWithLongDomain = "user@very-long-domain-name.example.com";

            // When
            Email email = new Email(emailWithLongDomain);

            // Then
            assertThat(email.value()).isEqualTo(emailWithLongDomain);
        }

        @Test
        @DisplayName("Should handle email with numbers in domain")
        void shouldHandleEmailWithNumbersInDomain() {
            // Given
            String emailWithNumbers = "user@domain123.example.com";

            // When
            Email email = new Email(emailWithNumbers);

            // Then
            assertThat(email.value()).isEqualTo(emailWithNumbers);
        }
    }

    @Nested
    @DisplayName("Value Object Behavior Tests")
    class ValueObjectBehaviorTests {

        @Test
        @DisplayName("Should be equal when email values are the same")
        void shouldBeEqualWhenEmailValuesAreTheSame() {
            // Given
            String emailValue = "test@example.com";
            Email email1 = new Email(emailValue);
            Email email2 = new Email(emailValue);

            // When & Then
            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when email values are different")
        void shouldNotBeEqualWhenEmailValuesAreDifferent() {
            // Given
            Email email1 = new Email("test1@example.com");
            Email email2 = new Email("test2@example.com");

            // When & Then
            assertThat(email1).isNotEqualTo(email2);
        }

        @Test
        @DisplayName("Should be equal when email values are same but with different whitespace")
        void shouldBeEqualWhenEmailValuesAreSameButWithDifferentWhitespace() {
            // Given
            Email email1 = new Email("test@example.com");
            Email email2 = new Email("  test@example.com  ");

            // When & Then
            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToString() {
            // Given
            Email email = new Email("test@example.com");

            // When & Then
            assertThat(email).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Email email = new Email("test@example.com");
            String stringValue = "test@example.com";

            // When & Then
            assertThat(email).isNotEqualTo(stringValue);
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return email value in toString")
        void shouldReturnEmailValueInToString() {
            // Given
            String emailValue = "test@example.com";
            Email email = new Email(emailValue);

            // When
            String result = email.toString();

            // Then
            assertThat(result).isEqualTo(emailValue);
        }

        @Test
        @DisplayName("Should return trimmed email value in toString")
        void shouldReturnTrimmedEmailValueInToString() {
            // Given
            String emailValue = "  test@example.com  ";
            String expectedValue = "test@example.com";
            Email email = new Email(emailValue);

            // When
            String result = email.toString();

            // Then
            assertThat(result).isEqualTo(expectedValue);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle email with special characters")
        void shouldHandleEmailWithSpecialCharacters() {
            // Given
            String emailWithSpecialChars = "user+tag@example.com";

            // When
            Email email = new Email(emailWithSpecialChars);

            // Then
            assertThat(email.value()).isEqualTo(emailWithSpecialChars);
        }

        @Test
        @DisplayName("Should handle email with hyphens in domain")
        void shouldHandleEmailWithHyphensInDomain() {
            // Given
            String emailWithHyphens = "user@example-domain.com";

            // When
            Email email = new Email(emailWithHyphens);

            // Then
            assertThat(email.value()).isEqualTo(emailWithHyphens);
        }

        @Test
        @DisplayName("Should handle email with underscores in local part")
        void shouldHandleEmailWithUnderscoresInLocalPart() {
            // Given
            String emailWithUnderscores = "user_name@example.com";

            // When
            Email email = new Email(emailWithUnderscores);

            // Then
            assertThat(email.value()).isEqualTo(emailWithUnderscores);
        }

        @Test
        @DisplayName("Should handle email with numbers in local part")
        void shouldHandleEmailWithNumbersInLocalPart() {
            // Given
            String emailWithNumbers = "user123@example.com";

            // When
            Email email = new Email(emailWithNumbers);

            // Then
            assertThat(email.value()).isEqualTo(emailWithNumbers);
        }

        @Test
        @DisplayName("Should handle very long email address")
        void shouldHandleVeryLongEmailAddress() {
            // Given
            String longLocalPart = "verylonglocalpartthatisstillvalid";
            String longDomain = "verylongdomainnamethatistillvalid.example.com";
            String longEmail = longLocalPart + "@" + longDomain;

            // When
            Email email = new Email(longEmail);

            // Then
            assertThat(email.value()).isEqualTo(longEmail);
        }

        @Test
        @DisplayName("Should handle email with mixed case")
        void shouldHandleEmailWithMixedCase() {
            // Given
            String mixedCaseEmail = "User@Example.COM";

            // When
            Email email = new Email(mixedCaseEmail);

            // Then
            assertThat(email.value()).isEqualTo(mixedCaseEmail);
        }

        @Test
        @DisplayName("Should handle email with international domain")
        void shouldHandleEmailWithInternationalDomain() {
            // Given
            String internationalEmail = "user@example.co.uk";

            // When
            Email email = new Email(internationalEmail);

            // Then
            assertThat(email.value()).isEqualTo(internationalEmail);
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - value cannot be changed after creation")
        void shouldBeImmutable() {
            // Given
            String originalValue = "test@example.com";
            Email email = new Email(originalValue);

            // When & Then
            // The record is immutable by design, so we can't modify the value
            // This test verifies that the value object behaves as expected
            assertThat(email.value()).isEqualTo(originalValue);
        }

        @Test
        @DisplayName("Should create new instance when value is different")
        void shouldCreateNewInstanceWhenValueIsDifferent() {
            // Given
            Email email1 = new Email("test1@example.com");
            Email email2 = new Email("test2@example.com");

            // When & Then
            assertThat(email1).isNotSameAs(email2);
            assertThat(email1).isNotEqualTo(email2);
        }
    }
}