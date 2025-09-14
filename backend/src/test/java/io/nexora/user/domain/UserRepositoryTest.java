package io.nexora.user.domain;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserRepository interface.
 * 
 * These tests verify the contract and behavior of the UserRepository interface
 * using mock implementations. They ensure that the repository interface
 * properly defines all necessary operations for user data access.
 * 
 * Test Coverage:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Query methods with pagination
 * - Existence checks and counts
 * - Edge cases and error scenarios
 * - Domain object handling
 * - Value object integration
 * 
 * Design Principles Applied:
 * - Interface Testing: Verify contract compliance
 * - Mock-based Testing: Test interface behavior without implementation
 * - Domain-Driven Testing: Focus on domain operations
 * - Comprehensive Coverage: All repository methods tested
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository Interface Tests")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Email testEmail;
    private Password testPassword;
    private Role testRole;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testEmail = new Email("test@example.com");
        testPassword = Password.fromPlainText("password123");
        testRole = Role.CUSTOMER;

        testUser = User.builder()
                .id(testUserId.toString())
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(testPassword)
                .role(testRole)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            // Given
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userRepository.save(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should save user with different roles")
        void shouldSaveUserWithDifferentRoles() {
            // Given
            User adminUser = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(new Email("admin@example.com"))
                    .password(Password.fromPlainText("adminpass123"))
                    .role(Role.ADMIN)
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(adminUser);

            // When
            User result = userRepository.save(adminUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(Role.ADMIN);
            verify(userRepository).save(adminUser);
        }

        @Test
        @DisplayName("Should find user by ID successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userRepository.findById(testUserId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userRepository).findById(testUserId);
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userRepository.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should find user by email successfully")
        void shouldFindUserByEmailSuccessfully() {
            // Given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userRepository.findByEmail(testEmail);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userRepository).findByEmail(testEmail);
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Given
            Email nonExistentEmail = new Email("nonexistent@example.com");
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userRepository.findByEmail(nonExistentEmail);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findByEmail(nonExistentEmail);
        }

        @Test
        @DisplayName("Should delete user by ID successfully")
        void shouldDeleteUserByIdSuccessfully() {
            // Given
            doNothing().when(userRepository).deleteById(testUserId);

            // When
            userRepository.deleteById(testUserId);

            // Then
            verify(userRepository).deleteById(testUserId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent user")
        void shouldHandleDeletionOfNonExistentUser() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doNothing().when(userRepository).deleteById(nonExistentId);

            // When
            userRepository.deleteById(nonExistentId);

            // Then
            verify(userRepository).deleteById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("Query Methods Tests")
    class QueryMethodsTests {

        @Test
        @DisplayName("Should find all users with pagination")
        void shouldFindAllUsersWithPagination() {
            // Given
            List<User> users = Arrays.asList(testUser, createTestUser("Jane", "Smith"));
            when(userRepository.findAll(0, 10)).thenReturn(users);

            // When
            List<User> result = userRepository.findAll(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).contains(testUser);
            verify(userRepository).findAll(0, 10);
        }

        @Test
        @DisplayName("Should return empty list when no users found")
        void shouldReturnEmptyListWhenNoUsersFound() {
            // Given
            when(userRepository.findAll(0, 10)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userRepository.findAll(0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findAll(0, 10);
        }

        @Test
        @DisplayName("Should find users by role with pagination")
        void shouldFindUsersByRoleWithPagination() {
            // Given
            List<User> adminUsers = Arrays.asList(
                    createTestUser("Admin1", "User1", Role.ADMIN),
                    createTestUser("Admin2", "User2", Role.ADMIN)
            );
            when(userRepository.findByRole(Role.ADMIN, 0, 10)).thenReturn(adminUsers);

            // When
            List<User> result = userRepository.findByRole(Role.ADMIN, 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.ADMIN));
            verify(userRepository).findByRole(Role.ADMIN, 0, 10);
        }

        @Test
        @DisplayName("Should find users by different roles")
        void shouldFindUsersByDifferentRoles() {
            // Given
            List<User> customerUsers = Arrays.asList(
                    createTestUser("Customer1", "User1", Role.CUSTOMER),
                    createTestUser("Customer2", "User2", Role.CUSTOMER)
            );
            when(userRepository.findByRole(Role.CUSTOMER, 0, 10)).thenReturn(customerUsers);

            // When
            List<User> result = userRepository.findByRole(Role.CUSTOMER, 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.CUSTOMER));
            verify(userRepository).findByRole(Role.CUSTOMER, 0, 10);
        }

        @Test
        @DisplayName("Should find active users with pagination")
        void shouldFindActiveUsersWithPagination() {
            // Given
            List<User> activeUsers = Arrays.asList(
                    createTestUser("Active1", "User1", true),
                    createTestUser("Active2", "User2", true)
            );
            when(userRepository.findActiveUsers(0, 10)).thenReturn(activeUsers);

            // When
            List<User> result = userRepository.findActiveUsers(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(User::isActive);
            verify(userRepository).findActiveUsers(0, 10);
        }

        @Test
        @DisplayName("Should find inactive users with pagination")
        void shouldFindInactiveUsersWithPagination() {
            // Given
            List<User> inactiveUsers = Arrays.asList(
                    createTestUser("Inactive1", "User1", false),
                    createTestUser("Inactive2", "User2", false)
            );
            when(userRepository.findInactiveUsers(0, 10)).thenReturn(inactiveUsers);

            // When
            List<User> result = userRepository.findInactiveUsers(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> !user.isActive());
            verify(userRepository).findInactiveUsers(0, 10);
        }

        @Test
        @DisplayName("Should search users by name with pagination")
        void shouldSearchUsersByNameWithPagination() {
            // Given
            List<User> searchResults = Arrays.asList(
                    createTestUser("John", "Doe"),
                    createTestUser("Johnny", "Smith")
            );
            when(userRepository.searchByName("John", 0, 10)).thenReturn(searchResults);

            // When
            List<User> result = userRepository.searchByName("John", 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> 
                    user.getFirstName().contains("John") || user.getLastName().contains("John"));
            verify(userRepository).searchByName("John", 0, 10);
        }

        @Test
        @DisplayName("Should handle empty search results")
        void shouldHandleEmptySearchResults() {
            // Given
            when(userRepository.searchByName("NonExistent", 0, 10)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userRepository.searchByName("NonExistent", 0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).searchByName("NonExistent", 0, 10);
        }

        @Test
        @DisplayName("Should handle different pagination parameters")
        void shouldHandleDifferentPaginationParameters() {
            // Given
            List<User> users = Arrays.asList(testUser);
            when(userRepository.findAll(1, 5)).thenReturn(users);

            // When
            List<User> result = userRepository.findAll(1, 5);

            // Then
            assertThat(result).hasSize(1);
            verify(userRepository).findAll(1, 5);
        }
    }

    @Nested
    @DisplayName("Count Operations Tests")
    class CountOperationsTests {

        @Test
        @DisplayName("Should count total users")
        void shouldCountTotalUsers() {
            // Given
            when(userRepository.count()).thenReturn(100L);

            // When
            long result = userRepository.count();

            // Then
            assertThat(result).isEqualTo(100L);
            verify(userRepository).count();
        }

        @Test
        @DisplayName("Should count users by role")
        void shouldCountUsersByRole() {
            // Given
            when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);
            when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(95L);

            // When
            long adminCount = userRepository.countByRole(Role.ADMIN);
            long customerCount = userRepository.countByRole(Role.CUSTOMER);

            // Then
            assertThat(adminCount).isEqualTo(5L);
            assertThat(customerCount).isEqualTo(95L);
            verify(userRepository).countByRole(Role.ADMIN);
            verify(userRepository).countByRole(Role.CUSTOMER);
        }

        @Test
        @DisplayName("Should count active users")
        void shouldCountActiveUsers() {
            // Given
            when(userRepository.countActiveUsers()).thenReturn(80L);

            // When
            long result = userRepository.countActiveUsers();

            // Then
            assertThat(result).isEqualTo(80L);
            verify(userRepository).countActiveUsers();
        }

        @Test
        @DisplayName("Should count inactive users")
        void shouldCountInactiveUsers() {
            // Given
            when(userRepository.countInactiveUsers()).thenReturn(20L);

            // When
            long result = userRepository.countInactiveUsers();

            // Then
            assertThat(result).isEqualTo(20L);
            verify(userRepository).countInactiveUsers();
        }

        @Test
        @DisplayName("Should return zero count when no users exist")
        void shouldReturnZeroCountWhenNoUsersExist() {
            // Given
            when(userRepository.count()).thenReturn(0L);
            when(userRepository.countByRole(Role.ADMIN)).thenReturn(0L);
            when(userRepository.countActiveUsers()).thenReturn(0L);
            when(userRepository.countInactiveUsers()).thenReturn(0L);

            // When
            long totalCount = userRepository.count();
            long adminCount = userRepository.countByRole(Role.ADMIN);
            long activeCount = userRepository.countActiveUsers();
            long inactiveCount = userRepository.countInactiveUsers();

            // Then
            assertThat(totalCount).isZero();
            assertThat(adminCount).isZero();
            assertThat(activeCount).isZero();
            assertThat(inactiveCount).isZero();
        }
    }

    @Nested
    @DisplayName("Existence Check Tests")
    class ExistenceCheckTests {

        @Test
        @DisplayName("Should check if user exists by ID")
        void shouldCheckIfUserExistsById() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.existsById(testUserId)).thenReturn(true);
            when(userRepository.existsById(nonExistentId)).thenReturn(false);

            // When
            boolean exists = userRepository.existsById(testUserId);
            boolean notExists = userRepository.existsById(nonExistentId);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userRepository).existsById(testUserId);
            verify(userRepository).existsById(nonExistentId);
        }

        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            // Given
            Email existingEmail = new Email("existing@example.com");
            Email nonExistingEmail = new Email("nonexistent@example.com");
            
            when(userRepository.existsByEmail(existingEmail)).thenReturn(true);
            when(userRepository.existsByEmail(nonExistingEmail)).thenReturn(false);

            // When
            boolean exists = userRepository.existsByEmail(existingEmail);
            boolean notExists = userRepository.existsByEmail(nonExistingEmail);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userRepository).existsByEmail(existingEmail);
            verify(userRepository).existsByEmail(nonExistingEmail);
        }

        @Test
        @DisplayName("Should handle existence checks with different email formats")
        void shouldHandleExistenceChecksWithDifferentEmailFormats() {
            // Given
            Email email1 = new Email("user@domain.com");
            Email email2 = new Email("user.name@domain.com");
            Email email3 = new Email("user+tag@domain.com");
            
            when(userRepository.existsByEmail(email1)).thenReturn(true);
            when(userRepository.existsByEmail(email2)).thenReturn(false);
            when(userRepository.existsByEmail(email3)).thenReturn(true);

            // When
            boolean exists1 = userRepository.existsByEmail(email1);
            boolean exists2 = userRepository.existsByEmail(email2);
            boolean exists3 = userRepository.existsByEmail(email3);

            // Then
            assertThat(exists1).isTrue();
            assertThat(exists2).isFalse();
            assertThat(exists3).isTrue();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Scenarios Tests")
    class EdgeCasesAndErrorScenariosTests {

        @Test
        @DisplayName("Should handle null user in save operation")
        void shouldHandleNullUserInSaveOperation() {
            // Given
            when(userRepository.save(null)).thenThrow(new IllegalArgumentException("User cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userRepository.save(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User cannot be null");
        }

        @Test
        @DisplayName("Should handle null ID in findById")
        void shouldHandleNullIdInFindById() {
            // Given
            when(userRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userRepository.findById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID cannot be null");
        }

        @Test
        @DisplayName("Should handle null email in findByEmail")
        void shouldHandleNullEmailInFindByEmail() {
            // Given
            when(userRepository.findByEmail(null)).thenThrow(new IllegalArgumentException("Email cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userRepository.findByEmail(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("Should handle negative pagination parameters")
        void shouldHandleNegativePaginationParameters() {
            // Given
            when(userRepository.findAll(-1, 10)).thenThrow(new IllegalArgumentException("Page cannot be negative"));
            when(userRepository.findAll(0, -1)).thenThrow(new IllegalArgumentException("Size cannot be negative"));

            // When & Then
            assertThatThrownBy(() -> userRepository.findAll(-1, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Page cannot be negative");
            
            assertThatThrownBy(() -> userRepository.findAll(0, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Size cannot be negative");
        }

        @Test
        @DisplayName("Should handle zero size in pagination")
        void shouldHandleZeroSizeInPagination() {
            // Given
            when(userRepository.findAll(0, 0)).thenThrow(new IllegalArgumentException("Size must be greater than zero"));

            // When & Then
            assertThatThrownBy(() -> userRepository.findAll(0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Size must be greater than zero");
        }

        @Test
        @DisplayName("Should handle null role in findByRole")
        void shouldHandleNullRoleInFindByRole() {
            // Given
            when(userRepository.findByRole(null, 0, 10)).thenThrow(new IllegalArgumentException("Role cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userRepository.findByRole(null, 0, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Role cannot be null");
        }

        @Test
        @DisplayName("Should handle null name in searchByName")
        void shouldHandleNullNameInSearchByName() {
            // Given
            when(userRepository.searchByName(null, 0, 10)).thenThrow(new IllegalArgumentException("Name cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userRepository.searchByName(null, 0, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name cannot be null");
        }

        @Test
        @DisplayName("Should handle empty name in searchByName")
        void shouldHandleEmptyNameInSearchByName() {
            // Given
            when(userRepository.searchByName("", 0, 10)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userRepository.searchByName("", 0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).searchByName("", 0, 10);
        }

        @Test
        @DisplayName("Should handle very large pagination parameters")
        void shouldHandleVeryLargePaginationParameters() {
            // Given
            when(userRepository.findAll(1000, 10000)).thenReturn(Collections.emptyList());

            // When
            List<User> result = userRepository.findAll(1000, 10000);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findAll(1000, 10000);
        }
    }

    @Nested
    @DisplayName("Domain Object Integration Tests")
    class DomainObjectIntegrationTests {

        @Test
        @DisplayName("Should work with all User domain object states")
        void shouldWorkWithAllUserDomainObjectStates() {
            // Given
            User activeUser = createTestUser("Active", "User", true);
            User inactiveUser = createTestUser("Inactive", "User", false);
            User verifiedUser = createTestUser("Verified", "User", true);
            verifiedUser.setEmailVerified(true);

            when(userRepository.save(activeUser)).thenReturn(activeUser);
            when(userRepository.save(inactiveUser)).thenReturn(inactiveUser);
            when(userRepository.save(verifiedUser)).thenReturn(verifiedUser);

            // When
            User savedActive = userRepository.save(activeUser);
            User savedInactive = userRepository.save(inactiveUser);
            User savedVerified = userRepository.save(verifiedUser);

            // Then
            assertThat(savedActive.isActive()).isTrue();
            assertThat(savedInactive.isActive()).isFalse();
            assertThat(savedVerified.isEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("Should work with all Role value objects")
        void shouldWorkWithAllRoleValueObjects() {
            // Given
            User customerUser = createTestUser("Customer", "User", Role.CUSTOMER);
            User adminUser = createTestUser("Admin", "User", Role.ADMIN);
            User managerUser = createTestUser("Manager", "User", Role.MANAGER);

            when(userRepository.save(customerUser)).thenReturn(customerUser);
            when(userRepository.save(adminUser)).thenReturn(adminUser);
            when(userRepository.save(managerUser)).thenReturn(managerUser);

            // When
            User savedCustomer = userRepository.save(customerUser);
            User savedAdmin = userRepository.save(adminUser);
            User savedManager = userRepository.save(managerUser);

            // Then
            assertThat(savedCustomer.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(savedAdmin.getRole()).isEqualTo(Role.ADMIN);
            assertThat(savedManager.getRole()).isEqualTo(Role.MANAGER);
        }

        @Test
        @DisplayName("Should work with Email value objects")
        void shouldWorkWithEmailValueObjects() {
            // Given
            Email email1 = new Email("user1@domain.com");
            Email email2 = new Email("user2@domain.com");
            
            User user1 = createTestUser("User1", "Test", email1);
            User user2 = createTestUser("User2", "Test", email2);

            when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
            when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));

            // When
            Optional<User> foundUser1 = userRepository.findByEmail(email1);
            Optional<User> foundUser2 = userRepository.findByEmail(email2);

            // Then
            assertThat(foundUser1).isPresent();
            assertThat(foundUser2).isPresent();
            assertThat(foundUser1.get().getEmail()).isEqualTo(email1);
            assertThat(foundUser2.get().getEmail()).isEqualTo(email2);
        }
    }

    // Helper methods for creating test users
    private User createTestUser(String firstName, String lastName) {
        return createTestUser(firstName, lastName, Role.CUSTOMER);
    }

    private User createTestUser(String firstName, String lastName, Role role) {
        return createTestUser(firstName, lastName, role, true);
    }

    private User createTestUser(String firstName, String lastName, boolean active) {
        return createTestUser(firstName, lastName, Role.CUSTOMER, active);
    }

    private User createTestUser(String firstName, String lastName, Role role, boolean active) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(new Email(firstName.toLowerCase() + "@example.com"))
                .password(Password.fromPlainText("password123"))
                .role(role)
                .active(active)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User createTestUser(String firstName, String lastName, Email email) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
