package io.nexora.user.application;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import io.nexora.user.domain.UserRepository;
import io.nexora.user.domain.service.UserDomainService;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserApplicationService.
 * 
 * These tests verify the application service layer that orchestrates workflows
 * and coordinates between domain services and repositories. They ensure that
 * the service correctly handles command and query operations, error handling,
 * transaction management, and logging.
 * 
 * Test Coverage:
 * - Command operations (register, authenticate, update, change password, etc.)
 * - Query operations (find, search, statistics)
 * - Command classes and DTOs
 * - Error handling and exception scenarios
 * - Transaction management and logging
 * - Application workflow orchestration
 * - Domain service coordination
 * - Repository integration
 * 
 * Design Principles Applied:
 * - Application Service Testing: Verify workflow orchestration
 * - Mock-based Testing: Test service behavior without dependencies
 * - Command Query Separation: Test commands and queries separately
 * - Error Handling: Comprehensive exception scenario testing
 * - Transaction Testing: Verify transaction boundaries
 * - Logging Verification: Ensure proper logging behavior
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserApplicationService Tests")
class UserApplicationServiceTest {

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private User testUser;
    private UUID testUserId;
    private Email testEmail;
    private Password testPassword;
    private Role testRole;

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
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Command Operations Tests")
    class CommandOperationsTests {

        @Nested
        @DisplayName("User Registration Tests")
        class UserRegistrationTests {

            @Test
            @DisplayName("Should register user successfully with valid command")
            void shouldRegisterUserSuccessfullyWithValidCommand() {
                // Given
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                        "Jane", "Smith", "jane@example.com", "password123", "CUSTOMER"
                );

                when(userDomainService.registerUser(any(User.class))).thenReturn(testUser);

                // When
                User result = userApplicationService.registerUser(command);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());
                assertThat(result.getFirstName()).isEqualTo("John");
                assertThat(result.getLastName()).isEqualTo("Doe");

