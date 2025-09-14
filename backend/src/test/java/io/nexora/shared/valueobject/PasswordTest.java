package io.nexora.shared.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Password value object.
 * 
 * These tests verify the validation logic and behavior of the Password value object.
 */
@DisplayName("Password Value Object Tests")
class PasswordTest {

    @Test
    @DisplayName("Should create password successfully with valid hashed value")
    void shouldCreatePasswordSuccessfullyWithValidHashedValue() {
        // Given
        String hashedValue = "hashedpassword123";

        // When
        Password password = new Password(hashedValue);

        // Then
        assertThat(password).isNotNull();
        assertThat(password.hashedValue()).isEqualTo(hashedValue);
    }

    @Test
    @DisplayName("Should create password from plain text successfully")
    void shouldCreatePasswordFromPlainTextSuccessfully() {
        // Given
        String plainText = "password123";

        // When
        Password password = Password.fromPlainText(plainText);

        // Then
        assertThat(password).isNotNull();
        assertThat(password.hashedValue()).isNotEqualTo(plainText); // bcrypt hash should be different from plain text
        assertThat(password.hashedValue()).startsWith("$2a$12$"); // bcrypt hash format
        assertThat(password.matches(plainText)).isTrue(); // Should match the original plain text
    }

    @Test
    @DisplayName("Should throw exception when hashed value is null")
    void shouldThrowExceptionWhenHashedValueIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Password(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hashed password value cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when hashed value is empty")
    void shouldThrowExceptionWhenHashedValueIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> new Password(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hashed password value cannot be null or empty");
    }

    @Test
    @DisplayName("Should create password from hashed value successfully")
    void shouldCreatePasswordFromHashedValueSuccessfully() {
        // Given
        String hashedValue = "$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        // When
        Password password = Password.fromHashedValue(hashedValue);

        // Then
        assertThat(password).isNotNull();
        assertThat(password.hashedValue()).isEqualTo(hashedValue);
    }

    @Test
    @DisplayName("Should throw exception when creating from hashed value with null")
    void shouldThrowExceptionWhenCreatingFromHashedValueWithNull() {
        // When & Then
        assertThatThrownBy(() -> Password.fromHashedValue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hashed password value cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when creating from hashed value with empty")
    void shouldThrowExceptionWhenCreatingFromHashedValueWithEmpty() {
        // When & Then
        assertThatThrownBy(() -> Password.fromHashedValue(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hashed password value cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when creating from plain text with null value")
    void shouldThrowExceptionWhenCreatingFromPlainTextWithNullValue() {
        // When & Then
        assertThatThrownBy(() -> Password.fromPlainText(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plain text password cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when creating from plain text with empty value")
    void shouldThrowExceptionWhenCreatingFromPlainTextWithEmptyValue() {
        // When & Then
        assertThatThrownBy(() -> Password.fromPlainText(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plain text password cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when creating from plain text with short value")
    void shouldThrowExceptionWhenCreatingFromPlainTextWithShortValue() {
        // When & Then
        assertThatThrownBy(() -> Password.fromPlainText("short"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be at least 8 characters long");
    }

    @Test
    @DisplayName("Should match password when plain text matches hashed value")
    void shouldMatchPasswordWhenPlainTextMatchesHashedValue() {
        // Given
        String plainText = "password123";
        Password password = Password.fromPlainText(plainText);

        // When
        boolean result = password.matches(plainText);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should not match password when plain text does not match hashed value")
    void shouldNotMatchPasswordWhenPlainTextDoesNotMatchHashedValue() {
        // Given
        String plainText = "password123";
        String differentText = "differentpassword";
        Password password = Password.fromPlainText(plainText);

        // When
        boolean result = password.matches(differentText);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should not match password when plain text is null")
    void shouldNotMatchPasswordWhenPlainTextIsNull() {
        // Given
        String plainText = "password123";
        Password password = Password.fromPlainText(plainText);

        // When
        boolean result = password.matches(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should be equal when password values are the same")
    void shouldBeEqualWhenPasswordValuesAreTheSame() {
        // Given
        String hashedValue = "hashedpassword123";
        Password password1 = new Password(hashedValue);
        Password password2 = new Password(hashedValue);

        // When & Then
        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when password values are different")
    void shouldNotBeEqualWhenPasswordValuesAreDifferent() {
        // Given
        Password password1 = new Password("hashedpassword1");
        Password password2 = new Password("hashedpassword2");

        // When & Then
        assertThat(password1).isNotEqualTo(password2);
    }

    @Test
    @DisplayName("Should return protected string in toString")
    void shouldReturnProtectedStringInToString() {
        // Given
        String hashedValue = "hashedpassword123";
        Password password = new Password(hashedValue);

        // When
        String result = password.toString();

        // Then
        assertThat(result).isEqualTo("[PROTECTED]");
    }

    @Test
    @DisplayName("Should produce different hashes for same password due to salt")
    void shouldProduceDifferentHashesForSamePasswordDueToSalt() {
        // Given
        String plainText = "password123";

        // When
        Password password1 = Password.fromPlainText(plainText);
        Password password2 = Password.fromPlainText(plainText);

        // Then
        assertThat(password1.hashedValue()).isNotEqualTo(password2.hashedValue()); // Different hashes due to salt
        assertThat(password1.matches(plainText)).isTrue(); // Both should match the original
        assertThat(password2.matches(plainText)).isTrue();
    }
}
