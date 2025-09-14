// io/nexora/catalog/infrastructure/JpaProductRepository.java
package io.nexora.catalog.infrastructure;

import io.nexora.catalog.domain.Product;
import io.nexora.catalog.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id.toString());
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku);
    }

    @Override
    public List<Product> findAll(int page, int size) {
        return productJpaRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public List<Product> findByCategoryId(UUID categoryId, int page, int size) {
        return productJpaRepository.findByCategory_Id(categoryId.toString());
    }

    @Override
    public List<Product> searchByText(String text, int page, int size) {
        return productJpaRepository.searchByText(text);
    }

    @Override
    public long count() {
        return productJpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        productJpaRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return productJpaRepository.existsById(id.toString());
    }
}
