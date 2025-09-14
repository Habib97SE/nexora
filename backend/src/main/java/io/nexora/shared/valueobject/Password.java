package io.nexora.shared.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Password value object that encapsulates password validation and behavior.
 * 
 * This value object ensures passwords meet security requirements and
 * provides a type-safe way to work with passwords throughout the domain.
 * 
 * Design Principles Applied:
 * - Immutability: Once created, password cannot be changed
 * - Validation: Ensures password meets security requirements
 * - Value Object: Equality based on value, not identity
 * - Security: Password is hashed using bcrypt with salt before storage
 */
@Embeddable
public record Password(@NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String hashedValue) {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public Password {
        if (hashedValue == null || hashedValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password value cannot be null or empty");
        }
        // Note: We don't validate length here since this is the hashed value, not plain text
        // The hashed value will be much longer than 8 characters
    }

    /**
     * Creates a password from a plain text value.
     * The password is hashed using bcrypt with a randomly generated salt.
     * 
     * @param plainText The plain text password
     * @return A Password value object with bcrypt hashed value
     */
    public static Password fromPlainText(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            throw new IllegalArgumentException("Plain text password cannot be null or empty");
        }
        if (plainText.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        // Hash the password using bcrypt with automatic salt generation
        String hashedPassword = passwordEncoder.encode(plainText);
        return new Password(hashedPassword);
    }

    /**
     * Creates a password from an already hashed value.
     * This is useful when loading passwords from the database.
     * 
     * @param hashedValue The already hashed password value
     * @return A Password value object with the provided hashed value
     */
    public static Password fromHashedValue(String hashedValue) {
        if (hashedValue == null || hashedValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password value cannot be null or empty");
        }
        return new Password(hashedValue);
    }

    /**
     * Verifies if the provided plain text matches this password.
     * Uses bcrypt to securely compare the plain text with the stored hash.
     * 
     * @param plainText The plain text to verify
     * @return true if the password matches, false otherwise
     */
    public boolean matches(String plainText) {
        if (plainText == null) {
            return false;
        }
        
        // Use bcrypt to verify the plain text against the stored hash
        return passwordEncoder.matches(plainText, this.hashedValue);
    }

    @Override
    public String toString() {
        return "[PROTECTED]"; // Never expose password in logs or toString
    }
}
