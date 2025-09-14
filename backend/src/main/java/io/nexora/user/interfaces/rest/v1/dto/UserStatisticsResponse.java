package io.nexora.user.interfaces.rest.v1.dto;

import io.nexora.user.application.UserApplicationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for user statistics.
 * 
 * This DTO represents user statistics data returned to clients.
 * It provides aggregated information about users in the system.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long customerUsers;
    private Long adminUsers;
    private Long managerUsers;

    /**
     * Creates a UserStatisticsResponse from UserStatistics domain object.
     * 
     * @param statistics The user statistics domain object
     * @return A UserStatisticsResponse DTO
     */
    public static UserStatisticsResponse fromDomain(UserApplicationService.UserStatistics statistics) {
        return new UserStatisticsResponse(
                statistics.getTotalUsers(),
                statistics.getActiveUsers(),
                statistics.getInactiveUsers(),
                statistics.getCustomerUsers(),
                statistics.getAdminUsers(),
                statistics.getManagerUsers()
        );
    }
}
