package io.nexora.user.domain.service;

import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import io.nexora.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Service for User aggregates.
 * <p>
 * This service encapsulates complex business logic that doesn't naturally fit
 * within a single User entity but is essential to the User domain.
 * <p>
 * Responsibilities:
 * - User lifecycle management (registration, updates, deactivation)
 * - Business rule enforcement across multiple aggregates
 * - Complex domain operations that require coordination between entities
 * - Domain-specific validation and invariants
 * - Authentication and authorization logic
 * <p>
 * Design Principles Applied:
 * - Single Responsibility: Focuses solely on User domain operations
 * - Open/Closed: Extensible for new business rules without modification
 * - Dependency Inversion: Depends on abstractions (UserRepository)
 * - Domain-Driven Design: Encapsulates domain logic, not infrastructure concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserDomainService {

    private final UserRepository userRepository;

    /**
     * Registers a new user with business rule validation.
     * <p>
     * Business Rules:
     * - Email must be unique across all users
     * - Password must meet security requirements
     * - Role must be valid
     * - User starts as inactive until email verification
     *
     * @param user The user to register
     * @return The registered user with generated ID and timestamps
     * @throws IllegalArgumentException if business rules are violated
     */
    @Transactional
    public User registerUser(User user) {
        log.debug("Registering new user: {}", user.getEmail());

        validateUserRegistration(user);

        // Set creation timestamp and initial state
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(false); // Inactive until email verification
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Authenticates a user with email and password.
     * <p>
     * Business Rules:
     * - User must exist
     * - User must be active
     * - Password must match
     * - Updates last login timestamp
     *
     * @param email    The user's email
     * @param password The user's password
     * @return The authenticated user
     * @throws IllegalArgumentException if authentication fails
     */
    @Transactional
    public User authenticateUser(Email email, Password password) {
        log.debug("Authenticating user: {}", email);

        User user = findUserByEmail(email);
        validateUserAuthentication(user, password);

        // Update last login timestamp
        user.updateLastLogin();
        User savedUser = userRepository.save(user);

        log.info("Successfully authenticated user: {}", email);
        return savedUser;
    }

    /**
     * Updates an existing user with business rule validation.
     * <p>
     * Business Rules:
     * - User must exist
     * - Email uniqueness (if email changed)
     * - Role changes require admin privileges
     * - Profile updates are allowed for own account
     *
     * @param userId       The ID of the user to update
     * @param updatedUser  The updated user data
     * @param currentUser  The user making the update (for authorization)
     * @return The updated user
     * @throws IllegalArgumentException if business rules are violated
     */
    @Transactional
    public User updateUser(UUID userId, User updatedUser, User currentUser) {
        log.debug("Updating user with ID: {}", userId);

        User existingUser = findUserById(userId);
        validateUserUpdate(existingUser, updatedUser, currentUser);

        // Preserve creation timestamp and ID
        updatedUser.setId(existingUser.getId());
        updatedUser.setCreatedAt(existingUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(updatedUser);
        log.info("Successfully updated user with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Changes user password with business rule validation.
     * <p>
     * Business Rules:
     * - User must exist
     * - Current password must be provided and correct
     * - New password must meet security requirements
     *
     * @param userId        The ID of the user
     * @param currentPassword The current password
     * @param newPassword   The new password
     * @return The updated user
     * @throws IllegalArgumentException if password change is invalid
     */
    @Transactional
    public User changePassword(UUID userId, Password currentPassword, Password newPassword) {
        log.debug("Changing password for user ID: {}", userId);

        User user = findUserById(userId);
        validatePasswordChange(user, currentPassword, newPassword);

        user.changePassword(newPassword);
        User savedUser = userRepository.save(user);

        log.info("Password changed for user: {}", user.getEmail());
        return savedUser;
    }

    /**
     * Changes user role with business rule validation.
     * <p>
     * Business Rules:
     * - User must exist
     * - Only admins can change roles
     * - Cannot change own role
     *
     * @param userId      The ID of the user
     * @param newRole     The new role
     * @param currentUser The user making the change (must be admin)
     * @return The updated user
     * @throws IllegalArgumentException if role change is invalid
     */
    @Transactional
    public User changeRole(UUID userId, Role newRole, User currentUser) {
        log.debug("Changing role for user ID: {} to role: {}", userId, newRole);

        User user = findUserById(userId);
        validateRoleChange(user, newRole, currentUser);

        user.changeRole(newRole);
        User savedUser = userRepository.save(user);

        log.info("Role changed for user {}: {} -> {}",
                user.getEmail(), user.getRole(), newRole);

        return savedUser;
    }

    /**
     * Activates a user account.
     * <p>
     * Business Rules:
     * - User must exist
     * - User must not already be active
     *
     * @param userId The ID of the user to activate
     * @return The activated user
     * @throws IllegalArgumentException if activation is invalid
     */
    @Transactional
    public User activateUser(UUID userId) {
        log.debug("Activating user with ID: {}", userId);

        User user = findUserById(userId);
        validateUserActivation(user);

        user.activate();
        User savedUser = userRepository.save(user);

        log.info("User activated: {}", user.getEmail());
        return savedUser;
    }

    /**
     * Deactivates a user account (soft delete pattern).
     * <p>
     * Business Rules:
     * - User must exist
     * - User must be active
     * - Cannot deactivate own account
     *
     * @param userId      The ID of the user to deactivate
     * @param currentUser The user making the deactivation (cannot be same user)
     * @return The deactivated user
     * @throws IllegalArgumentException if deactivation is invalid
     */
    @Transactional
    public User deactivateUser(UUID userId, User currentUser) {
        log.debug("Deactivating user with ID: {}", userId);

        User user = findUserById(userId);
        validateUserDeactivation(user, currentUser);

        user.deactivate();
        User savedUser = userRepository.save(user);

        log.info("User deactivated: {}", user.getEmail());
        return savedUser;
    }

    /**
     * Verifies user email address.
     * <p>
     * Business Rules:
     * - User must exist
     * - User must not already be verified
     *
     * @param userId The ID of the user
     * @return The user with verified email
     * @throws IllegalArgumentException if verification is invalid
     */
    @Transactional
    public User verifyEmail(UUID userId) {
        log.debug("Verifying email for user ID: {}", userId);

        User user = findUserById(userId);
        validateEmailVerification(user);

        user.verifyEmail();
        User savedUser = userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
        return savedUser;
    }

    /**
     * Finds a user by ID with proper error handling.
     *
     * @param userId The user ID
     * @return The user
     * @throws IllegalArgumentException if user doesn't exist
     */
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("User with ID %s not found", userId)
                ));
    }

    /**
     * Finds a user by email with proper error handling.
     *
     * @param email The user's email
     * @return The user
     * @throws IllegalArgumentException if user doesn't exist
     */
    public User findUserByEmail(Email email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("User with email %s not found", email)
                ));
    }

    // ==================== PRIVATE VALIDATION METHODS ====================

    /**
     * Validates user registration business rules.
     */
    private void validateUserRegistration(User user) {
        validateBasicUserRules(user);
        validateEmailUniqueness(user.getEmail(), null);
    }

    /**
     * Validates user authentication business rules.
     */
    private void validateUserAuthentication(User user, Password password) {
        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is not active");
        }

        if (!user.getPassword().matches(password.hashedValue())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    /**
     * Validates user update business rules.
     */
    private void validateUserUpdate(User existingUser, User updatedUser, User currentUser) {
        validateBasicUserRules(updatedUser);

        // Only validate email uniqueness if email changed
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            validateEmailUniqueness(updatedUser.getEmail(), existingUser.getId());
        }

        // Role changes require admin privileges
        if (!existingUser.getRole().equals(updatedUser.getRole())) {
            if (!currentUser.canPerformAdminOperations()) {
                throw new IllegalArgumentException("Only admins can change user roles");
            }
        }
    }

    /**
     * Validates password change business rules.
     */
    private void validatePasswordChange(User user, Password currentPassword, Password newPassword) {
        if (!user.getPassword().matches(currentPassword.hashedValue())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (currentPassword.hashedValue().equals(newPassword.hashedValue())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
    }

    /**
     * Validates role change business rules.
     */
    private void validateRoleChange(User user, Role newRole, User currentUser) {
        if (!currentUser.canPerformAdminOperations()) {
            throw new IllegalArgumentException("Only admins can change user roles");
        }

        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cannot change your own role");
        }
    }

    /**
     * Validates user activation business rules.
     */
    private void validateUserActivation(User user) {
        if (user.isActive()) {
            throw new IllegalArgumentException("User is already active");
        }
    }

    /**
     * Validates user deactivation business rules.
     */
    private void validateUserDeactivation(User user, User currentUser) {
        if (!user.isActive()) {
            throw new IllegalArgumentException("User is already inactive");
        }

        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cannot deactivate your own account");
        }
    }

    /**
     * Validates email verification business rules.
     */
    private void validateEmailVerification(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
    }

    /**
     * Validates basic user business rules.
     */
    private void validateBasicUserRules(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("Password is required");
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }

    /**
     * Validates that email is unique across all users.
     */
    private void validateEmailUniqueness(Email email, String excludeUserId) {
        if (userRepository.existsByEmail(email)) {
            // Check if it's the same user (for updates)
            if (excludeUserId != null) {
                Optional<User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeUserId)) {
                    throw new IllegalArgumentException("Email already exists: " + email);
                }
            } else {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
        }
    }
}
