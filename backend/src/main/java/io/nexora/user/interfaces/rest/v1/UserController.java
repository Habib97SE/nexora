package io.nexora.user.interfaces.rest.v1;

import io.nexora.user.application.UserApplicationService;
import io.nexora.user.domain.User;
import io.nexora.user.interfaces.rest.v1.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * REST Controller for User operations.
 * 
 * This controller provides HTTP endpoints for User use cases, following
 * RESTful principles and serving as the interface layer between external
 * clients and the application layer.
 * 
 * Responsibilities:
 * - Handle HTTP requests and responses
 * - Validate input data and parameters
 * - Transform between HTTP and domain representations
 * - Handle HTTP-specific concerns (status codes, headers)
 * - Provide RESTful API design
 * - Error handling and response formatting
 * 
 * Design Principles Applied:
 * - Single Responsibility: Focuses solely on HTTP request/response handling
 * - Open/Closed: Extensible for new endpoints without modification
 * - Dependency Inversion: Depends on abstractions (application service)
 * - Domain-Driven Design: Separates interface concerns from business logic
 * - RESTful Design: Follows REST conventions and HTTP semantics
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserApplicationService userApplicationService;

    // ==================== USER CRUD OPERATIONS ====================

    /**
     * Registers a new user.
     * 
     * @param request The user registration request
     * @return The registered user with HTTP 201 status
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        
        try {
            UserApplicationService.RegisterUserCommand command = 
                    new UserApplicationService.RegisterUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            User user = userApplicationService.registerUser(command);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully registered user with ID: {}", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to register user: {}", request.getEmail(), e);
            throw new UserControllerException("Failed to register user: " + e.getMessage(), e);
        }
    }

    /**
     * Authenticates a user.
     * 
     * @param request The authentication request
     * @return The authenticated user with HTTP 200 status
     */
    @PostMapping("/authenticate")
    public ResponseEntity<UserResponse> authenticateUser(@Valid @RequestBody AuthenticateUserRequest request) {
        log.info("Authenticating user: {}", request.getEmail());
        
        try {
            UserApplicationService.AuthenticateUserCommand command = 
                    new UserApplicationService.AuthenticateUserCommand(
                            request.getEmail(),
                            request.getPassword()
                    );
            User user = userApplicationService.authenticateUser(command);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully authenticated user: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", request.getEmail(), e);
            throw new UserControllerException("Failed to authenticate user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user by ID.
     * 
     * @param userId The user ID
     * @return The user with HTTP 200 status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        log.debug("Retrieving user with ID: {}", userId);
        
        try {
            User user = userApplicationService.findUserById(userId);
            UserResponse response = UserResponse.fromDomain(user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve user with ID: {}", userId, e);
            throw new UserControllerException("Failed to retrieve user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user.
     * 
     * @param userId The user ID
     * @param request The user update request
     * @param currentUserId The ID of the user making the update
     * @return The updated user with HTTP 200 status
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("X-Current-User-Id") UUID currentUserId) {
        log.info("Updating user with ID: {}", userId);
        
        try {
            UserApplicationService.UpdateUserCommand command = 
                    new UserApplicationService.UpdateUserCommand(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole()
                    );
            
            User user = userApplicationService.updateUser(userId, command, currentUserId);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully updated user with ID: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            throw new UserControllerException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a user.
     * 
     * @param userId The user ID
     * @param currentUserId The ID of the user making the deactivation
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID userId,
            @RequestHeader("X-Current-User-Id") UUID currentUserId) {
        log.info("Deactivating user with ID: {}", userId);
        
        try {
            userApplicationService.deactivateUser(userId, currentUserId);
            
            log.info("Successfully deactivated user with ID: {}", userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Failed to deactivate user with ID: {}", userId, e);
            throw new UserControllerException("Failed to deactivate user: " + e.getMessage(), e);
        }
    }

    // ==================== USER QUERY OPERATIONS ====================

    /**
     * Retrieves all users with pagination.
     * 
     * @param pageable Pagination parameters
     * @return A page of users with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.debug("Retrieving all users with pagination: {}", pageable);
        
        try {
            Page<User> users = userApplicationService.findAllUsers(pageable);
            Page<UserResponse> response = users.map(UserResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve all users", e);
            throw new UserControllerException("Failed to retrieve users: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves users by role with pagination.
     * 
     * @param role The role
     * @param pageable Pagination parameters
     * @return A page of users with HTTP 200 status
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(
            @PathVariable String role,
            Pageable pageable) {
        log.debug("Retrieving users by role: {} with pagination: {}", role, pageable);
        
        try {
            Page<User> users = userApplicationService.findUsersByRole(role, pageable);
            Page<UserResponse> response = users.map(UserResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve users by role: {}", role, e);
            throw new UserControllerException("Failed to retrieve users by role: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves active users with pagination.
     * 
     * @param pageable Pagination parameters
     * @return A page of active users with HTTP 200 status
     */
    @GetMapping("/active")
    public ResponseEntity<Page<UserResponse>> getActiveUsers(Pageable pageable) {
        log.debug("Retrieving active users with pagination: {}", pageable);
        
        try {
            Page<User> users = userApplicationService.findActiveUsers(pageable);
            Page<UserResponse> response = users.map(UserResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to retrieve active users", e);
            throw new UserControllerException("Failed to retrieve active users: " + e.getMessage(), e);
        }
    }

    /**
     * Searches users by name with pagination.
     * 
     * @param name The search name
     * @param pageable Pagination parameters
     * @return A page of users with HTTP 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsersByName(
            @RequestParam String name,
            Pageable pageable) {
        log.debug("Searching users by name: {} with pagination: {}", name, pageable);
        
        try {
            Page<User> users = userApplicationService.searchUsersByName(name, pageable);
            Page<UserResponse> response = users.map(UserResponse::fromDomain);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to search users by name: {}", name, e);
            throw new UserControllerException("Failed to search users: " + e.getMessage(), e);
        }
    }

    // ==================== USER MANAGEMENT OPERATIONS ====================

    /**
     * Changes user password.
     * 
     * @param userId The user ID
     * @param request The password change request
     * @return The updated user with HTTP 200 status
     */
    @PatchMapping("/{userId}/password")
    public ResponseEntity<UserResponse> changePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Changing password for user ID: {}", userId);
        
        try {
            UserApplicationService.ChangePasswordCommand command = 
                    new UserApplicationService.ChangePasswordCommand(
                            request.getCurrentPassword(),
                            request.getNewPassword()
                    );
            
            User user = userApplicationService.changePassword(userId, command);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully changed password for user ID: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to change password for user ID: {}", userId, e);
            throw new UserControllerException("Failed to change password: " + e.getMessage(), e);
        }
    }

    /**
     * Changes user role.
     * 
     * @param userId The user ID
     * @param request The role change request
     * @param currentUserId The ID of the user making the change
     * @return The updated user with HTTP 200 status
     */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponse> changeRole(
            @PathVariable UUID userId,
            @Valid @RequestBody ChangeRoleRequest request,
            @RequestHeader("X-Current-User-Id") UUID currentUserId) {
        log.info("Changing role for user ID: {} to role: {}", userId, request.getRole());
        
        try {
            UserApplicationService.ChangeRoleCommand command = 
                    new UserApplicationService.ChangeRoleCommand(request.getRole());
            
            User user = userApplicationService.changeRole(userId, command, currentUserId);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully changed role for user ID: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to change role for user ID: {}", userId, e);
            throw new UserControllerException("Failed to change role: " + e.getMessage(), e);
        }
    }

    /**
     * Activates a user.
     * 
     * @param userId The user ID
     * @return The activated user with HTTP 200 status
     */
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable UUID userId) {
        log.info("Activating user with ID: {}", userId);
        
        try {
            User user = userApplicationService.activateUser(userId);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully activated user with ID: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to activate user with ID: {}", userId, e);
            throw new UserControllerException("Failed to activate user: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies user email.
     * 
     * @param userId The user ID
     * @return The user with verified email and HTTP 200 status
     */
    @PatchMapping("/{userId}/verify-email")
    public ResponseEntity<UserResponse> verifyEmail(@PathVariable UUID userId) {
        log.info("Verifying email for user ID: {}", userId);
        
        try {
            User user = userApplicationService.verifyEmail(userId);
            UserResponse response = UserResponse.fromDomain(user);
            
            log.info("Successfully verified email for user ID: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to verify email for user ID: {}", userId, e);
            throw new UserControllerException("Failed to verify email: " + e.getMessage(), e);
        }
    }

    // ==================== REPORTING OPERATIONS ====================

    /**
     * Gets user statistics.
     * 
     * @return User statistics with HTTP 200 status
     */
    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
        log.debug("Getting user statistics");
        
        try {
            UserApplicationService.UserStatistics statistics = 
                    userApplicationService.getUserStatistics();
            UserStatisticsResponse response = UserStatisticsResponse.fromDomain(statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get user statistics", e);
            throw new UserControllerException("Failed to get user statistics: " + e.getMessage(), e);
        }
    }


    /**
     * Controller-specific exception for user operations.
     */
    public static class UserControllerException extends RuntimeException {
        public UserControllerException(String message) {
            super(message);
        }

        public UserControllerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
