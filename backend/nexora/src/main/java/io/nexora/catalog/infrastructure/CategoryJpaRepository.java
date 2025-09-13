package io.nexora.catalog.infrastructure;

import io.nexora.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {
}
