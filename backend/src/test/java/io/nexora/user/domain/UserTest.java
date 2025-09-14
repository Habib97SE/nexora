package io.nexora.user.domain;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for User domain entity.
 * 
 * These tests verify all business logic, validation rules, and behavior
 * implemented in the User aggregate root entity.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - Business method behavior
 * - Validation constraints
 * - Value object interactions
 * - Timestamp and audit field behavior
 * - Edge cases and error conditions
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All public methods and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - Domain-Driven Testing: Tests focus on business behavior
 */
@DisplayName("User Domain Tests")
class UserTest {

    private User user;
    private Email validEmail;
    private Password validPassword;
    private Role validRole;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validEmail = new Email("test@example.com");
        validPassword = Password.fromPlainText("password123");
        validRole = Role.CUSTOMER;
        
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with builder pattern successfully")
        void shouldCreateUserWithBuilderPatternSuccessfully() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            
            // When
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .active(true)
                    .emailVerified(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getFirstName()).isEqualTo("John");
            assertThat(user.getLastName()).isEqualTo("Doe");
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getPassword()).isEqualTo(validPassword);
            assertThat(user.getRole()).isEqualTo(validRole);
            assertThat(user.isActive()).isTrue();
            assertThat(user.isEmailVerified()).isFalse();
            assertThat(user.getCreatedAt()).isEqualTo(now);
            assertThat(user.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should create user with default values")
        void shouldCreateUserWithDefaultValues() {
            // When
            user = User.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getPassword()).isEqualTo(validPassword);
            assertThat(user.getRole()).isEqualTo(validRole);
            assertThat(user.isActive()).isFalse(); // Default value from @Column annotation
            assertThat(user.isEmailVerified()).isFalse(); // Default value from @Column annotation
            // Note: @CreationTimestamp and @UpdateTimestamp only work during persistence
            // In unit tests, these fields will be null unless explicitly set
        }

        @Test
        @DisplayName("Should create user with no-args constructor")
        void shouldCreateUserWithNoArgsConstructor() {
            // When
            user = new User();

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getFirstName()).isNull();
            assertThat(user.getLastName()).isNull();
            assertThat(user.getEmail()).isNull();
            assertThat(user.getPassword()).isNull();
            assertThat(user.getRole()).isNull();
            assertThat(user.isActive()).isFalse(); // Default value
            assertThat(user.isEmailVerified()).isFalse(); // Default value
        }
    }

    @Nested
    @DisplayName("Business Method Tests")
    class BusinessMethodTests {

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .active(false)
                    .emailVerified(false)
                    .build();
        }

        @Test
        @DisplayName("Should get full name correctly")
        void shouldGetFullNameCorrectly() {
            // When
            String fullName = user.getFullName();

            // Then
            assertThat(fullName).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should check if user is active")
        void shouldCheckIfUserIsActive() {
            // Given
            user.setActive(true);

            // When & Then
            assertThat(user.isActive()).isTrue();
            assertThat(user.isActive()).isTrue(); // Test getter method
        }

        @Test
        @DisplayName("Should check if email is verified")
        void shouldCheckIfEmailIsVerified() {
            // Given
            user.setEmailVerified(true);

            // When & Then
            assertThat(user.isEmailVerified()).isTrue();
            assertThat(user.isEmailVerified()).isTrue(); // Test getter method
        }

        @Test
        @DisplayName("Should activate user account")
        void shouldActivateUserAccount() {
            // Given
            LocalDateTime beforeActivation = LocalDateTime.now();
            user.setActive(false);

            // When
            user.activate();

            // Then
            assertThat(user.isActive()).isTrue();
            assertThat(user.getUpdatedAt()).isAfter(beforeActivation);
        }

        @Test
        @DisplayName("Should deactivate user account")
        void shouldDeactivateUserAccount() {
            // Given
            LocalDateTime beforeDeactivation = LocalDateTime.now();
            user.setActive(true);

            // When
            user.deactivate();

            // Then
            assertThat(user.isActive()).isFalse();
            assertThat(user.getUpdatedAt()).isAfter(beforeDeactivation);
        }

        @Test
        @DisplayName("Should verify email")
        void shouldVerifyEmail() {
            // Given
            LocalDateTime beforeVerification = LocalDateTime.now();
            user.setEmailVerified(false);

            // When
            user.verifyEmail();

            // Then
            assertThat(user.isEmailVerified()).isTrue();
            assertThat(user.getUpdatedAt()).isAfter(beforeVerification);
        }

        @Test
        @DisplayName("Should update last login timestamp")
        void shouldUpdateLastLoginTimestamp() {
            // Given
            LocalDateTime beforeUpdate = LocalDateTime.now();
            user.setLastLoginAt(null);

            // When
            user.updateLastLogin();

            // Then
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getLastLoginAt()).isAfter(beforeUpdate);
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should change password")
        void shouldChangePassword() {
            // Given
            Password newPassword = Password.fromPlainText("newpassword123");
            LocalDateTime beforeChange = LocalDateTime.now();

            // When
            user.changePassword(newPassword);

            // Then
            assertThat(user.getPassword()).isEqualTo(newPassword);
            assertThat(user.getUpdatedAt()).isAfter(beforeChange);
        }

        @Test
        @DisplayName("Should change role")
        void shouldChangeRole() {
            // Given
            Role newRole = Role.ADMIN;
            LocalDateTime beforeChange = LocalDateTime.now();

            // When
            user.changeRole(newRole);

            // Then
            assertThat(user.getRole()).isEqualTo(newRole);
            assertThat(user.getUpdatedAt()).isAfter(beforeChange);
        }

        @Test
        @DisplayName("Should update profile with valid names")
        void shouldUpdateProfileWithValidNames() {
            // Given
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.updateProfile("Jane", "Smith");

            // Then
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should update profile with partial names")
        void shouldUpdateProfileWithPartialNames() {
            // Given
            String originalLastName = user.getLastName();
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.updateProfile("Jane", null);

            // Then
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo(originalLastName);
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should not update profile with empty names")
        void shouldNotUpdateProfileWithEmptyNames() {
            // Given
            String originalFirstName = user.getFirstName();
            String originalLastName = user.getLastName();
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.updateProfile("", "   ");

            // Then
            assertThat(user.getFirstName()).isEqualTo(originalFirstName);
            assertThat(user.getLastName()).isEqualTo(originalLastName);
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should trim whitespace from profile names")
        void shouldTrimWhitespaceFromProfileNames() {
            // Given
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.updateProfile("  Jane  ", "  Smith  ");

            // Then
            assertThat(user.getFirstName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }
    }

    @Nested
    @DisplayName("Role Permission Tests")
    class RolePermissionTests {

        @Test
        @DisplayName("Should check admin operations permission for ADMIN role")
        void shouldCheckAdminOperationsPermissionForAdminRole() {
            // Given
            user = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(validEmail)
                    .password(validPassword)
                    .role(Role.ADMIN)
                    .build();

            // When & Then
            assertThat(user.canPerformAdminOperations()).isTrue();
            assertThat(user.canPerformCustomerOperations()).isFalse();
        }

        @Test
        @DisplayName("Should check admin operations permission for MANAGER role")
        void shouldCheckAdminOperationsPermissionForManagerRole() {
            // Given
            user = User.builder()
                    .firstName("Manager")
                    .lastName("User")
                    .email(validEmail)
                    .password(validPassword)
                    .role(Role.MANAGER)
                    .build();

            // When & Then
            assertThat(user.canPerformAdminOperations()).isTrue();
            assertThat(user.canPerformCustomerOperations()).isFalse();
        }

        @Test
        @DisplayName("Should check customer operations permission for CUSTOMER role")
        void shouldCheckCustomerOperationsPermissionForCustomerRole() {
            // Given
            user = User.builder()
                    .firstName("Customer")
                    .lastName("User")
                    .email(validEmail)
                    .password(validPassword)
                    .role(Role.CUSTOMER)
                    .build();

            // When & Then
            assertThat(user.canPerformAdminOperations()).isFalse();
            assertThat(user.canPerformCustomerOperations()).isTrue();
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate user with all required fields")
        void shouldValidateUserWithAllRequiredFields() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation with null first name")
        void shouldFailValidationWithNullFirstName() {
            // Given
            user = User.builder()
                    .firstName(null)
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("First name cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty first name")
        void shouldFailValidationWithEmptyFirstName() {
            // Given
            user = User.builder()
                    .firstName("")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(2); // Both @NotBlank and @Size constraints fail
            assertThat(violations).extracting("message")
                    .contains("First name cannot be empty", "First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with too short first name")
        void shouldFailValidationWithTooShortFirstName() {
            // Given
            user = User.builder()
                    .firstName("J")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with too long first name")
        void shouldFailValidationWithTooLongFirstName() {
            // Given
            String longFirstName = "A".repeat(51);
            user = User.builder()
                    .firstName(longFirstName)
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("First name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with null last name")
        void shouldFailValidationWithNullLastName() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName(null)
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Last name cannot be empty");
        }

        @Test
        @DisplayName("Should fail validation with empty last name")
        void shouldFailValidationWithEmptyLastName() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(2); // Both @NotBlank and @Size constraints fail
            assertThat(violations).extracting("message")
                    .contains("Last name cannot be empty", "Last name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with too short last name")
        void shouldFailValidationWithTooShortLastName() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("D")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Last name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with too long last name")
        void shouldFailValidationWithTooLongLastName() {
            // Given
            String longLastName = "A".repeat(51);
            user = User.builder()
                    .firstName("John")
                    .lastName(longLastName)
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Last name must be between 2 and 50 characters");
        }

        @Test
        @DisplayName("Should fail validation with null email")
        void shouldFailValidationWithNullEmail() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(null)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Email is required");
        }

        @Test
        @DisplayName("Should fail validation with null password")
        void shouldFailValidationWithNullPassword() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(null)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Password is required");
        }

        @Test
        @DisplayName("Should fail validation with null role")
        void shouldFailValidationWithNullRole() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(null)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Role is required");
        }

        @ParameterizedTest
        @ValueSource(strings = {"J", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})
        @DisplayName("Should fail validation with invalid first name lengths")
        void shouldFailValidationWithInvalidFirstNameLengths(String firstName) {
            // Given
            user = User.builder()
                    .firstName(firstName)
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("First name must be between 2 and 50 characters");
        }

        @ParameterizedTest
        @ValueSource(strings = {"D", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})
        @DisplayName("Should fail validation with invalid last name lengths")
        void shouldFailValidationWithInvalidLastNameLengths(String lastName) {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName(lastName)
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // When
            Set<ConstraintViolation<User>> violations = validator.validate(user);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Last name must be between 2 and 50 characters");
        }
    }

    @Nested
    @DisplayName("Value Object Integration Tests")
    class ValueObjectIntegrationTests {

        @Test
        @DisplayName("Should work with valid email value object")
        void shouldWorkWithValidEmailValueObject() {
            // Given
            Email email = new Email("user@domain.com");

            // When
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(email)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            // Then
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getEmail().value()).isEqualTo("user@domain.com");
        }

        @Test
        @DisplayName("Should work with valid password value object")
        void shouldWorkWithValidPasswordValueObject() {
            // Given
            Password password = Password.fromPlainText("securepass123");

            // When
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(password)
                    .role(validRole)
                    .build();

            // Then
            assertThat(user.getPassword()).isEqualTo(password);
            assertThat(user.getPassword().matches("securepass123")).isTrue();
        }

        @Test
        @DisplayName("Should work with different role value objects")
        void shouldWorkWithDifferentRoleValueObjects() {
            // Test CUSTOMER role
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(Role.CUSTOMER)
                    .build();

            assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
            assertThat(user.canPerformCustomerOperations()).isTrue();
            assertThat(user.canPerformAdminOperations()).isFalse();

            // Test ADMIN role
            user.changeRole(Role.ADMIN);
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.canPerformAdminOperations()).isTrue();
            assertThat(user.canPerformCustomerOperations()).isFalse();

            // Test MANAGER role
            user.changeRole(Role.MANAGER);
            assertThat(user.getRole()).isEqualTo(Role.MANAGER);
            assertThat(user.canPerformAdminOperations()).isTrue();
            assertThat(user.canPerformCustomerOperations()).isFalse();
        }
    }

    @Nested
    @DisplayName("Timestamp and Audit Field Tests")
    class TimestampAndAuditFieldTests {

        @Test
        @DisplayName("Should handle timestamp fields correctly")
        void shouldHandleTimestampFieldsCorrectly() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // Then
            assertThat(user.getCreatedAt()).isEqualTo(now);
            assertThat(user.getUpdatedAt()).isEqualTo(now);
            // Note: @CreationTimestamp and @UpdateTimestamp annotations only work during JPA persistence
            // In unit tests, timestamps must be set manually or will be null
        }

        @Test
        @DisplayName("Should update timestamp on business operations")
        void shouldUpdateTimestampOnBusinessOperations() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.activate();

            // Then
            assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("Should maintain creation timestamp on updates")
        void shouldMaintainCreationTimestampOnUpdates() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            LocalDateTime originalCreatedAt = user.getCreatedAt();

            // When
            user.activate();
            user.deactivate();
            user.verifyEmail();

            // Then
            assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
        }

        @Test
        @DisplayName("Should update last login timestamp correctly")
        void shouldUpdateLastLoginTimestampCorrectly() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            assertThat(user.getLastLoginAt()).isNull();

            // When
            user.updateLastLogin();

            // Then
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getLastLoginAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle null values in updateProfile gracefully")
        void shouldHandleNullValuesInUpdateProfileGracefully() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            String originalFirstName = user.getFirstName();
            String originalLastName = user.getLastName();

            // When
            user.updateProfile(null, null);

            // Then
            assertThat(user.getFirstName()).isEqualTo(originalFirstName);
            assertThat(user.getLastName()).isEqualTo(originalLastName);
        }

        @Test
        @DisplayName("Should handle whitespace-only values in updateProfile")
        void shouldHandleWhitespaceOnlyValuesInUpdateProfile() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            String originalFirstName = user.getFirstName();
            String originalLastName = user.getLastName();

            // When
            user.updateProfile("   ", "\t\n");

            // Then
            assertThat(user.getFirstName()).isEqualTo(originalFirstName);
            assertThat(user.getLastName()).isEqualTo(originalLastName);
        }

        @Test
        @DisplayName("Should handle multiple consecutive updates")
        void shouldHandleMultipleConsecutiveUpdates() {
            // Given
            user = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .active(false)
                    .emailVerified(false)
                    .build();

            // When
            user.activate();
            user.verifyEmail();
            user.updateLastLogin();
            user.changePassword(Password.fromPlainText("newpass123"));
            user.changeRole(Role.ADMIN);

            // Then
            assertThat(user.isActive()).isTrue();
            assertThat(user.isEmailVerified()).isTrue();
            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getPassword().matches("newpass123")).isTrue();
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.canPerformAdminOperations()).isTrue();
        }

        @Test
        @DisplayName("Should handle boundary values for name lengths")
        void shouldHandleBoundaryValuesForNameLengths() {
            // Test minimum valid length
            user = User.builder()
                    .firstName("Jo") // Exactly 2 characters
                    .lastName("Do")  // Exactly 2 characters
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertThat(violations).isEmpty();

            // Test maximum valid length
            user = User.builder()
                    .firstName("A".repeat(50)) // Exactly 50 characters
                    .lastName("B".repeat(50))  // Exactly 50 characters
                    .email(validEmail)
                    .password(validPassword)
                    .role(validRole)
                    .build();

            violations = validator.validate(user);
            assertThat(violations).isEmpty();
        }
    }
}
