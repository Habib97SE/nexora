package io.nexora.user.infrastructure;

import io.nexora.shared.valueobject.Role;
import io.nexora.shared.valueobject.Email;
import io.nexora.shared.valueobject.Password;
import io.nexora.user.domain.User;
import io.nexora.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of UserRepository (Adapter).
 * 
 * This class implements the UserRepository interface using Spring Data JPA.
 * It serves as the infrastructure adapter that bridges the domain layer
 * with the persistence layer.
 * 
 * Design Principles Applied:
 * - Dependency Inversion: Implements domain interface, not the other way around
 * - Single Responsibility: Focuses solely on data access implementation
 * - Adapter Pattern: Adapts JPA operations to domain repository interface
 * - Interface Segregation: Implements only the methods defined in the domain interface
 */
@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id.toString());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmailValue(email.value());
    }

    @Override
    public List<User> findAll(int page, int size) {
        return userJpaRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public List<User> findByRole(Role role, int page, int size) {
        List<User> allUsersWithRole = userJpaRepository.findByRoleValue(role.value());
        return paginateList(allUsersWithRole, page, size);
    }

    @Override
    public List<User> findActiveUsers(int page, int size) {
        List<User> allActiveUsers = userJpaRepository.findActiveUsers();
        return paginateList(allActiveUsers, page, size);
    }

    @Override
    public List<User> findInactiveUsers(int page, int size) {
        List<User> allInactiveUsers = userJpaRepository.findInactiveUsers();
        return paginateList(allInactiveUsers, page, size);
    }

    @Override
    public List<User> searchByName(String name, int page, int size) {
        List<User> allMatchingUsers = userJpaRepository.searchByName(name);
        return paginateList(allMatchingUsers, page, size);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public long countByRole(Role role) {
        return userJpaRepository.countByRoleValue(role.value());
    }

    @Override
    public long countActiveUsers() {
        return userJpaRepository.countActiveUsers();
    }

    @Override
    public long countInactiveUsers() {
        return userJpaRepository.countInactiveUsers();
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return userJpaRepository.existsById(id.toString());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmailValue(email.value());
    }

    /**
     * Helper method to paginate a list of users.
     * 
     * @param users The list of users to paginate
     * @param page The page number (0-based)
     * @param size The page size
     * @return The paginated list of users
     */
    private List<User> paginateList(List<User> users, int page, int size) {
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, users.size());
        
        if (startIndex >= users.size()) {
            return List.of();
        }
        
        return users.subList(startIndex, endIndex);
    }
}
