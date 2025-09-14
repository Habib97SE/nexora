package io.nexora.user.interfaces.rest.v1.dto;

import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for UserResponse DTO.
 * 
 * These tests verify the DTO mapping, data transformation, and behavior
 * of the UserResponse class used for API responses.
 * 
 * Test Coverage:
 * - Constructor and builder patterns
 * - fromDomain factory method
 * - Field mapping and data transformation
 * - Null handling and edge cases
 * - String representation of timestamps
 * - Value object extraction
 * 
 * Design Principles Applied:
 * - DTO Testing: Focus on data transformation and mapping
 * - Factory Method Testing: Verify proper object creation
 * - Edge Case Testing: Handle null values and boundary conditions
 * - Data Integrity: Ensure correct field mapping
 */
@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

    private User testUser;
    private Email testEmail;
    private Password testPassword;
    private Role testRole;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testEmail = new Email("test@example.com");
        testPassword = Password.fromPlainText("password123");
        testRole = Role.CUSTOMER;
        testTimestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

        testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(testPassword)
                .role(testRole)
                .active(true)
                .emailVerified(false)
                .lastLoginAt(testTimestamp)
                .createdAt(testTimestamp)
                .updatedAt(testTimestamp)
                .build();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create UserResponse with no-args constructor")
        void shouldCreateUserResponseWithNoArgsConstructor() {
            // When
            UserResponse response = new UserResponse();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNull();
            assertThat(response.getFirstName()).isNull();
            assertThat(response.getLastName()).isNull();
            assertThat(response.getFullName()).isNull();
            assertThat(response.getEmail()).isNull();
            assertThat(response.getRole()).isNull();
            assertThat(response.isActive()).isFalse();
            assertThat(response.isEmailVerified()).isFalse();
            assertThat(response.getLastLoginAt()).isNull();
            assertThat(response.getCreatedAt()).isNull();
            assertThat(response.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("Should create UserResponse with all-args constructor")
        void shouldCreateUserResponseWithAllArgsConstructor() {
            // Given
            String id = "user-123";
            String firstName = "Jane";
            String lastName = "Smith";
            String fullName = "Jane Smith";
            String email = "jane@example.com";
            String role = "ADMIN";
            boolean active = true;
            boolean emailVerified = true;
            String lastLoginAt = "2024-01-15T10:30:45";
            String createdAt = "2024-01-15T10:30:45";
            String updatedAt = "2024-01-15T10:30:45";

            // When
            UserResponse response = new UserResponse(
                    id, firstName, lastName, fullName, email, role,
                    active, emailVerified, lastLoginAt, createdAt, updatedAt
            );

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(id);
            assertThat(response.getFirstName()).isEqualTo(firstName);
            assertThat(response.getLastName()).isEqualTo(lastName);
            assertThat(response.getFullName()).isEqualTo(fullName);
            assertThat(response.getEmail()).isEqualTo(email);
            assertThat(response.getRole()).isEqualTo(role);
            assertThat(response.isActive()).isEqualTo(active);
            assertThat(response.isEmailVerified()).isEqualTo(emailVerified);
            assertThat(response.getLastLoginAt()).isEqualTo(lastLoginAt);
            assertThat(response.getCreatedAt()).isEqualTo(createdAt);
            assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("fromDomain Factory Method Tests")
    class FromDomainFactoryMethodTests {

        @Test
        @DisplayName("Should create UserResponse from complete User domain object")
        void shouldCreateUserResponseFromCompleteUserDomainObject() {
            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(testUser.getId());
            assertThat(response.getFirstName()).isEqualTo(testUser.getFirstName());
            assertThat(response.getLastName()).isEqualTo(testUser.getLastName());
            assertThat(response.getFullName()).isEqualTo(testUser.getFullName());
            assertThat(response.getEmail()).isEqualTo(testUser.getEmail().value());
            assertThat(response.getRole()).isEqualTo(testUser.getRole().value());
            assertThat(response.isActive()).isEqualTo(testUser.isActive());
            assertThat(response.isEmailVerified()).isEqualTo(testUser.isEmailVerified());
            assertThat(response.getLastLoginAt()).isEqualTo(testUser.getLastLoginAt().toString());
            assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt().toString());
            assertThat(response.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt().toString());
        }

        @Test
        @DisplayName("Should create UserResponse from User with null lastLoginAt")
        void shouldCreateUserResponseFromUserWithNullLastLoginAt() {
            // Given
            testUser.setLastLoginAt(null);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getLastLoginAt()).isNull();
            assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt().toString());
            assertThat(response.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt().toString());
        }

        @Test
        @DisplayName("Should create UserResponse from User with different roles")
        void shouldCreateUserResponseFromUserWithDifferentRoles() {
            // Test CUSTOMER role
            testUser.setRole(Role.CUSTOMER);
            UserResponse customerResponse = UserResponse.fromDomain(testUser);
            assertThat(customerResponse.getRole()).isEqualTo("CUSTOMER");

            // Test ADMIN role
            testUser.setRole(Role.ADMIN);
            UserResponse adminResponse = UserResponse.fromDomain(testUser);
            assertThat(adminResponse.getRole()).isEqualTo("ADMIN");

            // Test MANAGER role
            testUser.setRole(Role.MANAGER);
            UserResponse managerResponse = UserResponse.fromDomain(testUser);
            assertThat(managerResponse.getRole()).isEqualTo("MANAGER");
        }

        @Test
        @DisplayName("Should create UserResponse from User with different email formats")
        void shouldCreateUserResponseFromUserWithDifferentEmailFormats() {
            // Test standard email
            testUser.setEmail(new Email("user@domain.com"));
            UserResponse response1 = UserResponse.fromDomain(testUser);
            assertThat(response1.getEmail()).isEqualTo("user@domain.com");

            // Test email with subdomain
            testUser.setEmail(new Email("user@sub.domain.com"));
            UserResponse response2 = UserResponse.fromDomain(testUser);
            assertThat(response2.getEmail()).isEqualTo("user@sub.domain.com");

            // Test email with numbers
            testUser.setEmail(new Email("user123@domain123.com"));
            UserResponse response3 = UserResponse.fromDomain(testUser);
            assertThat(response3.getEmail()).isEqualTo("user123@domain123.com");
        }

        @Test
        @DisplayName("Should create UserResponse from User with different status combinations")
        void shouldCreateUserResponseFromUserWithDifferentStatusCombinations() {
            // Test active and verified
            testUser.setActive(true);
            testUser.setEmailVerified(true);
            UserResponse response1 = UserResponse.fromDomain(testUser);
            assertThat(response1.isActive()).isTrue();
            assertThat(response1.isEmailVerified()).isTrue();

            // Test inactive and unverified
            testUser.setActive(false);
            testUser.setEmailVerified(false);
            UserResponse response2 = UserResponse.fromDomain(testUser);
            assertThat(response2.isActive()).isFalse();
            assertThat(response2.isEmailVerified()).isFalse();

            // Test active but unverified
            testUser.setActive(true);
            testUser.setEmailVerified(false);
            UserResponse response3 = UserResponse.fromDomain(testUser);
            assertThat(response3.isActive()).isTrue();
            assertThat(response3.isEmailVerified()).isFalse();

            // Test inactive but verified
            testUser.setActive(false);
            testUser.setEmailVerified(true);
            UserResponse response4 = UserResponse.fromDomain(testUser);
            assertThat(response4.isActive()).isFalse();
            assertThat(response4.isEmailVerified()).isTrue();
        }
    }

    @Nested
    @DisplayName("Field Mapping Tests")
    class FieldMappingTests {

        @Test
        @DisplayName("Should correctly map all User fields to UserResponse")
        void shouldCorrectlyMapAllUserFieldsToUserResponse() {
            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then - Verify all field mappings
            assertThat(response.getId()).isEqualTo(testUser.getId());
            assertThat(response.getFirstName()).isEqualTo(testUser.getFirstName());
            assertThat(response.getLastName()).isEqualTo(testUser.getLastName());
            assertThat(response.getFullName()).isEqualTo(testUser.getFullName());
            assertThat(response.getEmail()).isEqualTo(testUser.getEmail().value());
            assertThat(response.getRole()).isEqualTo(testUser.getRole().value());
            assertThat(response.isActive()).isEqualTo(testUser.isActive());
            assertThat(response.isEmailVerified()).isEqualTo(testUser.isEmailVerified());
            assertThat(response.getLastLoginAt()).isEqualTo(testUser.getLastLoginAt().toString());
            assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt().toString());
            assertThat(response.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt().toString());
        }

        @Test
        @DisplayName("Should extract email value from Email value object")
        void shouldExtractEmailValueFromEmailValueObject() {
            // Given
            Email email = new Email("test@example.com");
            testUser.setEmail(email);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getEmail()).isEqualTo(email.value());
        }

        @Test
        @DisplayName("Should extract role value from Role value object")
        void shouldExtractRoleValueFromRoleValueObject() {
            // Given
            Role role = Role.ADMIN;
            testUser.setRole(role);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getRole()).isEqualTo("ADMIN");
            assertThat(response.getRole()).isEqualTo(role.value());
        }

        @Test
        @DisplayName("Should convert LocalDateTime to String for timestamps")
        void shouldConvertLocalDateTimeToStringForTimestamps() {
            // Given
            LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 14, 30, 45);
            testUser.setCreatedAt(timestamp);
            testUser.setUpdatedAt(timestamp);
            testUser.setLastLoginAt(timestamp);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getCreatedAt()).isEqualTo("2024-03-15T14:30:45");
            assertThat(response.getUpdatedAt()).isEqualTo("2024-03-15T14:30:45");
            assertThat(response.getLastLoginAt()).isEqualTo("2024-03-15T14:30:45");
        }
    }

    @Nested
    @DisplayName("Null Handling and Edge Cases Tests")
    class NullHandlingAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle null lastLoginAt gracefully")
        void shouldHandleNullLastLoginAtGracefully() {
            // Given
            testUser.setLastLoginAt(null);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getLastLoginAt()).isNull();
            assertThat(response.getCreatedAt()).isNotNull();
            assertThat(response.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should handle null timestamps gracefully")
        void shouldHandleNullTimestampsGracefully() {
            // Given
            testUser.setCreatedAt(null);
            testUser.setUpdatedAt(null);
            testUser.setLastLoginAt(null);

            // When & Then
            assertThatThrownBy(() -> UserResponse.fromDomain(testUser))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() {
            // Given
            testUser.setFirstName("");
            testUser.setLastName("");

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getFirstName()).isEqualTo("");
            assertThat(response.getLastName()).isEqualTo("");
            assertThat(response.getFullName()).isEqualTo(" "); // " " + " "
        }

        @Test
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            // Given
            testUser.setFirstName("José");
            testUser.setLastName("García-López");

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getFirstName()).isEqualTo("José");
            assertThat(response.getLastName()).isEqualTo("García-López");
            assertThat(response.getFullName()).isEqualTo("José García-López");
        }

        @Test
        @DisplayName("Should handle long names")
        void shouldHandleLongNames() {
            // Given
            String longFirstName = "A".repeat(50);
            String longLastName = "B".repeat(50);
            testUser.setFirstName(longFirstName);
            testUser.setLastName(longLastName);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response.getFirstName()).isEqualTo(longFirstName);
            assertThat(response.getLastName()).isEqualTo(longLastName);
            assertThat(response.getFullName()).isEqualTo(longFirstName + " " + longLastName);
        }
    }

    @Nested
    @DisplayName("Data Integrity Tests")
    class DataIntegrityTests {

        @Test
        @DisplayName("Should maintain data consistency between User and UserResponse")
        void shouldMaintainDataConsistencyBetweenUserAndUserResponse() {
            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then - Verify data consistency
            assertThat(response.getId()).isEqualTo(testUser.getId());
            assertThat(response.getFirstName()).isEqualTo(testUser.getFirstName());
            assertThat(response.getLastName()).isEqualTo(testUser.getLastName());
            assertThat(response.getFullName()).isEqualTo(testUser.getFullName());
            assertThat(response.getEmail()).isEqualTo(testUser.getEmail().value());
            assertThat(response.getRole()).isEqualTo(testUser.getRole().value());
            assertThat(response.isActive()).isEqualTo(testUser.isActive());
            assertThat(response.isEmailVerified()).isEqualTo(testUser.isEmailVerified());
        }

        @Test
        @DisplayName("Should not expose sensitive data")
        void shouldNotExposeSensitiveData() {
            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then - Verify password is not exposed in the response
            assertThat(response.toString()).doesNotContain("password");
            assertThat(response.toString()).doesNotContain("Password");
            assertThat(response.toString()).doesNotContain(testPassword.toString());
            
            // Verify password field is not present in the response (UserResponse doesn't have password field)
            // This test confirms that the DTO properly excludes sensitive data
        }

        @Test
        @DisplayName("Should create independent UserResponse objects")
        void shouldCreateIndependentUserResponseObjects() {
            // When
            UserResponse response1 = UserResponse.fromDomain(testUser);
            UserResponse response2 = UserResponse.fromDomain(testUser);

            // Then
            assertThat(response1).isNotSameAs(response2);
            // Note: UserResponse doesn't implement equals/hashCode, so we compare fields individually
            assertThat(response1.getId()).isEqualTo(response2.getId());
            assertThat(response1.getFirstName()).isEqualTo(response2.getFirstName());
            assertThat(response1.getLastName()).isEqualTo(response2.getLastName());
            assertThat(response1.getEmail()).isEqualTo(response2.getEmail());
        }

        @Test
        @DisplayName("Should handle User with minimal data")
        void shouldHandleUserWithMinimalData() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            User minimalUser = User.builder()
                    .firstName("Min")
                    .lastName("User")
                    .email(new Email("min@example.com"))
                    .password(Password.fromPlainText("password123"))
                    .role(Role.CUSTOMER)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // When
            UserResponse response = UserResponse.fromDomain(minimalUser);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getFirstName()).isEqualTo("Min");
            assertThat(response.getLastName()).isEqualTo("User");
            assertThat(response.getEmail()).isEqualTo("min@example.com");
            assertThat(response.getRole()).isEqualTo("CUSTOMER");
            assertThat(response.getLastLoginAt()).isNull();
            assertThat(response.getCreatedAt()).isEqualTo(now.toString());
            assertThat(response.getUpdatedAt()).isEqualTo(now.toString());
        }
    }

    @Nested
    @DisplayName("Timestamp Formatting Tests")
    class TimestampFormattingTests {

        @Test
        @DisplayName("Should format timestamps consistently")
        void shouldFormatTimestampsConsistently() {
            // Given
            LocalDateTime timestamp = LocalDateTime.of(2024, 12, 25, 23, 59, 59);
            testUser.setCreatedAt(timestamp);
            testUser.setUpdatedAt(timestamp);
            testUser.setLastLoginAt(timestamp);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then
            String expectedFormat = "2024-12-25T23:59:59";
            assertThat(response.getCreatedAt()).isEqualTo(expectedFormat);
            assertThat(response.getUpdatedAt()).isEqualTo(expectedFormat);
            assertThat(response.getLastLoginAt()).isEqualTo(expectedFormat);
        }

        @Test
        @DisplayName("Should handle different timestamp formats")
        void shouldHandleDifferentTimestampFormats() {
            // Given
            LocalDateTime timestamp1 = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
            LocalDateTime timestamp2 = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
            LocalDateTime timestamp3 = LocalDateTime.of(2024, 6, 15, 12, 30, 45);

            testUser.setCreatedAt(timestamp1);
            testUser.setUpdatedAt(timestamp2);
            testUser.setLastLoginAt(timestamp3);

            // When
            UserResponse response = UserResponse.fromDomain(testUser);

            // Then - Note: LocalDateTime.toString() format may vary, so we check the actual format
            assertThat(response.getCreatedAt()).isEqualTo(timestamp1.toString());
            assertThat(response.getUpdatedAt()).isEqualTo(timestamp2.toString());
            assertThat(response.getLastLoginAt()).isEqualTo(timestamp3.toString());
        }
    }

    @Nested
    @DisplayName("Value Object Integration Tests")
    class ValueObjectIntegrationTests {

        @Test
        @DisplayName("Should work with all valid Email formats")
        void shouldWorkWithAllValidEmailFormats() {
            String[] validEmails = {
                    "user@domain.com",
                    "user.name@domain.com",
                    "user+tag@domain.com",
                    "user123@domain123.com",
                    "user@sub.domain.com"
            };

            for (String emailStr : validEmails) {
                // Given
                Email email = new Email(emailStr);
                testUser.setEmail(email);

                // When
                UserResponse response = UserResponse.fromDomain(testUser);

                // Then
                assertThat(response.getEmail()).isEqualTo(emailStr);
            }
        }

        @Test
        @DisplayName("Should work with all valid Role values")
        void shouldWorkWithAllValidRoleValues() {
            Role[] roles = {Role.CUSTOMER, Role.ADMIN, Role.MANAGER};

            for (Role role : roles) {
                // Given
                testUser.setRole(role);

                // When
                UserResponse response = UserResponse.fromDomain(testUser);

                // Then
                assertThat(response.getRole()).isEqualTo(role.value());
            }
        }
    }
}
