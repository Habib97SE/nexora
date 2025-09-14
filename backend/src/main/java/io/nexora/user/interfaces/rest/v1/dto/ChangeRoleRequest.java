package io.nexora.user.interfaces.rest.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for changing user role.
 * 
 * This DTO represents the data required to change a user's role.
 * It includes validation annotations to ensure data integrity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequest {
    
    @NotBlank(message = "Role cannot be empty")
    private String role;
}
