package io.nexora.user.application;

import io.nexora.shared.valueobject.Password;
import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Role;
import io.nexora.user.domain.User;
import io.nexora.user.domain.UserRepository;
import io.nexora.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for User use cases.
 * 
 * This service orchestrates application workflows and coordinates between
 * domain services, repositories, and external systems. It handles the
 * application layer concerns while delegating domain logic to domain services.
 * 
 * Responsibilities:
 * - Orchestrate complex use cases and workflows
 * - Coordinate between domain services and repositories
 * - Handle application-level validation and error handling
 * - Manage transactions and cross-cutting concerns
 * - Provide a clean API for the presentation layer
 * 
 * Design Principles Applied:
 * - Single Responsibility: Focuses on application workflow orchestration
 * - Open/Closed: Extensible for new use cases without modification
 * - Dependency Inversion: Depends on abstractions (domain services, repositories)
 * - Domain-Driven Design: Separates application concerns from domain logic
 * - Command Query Separation: Clear separation between commands and queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;

    // ==================== COMMAND OPERATIONS ====================

    /**
     * Registers a new user through the application workflow.
     * 
     * This method orchestrates the user registration process, including
     * validation, domain service coordination, and result handling.
     * 
     * @param registerUserCommand The user registration command
     * @return The registered user
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public User registerUser(RegisterUserCommand registerUserCommand) {
        log.info("Registering new user: {}", registerUserCommand.getEmail());
        
        try {
            // Convert command to domain object
            User user = convertToUser(registerUserCommand);
            
            // Delegate to domain service for business logic
            User registeredUser = userDomainService.registerUser(user);
            
            log.info("Successfully registered user with ID: {}", registeredUser.getId());
            return registeredUser;
            
        } catch (Exception e) {
            log.error("Failed to register user: {}", registerUserCommand.getEmail(), e);
            throw new UserApplicationException("Failed to register user: " + e.getMessage(), e);
        }
    }

    /**
     * Authenticates a user through the application workflow.
     * 
     * @param authenticateUserCommand The authentication command
     * @return The authenticated user
     * @throws IllegalArgumentException if authentication fails
     */
    @Transactional
    public User authenticateUser(AuthenticateUserCommand authenticateUserCommand) {
        log.info("Authenticating user: {}", authenticateUserCommand.getEmail());
        
        try {
            // Convert command to domain objects
            Email email = new Email(authenticateUserCommand.getEmail());
            Password password = Password.fromPlainText(authenticateUserCommand.getPassword());
            
            // Delegate to domain service for business logic
            User authenticatedUser = userDomainService.authenticateUser(email, password);
            
            log.info("Successfully authenticated user: {}", authenticatedUser.getEmail());
            return authenticatedUser;
            
        } catch (Exception e) {
            log.error("Failed to authenticate user: {}", authenticateUserCommand.getEmail(), e);
            throw new UserApplicationException("Failed to authenticate user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user through the application workflow.
     * 
     * @param userId The ID of the user to update
     * @param updateUserCommand The user update command
     * @param currentUserId The ID of the user making the update
     * @return The updated user
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public User updateUser(UUID userId, UpdateUserCommand updateUserCommand, UUID currentUserId) {
        log.info("Updating user with ID: {}", userId);
        
        try {
            // Get current user for authorization
            User currentUser = userDomainService.findUserById(currentUserId);
            
            // Convert command to domain object
            User updatedUser = convertToUser(updateUserCommand);
            
            // Delegate to domain service for business logic
            User result = userDomainService.updateUser(userId, updatedUser, currentUser);
            
            log.info("Successfully updated user with ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            throw new UserApplicationException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Changes user password through the application workflow.
     * 
     * @param userId The ID of the user
     * @param changePasswordCommand The password change command
     * @return The updated user
     * @throws IllegalArgumentException if password change is invalid
     */
    @Transactional
    public User changePassword(UUID userId, ChangePasswordCommand changePasswordCommand) {
        log.info("Changing password for user ID: {}", userId);
        
        try {
            // Convert command to domain objects
            Password currentPassword = Password.fromPlainText(changePasswordCommand.getCurrentPassword());
            Password newPassword = Password.fromPlainText(changePasswordCommand.getNewPassword());
            
            // Delegate to domain service for business logic
            User result = userDomainService.changePassword(userId, currentPassword, newPassword);
            
            log.info("Successfully changed password for user ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to change password for user ID: {}", userId, e);
            throw new UserApplicationException("Failed to change password: " + e.getMessage(), e);
        }
    }

    /**
     * Changes user role through the application workflow.
     * 
     * @param userId The ID of the user
     * @param changeRoleCommand The role change command
     * @param currentUserId The ID of the user making the change
     * @return The updated user
     * @throws IllegalArgumentException if role change is invalid
     */
    @Transactional
    public User changeRole(UUID userId, ChangeRoleCommand changeRoleCommand, UUID currentUserId) {
        log.info("Changing role for user ID: {} to role: {}", userId, changeRoleCommand.getRole());
        
        try {
            // Get current user for authorization
            User currentUser = userDomainService.findUserById(currentUserId);
            
            // Convert command to domain object
            Role newRole = new Role(changeRoleCommand.getRole());
            
            // Delegate to domain service for business logic
            User result = userDomainService.changeRole(userId, newRole, currentUser);
            
            log.info("Successfully changed role for user ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to change role for user ID: {}", userId, e);
            throw new UserApplicationException("Failed to change role: " + e.getMessage(), e);
        }
    }

    /**
     * Activates a user through the application workflow.
     * 
     * @param userId The ID of the user to activate
     * @return The activated user
     * @throws IllegalArgumentException if activation is invalid
     */
    @Transactional
    public User activateUser(UUID userId) {
        log.info("Activating user with ID: {}", userId);
        
        try {
            // Delegate to domain service for business logic
            User result = userDomainService.activateUser(userId);
            
            log.info("Successfully activated user with ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to activate user with ID: {}", userId, e);
            throw new UserApplicationException("Failed to activate user: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a user through the application workflow.
     * 
     * @param userId The ID of the user to deactivate
     * @param currentUserId The ID of the user making the deactivation
     * @throws IllegalArgumentException if deactivation is invalid
     */
    @Transactional
    public void deactivateUser(UUID userId, UUID currentUserId) {
        log.info("Deactivating user with ID: {}", userId);
        
        try {
            // Get current user for authorization
            User currentUser = userDomainService.findUserById(currentUserId);
            
            // Delegate to domain service for business logic
            userDomainService.deactivateUser(userId, currentUser);
            
            log.info("Successfully deactivated user with ID: {}", userId);
            
        } catch (Exception e) {
            log.error("Failed to deactivate user with ID: {}", userId, e);
            throw new UserApplicationException("Failed to deactivate user: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies user email through the application workflow.
     * 
     * @param userId The ID of the user
     * @return The user with verified email
     * @throws IllegalArgumentException if verification is invalid
     */
    @Transactional
    public User verifyEmail(UUID userId) {
        log.info("Verifying email for user ID: {}", userId);
        
        try {
            // Delegate to domain service for business logic
            User result = userDomainService.verifyEmail(userId);
            
            log.info("Successfully verified email for user ID: {}", result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to verify email for user ID: {}", userId, e);
            throw new UserApplicationException("Failed to verify email: " + e.getMessage(), e);
        }
    }

    // ==================== QUERY OPERATIONS ====================

    /**
     * Finds a user by ID.
     * 
     * @param userId The user ID
     * @return The user
     * @throws IllegalArgumentException if user doesn't exist
     */
    public User findUserById(UUID userId) {
        log.debug("Finding user with ID: {}", userId);
        
        try {
            return userDomainService.findUserById(userId);
        } catch (Exception e) {
            log.error("Failed to find user with ID: {}", userId, e);
            throw new UserApplicationException("Failed to find user: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a user by email.
     * 
     * @param email The user's email
     * @return The user
     * @throws IllegalArgumentException if user doesn't exist
     */
    public User findUserByEmail(String email) {
        log.debug("Finding user with email: {}", email);
        
        try {
            Email emailVO = new Email(email);
            return userDomainService.findUserByEmail(emailVO);
        } catch (Exception e) {
            log.error("Failed to find user with email: {}", email, e);
            throw new UserApplicationException("Failed to find user: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all users with pagination.
     * 
     * @param pageable The pagination information
     * @return A page of users
     */
    public Page<User> findAllUsers(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        
        try {
            List<User> users = userRepository.findAll(pageable.getPageNumber(), pageable.getPageSize());
            long totalElements = userRepository.count();
            
            return new PageImpl<>(users, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to find all users", e);
            throw new UserApplicationException("Failed to find all users: " + e.getMessage(), e);
        }
    }

    /**
     * Finds users by role with pagination.
     * 
     * @param role The role
     * @param pageable The pagination information
     * @return A page of users
     */
    public Page<User> findUsersByRole(String role, Pageable pageable) {
        log.debug("Finding users by role: {} with pagination: {}", role, pageable);
        
        try {
            Role roleVO = new Role(role);
            List<User> users = userRepository.findByRole(roleVO, pageable.getPageNumber(), pageable.getPageSize());
            long totalElements = userRepository.countByRole(roleVO);
            
            return new PageImpl<>(users, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to find users by role: {}", role, e);
            throw new UserApplicationException("Failed to find users by role: " + e.getMessage(), e);
        }
    }

    /**
     * Finds active users with pagination.
     * 
     * @param pageable The pagination information
     * @return A page of active users
     */
    public Page<User> findActiveUsers(Pageable pageable) {
        log.debug("Finding active users with pagination: {}", pageable);
        
        try {
            List<User> users = userRepository.findActiveUsers(pageable.getPageNumber(), pageable.getPageSize());
            long totalElements = userRepository.countActiveUsers();
            
            return new PageImpl<>(users, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to find active users", e);
            throw new UserApplicationException("Failed to find active users: " + e.getMessage(), e);
        }
    }

    /**
     * Searches users by name with pagination.
     * 
     * @param name The search name
     * @param pageable The pagination information
     * @return A page of users
     */
    public Page<User> searchUsersByName(String name, Pageable pageable) {
        log.debug("Searching users by name: {} with pagination: {}", name, pageable);
        
        try {
            List<User> users = userRepository.searchByName(name, pageable.getPageNumber(), pageable.getPageSize());
            // In a real implementation, you would have a count method for search
            long totalElements = users.size(); // This is a simplification
            
            return new PageImpl<>(users, pageable, totalElements);
            
        } catch (Exception e) {
            log.error("Failed to search users by name: {}", name, e);
            throw new UserApplicationException("Failed to search users: " + e.getMessage(), e);
        }
    }

    /**
     * Gets user statistics for reporting.
     * 
     * @return User statistics
     */
    public UserStatistics getUserStatistics() {
        log.debug("Getting user statistics");
        
        try {
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countActiveUsers();
            long inactiveUsers = userRepository.countInactiveUsers();
            long customerUsers = userRepository.countByRole(Role.CUSTOMER);
            long adminUsers = userRepository.countByRole(Role.ADMIN);
            long managerUsers = userRepository.countByRole(Role.MANAGER);
            
            return UserStatistics.builder()
                    .totalUsers(totalUsers)
                    .activeUsers(activeUsers)
                    .inactiveUsers(inactiveUsers)
                    .customerUsers(customerUsers)
                    .adminUsers(adminUsers)
                    .managerUsers(managerUsers)
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to get user statistics", e);
            throw new UserApplicationException("Failed to get user statistics: " + e.getMessage(), e);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Converts a register user command to a User domain object.
     */
    private User convertToUser(RegisterUserCommand command) {
        Email email = new Email(command.getEmail());
        Password password = Password.fromPlainText(command.getPassword());
        Role role = new Role(command.getRole());
        
        return User.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    /**
     * Converts an update user command to a User domain object.
     */
    private User convertToUser(UpdateUserCommand command) {
        Email email = new Email(command.getEmail());
        Password password = Password.fromPlainText(command.getPassword());
        Role role = new Role(command.getRole());
        
        return User.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    // ==================== COMMAND CLASSES ====================

    /**
     * Command for registering a new user.
     */
    public static class RegisterUserCommand {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String role;

        // Constructors, getters, and setters
        public RegisterUserCommand() {}

        public RegisterUserCommand(String firstName, String lastName, String email, String password, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /**
     * Command for authenticating a user.
     */
    public static class AuthenticateUserCommand {
        private String email;
        private String password;

        // Constructors, getters, and setters
        public AuthenticateUserCommand() {}

        public AuthenticateUserCommand(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * Command for updating an existing user.
     */
    public static class UpdateUserCommand {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String role;

        // Constructors, getters, and setters
        public UpdateUserCommand() {}

        public UpdateUserCommand(String firstName, String lastName, String email, String password, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.role = role;
        }

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /**
     * Command for changing user password.
     */
    public static class ChangePasswordCommand {
        private String currentPassword;
        private String newPassword;

        // Constructors, getters, and setters
        public ChangePasswordCommand() {}

        public ChangePasswordCommand(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    /**
     * Command for changing user role.
     */
    public static class ChangeRoleCommand {
        private String role;

        // Constructors, getters, and setters
        public ChangeRoleCommand() {}

        public ChangeRoleCommand(String role) {
            this.role = role;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    /**
     * User statistics for reporting.
     */
    public static class UserStatistics {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long customerUsers;
        private long adminUsers;
        private long managerUsers;

        // Constructors, getters, and setters
        public UserStatistics() {}

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers, 
                            long customerUsers, long adminUsers, long managerUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.customerUsers = customerUsers;
            this.adminUsers = adminUsers;
            this.managerUsers = managerUsers;
        }

        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(long inactiveUsers) { this.inactiveUsers = inactiveUsers; }
        public long getCustomerUsers() { return customerUsers; }
        public void setCustomerUsers(long customerUsers) { this.customerUsers = customerUsers; }
        public long getAdminUsers() { return adminUsers; }
        public void setAdminUsers(long adminUsers) { this.adminUsers = adminUsers; }
        public long getManagerUsers() { return managerUsers; }
        public void setManagerUsers(long managerUsers) { this.managerUsers = managerUsers; }

        public static UserStatisticsBuilder builder() {
            return new UserStatisticsBuilder();
        }

        public static class UserStatisticsBuilder {
            private long totalUsers;
            private long activeUsers;
            private long inactiveUsers;
            private long customerUsers;
            private long adminUsers;
            private long managerUsers;

            public UserStatisticsBuilder totalUsers(long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }

            public UserStatisticsBuilder activeUsers(long activeUsers) {
                this.activeUsers = activeUsers;
                return this;
            }

            public UserStatisticsBuilder inactiveUsers(long inactiveUsers) {
                this.inactiveUsers = inactiveUsers;
                return this;
            }

            public UserStatisticsBuilder customerUsers(long customerUsers) {
                this.customerUsers = customerUsers;
                return this;
            }

            public UserStatisticsBuilder adminUsers(long adminUsers) {
                this.adminUsers = adminUsers;
                return this;
            }

            public UserStatisticsBuilder managerUsers(long managerUsers) {
                this.managerUsers = managerUsers;
                return this;
            }

            public UserStatistics build() {
                return new UserStatistics(totalUsers, activeUsers, inactiveUsers, 
                                        customerUsers, adminUsers, managerUsers);
            }
        }
    }

    /**
     * Application-specific exception for user operations.
     */
    public static class UserApplicationException extends RuntimeException {
        public UserApplicationException(String message) {
            super(message);
        }

        public UserApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
