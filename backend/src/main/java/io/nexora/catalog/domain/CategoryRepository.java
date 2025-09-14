package io.nexora.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    List<Category> findAll();
    long count();
    void deleteById(String id);
    boolean existsById(String id);
}
