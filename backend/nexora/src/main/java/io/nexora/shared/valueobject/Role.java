package io.nexora.shared.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Role value object that represents user roles in the system.
 * 
 * This value object encapsulates role validation and provides a type-safe
 * way to work with user roles throughout the domain.
 * 
 * Design Principles Applied:
 * - Immutability: Once created, role cannot be changed
 * - Validation: Ensures role is valid
 * - Value Object: Equality based on value, not identity
 * - Enum-like behavior: Predefined valid roles
 */
@Embeddable
public record Role(@NotBlank String value) {

    // Predefined roles
    public static final Role CUSTOMER = new Role("CUSTOMER");
    public static final Role ADMIN = new Role("ADMIN");
    public static final Role MANAGER = new Role("MANAGER");

    public Role {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        String normalizedValue = value.trim().toUpperCase();
        if (!isValidRole(normalizedValue)) {
            throw new IllegalArgumentException("Invalid role: " + value + ". Valid roles are: CUSTOMER, ADMIN, MANAGER");
        }
    }

    private boolean isValidRole(String role) {
        return role.equals("CUSTOMER") || role.equals("ADMIN") || role.equals("MANAGER");
    }

    /**
     * Checks if this role has admin privileges.
     * 
     * @return true if the role is ADMIN or MANAGER, false otherwise
     */
    public boolean isAdmin() {
        return value.equals("ADMIN") || value.equals("MANAGER");
    }

    /**
     * Checks if this role has customer privileges.
     * 
     * @return true if the role is CUSTOMER, false otherwise
     */
    public boolean isCustomer() {
        return value.equals("CUSTOMER");
    }

    /**
     * Checks if this role has manager privileges.
     * 
     * @return true if the role is MANAGER, false otherwise
     */
    public boolean isManager() {
        return value.equals("MANAGER");
    }

    @Override
    public String toString() {
        return value;
    }
}
