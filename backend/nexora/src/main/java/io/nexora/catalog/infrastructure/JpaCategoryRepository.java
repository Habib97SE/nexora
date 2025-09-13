package io.nexora.catalog.infrastructure;

import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCategoryRepository implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public Category findById(String id) {
        return categoryJpaRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public void deleteById(String id) {
        this.categoryJpaRepository.deleteById(UUID.fromString(id));
    }
}
