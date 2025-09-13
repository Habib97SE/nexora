package io.nexora.user.domain;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User aggregate root entity.
 * 
 * This entity represents a user in the system and encapsulates all user-related
 * data and behavior. It serves as the aggregate root for the User domain.
 * 
 * Design Principles Applied:
 * - Aggregate Root: Controls access to user data and enforces invariants
 * - Rich Domain Model: Contains business logic and validation
 * - Immutable Value Objects: Uses Email, Password, and Role value objects
 * - Encapsulation: Protects internal state and provides controlled access
 * - Single Responsibility: Focuses solely on user-related concerns
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Embedded
    @NotNull(message = "Email is required")
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    @Embedded
    @NotNull(message = "Password is required")
    @AttributeOverride(name = "hashedValue", column = @Column(name = "password"))
    private Password password;

    @Embedded
    @NotNull(message = "Role is required")
    @AttributeOverride(name = "value", column = @Column(name = "role"))
    private Role role;

    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @Column(columnDefinition = "boolean default false")
    private boolean emailVerified;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    @CreationTimestamp
    private LocalDateTime createdAt;

    @PastOrPresent
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Gets the user's full name.
     * 
     * @return The concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Checks if the user is active.
     * 
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Checks if the user's email is verified.
     * 
     * @return true if the email is verified, false otherwise
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * Activates the user account.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the user's email as verified.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the user's last login timestamp.
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Changes the user's password.
     * 
     * @param newPassword The new password
     */
    public void changePassword(Password newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the user's role.
     * 
     * @param newRole The new role
     */
    public void changeRole(Role newRole) {
        this.role = newRole;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the user's profile information.
     * 
     * @param firstName The new first name
     * @param lastName The new last name
     */
    public void updateProfile(String firstName, String lastName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the user can perform admin operations.
     * 
     * @return true if the user has admin privileges, false otherwise
     */
    public boolean canPerformAdminOperations() {
        return this.role.isAdmin();
    }

    /**
     * Checks if the user can perform customer operations.
     * 
     * @return true if the user has customer privileges, false otherwise
     */
    public boolean canPerformCustomerOperations() {
        return this.role.isCustomer();
    }
}
