package io.nexora.user.domain.service;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import io.nexora.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserDomainService.
 * 
 * These tests verify the business logic and domain rules implemented
 * in the UserDomainService class.
 * 
 * Test Coverage:
 * - User registration with validation
 * - User authentication
 * - User updates with authorization
 * - Password changes
 * - Role changes
 * - User activation/deactivation
 * - Email verification
 * - Error handling and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDomainService Tests")
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainService userDomainService;

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

    // ==================== USER REGISTRATION TESTS ====================

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() {
        // Given
        User newUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(new Email("jane@example.com"))
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userDomainService.registerUser(newUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail().value()).isEqualTo("jane@example.com");
        assertThat(result.isActive()).isFalse(); // Should be inactive until email verification
        assertThat(result.isEmailVerified()).isFalse();

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with existing email")
    void shouldThrowExceptionWhenRegisteringUserWithExistingEmail() {
        // Given
        User newUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(testEmail)
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with invalid data")
    void shouldThrowExceptionWhenRegisteringUserWithInvalidData() {
        // Given
        User invalidUser = User.builder()
                .firstName("") // Invalid: empty first name
                .lastName("Smith")
                .email(testEmail)
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name is required");
    }

    // ==================== USER AUTHENTICATION TESTS ====================

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfully() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.authenticateUser(testEmail, testPassword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testEmail);
        assertThat(result.getLastLoginAt()).isNotNull();

        verify(userRepository).findByEmail(testEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when authenticating non-existent user")
    void shouldThrowExceptionWhenAuthenticatingNonExistentUser() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDomainService.authenticateUser(testEmail, testPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with email " + testEmail + " not found");

        verify(userRepository).findByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when authenticating inactive user")
    void shouldThrowExceptionWhenAuthenticatingInactiveUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.authenticateUser(testEmail, testPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User account is not active");

        verify(userRepository).findByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when authenticating with wrong password")
    void shouldThrowExceptionWhenAuthenticatingWithWrongPassword() {
        // Given
        Password wrongPassword = Password.fromPlainText("wrongpassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.authenticateUser(testEmail, wrongPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");

        verify(userRepository).findByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== USER UPDATE TESTS ====================

    @Test
    @DisplayName("Should update user successfully with valid data")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(testEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userDomainService.updateUser(testUserId, updatedUser, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(testEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDomainService.updateUser(testUserId, updatedUser, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with ID " + testUserId + " not found");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== PASSWORD CHANGE TESTS ====================

    @Test
    @DisplayName("Should change password successfully with valid current password")
    void shouldChangePasswordSuccessfully() {
        // Given
        Password newPassword = Password.fromPlainText("newpassword123");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.changePassword(testUserId, testPassword, newPassword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo(newPassword);

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when changing password with wrong current password")
    void shouldThrowExceptionWhenChangingPasswordWithWrongCurrentPassword() {
        // Given
        Password wrongPassword = Password.fromPlainText("wrongpassword");
        Password newPassword = Password.fromPlainText("newpassword123");
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changePassword(testUserId, wrongPassword, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== ROLE CHANGE TESTS ====================

    @Test
    @DisplayName("Should change role successfully when current user is admin")
    void shouldChangeRoleSuccessfullyWhenCurrentUserIsAdmin() {
        // Given
        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        Role newRole = Role.MANAGER;
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.changeRole(testUserId, newRole, adminUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(newRole);

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to change role")
    void shouldThrowExceptionWhenNonAdminTriesToChangeRole() {
        // Given
        User customerUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Customer")
                .lastName("User")
                .email(new Email("customer@example.com"))
                .password(testPassword)
                .role(Role.CUSTOMER)
                .active(true)
                .emailVerified(true)
                .build();

        Role newRole = Role.MANAGER;
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changeRole(testUserId, newRole, customerUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only admins can change user roles");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user tries to change own role")
    void shouldThrowExceptionWhenUserTriesToChangeOwnRole() {
        // Given
        User adminUser = User.builder()
                .id(testUserId.toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        Role newRole = Role.MANAGER;
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changeRole(testUserId, newRole, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot change your own role");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== USER ACTIVATION TESTS ====================

    @Test
    @DisplayName("Should activate user successfully")
    void shouldActivateUserSuccessfully() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.activateUser(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isTrue();

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when activating already active user")
    void shouldThrowExceptionWhenActivatingAlreadyActiveUser() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.activateUser(testUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already active");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== USER DEACTIVATION TESTS ====================

    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() {
        // Given
        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.deactivateUser(testUserId, adminUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isFalse();

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user tries to deactivate own account")
    void shouldThrowExceptionWhenUserTriesToDeactivateOwnAccount() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.deactivateUser(testUserId, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot deactivate your own account");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== EMAIL VERIFICATION TESTS ====================

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() {
        // Given
        testUser.setEmailVerified(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.verifyEmail(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isEmailVerified()).isTrue();

        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when verifying already verified email")
    void shouldThrowExceptionWhenVerifyingAlreadyVerifiedEmail() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.verifyEmail(testUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already verified");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== FIND USER TESTS ====================

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userDomainService.findUserById(testUserId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUserId.toString());

        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent user by ID")
    void shouldThrowExceptionWhenFindingNonExistentUserById() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDomainService.findUserById(testUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with ID " + testUserId + " not found");

        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        User result = userDomainService.findUserByEmail(testEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testEmail);

        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    @DisplayName("Should throw exception when finding non-existent user by email")
    void shouldThrowExceptionWhenFindingNonExistentUserByEmail() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDomainService.findUserByEmail(testEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with email " + testEmail + " not found");

        verify(userRepository).findByEmail(testEmail);
    }

    // ==================== ADDITIONAL COMPREHENSIVE TESTS ====================

    @Test
    @DisplayName("Should handle email change during user update")
    void shouldHandleEmailChangeDuringUserUpdate() {
        // Given
        Email newEmail = new Email("newemail@example.com");
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(newEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userDomainService.updateUser(testUserId, updatedUser, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(newEmail);
        verify(userRepository).existsByEmail(newEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating user with existing email")
    void shouldThrowExceptionWhenUpdatingUserWithExistingEmail() {
        // Given
        Email existingEmail = new Email("existing@example.com");
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email(existingEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        User existingUserWithEmail = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Existing")
                .lastName("User")
                .email(existingEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUserWithEmail));

        // When & Then
        assertThatThrownBy(() -> userDomainService.updateUser(testUserId, updatedUser, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists: " + existingEmail);

        verify(userRepository).findById(testUserId);
        verify(userRepository).existsByEmail(existingEmail);
        verify(userRepository).findByEmail(existingEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow role change when current user is admin")
    void shouldAllowRoleChangeWhenCurrentUserIsAdmin() {
        // Given
        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        User updatedUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(testPassword)
                .role(Role.MANAGER)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userDomainService.updateUser(testUserId, updatedUser, adminUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.MANAGER);
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }


    @Test
    @DisplayName("Should throw exception when trying to change own role")
    void shouldThrowExceptionWhenTryingToChangeOwnRole() {
        // Given
        User adminUser = User.builder()
                .id(testUserId.toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        User updatedUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(adminUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changeRole(testUserId, Role.CUSTOMER, adminUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot change your own role");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password change with incorrect current password")
    void shouldThrowExceptionWhenPasswordChangeWithIncorrectCurrentPassword() {
        // Given
        Password incorrectPassword = Password.fromPlainText("wrongpassword");
        Password newPassword = Password.fromPlainText("newpassword123");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changePassword(testUserId, incorrectPassword, newPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password change with same password")
    void shouldThrowExceptionWhenPasswordChangeWithSamePassword() {
        // Given
        Password samePassword = Password.fromPlainText("password123");

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.changePassword(testUserId, testPassword, samePassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New password must be different from current password");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when deactivating already inactive user")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userDomainService.deactivateUser(testUserId, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already inactive");

        verify(userRepository).findById(testUserId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user registration with all required fields")
    void shouldHandleUserRegistrationWithAllRequiredFields() {
        // Given
        User newUser = User.builder()
                .firstName("New")
                .lastName("User")
                .email(new Email("newuser@example.com"))
                .password(Password.fromPlainText("newpassword123"))
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userDomainService.registerUser(newUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("New");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.getEmail()).isEqualTo(new Email("newuser@example.com"));
        assertThat(result.isActive()).isFalse(); // Should be inactive until email verification
        assertThat(result.isEmailVerified()).isFalse();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with missing first name")
    void shouldThrowExceptionWhenRegisteringUserWithMissingFirstName() {
        // Given
        User invalidUser = User.builder()
                .firstName("")
                .lastName("User")
                .email(new Email("user@example.com"))
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("First name is required");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with missing last name")
    void shouldThrowExceptionWhenRegisteringUserWithMissingLastName() {
        // Given
        User invalidUser = User.builder()
                .firstName("User")
                .lastName("")
                .email(new Email("user@example.com"))
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Last name is required");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with missing email")
    void shouldThrowExceptionWhenRegisteringUserWithMissingEmail() {
        // Given
        User invalidUser = User.builder()
                .firstName("User")
                .lastName("Name")
                .email(null)
                .password(Password.fromPlainText("password123"))
                .role(Role.CUSTOMER)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with missing password")
    void shouldThrowExceptionWhenRegisteringUserWithMissingPassword() {
        // Given
        User invalidUser = User.builder()
                .firstName("User")
                .lastName("Name")
                .email(new Email("user@example.com"))
                .password(null)
                .role(Role.CUSTOMER)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with missing role")
    void shouldThrowExceptionWhenRegisteringUserWithMissingRole() {
        // Given
        User invalidUser = User.builder()
                .firstName("User")
                .lastName("Name")
                .email(new Email("user@example.com"))
                .password(Password.fromPlainText("password123"))
                .role(null)
                .build();

        // When & Then
        assertThatThrownBy(() -> userDomainService.registerUser(invalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role is required");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle authentication with different user states")
    void shouldHandleAuthenticationWithDifferentUserStates() {
        // Test with inactive user
        testUser.setActive(false);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userDomainService.authenticateUser(testEmail, testPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User account is not active");

        // Test with active user
        testUser.setActive(true);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userDomainService.authenticateUser(testEmail, testPassword);
        assertThat(result).isNotNull();
        assertThat(result.getLastLoginAt()).isNotNull();

        verify(userRepository, times(2)).findByEmail(testEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle role change with different admin roles")
    void shouldHandleRoleChangeWithDifferentAdminRoles() {
        // Test with ADMIN role
        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Admin")
                .lastName("User")
                .email(new Email("admin@example.com"))
                .password(testPassword)
                .role(Role.ADMIN)
                .active(true)
                .emailVerified(true)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userDomainService.changeRole(testUserId, Role.MANAGER, adminUser);
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.MANAGER);

        // Test with MANAGER role
        User managerUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Manager")
                .lastName("User")
                .email(new Email("manager@example.com"))
                .password(testPassword)
                .role(Role.MANAGER)
                .active(true)
                .emailVerified(true)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result2 = userDomainService.changeRole(testUserId, Role.CUSTOMER, managerUser);
        assertThat(result2).isNotNull();
        assertThat(result2.getRole()).isEqualTo(Role.CUSTOMER);

        verify(userRepository, times(2)).findById(testUserId);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should preserve timestamps during user update")
    void shouldPreserveTimestampsDuringUserUpdate() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        testUser.setCreatedAt(originalCreatedAt);
        testUser.setUpdatedAt(originalCreatedAt);

        User updatedUser = User.builder()
                .firstName("Updated")
                .lastName("User")
                .email(testEmail)
                .password(testPassword)
                .role(testRole)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertThat(savedUser.getCreatedAt()).isEqualTo(originalCreatedAt);
            assertThat(savedUser.getUpdatedAt()).isAfter(originalCreatedAt);
            return savedUser;
        });

        // When
        User result = userDomainService.updateUser(testUserId, updatedUser, testUser);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(any(User.class));
    }
}
