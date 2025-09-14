package io.nexora.user.interfaces.rest.v1.dto;

import io.nexora.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for user data.
 * 
 * This DTO represents the user data returned to clients.
 * It provides a clean, safe representation of user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String role;
    private boolean active;
    private boolean emailVerified;
    private String lastLoginAt;
    private String createdAt;
    private String updatedAt;

    /**
     * Creates a UserResponse from a User domain object.
     * 
     * @param user The user domain object
     * @return A UserResponse DTO
     */
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEmail().value(),
                user.getRole().value(),
                user.isActive(),
                user.isEmailVerified(),
                user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null,
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}
