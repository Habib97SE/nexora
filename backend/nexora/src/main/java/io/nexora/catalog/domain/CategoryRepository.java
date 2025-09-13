package io.nexora.catalog.domain;

public interface CategoryRepository {
    Category save(Category category);
    Category findById(String id);
    void deleteById(String id);
}
