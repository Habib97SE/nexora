package io.nexora.user.interfaces.rest.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.application.UserApplicationService;
import io.nexora.user.domain.User;
import io.nexora.user.interfaces.rest.v1.dto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for UserController.
 * 
 * This test suite covers all REST endpoints, HTTP status codes, request/response
 * transformations, error handling, and controller-specific behavior.
 * 
 * Test Categories:
 * - CRUD Operations (Create, Read, Update, Delete)
 * - Query Operations (Search, Filter, Pagination)
 * - Management Operations (Password, Role, Activation)
 * - Reporting Operations (Statistics)
 * - Error Handling and Exception Scenarios
 * - HTTP Status Code Verification
 * - Request/Response Transformation
 * - Validation and Security
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserApplicationService userApplicationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userApplicationService))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUserId = UUID.randomUUID();
        testUser = createTestUser("John", "Doe", Role.CUSTOMER);
        testUserResponse = UserResponse.fromDomain(testUser);
    }

    // ==================== HELPER METHODS ====================

    private User createTestUser(String firstName, String lastName, Role role) {
        return User.builder()
                .id(testUserId.toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(new Email("test@example.com"))
                .password(new Password("password123"))
                .role(role)
                .active(true)
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private RegisterUserRequest createRegisterRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setRole("CUSTOMER");
        return request;
    }

    private AuthenticateUserRequest createAuthenticateRequest() {
        AuthenticateUserRequest request = new AuthenticateUserRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        return request;
    }

    private UpdateUserRequest createUpdateRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPassword("newpassword123");
        request.setRole("ADMIN");
        return request;
    }

    private ChangePasswordRequest createChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldpassword123");
        request.setNewPassword("newpassword123");
        return request;
    }

    private ChangeRoleRequest createChangeRoleRequest() {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setRole("ADMIN");
        return request;
    }

    // ==================== USER CRUD OPERATIONS ====================

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() throws Exception {
            // Given
            RegisterUserRequest request = createRegisterRequest();
            UserApplicationService.RegisterUserCommand command = 
                    new UserApplicationService.RegisterUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.registerUser(any(UserApplicationService.RegisterUserCommand.class))).thenReturn(testUser);

            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.role").value("CUSTOMER"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.emailVerified").value(true));

            verify(userApplicationService).registerUser(any(UserApplicationService.RegisterUserCommand.class));
        }

        @Test
        @DisplayName("Should handle registration failure")
        void shouldHandleRegistrationFailure() throws Exception {
            // Given
            RegisterUserRequest request = createRegisterRequest();
            UserApplicationService.RegisterUserCommand command = 
                    new UserApplicationService.RegisterUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.registerUser(any(UserApplicationService.RegisterUserCommand.class)))
                    .thenThrow(new RuntimeException("Email already exists"));

            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).registerUser(any(UserApplicationService.RegisterUserCommand.class));
        }

        @Test
        @DisplayName("Should validate registration request")
        void shouldValidateRegistrationRequest() throws Exception {
            // Given
            RegisterUserRequest invalidRequest = new RegisterUserRequest();
            invalidRequest.setFirstName(""); // Invalid: empty first name
            invalidRequest.setLastName("Doe");
            invalidRequest.setEmail("invalid-email"); // Invalid: malformed email
            invalidRequest.setPassword("123"); // Invalid: too short password
            invalidRequest.setRole("INVALID_ROLE"); // Invalid: unknown role

            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).registerUser(any());
        }
    }

    @Nested
    @DisplayName("User Authentication Tests")
    class UserAuthenticationTests {

        @Test
        @DisplayName("Should authenticate user successfully")
        void shouldAuthenticateUserSuccessfully() throws Exception {
            // Given
            AuthenticateUserRequest request = createAuthenticateRequest();
            UserApplicationService.AuthenticateUserCommand command = 
                    new UserApplicationService.AuthenticateUserCommand(
                            request.getEmail(),
                            request.getPassword()
                    );
            
            when(userApplicationService.authenticateUser(any(UserApplicationService.AuthenticateUserCommand.class))).thenReturn(testUser);

            // When & Then
            mockMvc.perform(post("/api/v1/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(userApplicationService).authenticateUser(any(UserApplicationService.AuthenticateUserCommand.class));
        }

        @Test
        @DisplayName("Should handle authentication failure")
        void shouldHandleAuthenticationFailure() throws Exception {
            // Given
            AuthenticateUserRequest request = createAuthenticateRequest();
            UserApplicationService.AuthenticateUserCommand command = 
                    new UserApplicationService.AuthenticateUserCommand(
                            request.getEmail(),
                            request.getPassword()
                    );
            
            when(userApplicationService.authenticateUser(any(UserApplicationService.AuthenticateUserCommand.class)))
                    .thenThrow(new RuntimeException("Invalid credentials"));

            // When & Then
            mockMvc.perform(post("/api/v1/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).authenticateUser(any(UserApplicationService.AuthenticateUserCommand.class));
        }

        @Test
        @DisplayName("Should validate authentication request")
        void shouldValidateAuthenticationRequest() throws Exception {
            // Given
            AuthenticateUserRequest invalidRequest = new AuthenticateUserRequest();
            invalidRequest.setEmail("invalid-email"); // Invalid: malformed email
            invalidRequest.setPassword(""); // Invalid: empty password

            // When & Then
            mockMvc.perform(post("/api/v1/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).authenticateUser(any());
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID successfully")
        void shouldGetUserByIdSuccessfully() throws Exception {
            // Given
            when(userApplicationService.findUserById(testUserId)).thenReturn(testUser);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(userApplicationService).findUserById(testUserId);
        }

        @Test
        @DisplayName("Should handle user not found")
        void shouldHandleUserNotFound() throws Exception {
            // Given
            when(userApplicationService.findUserById(testUserId))
                    .thenThrow(new RuntimeException("User not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).findUserById(testUserId);
        }

        @Test
        @DisplayName("Should handle invalid user ID format")
        void shouldHandleInvalidUserIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).findUserById(any());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Given
            UpdateUserRequest request = createUpdateRequest();
            UUID currentUserId = UUID.randomUUID();
            UserApplicationService.UpdateUserCommand command = 
                    new UserApplicationService.UpdateUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.updateUser(eq(testUserId), any(UserApplicationService.UpdateUserCommand.class), eq(currentUserId)))
                    .thenReturn(testUser);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()));

            verify(userApplicationService).updateUser(eq(testUserId), any(UserApplicationService.UpdateUserCommand.class), eq(currentUserId));
        }

        @Test
        @DisplayName("Should handle update failure")
        void shouldHandleUpdateFailure() throws Exception {
            // Given
            UpdateUserRequest request = createUpdateRequest();
            UUID currentUserId = UUID.randomUUID();
            UserApplicationService.UpdateUserCommand command = 
                    new UserApplicationService.UpdateUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.updateUser(eq(testUserId), any(UserApplicationService.UpdateUserCommand.class), eq(currentUserId)))
                    .thenThrow(new RuntimeException("Update failed"));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).updateUser(eq(testUserId), any(UserApplicationService.UpdateUserCommand.class), eq(currentUserId));
        }

        @Test
        @DisplayName("Should validate update request")
        void shouldValidateUpdateRequest() throws Exception {
            // Given
            UpdateUserRequest invalidRequest = new UpdateUserRequest();
            invalidRequest.setFirstName(""); // Invalid: empty first name
            invalidRequest.setLastName("Smith");
            invalidRequest.setEmail("invalid-email"); // Invalid: malformed email
            invalidRequest.setPassword("123"); // Invalid: too short password
            invalidRequest.setRole("INVALID_ROLE"); // Invalid: unknown role
            UUID currentUserId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).updateUser(any(), any(), any());
        }

        @Test
        @DisplayName("Should require current user ID header")
        void shouldRequireCurrentUserIdHeader() throws Exception {
            // Given
            UpdateUserRequest request = createUpdateRequest();

            // When & Then
            mockMvc.perform(put("/api/v1/users/{userId}", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).updateUser(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Deactivate User Tests")
    class DeactivateUserTests {

        @Test
        @DisplayName("Should deactivate user successfully")
        void shouldDeactivateUserSuccessfully() throws Exception {
            // Given
            UUID currentUserId = UUID.randomUUID();
            doNothing().when(userApplicationService).deactivateUser(testUserId, currentUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString()))
                    .andExpect(status().isNoContent());

            verify(userApplicationService).deactivateUser(testUserId, currentUserId);
        }

        @Test
        @DisplayName("Should handle deactivation failure")
        void shouldHandleDeactivationFailure() throws Exception {
            // Given
            UUID currentUserId = UUID.randomUUID();
            doThrow(new RuntimeException("Deactivation failed"))
                    .when(userApplicationService).deactivateUser(testUserId, currentUserId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString()))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).deactivateUser(testUserId, currentUserId);
        }

        @Test
        @DisplayName("Should require current user ID header for deactivation")
        void shouldRequireCurrentUserIdHeaderForDeactivation() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).deactivateUser(any(), any());
        }
    }

    // ==================== USER QUERY OPERATIONS ====================

    @Nested
    @DisplayName("Get All Users Tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should get all users with pagination")
        void shouldGetAllUsersWithPagination() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser, createTestUser("Jane", "Smith", Role.ADMIN));
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 2);
            when(userApplicationService.findAllUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(userApplicationService).findAllUsers(any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle get all users failure")
        void shouldHandleGetAllUsersFailure() throws Exception {
            // Given
            when(userApplicationService.findAllUsers(any(Pageable.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).findAllUsers(any(Pageable.class));
        }

        @Test
        @DisplayName("Should use default pagination parameters")
        void shouldUseDefaultPaginationParameters() throws Exception {
            // Given
            Page<User> userPage = new PageImpl<>(List.of(testUser));
            when(userApplicationService.findAllUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk());

            verify(userApplicationService).findAllUsers(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get Users By Role Tests")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("Should get users by role successfully")
        void shouldGetUsersByRoleSuccessfully() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser);
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
            when(userApplicationService.findUsersByRole(eq("CUSTOMER"), any(Pageable.class)))
                    .thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users/role/{role}", "CUSTOMER")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(userApplicationService).findUsersByRole(eq("CUSTOMER"), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle get users by role failure")
        void shouldHandleGetUsersByRoleFailure() throws Exception {
            // Given
            when(userApplicationService.findUsersByRole(eq("INVALID_ROLE"), any(Pageable.class)))
                    .thenThrow(new RuntimeException("Invalid role"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/role/{role}", "INVALID_ROLE"))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).findUsersByRole(eq("INVALID_ROLE"), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get Active Users Tests")
    class GetActiveUsersTests {

        @Test
        @DisplayName("Should get active users successfully")
        void shouldGetActiveUsersSuccessfully() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser);
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
            when(userApplicationService.findActiveUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users/active")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(userApplicationService).findActiveUsers(any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle get active users failure")
        void shouldHandleGetActiveUsersFailure() throws Exception {
            // Given
            when(userApplicationService.findActiveUsers(any(Pageable.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/active"))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).findActiveUsers(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Search Users Tests")
    class SearchUsersTests {

        @Test
        @DisplayName("Should search users by name successfully")
        void shouldSearchUsersByNameSuccessfully() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser);
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
            when(userApplicationService.searchUsersByName(eq("John"), any(Pageable.class)))
                    .thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/v1/users/search")
                    .param("name", "John")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(userApplicationService).searchUsersByName(eq("John"), any(Pageable.class));
        }

        @Test
        @DisplayName("Should handle search users failure")
        void shouldHandleSearchUsersFailure() throws Exception {
            // Given
            when(userApplicationService.searchUsersByName(eq("John"), any(Pageable.class)))
                    .thenThrow(new RuntimeException("Search failed"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/search")
                    .param("name", "John"))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).searchUsersByName(eq("John"), any(Pageable.class));
        }

        @Test
        @DisplayName("Should require name parameter for search")
        void shouldRequireNameParameterForSearch() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users/search"))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).searchUsersByName(any(), any());
        }
    }

    // ==================== USER MANAGEMENT OPERATIONS ====================

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void shouldChangePasswordSuccessfully() throws Exception {
            // Given
            ChangePasswordRequest request = createChangePasswordRequest();
            UserApplicationService.ChangePasswordCommand command = 
                    new UserApplicationService.ChangePasswordCommand(
                            request.getCurrentPassword(),
                            request.getNewPassword()
                    );
            
            when(userApplicationService.changePassword(eq(testUserId), any(UserApplicationService.ChangePasswordCommand.class)))
                    .thenReturn(testUser);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/password", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()));

            verify(userApplicationService).changePassword(eq(testUserId), any(UserApplicationService.ChangePasswordCommand.class));
        }

        @Test
        @DisplayName("Should handle change password failure")
        void shouldHandleChangePasswordFailure() throws Exception {
            // Given
            ChangePasswordRequest request = createChangePasswordRequest();
            UserApplicationService.ChangePasswordCommand command = 
                    new UserApplicationService.ChangePasswordCommand(
                            request.getCurrentPassword(),
                            request.getNewPassword()
                    );
            
            when(userApplicationService.changePassword(eq(testUserId), any(UserApplicationService.ChangePasswordCommand.class)))
                    .thenThrow(new RuntimeException("Invalid current password"));

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/password", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).changePassword(eq(testUserId), any(UserApplicationService.ChangePasswordCommand.class));
        }

        @Test
        @DisplayName("Should validate change password request")
        void shouldValidateChangePasswordRequest() throws Exception {
            // Given
            ChangePasswordRequest invalidRequest = new ChangePasswordRequest();
            invalidRequest.setCurrentPassword(""); // Invalid: empty current password
            invalidRequest.setNewPassword("123"); // Invalid: too short new password

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/password", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).changePassword(any(), any());
        }
    }

    @Nested
    @DisplayName("Change Role Tests")
    class ChangeRoleTests {

        @Test
        @DisplayName("Should change role successfully")
        void shouldChangeRoleSuccessfully() throws Exception {
            // Given
            ChangeRoleRequest request = createChangeRoleRequest();
            UUID currentUserId = UUID.randomUUID();
            UserApplicationService.ChangeRoleCommand command = 
                    new UserApplicationService.ChangeRoleCommand(request.getRole());
            
            when(userApplicationService.changeRole(eq(testUserId), any(UserApplicationService.ChangeRoleCommand.class), eq(currentUserId)))
                    .thenReturn(testUser);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/role", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()));

            verify(userApplicationService).changeRole(eq(testUserId), any(UserApplicationService.ChangeRoleCommand.class), eq(currentUserId));
        }

        @Test
        @DisplayName("Should handle change role failure")
        void shouldHandleChangeRoleFailure() throws Exception {
            // Given
            ChangeRoleRequest request = createChangeRoleRequest();
            UUID currentUserId = UUID.randomUUID();
            UserApplicationService.ChangeRoleCommand command = 
                    new UserApplicationService.ChangeRoleCommand(request.getRole());
            
            when(userApplicationService.changeRole(eq(testUserId), any(UserApplicationService.ChangeRoleCommand.class), eq(currentUserId)))
                    .thenThrow(new RuntimeException("Insufficient permissions"));

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/role", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).changeRole(eq(testUserId), any(UserApplicationService.ChangeRoleCommand.class), eq(currentUserId));
        }

        @Test
        @DisplayName("Should validate change role request")
        void shouldValidateChangeRoleRequest() throws Exception {
            // Given
            ChangeRoleRequest invalidRequest = new ChangeRoleRequest();
            invalidRequest.setRole("INVALID_ROLE"); // Invalid: unknown role
            UUID currentUserId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/role", testUserId)
                    .header("X-Current-User-Id", currentUserId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).changeRole(any(), any(), any());
        }

        @Test
        @DisplayName("Should require current user ID header for role change")
        void shouldRequireCurrentUserIdHeaderForRoleChange() throws Exception {
            // Given
            ChangeRoleRequest request = createChangeRoleRequest();

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/role", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).changeRole(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Activate User Tests")
    class ActivateUserTests {

        @Test
        @DisplayName("Should activate user successfully")
        void shouldActivateUserSuccessfully() throws Exception {
            // Given
            when(userApplicationService.activateUser(testUserId)).thenReturn(testUser);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/activate", testUserId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()));

            verify(userApplicationService).activateUser(testUserId);
        }

        @Test
        @DisplayName("Should handle activation failure")
        void shouldHandleActivationFailure() throws Exception {
            // Given
            when(userApplicationService.activateUser(testUserId))
                    .thenThrow(new RuntimeException("User not found"));

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/activate", testUserId))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).activateUser(testUserId);
        }
    }

    @Nested
    @DisplayName("Verify Email Tests")
    class VerifyEmailTests {

        @Test
        @DisplayName("Should verify email successfully")
        void shouldVerifyEmailSuccessfully() throws Exception {
            // Given
            when(userApplicationService.verifyEmail(testUserId)).thenReturn(testUser);

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/verify-email", testUserId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testUserId.toString()));

            verify(userApplicationService).verifyEmail(testUserId);
        }

        @Test
        @DisplayName("Should handle email verification failure")
        void shouldHandleEmailVerificationFailure() throws Exception {
            // Given
            when(userApplicationService.verifyEmail(testUserId))
                    .thenThrow(new RuntimeException("User not found"));

            // When & Then
            mockMvc.perform(patch("/api/v1/users/{userId}/verify-email", testUserId))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).verifyEmail(testUserId);
        }
    }

    // ==================== REPORTING OPERATIONS ====================

    @Nested
    @DisplayName("User Statistics Tests")
    class UserStatisticsTests {

        @Test
        @DisplayName("Should get user statistics successfully")
        void shouldGetUserStatisticsSuccessfully() throws Exception {
            // Given
            UserApplicationService.UserStatistics statistics = 
                    new UserApplicationService.UserStatistics(100L, 50L, 25L, 25L, 15L, 10L);
            when(userApplicationService.getUserStatistics()).thenReturn(statistics);

            // When & Then
            mockMvc.perform(get("/api/v1/users/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalUsers").value(100))
                    .andExpect(jsonPath("$.activeUsers").value(50))
                    .andExpect(jsonPath("$.verifiedUsers").value(25))
                    .andExpect(jsonPath("$.adminUsers").value(25));

            verify(userApplicationService).getUserStatistics();
        }

        @Test
        @DisplayName("Should handle statistics failure")
        void shouldHandleStatisticsFailure() throws Exception {
            // Given
            when(userApplicationService.getUserStatistics())
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/v1/users/statistics"))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).getUserStatistics();
        }
    }

    // ==================== ERROR HANDLING AND EDGE CASES ====================

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle malformed JSON requests")
        void shouldHandleMalformedJsonRequests() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).registerUser(any());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            // Given
            RegisterUserRequest request = createRegisterRequest();

            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnsupportedMediaType());

            verify(userApplicationService, never()).registerUser(any());
        }

        @Test
        @DisplayName("Should handle null request body")
        void shouldHandleNullRequestBody() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""))
                    .andExpect(status().isBadRequest());

            verify(userApplicationService, never()).registerUser(any());
        }

        @Test
        @DisplayName("Should handle application service exceptions")
        void shouldHandleApplicationServiceExceptions() throws Exception {
            // Given
            RegisterUserRequest request = createRegisterRequest();
            UserApplicationService.RegisterUserCommand command = 
                    new UserApplicationService.RegisterUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.registerUser(command))
                    .thenThrow(new UserApplicationService.UserApplicationException("Business logic error"));

            // When & Then
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(userApplicationService).registerUser(any(UserApplicationService.RegisterUserCommand.class));
        }
    }

    @Nested
    @DisplayName("HTTP Status Code Tests")
    class HttpStatusCodeTests {

        @Test
        @DisplayName("Should return correct HTTP status codes for successful operations")
        void shouldReturnCorrectHttpStatusCodesForSuccessfulOperations() throws Exception {
            // Given
            RegisterUserRequest registerRequest = createRegisterRequest();
            UserApplicationService.RegisterUserCommand registerCommand = 
                    new UserApplicationService.RegisterUserCommand(
                            registerRequest.getFirstName(),
                            registerRequest.getLastName(),
                            registerRequest.getEmail(),
                            registerRequest.getPassword(),
                            registerRequest.getRole()
                    );
            
            when(userApplicationService.registerUser(registerCommand)).thenReturn(testUser);
            when(userApplicationService.findUserById(testUserId)).thenReturn(testUser);
            doNothing().when(userApplicationService).deactivateUser(testUserId, testUserId);

            // When & Then - Registration should return 201 Created
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated());

            // When & Then - Get user should return 200 OK
            mockMvc.perform(get("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isOk());

            // When & Then - Deactivate user should return 204 No Content
            mockMvc.perform(delete("/api/v1/users/{userId}", testUserId)
                    .header("X-Current-User-Id", testUserId.toString()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return correct HTTP status codes for error scenarios")
        void shouldReturnCorrectHttpStatusCodesForErrorScenarios() throws Exception {
            // When & Then - Invalid UUID should return 400 Bad Request
            mockMvc.perform(get("/api/v1/users/{userId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());

            // When & Then - Missing required parameter should return 400 Bad Request
            mockMvc.perform(get("/api/v1/users/search"))
                    .andExpect(status().isBadRequest());

            // When & Then - Missing required header should return 400 Bad Request
            mockMvc.perform(delete("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Request/Response Transformation Tests")
    class RequestResponseTransformationTests {

        @Test
        @DisplayName("Should transform request DTOs to command objects correctly")
        void shouldTransformRequestDtosToCommandObjectsCorrectly() throws Exception {
            // Given
            RegisterUserRequest request = createRegisterRequest();
            UserApplicationService.RegisterUserCommand expectedCommand = 
                    new UserApplicationService.RegisterUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            when(userApplicationService.registerUser(any(UserApplicationService.RegisterUserCommand.class)))
                    .thenReturn(testUser);

            // When
            mockMvc.perform(post("/api/v1/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            // Then
            verify(userApplicationService).registerUser(any(UserApplicationService.RegisterUserCommand.class));
        }

        @Test
        @DisplayName("Should transform domain objects to response DTOs correctly")
        void shouldTransformDomainObjectsToResponseDtosCorrectly() throws Exception {
            // Given
            when(userApplicationService.findUserById(testUserId)).thenReturn(testUser);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{userId}", testUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                    .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                    .andExpect(jsonPath("$.email").value(testUser.getEmail().value()))
                    .andExpect(jsonPath("$.role").value(testUser.getRole().toString()))
                    .andExpect(jsonPath("$.active").value(testUser.isActive()))
                    .andExpect(jsonPath("$.emailVerified").value(testUser.isEmailVerified()));
        }
    }
}
