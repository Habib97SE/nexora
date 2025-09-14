package io.nexora.user.domain;

import io.nexora.shared.valueobject.Role;
import io.nexora.shared.valueobject.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository (Port) for User aggregates.
 * 
 * This interface defines the contract for user data access without any
 * infrastructure concerns. It follows the Port-Adapter pattern and keeps
 * the domain layer independent of persistence technology.
 * 
 * Design Principles Applied:
 * - Dependency Inversion: Domain depends on abstraction, not implementation
 * - Interface Segregation: Focused interface for user-specific operations
 * - Domain-Driven Design: Uses domain objects, not infrastructure types
 * - Single Responsibility: Handles only user data access concerns
 */
public interface UserRepository {

    /** Create or update a user aggregate. */
    User save(User user);

    /** Fetch a user by its aggregate ID. */
    Optional<User> findById(UUID id);

    /** Fetch a user by email address (unique business key). */
    Optional<User> findByEmail(Email email);

    /** Simple paged listing (use count() to compute total pages). */
    List<User> findAll(int page, int size);

    /** List users by role (paged). */
    List<User> findByRole(Role role, int page, int size);

    /** List active users (paged). */
    List<User> findActiveUsers(int page, int size);

    /** List inactive users (paged). */
    List<User> findInactiveUsers(int page, int size);

    /** Search users by name (paged). */
    List<User> searchByName(String name, int page, int size);

    /** Total number of users (use with findAll for pagination). */
    long count();

    /** Count users by role. */
    long countByRole(Role role);

    /** Count active users. */
    long countActiveUsers();

    /** Count inactive users. */
    long countInactiveUsers();

    /** Delete by ID. */
    void deleteById(UUID id);

    /** Existence check (useful for invariants/validators). */
    boolean existsById(UUID id);

    /** Check if email exists (useful for uniqueness validation). */
    boolean existsByEmail(Email email);
}