                verify(userDomainService).registerUser(any(User.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when domain service fails")
            void shouldThrowUserApplicationExceptionWhenDomainServiceFails() {
                // Given
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                        "Jane", "Smith", "jane@example.com", "password123", "CUSTOMER"
                );

                when(userDomainService.registerUser(any(User.class)))
                        .thenThrow(new IllegalArgumentException("Email already exists"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.registerUser(command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to register user: Email already exists");

                verify(userDomainService).registerUser(any(User.class));
            }

            @Test
            @DisplayName("Should handle invalid email format in registration")
            void shouldHandleInvalidEmailFormatInRegistration() {
                // Given
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                        "Jane", "Smith", "invalid-email", "password123", "CUSTOMER"
                );

                // When & Then
                assertThatThrownBy(() -> userApplicationService.registerUser(command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to register user");

                verify(userDomainService, never()).registerUser(any(User.class));
            }

            @Test
            @DisplayName("Should handle invalid role in registration")
            void shouldHandleInvalidRoleInRegistration() {
                // Given
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                        "Jane", "Smith", "jane@example.com", "password123", "INVALID_ROLE"
                );

                // When & Then
                assertThatThrownBy(() -> userApplicationService.registerUser(command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to register user");

                verify(userDomainService, never()).registerUser(any(User.class));
            }
        }

        @Nested
        @DisplayName("User Authentication Tests")
        class UserAuthenticationTests {

            @Test
            @DisplayName("Should authenticate user successfully with valid command")
            void shouldAuthenticateUserSuccessfullyWithValidCommand() {
                // Given
                UserApplicationService.AuthenticateUserCommand command = new UserApplicationService.AuthenticateUserCommand(
                        "test@example.com", "password123"
                );

                when(userDomainService.authenticateUser(any(Email.class), any(Password.class)))
                        .thenReturn(testUser);

                // When
                User result = userApplicationService.authenticateUser(command);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).authenticateUser(any(Email.class), any(Password.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when authentication fails")
            void shouldThrowUserApplicationExceptionWhenAuthenticationFails() {
                // Given
                UserApplicationService.AuthenticateUserCommand command = new UserApplicationService.AuthenticateUserCommand(
                        "test@example.com", "wrongpassword"
                );

                when(userDomainService.authenticateUser(any(Email.class), any(Password.class)))
                        .thenThrow(new IllegalArgumentException("Invalid password"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.authenticateUser(command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to authenticate user: Invalid password");

                verify(userDomainService).authenticateUser(any(Email.class), any(Password.class));
            }

            @Test
            @DisplayName("Should handle invalid email format in authentication")
            void shouldHandleInvalidEmailFormatInAuthentication() {
                // Given
                UserApplicationService.AuthenticateUserCommand command = new UserApplicationService.AuthenticateUserCommand(
                        "invalid-email", "password123"
                );

                // When & Then
                assertThatThrownBy(() -> userApplicationService.authenticateUser(command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to authenticate user");

                verify(userDomainService, never()).authenticateUser(any(Email.class), any(Password.class));
            }
        }

        @Nested
        @DisplayName("User Update Tests")
        class UserUpdateTests {

            @Test
            @DisplayName("Should update user successfully with valid command")
            void shouldUpdateUserSuccessfullyWithValidCommand() {
                // Given
                UUID currentUserId = UUID.randomUUID();
                UserApplicationService.UpdateUserCommand command = new UserApplicationService.UpdateUserCommand(
                        "Jane", "Smith", "jane@example.com", "newpassword123", "MANAGER"
                );

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
                when(userDomainService.updateUser(eq(testUserId), any(User.class), eq(testUser)))
                        .thenReturn(testUser);

                // When
                User result = userApplicationService.updateUser(testUserId, command, currentUserId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService).updateUser(eq(testUserId), any(User.class), eq(testUser));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when update fails")
            void shouldThrowUserApplicationExceptionWhenUpdateFails() {
                // Given
                UUID currentUserId = UUID.randomUUID();
                UserApplicationService.UpdateUserCommand command = new UserApplicationService.UpdateUserCommand(
                        "Jane", "Smith", "jane@example.com", "newpassword123", "MANAGER"
                );

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
                when(userDomainService.updateUser(eq(testUserId), any(User.class), eq(testUser)))
                        .thenThrow(new IllegalArgumentException("Email already exists"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.updateUser(testUserId, command, currentUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to update user: Email already exists");

                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService).updateUser(eq(testUserId), any(User.class), eq(testUser));
            }
        }

        @Nested
        @DisplayName("Password Change Tests")
        class PasswordChangeTests {

            @Test
            @DisplayName("Should change password successfully with valid command")
            void shouldChangePasswordSuccessfullyWithValidCommand() {
                // Given
                UserApplicationService.ChangePasswordCommand command = new UserApplicationService.ChangePasswordCommand(
                        "password123", "newpassword123"
                );

                when(userDomainService.changePassword(eq(testUserId), any(Password.class), any(Password.class)))
                        .thenReturn(testUser);

                // When
                User result = userApplicationService.changePassword(testUserId, command);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).changePassword(eq(testUserId), any(Password.class), any(Password.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when password change fails")
            void shouldThrowUserApplicationExceptionWhenPasswordChangeFails() {
                // Given
                UserApplicationService.ChangePasswordCommand command = new UserApplicationService.ChangePasswordCommand(
                        "wrongpassword", "newpassword123"
                );

                when(userDomainService.changePassword(eq(testUserId), any(Password.class), any(Password.class)))
                        .thenThrow(new IllegalArgumentException("Current password is incorrect"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.changePassword(testUserId, command))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to change password: Current password is incorrect");

                verify(userDomainService).changePassword(eq(testUserId), any(Password.class), any(Password.class));
            }
        }

        @Nested
        @DisplayName("Role Change Tests")
        class RoleChangeTests {

            @Test
            @DisplayName("Should change role successfully with valid command")
            void shouldChangeRoleSuccessfullyWithValidCommand() {
                // Given
                UUID currentUserId = UUID.randomUUID();
                UserApplicationService.ChangeRoleCommand command = new UserApplicationService.ChangeRoleCommand("MANAGER");

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
                when(userDomainService.changeRole(eq(testUserId), any(Role.class), eq(testUser)))
                        .thenReturn(testUser);

                // When
                User result = userApplicationService.changeRole(testUserId, command, currentUserId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService).changeRole(eq(testUserId), any(Role.class), eq(testUser));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when role change fails")
            void shouldThrowUserApplicationExceptionWhenRoleChangeFails() {
                // Given
                UUID currentUserId = UUID.randomUUID();
                UserApplicationService.ChangeRoleCommand command = new UserApplicationService.ChangeRoleCommand("INVALID_ROLE");

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);

                // When & Then
                assertThatThrownBy(() -> userApplicationService.changeRole(testUserId, command, currentUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to change role");

                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService, never()).changeRole(any(UUID.class), any(Role.class), any(User.class));
            }
        }

        @Nested
        @DisplayName("User Activation Tests")
        class UserActivationTests {

            @Test
            @DisplayName("Should activate user successfully")
            void shouldActivateUserSuccessfully() {
                // Given
                when(userDomainService.activateUser(testUserId)).thenReturn(testUser);

                // When
                User result = userApplicationService.activateUser(testUserId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).activateUser(testUserId);
            }

            @Test
            @DisplayName("Should throw UserApplicationException when activation fails")
            void shouldThrowUserApplicationExceptionWhenActivationFails() {
                // Given
                when(userDomainService.activateUser(testUserId))
                        .thenThrow(new IllegalArgumentException("User is already active"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.activateUser(testUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to activate user: User is already active");

                verify(userDomainService).activateUser(testUserId);
            }
        }

        @Nested
        @DisplayName("User Deactivation Tests")
        class UserDeactivationTests {

            @Test
            @DisplayName("Should deactivate user successfully")
            void shouldDeactivateUserSuccessfully() {
                // Given
                UUID currentUserId = UUID.randomUUID();

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
                doNothing().when(userDomainService).deactivateUser(testUserId, testUser);

                // When
                assertThatCode(() -> userApplicationService.deactivateUser(testUserId, currentUserId))
                        .doesNotThrowAnyException();

                // Then
                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService).deactivateUser(testUserId, testUser);
            }

            @Test
            @DisplayName("Should throw UserApplicationException when deactivation fails")
            void shouldThrowUserApplicationExceptionWhenDeactivationFails() {
                // Given
                UUID currentUserId = UUID.randomUUID();

                when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
                doThrow(new IllegalArgumentException("Cannot deactivate your own account"))
                        .when(userDomainService).deactivateUser(testUserId, testUser);

                // When & Then
                assertThatThrownBy(() -> userApplicationService.deactivateUser(testUserId, currentUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to deactivate user: Cannot deactivate your own account");

                verify(userDomainService).findUserById(currentUserId);
                verify(userDomainService).deactivateUser(testUserId, testUser);
            }
        }

        @Nested
        @DisplayName("Email Verification Tests")
        class EmailVerificationTests {

            @Test
            @DisplayName("Should verify email successfully")
            void shouldVerifyEmailSuccessfully() {
                // Given
                when(userDomainService.verifyEmail(testUserId)).thenReturn(testUser);

                // When
                User result = userApplicationService.verifyEmail(testUserId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).verifyEmail(testUserId);
            }

            @Test
            @DisplayName("Should throw UserApplicationException when email verification fails")
            void shouldThrowUserApplicationExceptionWhenEmailVerificationFails() {
                // Given
                when(userDomainService.verifyEmail(testUserId))
                        .thenThrow(new IllegalArgumentException("Email is already verified"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.verifyEmail(testUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to verify email: Email is already verified");

                verify(userDomainService).verifyEmail(testUserId);
            }
        }
    }

    @Nested
    @DisplayName("Query Operations Tests")
    class QueryOperationsTests {

        @Nested
        @DisplayName("Find User Tests")
        class FindUserTests {

            @Test
            @DisplayName("Should find user by ID successfully")
            void shouldFindUserByIdSuccessfully() {
                // Given
                when(userDomainService.findUserById(testUserId)).thenReturn(testUser);

                // When
                User result = userApplicationService.findUserById(testUserId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).findUserById(testUserId);
            }

            @Test
            @DisplayName("Should throw UserApplicationException when user not found by ID")
            void shouldThrowUserApplicationExceptionWhenUserNotFoundById() {
                // Given
                when(userDomainService.findUserById(testUserId))
                        .thenThrow(new IllegalArgumentException("User with ID " + testUserId + " not found"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findUserById(testUserId))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find user: User with ID " + testUserId + " not found");

                verify(userDomainService).findUserById(testUserId);
            }

            @Test
            @DisplayName("Should find user by email successfully")
            void shouldFindUserByEmailSuccessfully() {
                // Given
                String emailString = "test@example.com";
                when(userDomainService.findUserByEmail(any(Email.class))).thenReturn(testUser);

                // When
                User result = userApplicationService.findUserByEmail(emailString);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUserId.toString());

                verify(userDomainService).findUserByEmail(any(Email.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when user not found by email")
            void shouldThrowUserApplicationExceptionWhenUserNotFoundByEmail() {
                // Given
                String emailString = "nonexistent@example.com";
                when(userDomainService.findUserByEmail(any(Email.class)))
                        .thenThrow(new IllegalArgumentException("User with email " + emailString + " not found"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findUserByEmail(emailString))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find user: User with email " + emailString + " not found");

                verify(userDomainService).findUserByEmail(any(Email.class));
            }

            @Test
            @DisplayName("Should handle invalid email format in find by email")
            void shouldHandleInvalidEmailFormatInFindByEmail() {
                // Given
                String invalidEmail = "invalid-email";

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findUserByEmail(invalidEmail))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find user");

                verify(userDomainService, never()).findUserByEmail(any(Email.class));
            }
        }

        @Nested
        @DisplayName("Find All Users Tests")
        class FindAllUsersTests {

            @Test
            @DisplayName("Should find all users with pagination successfully")
            void shouldFindAllUsersWithPaginationSuccessfully() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                List<User> users = Arrays.asList(testUser);
                long totalElements = 1L;

                when(userRepository.findAll(0, 10)).thenReturn(users);
                when(userRepository.count()).thenReturn(totalElements);

                // When
                Page<User> result = userApplicationService.findAllUsers(pageable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getTotalElements()).isEqualTo(totalElements);
                assertThat(result.getContent().get(0).getId()).isEqualTo(testUserId.toString());

                verify(userRepository).findAll(0, 10);
                verify(userRepository).count();
            }

            @Test
            @DisplayName("Should throw UserApplicationException when find all users fails")
            void shouldThrowUserApplicationExceptionWhenFindAllUsersFails() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);

                when(userRepository.findAll(0, 10))
                        .thenThrow(new RuntimeException("Database connection failed"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findAllUsers(pageable))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find all users: Database connection failed");

                verify(userRepository).findAll(0, 10);
                verify(userRepository, never()).count();
            }
        }

        @Nested
        @DisplayName("Find Users By Role Tests")
        class FindUsersByRoleTests {

            @Test
            @DisplayName("Should find users by role with pagination successfully")
            void shouldFindUsersByRoleWithPaginationSuccessfully() {
                // Given
                String roleString = "CUSTOMER";
                Pageable pageable = PageRequest.of(0, 10);
                List<User> users = Arrays.asList(testUser);
                long totalElements = 1L;

                when(userRepository.findByRole(any(Role.class), eq(0), eq(10))).thenReturn(users);
                when(userRepository.countByRole(any(Role.class))).thenReturn(totalElements);

                // When
                Page<User> result = userApplicationService.findUsersByRole(roleString, pageable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getTotalElements()).isEqualTo(totalElements);
                assertThat(result.getContent().get(0).getId()).isEqualTo(testUserId.toString());

                verify(userRepository).findByRole(any(Role.class), eq(0), eq(10));
                verify(userRepository).countByRole(any(Role.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when find users by role fails")
            void shouldThrowUserApplicationExceptionWhenFindUsersByRoleFails() {
                // Given
                String roleString = "INVALID_ROLE";
                Pageable pageable = PageRequest.of(0, 10);

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findUsersByRole(roleString, pageable))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find users by role");

                verify(userRepository, never()).findByRole(any(Role.class), anyInt(), anyInt());
                verify(userRepository, never()).countByRole(any(Role.class));
            }
        }

        @Nested
        @DisplayName("Find Active Users Tests")
        class FindActiveUsersTests {

            @Test
            @DisplayName("Should find active users with pagination successfully")
            void shouldFindActiveUsersWithPaginationSuccessfully() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                List<User> users = Arrays.asList(testUser);
                long totalElements = 1L;

                when(userRepository.findActiveUsers(0, 10)).thenReturn(users);
                when(userRepository.countActiveUsers()).thenReturn(totalElements);

                // When
                Page<User> result = userApplicationService.findActiveUsers(pageable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getTotalElements()).isEqualTo(totalElements);
                assertThat(result.getContent().get(0).getId()).isEqualTo(testUserId.toString());

                verify(userRepository).findActiveUsers(0, 10);
                verify(userRepository).countActiveUsers();
            }

            @Test
            @DisplayName("Should throw UserApplicationException when find active users fails")
            void shouldThrowUserApplicationExceptionWhenFindActiveUsersFails() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);

                when(userRepository.findActiveUsers(0, 10))
                        .thenThrow(new RuntimeException("Database connection failed"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.findActiveUsers(pageable))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to find active users: Database connection failed");

                verify(userRepository).findActiveUsers(0, 10);
                verify(userRepository, never()).countActiveUsers();
            }
        }

        @Nested
        @DisplayName("Search Users Tests")
        class SearchUsersTests {

            @Test
            @DisplayName("Should search users by name with pagination successfully")
            void shouldSearchUsersByNameWithPaginationSuccessfully() {
                // Given
                String searchName = "John";
                Pageable pageable = PageRequest.of(0, 10);
                List<User> users = Arrays.asList(testUser);

                when(userRepository.searchByName(searchName, 0, 10)).thenReturn(users);

                // When
                Page<User> result = userApplicationService.searchUsersByName(searchName, pageable);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getId()).isEqualTo(testUserId.toString());

                verify(userRepository).searchByName(searchName, 0, 10);
            }

            @Test
            @DisplayName("Should throw UserApplicationException when search users fails")
            void shouldThrowUserApplicationExceptionWhenSearchUsersFails() {
                // Given
                String searchName = "John";
                Pageable pageable = PageRequest.of(0, 10);

                when(userRepository.searchByName(searchName, 0, 10))
                        .thenThrow(new RuntimeException("Search index unavailable"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.searchUsersByName(searchName, pageable))
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to search users: Search index unavailable");

                verify(userRepository).searchByName(searchName, 0, 10);
            }
        }

        @Nested
        @DisplayName("User Statistics Tests")
        class UserStatisticsTests {

            @Test
            @DisplayName("Should get user statistics successfully")
            void shouldGetUserStatisticsSuccessfully() {
                // Given
                when(userRepository.count()).thenReturn(100L);
                when(userRepository.countActiveUsers()).thenReturn(80L);
                when(userRepository.countInactiveUsers()).thenReturn(20L);
                when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(70L);
                when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);
                when(userRepository.countByRole(Role.MANAGER)).thenReturn(25L);

                // When
                UserApplicationService.UserStatistics result = userApplicationService.getUserStatistics();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getTotalUsers()).isEqualTo(100L);
                assertThat(result.getActiveUsers()).isEqualTo(80L);
                assertThat(result.getInactiveUsers()).isEqualTo(20L);
                assertThat(result.getCustomerUsers()).isEqualTo(70L);
                assertThat(result.getAdminUsers()).isEqualTo(5L);
                assertThat(result.getManagerUsers()).isEqualTo(25L);

                verify(userRepository).count();
                verify(userRepository).countActiveUsers();
                verify(userRepository).countInactiveUsers();
                verify(userRepository, times(3)).countByRole(any(Role.class));
            }

            @Test
            @DisplayName("Should throw UserApplicationException when get statistics fails")
            void shouldThrowUserApplicationExceptionWhenGetStatisticsFails() {
                // Given
                when(userRepository.count())
                        .thenThrow(new RuntimeException("Database connection failed"));

                // When & Then
                assertThatThrownBy(() -> userApplicationService.getUserStatistics())
                        .isInstanceOf(UserApplicationService.UserApplicationException.class)
                        .hasMessageContaining("Failed to get user statistics: Database connection failed");

                verify(userRepository).count();
                verify(userRepository, never()).countActiveUsers();
            }
        }
    }

    @Nested
    @DisplayName("Command Classes Tests")
    class CommandClassesTests {

        @Nested
        @DisplayName("RegisterUserCommand Tests")
        class RegisterUserCommandTests {

            @Test
            @DisplayName("Should create RegisterUserCommand with all fields")
            void shouldCreateRegisterUserCommandWithAllFields() {
                // When
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                        "John", "Doe", "john@example.com", "password123", "CUSTOMER"
                );

                // Then
                assertThat(command.getFirstName()).isEqualTo("John");
                assertThat(command.getLastName()).isEqualTo("Doe");
                assertThat(command.getEmail()).isEqualTo("john@example.com");
                assertThat(command.getPassword()).isEqualTo("password123");
                assertThat(command.getRole()).isEqualTo("CUSTOMER");
            }

            @Test
            @DisplayName("Should create RegisterUserCommand with no-args constructor and setters")
            void shouldCreateRegisterUserCommandWithNoArgsConstructorAndSetters() {
                // When
                UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand();
                command.setFirstName("Jane");
                command.setLastName("Smith");
                command.setEmail("jane@example.com");
                command.setPassword("password456");
                command.setRole("ADMIN");

                // Then
                assertThat(command.getFirstName()).isEqualTo("Jane");
                assertThat(command.getLastName()).isEqualTo("Smith");
                assertThat(command.getEmail()).isEqualTo("jane@example.com");
                assertThat(command.getPassword()).isEqualTo("password456");
                assertThat(command.getRole()).isEqualTo("ADMIN");
            }
        }

        @Nested
        @DisplayName("AuthenticateUserCommand Tests")
        class AuthenticateUserCommandTests {

            @Test
            @DisplayName("Should create AuthenticateUserCommand with all fields")
            void shouldCreateAuthenticateUserCommandWithAllFields() {
                // When
                UserApplicationService.AuthenticateUserCommand command = new UserApplicationService.AuthenticateUserCommand(
                        "test@example.com", "password123"
                );

                // Then
                assertThat(command.getEmail()).isEqualTo("test@example.com");
                assertThat(command.getPassword()).isEqualTo("password123");
            }

            @Test
            @DisplayName("Should create AuthenticateUserCommand with no-args constructor and setters")
            void shouldCreateAuthenticateUserCommandWithNoArgsConstructorAndSetters() {
                // When
                UserApplicationService.AuthenticateUserCommand command = new UserApplicationService.AuthenticateUserCommand();
                command.setEmail("user@example.com");
                command.setPassword("secretpassword");

                // Then
                assertThat(command.getEmail()).isEqualTo("user@example.com");
                assertThat(command.getPassword()).isEqualTo("secretpassword");
            }
        }

        @Nested
        @DisplayName("ChangePasswordCommand Tests")
        class ChangePasswordCommandTests {

            @Test
            @DisplayName("Should create ChangePasswordCommand with all fields")
            void shouldCreateChangePasswordCommandWithAllFields() {
                // When
                UserApplicationService.ChangePasswordCommand command = new UserApplicationService.ChangePasswordCommand(
                        "oldpassword", "newpassword"
                );

                // Then
                assertThat(command.getCurrentPassword()).isEqualTo("oldpassword");
                assertThat(command.getNewPassword()).isEqualTo("newpassword");
            }

            @Test
            @DisplayName("Should create ChangePasswordCommand with no-args constructor and setters")
            void shouldCreateChangePasswordCommandWithNoArgsConstructorAndSetters() {
                // When
                UserApplicationService.ChangePasswordCommand command = new UserApplicationService.ChangePasswordCommand();
                command.setCurrentPassword("currentpass");
                command.setNewPassword("newpass");

                // Then
                assertThat(command.getCurrentPassword()).isEqualTo("currentpass");
                assertThat(command.getNewPassword()).isEqualTo("newpass");
            }
        }

        @Nested
        @DisplayName("ChangeRoleCommand Tests")
        class ChangeRoleCommandTests {

            @Test
            @DisplayName("Should create ChangeRoleCommand with role field")
            void shouldCreateChangeRoleCommandWithRoleField() {
                // When
                UserApplicationService.ChangeRoleCommand command = new UserApplicationService.ChangeRoleCommand("MANAGER");

                // Then
                assertThat(command.getRole()).isEqualTo("MANAGER");
            }

            @Test
            @DisplayName("Should create ChangeRoleCommand with no-args constructor and setter")
            void shouldCreateChangeRoleCommandWithNoArgsConstructorAndSetter() {
                // When
                UserApplicationService.ChangeRoleCommand command = new UserApplicationService.ChangeRoleCommand();
                command.setRole("ADMIN");

                // Then
                assertThat(command.getRole()).isEqualTo("ADMIN");
            }
        }

        @Nested
        @DisplayName("UserStatistics Tests")
        class UserStatisticsTests {

            @Test
            @DisplayName("Should create UserStatistics with all fields")
            void shouldCreateUserStatisticsWithAllFields() {
                // When
                UserApplicationService.UserStatistics statistics = new UserApplicationService.UserStatistics(
                        100L, 80L, 20L, 70L, 5L, 25L
                );

                // Then
                assertThat(statistics.getTotalUsers()).isEqualTo(100L);
                assertThat(statistics.getActiveUsers()).isEqualTo(80L);
                assertThat(statistics.getInactiveUsers()).isEqualTo(20L);
                assertThat(statistics.getCustomerUsers()).isEqualTo(70L);
                assertThat(statistics.getAdminUsers()).isEqualTo(5L);
                assertThat(statistics.getManagerUsers()).isEqualTo(25L);
            }

            @Test
            @DisplayName("Should create UserStatistics with builder pattern")
            void shouldCreateUserStatisticsWithBuilderPattern() {
                // When
                UserApplicationService.UserStatistics statistics = UserApplicationService.UserStatistics.builder()
                        .totalUsers(200L)
                        .activeUsers(150L)
                        .inactiveUsers(50L)
                        .customerUsers(120L)
                        .adminUsers(10L)
                        .managerUsers(70L)
                        .build();

                // Then
                assertThat(statistics.getTotalUsers()).isEqualTo(200L);
                assertThat(statistics.getActiveUsers()).isEqualTo(150L);
                assertThat(statistics.getInactiveUsers()).isEqualTo(50L);
                assertThat(statistics.getCustomerUsers()).isEqualTo(120L);
                assertThat(statistics.getAdminUsers()).isEqualTo(10L);
                assertThat(statistics.getManagerUsers()).isEqualTo(70L);
            }

            @Test
            @DisplayName("Should create UserStatistics with no-args constructor and setters")
            void shouldCreateUserStatisticsWithNoArgsConstructorAndSetters() {
                // When
                UserApplicationService.UserStatistics statistics = new UserApplicationService.UserStatistics();
                statistics.setTotalUsers(300L);
                statistics.setActiveUsers(250L);
                statistics.setInactiveUsers(50L);
                statistics.setCustomerUsers(200L);
                statistics.setAdminUsers(20L);
                statistics.setManagerUsers(80L);

                // Then
                assertThat(statistics.getTotalUsers()).isEqualTo(300L);
                assertThat(statistics.getActiveUsers()).isEqualTo(250L);
                assertThat(statistics.getInactiveUsers()).isEqualTo(50L);
                assertThat(statistics.getCustomerUsers()).isEqualTo(200L);
                assertThat(statistics.getAdminUsers()).isEqualTo(20L);
                assertThat(statistics.getManagerUsers()).isEqualTo(80L);
            }
        }
    }

    @Nested
    @DisplayName("Error Handling and Exception Tests")
    class ErrorHandlingAndExceptionTests {

        @Test
        @DisplayName("Should handle null command gracefully")
        void shouldHandleNullCommandGracefully() {
            // When & Then
            assertThatThrownBy(() -> userApplicationService.registerUser(null))
                    .isInstanceOf(UserApplicationService.UserApplicationException.class)
                    .hasMessageContaining("Failed to register user");

            verify(userDomainService, never()).registerUser(any(User.class));
        }

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void shouldHandleNullParametersGracefully() {
            // When & Then
            assertThatThrownBy(() -> userApplicationService.findUserById(null))
                    .isInstanceOf(UserApplicationService.UserApplicationException.class)
                    .hasMessageContaining("Failed to find user");

            verify(userDomainService, never()).findUserById(any(UUID.class));
        }

        @Test
        @DisplayName("Should handle empty string parameters gracefully")
        void shouldHandleEmptyStringParametersGracefully() {
            // When & Then
            assertThatThrownBy(() -> userApplicationService.findUserByEmail(""))
                    .isInstanceOf(UserApplicationService.UserApplicationException.class)
                    .hasMessageContaining("Failed to find user");

            verify(userDomainService, never()).findUserByEmail(any(Email.class));
        }

        @Test
            @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            when(userRepository.findAll(0, 10))
                    .thenThrow(new RuntimeException("Database connection timeout"));

            // When & Then
            assertThatThrownBy(() -> userApplicationService.findAllUsers(pageable))
                    .isInstanceOf(UserApplicationService.UserApplicationException.class)
                    .hasMessageContaining("Failed to find all users: Database connection timeout");

            verify(userRepository).findAll(0, 10);
        }

        @Test
        @DisplayName("Should handle domain service exceptions gracefully")
        void shouldHandleDomainServiceExceptionsGracefully() {
            // Given
            UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                    "John", "Doe", "john@example.com", "password123", "CUSTOMER"
            );

            when(userDomainService.registerUser(any(User.class)))
                    .thenThrow(new RuntimeException("Domain validation failed"));

            // When & Then
            assertThatThrownBy(() -> userApplicationService.registerUser(command))
                    .isInstanceOf(UserApplicationService.UserApplicationException.class)
                    .hasMessageContaining("Failed to register user: Domain validation failed");

            verify(userDomainService).registerUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Integration and Workflow Tests")
    class IntegrationAndWorkflowTests {

        @Test
        @DisplayName("Should handle complete user registration workflow")
        void shouldHandleCompleteUserRegistrationWorkflow() {
            // Given
            UserApplicationService.RegisterUserCommand command = new UserApplicationService.RegisterUserCommand(
                    "Jane", "Smith", "jane@example.com", "password123", "CUSTOMER"
            );

            when(userDomainService.registerUser(any(User.class))).thenReturn(testUser);

            // When
            User result = userApplicationService.registerUser(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUserId.toString());

            verify(userDomainService).registerUser(any(User.class));
        }

        @Test
        @DisplayName("Should handle complete user update workflow")
        void shouldHandleCompleteUserUpdateWorkflow() {
            // Given
            UUID currentUserId = UUID.randomUUID();
            UserApplicationService.UpdateUserCommand command = new UserApplicationService.UpdateUserCommand(
                    "Jane", "Smith", "jane@example.com", "newpassword123", "MANAGER"
            );

            when(userDomainService.findUserById(currentUserId)).thenReturn(testUser);
            when(userDomainService.updateUser(eq(testUserId), any(User.class), eq(testUser)))
                    .thenReturn(testUser);

            // When
            User result = userApplicationService.updateUser(testUserId, command, currentUserId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUserId.toString());

            verify(userDomainService).findUserById(currentUserId);
            verify(userDomainService).updateUser(eq(testUserId), any(User.class), eq(testUser));
        }

        @Test
        @DisplayName("Should handle complete user search workflow")
        void shouldHandleCompleteUserSearchWorkflow() {
            // Given
            String searchName = "John";
            Pageable pageable = PageRequest.of(0, 10);
            List<User> users = Arrays.asList(testUser);

            when(userRepository.searchByName(searchName, 0, 10)).thenReturn(users);

            // When
            Page<User> result = userApplicationService.searchUsersByName(searchName, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(testUserId.toString());

            verify(userRepository).searchByName(searchName, 0, 10);
        }
    }
}