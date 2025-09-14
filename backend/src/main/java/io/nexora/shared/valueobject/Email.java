package io.nexora.shared.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Email value object that encapsulates email validation and behavior.
 * 
 * This value object ensures email addresses are properly formatted and
 * provides a type-safe way to work with email addresses throughout the domain.
 * 
 * Design Principles Applied:
 * - Immutability: Once created, email cannot be changed
 * - Validation: Ensures email format is valid
 * - Value Object: Equality based on value, not identity
 */
@Embeddable
public record Email(@NotBlank String value) {

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!isValidEmail(value.trim())) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    @Override
    public String toString() {
        return value;
    }
}
