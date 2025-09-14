package io.nexora.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository (Port) for Product aggregates.
 * No Spring/JPA types here—keep it storage-agnostic.
 */
public interface ProductRepository {

    /** Create or update a product aggregate. */
    Product save(Product product);

    /** Fetch a product by its aggregate ID. */
    Optional<Product> findById(UUID id);

    /** Fetch a product by its SKU (unique business key). */
    Optional<Product> findBySku(String sku);

    /** Simple paged listing (use count() to compute total pages). */
    List<Product> findAll(int page, int size);

    /** List products for a category (paged). */
    List<Product> findByCategoryId(UUID categoryId, int page, int size);

    /** Basic text search over name/description (paged). */
    List<Product> searchByText(String text, int page, int size);

    /** Total number of products (use with findAll for pagination). */
    long count();

    /** Delete by ID. */
    void deleteById(UUID id);

    /** Existence check (useful for invariants/validators). */
    boolean existsById(UUID id);
}
