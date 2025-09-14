package io.nexora.user.infrastructure;

import io.nexora.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for User entities.
 * 
 * This interface provides Spring Data JPA methods for user data access.
 * It serves as the infrastructure adapter for the User domain repository.
 * 
 * Design Principles Applied:
 * - Interface Segregation: Focused on JPA-specific operations
 * - Dependency Inversion: Infrastructure depends on domain abstractions
 * - Single Responsibility: Handles only JPA data access concerns
 */
public interface UserJpaRepository extends JpaRepository<User, String> {
    
    /**
     * Find user by email address.
     * 
     * @param email The email address
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE u.email.value = :email")
    Optional<User> findByEmailValue(@Param("email") String email);
    
    /**
     * Find users by role.
     * 
     * @param role The role value
     * @return List of users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role.value = :role")
    List<User> findByRoleValue(@Param("role") String role);
    
    /**
     * Find active users.
     * 
     * @return List of active users
     */
    @Query("SELECT u FROM User u WHERE u.active = true")
    List<User> findActiveUsers();
    
    /**
     * Find inactive users.
     * 
     * @return List of inactive users
     */
    @Query("SELECT u FROM User u WHERE u.active = false")
    List<User> findInactiveUsers();
    
    /**
     * Search users by name (first name or last name).
     * 
     * @param name The name to search for
     * @return List of users matching the name
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);
    
    /**
     * Count users by role.
     * 
     * @param role The role value
     * @return Number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.value = :role")
    long countByRoleValue(@Param("role") String role);
    
    /**
     * Count active users.
     * 
     * @return Number of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();
    
    /**
     * Count inactive users.
     * 
     * @return Number of inactive users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = false")
    long countInactiveUsers();
    
    /**
     * Check if email exists.
     * 
     * @param email The email address
     * @return true if email exists, false otherwise
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email.value = :email")
    boolean existsByEmailValue(@Param("email") String email);
}
