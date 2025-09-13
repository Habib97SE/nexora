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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
 * Comprehensive unit tests for JpaUserRepository.
 * 
 * These tests verify the JPA implementation of the UserRepository interface.
 * They ensure that the adapter correctly translates domain operations to
 * JPA operations and handles pagination, value object conversion, and
 * error scenarios properly.
 * 
 * Test Coverage:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Query methods with pagination
 * - Count operations and existence checks
 * - Value object integration
 * - Pagination helper method
 * - Edge cases and error scenarios
 * - Adapter pattern verification
 * 
 * Design Principles Applied:
 * - Adapter Testing: Verify correct translation between domain and JPA
 * - Mock-based Testing: Test adapter behavior without database
 * - Infrastructure Testing: Focus on data access implementation
 * - Comprehensive Coverage: All repository methods tested
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JpaUserRepository Tests")
class JpaUserRepositoryTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private JpaUserRepository jpaUserRepository;

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
            when(userJpaRepository.save(testUser)).thenReturn(testUser);

            // When
            User result = jpaUserRepository.save(testUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(testUser);
            verify(userJpaRepository).save(testUser);
        }

        @Test
        @DisplayName("Should find user by ID successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            when(userJpaRepository.findById(testUserId.toString())).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = jpaUserRepository.findById(testUserId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userJpaRepository).findById(testUserId.toString());
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(userJpaRepository.findById(nonExistentId.toString())).thenReturn(Optional.empty());

            // When
            Optional<User> result = jpaUserRepository.findById(nonExistentId);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findById(nonExistentId.toString());
        }

        @Test
        @DisplayName("Should find user by email successfully")
        void shouldFindUserByEmailSuccessfully() {
            // Given
            when(userJpaRepository.findByEmailValue(testEmail.value())).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = jpaUserRepository.findByEmail(testEmail);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userJpaRepository).findByEmailValue(testEmail.value());
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Given
            Email nonExistentEmail = new Email("nonexistent@example.com");
            when(userJpaRepository.findByEmailValue(nonExistentEmail.value())).thenReturn(Optional.empty());

            // When
            Optional<User> result = jpaUserRepository.findByEmail(nonExistentEmail);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findByEmailValue(nonExistentEmail.value());
        }

        @Test
        @DisplayName("Should delete user by ID successfully")
        void shouldDeleteUserByIdSuccessfully() {
            // Given
            doNothing().when(userJpaRepository).deleteById(testUserId.toString());

            // When
            jpaUserRepository.deleteById(testUserId);

            // Then
            verify(userJpaRepository).deleteById(testUserId.toString());
        }

        @Test
        @DisplayName("Should handle deletion of non-existent user")
        void shouldHandleDeletionOfNonExistentUser() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doNothing().when(userJpaRepository).deleteById(nonExistentId.toString());

            // When
            jpaUserRepository.deleteById(nonExistentId);

            // Then
            verify(userJpaRepository).deleteById(nonExistentId.toString());
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
            Page<User> userPage = new PageImpl<>(users);
            when(userJpaRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

            // When
            List<User> result = jpaUserRepository.findAll(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).contains(testUser);
            verify(userJpaRepository).findAll(PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should return empty list when no users found")
        void shouldReturnEmptyListWhenNoUsersFound() {
            // Given
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
            when(userJpaRepository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);

            // When
            List<User> result = jpaUserRepository.findAll(0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findAll(PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should find users by role with pagination")
        void shouldFindUsersByRoleWithPagination() {
            // Given
            List<User> adminUsers = Arrays.asList(
                    createTestUser("Admin1", "User1", Role.ADMIN),
                    createTestUser("Admin2", "User2", Role.ADMIN)
            );
            when(userJpaRepository.findByRoleValue(Role.ADMIN.value())).thenReturn(adminUsers);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.ADMIN, 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.ADMIN));
            verify(userJpaRepository).findByRoleValue(Role.ADMIN.value());
        }

        @Test
        @DisplayName("Should find users by different roles")
        void shouldFindUsersByDifferentRoles() {
            // Given
            List<User> customerUsers = Arrays.asList(
                    createTestUser("Customer1", "User1", Role.CUSTOMER),
                    createTestUser("Customer2", "User2", Role.CUSTOMER)
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(customerUsers);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getRole().equals(Role.CUSTOMER));
            verify(userJpaRepository).findByRoleValue(Role.CUSTOMER.value());
        }

        @Test
        @DisplayName("Should find active users with pagination")
        void shouldFindActiveUsersWithPagination() {
            // Given
            List<User> activeUsers = Arrays.asList(
                    createTestUser("Active1", "User1", true),
                    createTestUser("Active2", "User2", true)
            );
            when(userJpaRepository.findActiveUsers()).thenReturn(activeUsers);

            // When
            List<User> result = jpaUserRepository.findActiveUsers(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(User::isActive);
            verify(userJpaRepository).findActiveUsers();
        }

        @Test
        @DisplayName("Should find inactive users with pagination")
        void shouldFindInactiveUsersWithPagination() {
            // Given
            List<User> inactiveUsers = Arrays.asList(
                    createTestUser("Inactive1", "User1", false),
                    createTestUser("Inactive2", "User2", false)
            );
            when(userJpaRepository.findInactiveUsers()).thenReturn(inactiveUsers);

            // When
            List<User> result = jpaUserRepository.findInactiveUsers(0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> !user.isActive());
            verify(userJpaRepository).findInactiveUsers();
        }

        @Test
        @DisplayName("Should search users by name with pagination")
        void shouldSearchUsersByNameWithPagination() {
            // Given
            List<User> searchResults = Arrays.asList(
                    createTestUser("John", "Doe"),
                    createTestUser("Johnny", "Smith")
            );
            when(userJpaRepository.searchByName("John")).thenReturn(searchResults);

            // When
            List<User> result = jpaUserRepository.searchByName("John", 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> 
                    user.getFirstName().contains("John") || user.getLastName().contains("John"));
            verify(userJpaRepository).searchByName("John");
        }

        @Test
        @DisplayName("Should handle empty search results")
        void shouldHandleEmptySearchResults() {
            // Given
            when(userJpaRepository.searchByName("NonExistent")).thenReturn(Collections.emptyList());

            // When
            List<User> result = jpaUserRepository.searchByName("NonExistent", 0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).searchByName("NonExistent");
        }

        @Test
        @DisplayName("Should handle different pagination parameters")
        void shouldHandleDifferentPaginationParameters() {
            // Given
            List<User> users = Arrays.asList(testUser);
            Page<User> userPage = new PageImpl<>(users);
            when(userJpaRepository.findAll(PageRequest.of(1, 5))).thenReturn(userPage);

            // When
            List<User> result = jpaUserRepository.findAll(1, 5);

            // Then
            assertThat(result).hasSize(1);
            verify(userJpaRepository).findAll(PageRequest.of(1, 5));
        }
    }

    @Nested
    @DisplayName("Count Operations Tests")
    class CountOperationsTests {

        @Test
        @DisplayName("Should count total users")
        void shouldCountTotalUsers() {
            // Given
            when(userJpaRepository.count()).thenReturn(100L);

            // When
            long result = jpaUserRepository.count();

            // Then
            assertThat(result).isEqualTo(100L);
            verify(userJpaRepository).count();
        }

        @Test
        @DisplayName("Should count users by role")
        void shouldCountUsersByRole() {
            // Given
            when(userJpaRepository.countByRoleValue(Role.ADMIN.value())).thenReturn(5L);
            when(userJpaRepository.countByRoleValue(Role.CUSTOMER.value())).thenReturn(95L);

            // When
            long adminCount = jpaUserRepository.countByRole(Role.ADMIN);
            long customerCount = jpaUserRepository.countByRole(Role.CUSTOMER);

            // Then
            assertThat(adminCount).isEqualTo(5L);
            assertThat(customerCount).isEqualTo(95L);
            verify(userJpaRepository).countByRoleValue(Role.ADMIN.value());
            verify(userJpaRepository).countByRoleValue(Role.CUSTOMER.value());
        }

        @Test
        @DisplayName("Should count active users")
        void shouldCountActiveUsers() {
            // Given
            when(userJpaRepository.countActiveUsers()).thenReturn(80L);

            // When
            long result = jpaUserRepository.countActiveUsers();

            // Then
            assertThat(result).isEqualTo(80L);
            verify(userJpaRepository).countActiveUsers();
        }

        @Test
        @DisplayName("Should count inactive users")
        void shouldCountInactiveUsers() {
            // Given
            when(userJpaRepository.countInactiveUsers()).thenReturn(20L);

            // When
            long result = jpaUserRepository.countInactiveUsers();

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
            long totalCount = jpaUserRepository.count();
            long adminCount = jpaUserRepository.countByRole(Role.ADMIN);
            long activeCount = jpaUserRepository.countActiveUsers();
            long inactiveCount = jpaUserRepository.countInactiveUsers();

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
            when(userJpaRepository.existsById(testUserId.toString())).thenReturn(true);
            when(userJpaRepository.existsById(nonExistentId.toString())).thenReturn(false);

            // When
            boolean exists = jpaUserRepository.existsById(testUserId);
            boolean notExists = jpaUserRepository.existsById(nonExistentId);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userJpaRepository).existsById(testUserId.toString());
            verify(userJpaRepository).existsById(nonExistentId.toString());
        }

        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            // Given
            Email existingEmail = new Email("existing@example.com");
            Email nonExistingEmail = new Email("nonexistent@example.com");
            
            when(userJpaRepository.existsByEmailValue(existingEmail.value())).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(nonExistingEmail.value())).thenReturn(false);

            // When
            boolean exists = jpaUserRepository.existsByEmail(existingEmail);
            boolean notExists = jpaUserRepository.existsByEmail(nonExistingEmail);

            // Then
            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
            verify(userJpaRepository).existsByEmailValue(existingEmail.value());
            verify(userJpaRepository).existsByEmailValue(nonExistingEmail.value());
        }

        @Test
        @DisplayName("Should handle existence checks with different email formats")
        void shouldHandleExistenceChecksWithDifferentEmailFormats() {
            // Given
            Email email1 = new Email("user@domain.com");
            Email email2 = new Email("user.name@domain.com");
            Email email3 = new Email("user+tag@domain.com");
            
            when(userJpaRepository.existsByEmailValue(email1.value())).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(email2.value())).thenReturn(false);
            when(userJpaRepository.existsByEmailValue(email3.value())).thenReturn(true);

            // When
            boolean exists1 = jpaUserRepository.existsByEmail(email1);
            boolean exists2 = jpaUserRepository.existsByEmail(email2);
            boolean exists3 = jpaUserRepository.existsByEmail(email3);

            // Then
            assertThat(exists1).isTrue();
            assertThat(exists2).isFalse();
            assertThat(exists3).isTrue();
        }
    }

    @Nested
    @DisplayName("Pagination Helper Method Tests")
    class PaginationHelperMethodTests {

        @Test
        @DisplayName("Should paginate list correctly for first page")
        void shouldPaginateListCorrectlyForFirstPage() {
            // Given
            List<User> users = Arrays.asList(
                    createTestUser("User1", "Test"),
                    createTestUser("User2", "Test"),
                    createTestUser("User3", "Test"),
                    createTestUser("User4", "Test"),
                    createTestUser("User5", "Test")
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 3);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getFirstName()).isEqualTo("User1");
            assertThat(result.get(1).getFirstName()).isEqualTo("User2");
            assertThat(result.get(2).getFirstName()).isEqualTo("User3");
        }

        @Test
        @DisplayName("Should paginate list correctly for second page")
        void shouldPaginateListCorrectlyForSecondPage() {
            // Given
            List<User> users = Arrays.asList(
                    createTestUser("User1", "Test"),
                    createTestUser("User2", "Test"),
                    createTestUser("User3", "Test"),
                    createTestUser("User4", "Test"),
                    createTestUser("User5", "Test")
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 1, 3);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("User4");
            assertThat(result.get(1).getFirstName()).isEqualTo("User5");
        }

        @Test
        @DisplayName("Should return empty list when page is beyond available data")
        void shouldReturnEmptyListWhenPageIsBeyondAvailableData() {
            // Given
            List<User> users = Arrays.asList(
                    createTestUser("User1", "Test"),
                    createTestUser("User2", "Test")
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 2, 3);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty list pagination")
        void shouldHandleEmptyListPagination() {
            // Given
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(Collections.emptyList());

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 10);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle page size larger than available data")
        void shouldHandlePageSizeLargerThanAvailableData() {
            // Given
            List<User> users = Arrays.asList(
                    createTestUser("User1", "Test"),
                    createTestUser("User2", "Test")
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 10);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("User1");
            assertThat(result.get(1).getFirstName()).isEqualTo("User2");
        }

        @Test
        @DisplayName("Should handle zero page size")
        void shouldHandleZeroPageSize() {
            // Given
            List<User> users = Arrays.asList(
                    createTestUser("User1", "Test"),
                    createTestUser("User2", "Test")
            );
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 0);

            // Then
            assertThat(result).isEmpty();
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
            assertThatThrownBy(() -> jpaUserRepository.save(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User cannot be null");
        }

        @Test
        @DisplayName("Should handle null ID in findById")
        void shouldHandleNullIdInFindById() {
            // When & Then
            assertThatThrownBy(() -> jpaUserRepository.findById(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle null email in findByEmail")
        void shouldHandleNullEmailInFindByEmail() {
            // When & Then
            assertThatThrownBy(() -> jpaUserRepository.findByEmail(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle negative pagination parameters")
        void shouldHandleNegativePaginationParameters() {
            // Given
            List<User> users = Arrays.asList(testUser);
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When & Then
            assertThatThrownBy(() -> jpaUserRepository.findByRole(Role.CUSTOMER, -1, 10))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("Should handle very large pagination parameters")
        void shouldHandleVeryLargePaginationParameters() {
            // Given
            List<User> users = Arrays.asList(testUser);
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(users);

            // When
            List<User> result = jpaUserRepository.findByRole(Role.CUSTOMER, 1000, 10000);

            // Then
            assertThat(result).isEmpty(); // Should return empty for page beyond data
            verify(userJpaRepository).findByRoleValue(Role.CUSTOMER.value());
        }

        @Test
        @DisplayName("Should handle null name in searchByName")
        void shouldHandleNullNameInSearchByName() {
            // Given
            when(userJpaRepository.searchByName(null)).thenThrow(new IllegalArgumentException("Name cannot be null"));

            // When & Then
            assertThatThrownBy(() -> jpaUserRepository.searchByName(null, 0, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name cannot be null");
        }

        @Test
        @DisplayName("Should handle empty name in searchByName")
        void shouldHandleEmptyNameInSearchByName() {
            // Given
            when(userJpaRepository.searchByName("")).thenReturn(Collections.emptyList());

            // When
            List<User> result = jpaUserRepository.searchByName("", 0, 10);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).searchByName("");
        }
    }

    @Nested
    @DisplayName("Value Object Integration Tests")
    class ValueObjectIntegrationTests {

        @Test
        @DisplayName("Should work with all Email value objects")
        void shouldWorkWithAllEmailValueObjects() {
            // Given
            Email email1 = new Email("user1@domain.com");
            Email email2 = new Email("user2@domain.com");
            
            User user1 = createTestUser("User1", "Test", email1);
            User user2 = createTestUser("User2", "Test", email2);

            when(userJpaRepository.findByEmailValue(email1.value())).thenReturn(Optional.of(user1));
            when(userJpaRepository.findByEmailValue(email2.value())).thenReturn(Optional.of(user2));

            // When
            Optional<User> foundUser1 = jpaUserRepository.findByEmail(email1);
            Optional<User> foundUser2 = jpaUserRepository.findByEmail(email2);

            // Then
            assertThat(foundUser1).isPresent();
            assertThat(foundUser2).isPresent();
            assertThat(foundUser1.get().getEmail()).isEqualTo(email1);
            assertThat(foundUser2.get().getEmail()).isEqualTo(email2);
        }

        @Test
        @DisplayName("Should work with all Role value objects")
        void shouldWorkWithAllRoleValueObjects() {
            // Given
            List<User> adminUsers = Arrays.asList(createTestUser("Admin", "User", Role.ADMIN));
            List<User> customerUsers = Arrays.asList(createTestUser("Customer", "User", Role.CUSTOMER));
            List<User> managerUsers = Arrays.asList(createTestUser("Manager", "User", Role.MANAGER));

            when(userJpaRepository.findByRoleValue(Role.ADMIN.value())).thenReturn(adminUsers);
            when(userJpaRepository.findByRoleValue(Role.CUSTOMER.value())).thenReturn(customerUsers);
            when(userJpaRepository.findByRoleValue(Role.MANAGER.value())).thenReturn(managerUsers);

            // When
            List<User> adminResult = jpaUserRepository.findByRole(Role.ADMIN, 0, 10);
            List<User> customerResult = jpaUserRepository.findByRole(Role.CUSTOMER, 0, 10);
            List<User> managerResult = jpaUserRepository.findByRole(Role.MANAGER, 0, 10);

            // Then
            assertThat(adminResult).hasSize(1);
            assertThat(customerResult).hasSize(1);
            assertThat(managerResult).hasSize(1);
            assertThat(adminResult.get(0).getRole()).isEqualTo(Role.ADMIN);
            assertThat(customerResult.get(0).getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(managerResult.get(0).getRole()).isEqualTo(Role.MANAGER);
        }

        @Test
        @DisplayName("Should convert UUID to string for JPA operations")
        void shouldConvertUuidToStringForJpaOperations() {
            // Given
            UUID testId = UUID.randomUUID();
            when(userJpaRepository.findById(testId.toString())).thenReturn(Optional.of(testUser));
            when(userJpaRepository.existsById(testId.toString())).thenReturn(true);

            // When
            Optional<User> foundUser = jpaUserRepository.findById(testId);
            boolean exists = jpaUserRepository.existsById(testId);

            // Then
            assertThat(foundUser).isPresent();
            assertThat(exists).isTrue();
            verify(userJpaRepository).findById(testId.toString());
            verify(userJpaRepository).existsById(testId.toString());
        }
    }

    @Nested
    @DisplayName("Adapter Pattern Verification Tests")
    class AdapterPatternVerificationTests {

        @Test
        @DisplayName("Should correctly translate domain operations to JPA operations")
        void shouldCorrectlyTranslateDomainOperationsToJpaOperations() {
            // Given
            when(userJpaRepository.save(testUser)).thenReturn(testUser);
            when(userJpaRepository.findById(testUserId.toString())).thenReturn(Optional.of(testUser));
            when(userJpaRepository.findByEmailValue(testEmail.value())).thenReturn(Optional.of(testUser));
            when(userJpaRepository.count()).thenReturn(1L);
            when(userJpaRepository.existsById(testUserId.toString())).thenReturn(true);
            when(userJpaRepository.existsByEmailValue(testEmail.value())).thenReturn(true);

            // When
            User savedUser = jpaUserRepository.save(testUser);
            Optional<User> foundById = jpaUserRepository.findById(testUserId);
            Optional<User> foundByEmail = jpaUserRepository.findByEmail(testEmail);
            long count = jpaUserRepository.count();
            boolean existsById = jpaUserRepository.existsById(testUserId);
            boolean existsByEmail = jpaUserRepository.existsByEmail(testEmail);

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(foundById).isPresent();
            assertThat(foundByEmail).isPresent();
            assertThat(count).isEqualTo(1L);
            assertThat(existsById).isTrue();
            assertThat(existsByEmail).isTrue();

            // Verify all JPA methods were called
            verify(userJpaRepository).save(testUser);
            verify(userJpaRepository).findById(testUserId.toString());
            verify(userJpaRepository).findByEmailValue(testEmail.value());
            verify(userJpaRepository).count();
            verify(userJpaRepository).existsById(testUserId.toString());
            verify(userJpaRepository).existsByEmailValue(testEmail.value());
        }

        @Test
        @DisplayName("Should handle JPA exceptions gracefully")
        void shouldHandleJpaExceptionsGracefully() {
            // Given
            when(userJpaRepository.save(testUser)).thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            assertThatThrownBy(() -> jpaUserRepository.save(testUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database connection failed");
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
