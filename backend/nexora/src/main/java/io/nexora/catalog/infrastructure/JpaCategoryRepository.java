package io.nexora.catalog.infrastructure;

import io.nexora.catalog.domain.Category;
import io.nexora.catalog.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
    public Optional<Category> findById(String id) {
        return categoryJpaRepository.findById(UUID.fromString(id));
    }

    @Override
    public List<Category> findAll() {
        return this.categoryJpaRepository.findAll();
    }

    @Override
    public long count() {
        return this.categoryJpaRepository.count();
    }

    @Override
    public void deleteById(String id) {
        this.categoryJpaRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public boolean existsById(String id) {
        return this.categoryJpaRepository.existsById(UUID.fromString(id));
    }
}
