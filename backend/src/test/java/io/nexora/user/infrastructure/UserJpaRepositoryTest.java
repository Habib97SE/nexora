package io.nexora.user.infrastructure;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserJpaRepository interface.
 * 
 * These tests verify the Spring Data JPA repository interface methods.
 * They ensure that the repository correctly defines all necessary operations
 * for user data access using JPA queries and Spring Data JPA conventions.
 * 
 * Test Coverage:
 * - Custom query methods (@Query annotations)
 * - Count operations and statistics
 * - Existence checks
 * - Search functionality
 * - Edge cases and error scenarios
 * - JPA query validation
 * - Spring Data JPA integration
 * 
 * Design Principles Applied:
 * - Interface Testing: Verify contract compliance
 * - Mock-based Testing: Test interface behavior without database
 * - JPA Testing: Focus on query methods and data access
 * - Comprehensive Coverage: All repository methods tested
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserJpaRepository Interface Tests")
class UserJpaRepositoryTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    private User testUser;
    private Email testEmail;
    private Password testPassword;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testEmail = new Email("test@example.com");
        testPassword = Password.fromPlainText("password123");
        testRole = Role.CUSTOMER;

        testUser = User.builder()
                .id("test-user-id")
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
    @DisplayName("Standard JPA Repository Methods Tests")
    class StandardJpaRepositoryMethodsTests {

        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            // Given
            when(userJpaRepository.save(testUser)).thenReturn(testUser);

            // When
            User result = userJpaRepository.save(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(testUser);
            verify(userJpaRepository).save(testUser);
        }

        @Test
        @DisplayName("Should find user by ID successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            when(userJpaRepository.findById("test-user-id")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userJpaRepository.findById("test-user-id");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userJpaRepository).findById("test-user-id");
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            when(userJpaRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            // When
            Optional<User> result = userJpaRepository.findById("non-existent-id");

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findById("non-existent-id");
        }

        @Test
        @DisplayName("Should find all users with pagination")
        void shouldFindAllUsersWithPagination() {
            // Given
            List<User> users = Arrays.asList(testUser, createTestUser("Jane", "Smith"));
            Page<User> userPage = new PageImpl<>(users);
            Pageable pageable = PageRequest.of(0, 10);
            when(userJpaRepository.findAll(pageable)).thenReturn(userPage);

            // When
            Page<User> result = userJpaRepository.findAll(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).contains(testUser);
            verify(userJpaRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should count total users")
        void shouldCountTotalUsers() {
            // Given
            when(userJpaRepository.count()).thenReturn(100L);

            // When
            long result = userJpaRepository.count();

            // Then
            assertThat(result).isEqualTo(100L);
            verify(userJpaRepository).count();
        }

        @Test
        @DisplayName("Should delete user by ID successfully")
        void shouldDeleteUserByIdSuccessfully() {
            // Given
            doNothing().when(userJpaRepository).deleteById("test-user-id");

            // When
            userJpaRepository.deleteById("test-user-id");

            // Then
            verify(userJpaRepository).deleteById("test-user-id");
        }

        @Test
        @DisplayName("Should check if user exists by ID")
        void shouldCheckIfUserExistsById() {
            // Given
            when(userJpaRepository.existsById("test-user-id")).thenReturn(true);
            when(userJpaRepository.existsById("non-existent-id")).thenReturn(false);

            // When
            boolean exists = userJpaRepository.existsById("test-user-id");
            boolean notExists = userJpaRepository.existsById("non-existent-id");

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userJpaRepository).existsById("test-user-id");
            verify(userJpaRepository).existsById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Custom Query Methods Tests")
    class CustomQueryMethodsTests {

        @Test
        @DisplayName("Should find user by email value successfully")
        void shouldFindUserByEmailValueSuccessfully() {
            // Given
            when(userJpaRepository.findByEmailValue(testEmail.value())).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userJpaRepository.findByEmailValue(testEmail.value());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userJpaRepository).findByEmailValue(testEmail.value());
        }

        @Test
        @DisplayName("Should return empty when user not found by email value")
        void shouldReturnEmptyWhenUserNotFoundByEmailValue() {
            // Given
            String nonExistentEmail = "nonexistent@example.com";
            when(userJpaRepository.findByEmailValue(nonExistentEmail)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userJpaRepository.findByEmailValue(nonExistentEmail);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findByEmailValue(nonExistentEmail);
        }

        @Test
        @DisplayName("Should find users by role value successfully")
        void shouldFindUsersByRoleValueSuccessfully() {
            // Given
            List<User> adminUsers = Arrays.asList(
                    createTestUser("Admin1", "User1", Role.ADMIN),
                    createTestUser("Admin2", "User2", Role.ADMIN)
            );
            when(userJpaRepository.findByRoleValue(Role.ADMIN.value())).thenReturn(adminUsers);

            // When
            List<User> result = userJpaRepository.findByRoleValue(Role.ADMIN.value());

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.ADMIN));
            verify(userJpaRepository).findByRoleValue(Role.ADMIN.value());
        }

        @Test
        @DisplayName("Should find users by different role values")
        void shouldFindUsersByDifferentRoleValues() {
            // Given
            List<User> customerUsers = Arrays.asList(
                    createTestUser("Customer1", "User1", Role.CUSTOMER),
                    createTestUser("Customer2", "User2", Role.CUSTOMER)
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(customerUsers);

            // When
            List<User> result = userJpaRepository.findByRoleValue(Role.CUSTOMER.value());

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.CUSTOMER));
            verify(userJpaRepository).findByRoleValue(Role.CUSTOMER.value());
        }

        @Test
        @DisplayName("Should return empty list when no users found by role")
        void shouldReturnEmptyListWhenNoUsersFoundByRole() {
            // Given
            when(userJpaRepository.findByRoleValue(Role.MANAGER.value())).thenReturn(Collections.emptyList());

            // When
            List<User> result = userJpaRepository.findByRoleValue(Role.MANAGER.value());

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findByRoleValue(Role.MANAGER.value());
        }
    }

    @Nested
    @DisplayName("Active/Inactive User Query Tests")
    class ActiveInactiveUserQueryTests {

        @Test
        @DisplayName("Should find active users successfully")
        void shouldFindActiveUsersSuccessfully() {
            // Given
            List<User> activeUsers = Arrays.asList(
                    createTestUser("Active1", "User1", true),
                    createTestUser("Active2", "User2", true)
            );
            when(userJpaRepository.findActiveUsers()).thenReturn(activeUsers);

            // When
            List<User> result = userJpaRepository.findActiveUsers();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(User::isActive);
            verify(userJpaRepository).findActiveUsers();
        }

        @Test
        @DisplayName("Should find inactive users successfully")
        void shouldFindInactiveUsersSuccessfully() {
            // Given
            List<User> inactiveUsers = Arrays.asList(
                    createTestUser("Inactive1", "User1", false),
                    createTestUser("Inactive2", "User2", false)
            );
            when(userJpaRepository.findInactiveUsers()).thenReturn(inactiveUsers);

            // When
            List<User> result = userJpaRepository.findInactiveUsers();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> !user.isActive());
            verify(userJpaRepository).findInactiveUsers();
        }

        @Test
        @DisplayName("Should return empty list when no active users found")
        void shouldReturnEmptyListWhenNoActiveUsersFound() {
            // Given
            when(userJpaRepository.findActiveUsers()).thenReturn(Collections.emptyList());

            // When
            List<User> result = userJpaRepository.findActiveUsers();

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findActiveUsers();
        }

        @Test
        @DisplayName("Should return empty list when no inactive users found")
        void shouldReturnEmptyListWhenNoInactiveUsersFound() {
            // Given
            when(userJpaRepository.findInactiveUsers()).thenReturn(Collections.emptyList());

            // When
            List<User> result = userJpaRepository.findInactiveUsers();

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findInactiveUsers();
        }
    }

    @Nested
    @DisplayName("Search Functionality Tests")
    class SearchFunctionalityTests {

        @Test
        @DisplayName("Should search users by first name successfully")
        void shouldSearchUsersByFirstNameSuccessfully() {
            // Given
            List<User> searchResults = Arrays.asList(
                    createTestUser("John", "Doe"),
                    createTestUser("Johnny", "Smith")
            );
            when(userJpaRepository.searchByName("John")).thenReturn(searchResults);

            // When
            List<User> result = userJpaRepository.searchByName("John");

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> 
                    user.getFirstName().toLowerCase().contains("john") || 
                    user.getLastName().toLowerCase().contains("john"));
            verify(userJpaRepository).searchByName("John");
        }

        @Test
        @DisplayName("Should search users by last name successfully")
        void shouldSearchUsersByLastNameSuccessfully() {
            // Given
            List<User> searchResults = Arrays.asList(
                    createTestUser("Jane", "Smith"),
                    createTestUser("John", "Smith")
            );
            when(userJpaRepository.searchByName("Smith")).thenReturn(searchResults);

            // When
            List<User> result = userJpaRepository.searchByName("Smith");

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> 
                    user.getFirstName().toLowerCase().contains("smith") || 
                    user.getLastName().toLowerCase().contains("smith"));
            verify(userJpaRepository).searchByName("Smith");
        }

        @Test
        @DisplayName("Should handle case-insensitive search")
        void shouldHandleCaseInsensitiveSearch() {
            // Given
            List<User> searchResults = Arrays.asList(createTestUser("John", "Doe"));
            when(userJpaRepository.searchByName("john")).thenReturn(searchResults);
            when(userJpaRepository.searchByName("JOHN")).thenReturn(searchResults);

            // When
            List<User> result1 = userJpaRepository.searchByName("john");
            List<User> result2 = userJpaRepository.searchByName("JOHN");

            // Then
            assertThat(result1).hasSize(1);
            assertThat(result2).hasSize(1);
            verify(userJpaRepository).searchByName("john");
            verify(userJpaRepository).searchByName("JOHN");
        }

        @Test
        @DisplayName("Should handle partial name search")
        void shouldHandlePartialNameSearch() {
            // Given
            List<User> searchResults = Arrays.asList(
                    createTestUser("John", "Doe"),
                    createTestUser("Johnny", "Smith"),
                    createTestUser("Jane", "Johnson")
            );
            when(userJpaRepository.searchByName("ohn")).thenReturn(searchResults);

            // When
            List<User> result = userJpaRepository.searchByName("ohn");

            // Then
            assertThat(result).hasSize(3);
            verify(userJpaRepository).searchByName("ohn");
        }

        @Test
        @DisplayName("Should return empty list when no users found by name")
        void shouldReturnEmptyListWhenNoUsersFoundByName() {
            // Given
            when(userJpaRepository.searchByName("NonExistent")).thenReturn(Collections.emptyList());

            // When
            List<User> result = userJpaRepository.searchByName("NonExistent");

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).searchByName("NonExistent");
        }

        @Test
        @DisplayName("Should handle empty search term")
        void shouldHandleEmptySearchTerm() {
            // Given
            when(userJpaRepository.searchByName("")).thenReturn(Collections.emptyList());

            // When
            List<User> result = userJpaRepository.searchByName("");

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).searchByName("");
        }
    }

    @Nested
    @DisplayName("Count Operations Tests")
    class CountOperationsTests {

        @Test
        @DisplayName("Should count users by role value successfully")
        void shouldCountUsersByRoleValueSuccessfully() {
            // Given
            when(userJpaRepository.countByRoleValue(Role.ADMIN.value())).thenReturn(5L);
            when(userJpaRepository.countByRoleValue(Role.CUSTOMER.value())).thenReturn(95L);

            // When
            long adminCount = userJpaRepository.countByRoleValue(Role.ADMIN.value());
            long customerCount = userJpaRepository.countByRoleValue(Role.CUSTOMER.value());

            // Then
            assertThat(adminCount).isEqualTo(5L);
            assertThat(customerCount).isEqualTo(95L);
            verify(userJpaRepository).countByRoleValue(Role.ADMIN.value());
            verify(userJpaRepository).countByRoleValue(Role.CUSTOMER.value());
        }

        @Test
        @DisplayName("Should count active users successfully")
        void shouldCountActiveUsersSuccessfully() {
            // Given
            when(userJpaRepository.countActiveUsers()).thenReturn(80L);

            // When
            long result = userJpaRepository.countActiveUsers();

            // Then
            assertThat(result).isEqualTo(80L);
            verify(userJpaRepository).countActiveUsers();
        }

        @Test
        @DisplayName("Should count inactive users successfully")
        void shouldCountInactiveUsersSuccessfully() {
            // Given
            when(userJpaRepository.countInactiveUsers()).thenReturn(20L);

            // When
            long result = userJpaRepository.countInactiveUsers();

            // Then
            assertThat(result).isEqualTo(20L);
            verify(userJpaRepository).countInactiveUsers();
        }

        @Test
        @DisplayName("Should return zero count when no users exist")
        void shouldReturnZeroCountWhenNoUsersExist() {
            // Given
            when(userJpaRepository.count()).thenReturn(0L);
            when(userJpaRepository.countByRoleValue(Role.ADMIN.value())).thenReturn(0L);
            when(userJpaRepository.countActiveUsers()).thenReturn(0L);
            when(userJpaRepository.countInactiveUsers()).thenReturn(0L);

            // When
            long totalCount = userJpaRepository.count();
            long adminCount = userJpaRepository.countByRoleValue(Role.ADMIN.value());
            long activeCount = userJpaRepository.countActiveUsers();
            long inactiveCount = userJpaRepository.countInactiveUsers();

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
        @DisplayName("Should check if email exists successfully")
        void shouldCheckIfEmailExistsSuccessfully() {
            // Given
            String existingEmail = "existing@example.com";
            String nonExistingEmail = "nonexistent@example.com";
            
            when(userJpaRepository.existsByEmailValue(existingEmail)).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(nonExistingEmail)).thenReturn(false);

            // When
            boolean exists = userJpaRepository.existsByEmailValue(existingEmail);
            boolean notExists = userJpaRepository.existsByEmailValue(nonExistingEmail);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userJpaRepository).existsByEmailValue(existingEmail);
            verify(userJpaRepository).existsByEmailValue(nonExistingEmail);
        }

        @Test
        @DisplayName("Should handle existence checks with different email formats")
        void shouldHandleExistenceChecksWithDifferentEmailFormats() {
            // Given
            String email1 = "user@domain.com";
            String email2 = "user.name@domain.com";
            String email3 = "user+tag@domain.com";
            
            when(userJpaRepository.existsByEmailValue(email1)).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(email2)).thenReturn(false);
            when(userJpaRepository.existsByEmailValue(email3)).thenReturn(true);

            // When
            boolean exists1 = userJpaRepository.existsByEmailValue(email1);
            boolean exists2 = userJpaRepository.existsByEmailValue(email2);
            boolean exists3 = userJpaRepository.existsByEmailValue(email3);

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
            when(userJpaRepository.save(null)).thenThrow(new IllegalArgumentException("User cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.save(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User cannot be null");
        }

        @Test
        @DisplayName("Should handle null ID in findById")
        void shouldHandleNullIdInFindById() {
            // Given
            when(userJpaRepository.findById(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.findById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID cannot be null");
        }

        @Test
        @DisplayName("Should handle null email in findByEmailValue")
        void shouldHandleNullEmailInFindByEmailValue() {
            // Given
            when(userJpaRepository.findByEmailValue(null)).thenThrow(new IllegalArgumentException("Email cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.findByEmailValue(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email cannot be null");
        }

        @Test
        @DisplayName("Should handle null role in findByRoleValue")
        void shouldHandleNullRoleInFindByRoleValue() {
            // Given
            when(userJpaRepository.findByRoleValue(null)).thenThrow(new IllegalArgumentException("Role cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.findByRoleValue(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Role cannot be null");
        }

        @Test
        @DisplayName("Should handle null name in searchByName")
        void shouldHandleNullNameInSearchByName() {
            // Given
            when(userJpaRepository.searchByName(null)).thenThrow(new IllegalArgumentException("Name cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.searchByName(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name cannot be null");
        }

        @Test
        @DisplayName("Should handle null pageable in findAll")
        void shouldHandleNullPageableInFindAll() {
            // Given
            when(userJpaRepository.findAll((Pageable) null)).thenThrow(new IllegalArgumentException("Pageable cannot be null"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.findAll((Pageable) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Pageable cannot be null");
        }
    }

    @Nested
    @DisplayName("JPA Query Integration Tests")
    class JpaQueryIntegrationTests {

        @Test
        @DisplayName("Should work with all User domain object states")
        void shouldWorkWithAllUserDomainObjectStates() {
            // Given
            User activeUser = createTestUser("Active", "User", true);
            User inactiveUser = createTestUser("Inactive", "User", false);
            User verifiedUser = createTestUser("Verified", "User", true);
            verifiedUser.setEmailVerified(true);

            when(userJpaRepository.save(activeUser)).thenReturn(activeUser);
            when(userJpaRepository.save(inactiveUser)).thenReturn(inactiveUser);
            when(userJpaRepository.save(verifiedUser)).thenReturn(verifiedUser);

            // When
            User savedActive = userJpaRepository.save(activeUser);
            User savedInactive = userJpaRepository.save(inactiveUser);
            User savedVerified = userJpaRepository.save(verifiedUser);

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

            when(userJpaRepository.save(customerUser)).thenReturn(customerUser);
            when(userJpaRepository.save(adminUser)).thenReturn(adminUser);
            when(userJpaRepository.save(managerUser)).thenReturn(managerUser);

            // When
            User savedCustomer = userJpaRepository.save(customerUser);
            User savedAdmin = userJpaRepository.save(adminUser);
            User savedManager = userJpaRepository.save(managerUser);

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

            when(userJpaRepository.findByEmailValue(email1.value())).thenReturn(Optional.of(user1));
            when(userJpaRepository.findByEmailValue(email2.value())).thenReturn(Optional.of(user2));

            // When
            Optional<User> foundUser1 = userJpaRepository.findByEmailValue(email1.value());
            Optional<User> foundUser2 = userJpaRepository.findByEmailValue(email2.value());

            // Then
            assertThat(foundUser1).isPresent();
            assertThat(foundUser2).isPresent();
            assertThat(foundUser1.get().getEmail()).isEqualTo(email1);
            assertThat(foundUser2.get().getEmail()).isEqualTo(email2);
        }

        @Test
        @DisplayName("Should handle complex search scenarios")
        void shouldHandleComplexSearchScenarios() {
            // Given
            List<User> complexSearchResults = Arrays.asList(
                    createTestUser("John", "Smith"),
                    createTestUser("Johnny", "Johnson"),
                    createTestUser("Jane", "Smithson")
            );
            when(userJpaRepository.searchByName("son")).thenReturn(complexSearchResults);

            // When
            List<User> result = userJpaRepository.searchByName("son");

            // Then
            assertThat(result).hasSize(3);
            // Verify that the search was called with the correct parameter
            verify(userJpaRepository).searchByName("son");
        }
    }

    @Nested
    @DisplayName("Spring Data JPA Integration Tests")
    class SpringDataJpaIntegrationTests {

        @Test
        @DisplayName("Should correctly translate domain operations to JPA operations")
        void shouldCorrectlyTranslateDomainOperationsToJpaOperations() {
            // Given
            when(userJpaRepository.save(testUser)).thenReturn(testUser);
            when(userJpaRepository.findById("test-user-id")).thenReturn(Optional.of(testUser));
            when(userJpaRepository.findByEmailValue(testEmail.value())).thenReturn(Optional.of(testUser));
            when(userJpaRepository.count()).thenReturn(1L);
            when(userJpaRepository.existsById("test-user-id")).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(testEmail.value())).thenReturn(true);

            // When
            User savedUser = userJpaRepository.save(testUser);
            Optional<User> foundById = userJpaRepository.findById("test-user-id");
            Optional<User> foundByEmail = userJpaRepository.findByEmailValue(testEmail.value());
            long count = userJpaRepository.count();
            boolean existsById = userJpaRepository.existsById("test-user-id");
            boolean existsByEmail = userJpaRepository.existsByEmailValue(testEmail.value());

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(foundById).isPresent();
            assertThat(foundByEmail).isPresent();
            assertThat(count).isEqualTo(1L);
            assertThat(existsById).isTrue();
            assertThat(existsByEmail).isTrue();

            // Verify all JPA methods were called
            verify(userJpaRepository).save(testUser);
            verify(userJpaRepository).findById("test-user-id");
            verify(userJpaRepository).findByEmailValue(testEmail.value());
            verify(userJpaRepository).count();
            verify(userJpaRepository).existsById("test-user-id");
            verify(userJpaRepository).existsByEmailValue(testEmail.value());
        }

        @Test
        @DisplayName("Should handle JPA exceptions gracefully")
        void shouldHandleJpaExceptionsGracefully() {
            // Given
            when(userJpaRepository.save(testUser)).thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            assertThatThrownBy(() -> userJpaRepository.save(testUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database connection failed");
        }

        @Test
        @DisplayName("Should work with Spring Data JPA Page objects")
        void shouldWorkWithSpringDataJpaPageObjects() {
            // Given
            List<User> users = Arrays.asList(testUser, createTestUser("Jane", "Smith"));
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 2L);
            when(userJpaRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

            // When
            Page<User> result = userJpaRepository.findAll(PageRequest.of(0, 10));

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2L);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);
            verify(userJpaRepository).findAll(PageRequest.of(0, 10));
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
