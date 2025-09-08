package io.nexora.catalog.domain;

import io.nexora.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;




@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Size(min = 2)
    private String name;

    private String description;
    @Embedded
    private Money price;

    @Column(columnDefinition = "bigint default 0")
    private int stockQuantity;

    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    private Category category;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    @CreationTimestamp
    private LocalDateTime createdAt;

    @PastOrPresent
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}