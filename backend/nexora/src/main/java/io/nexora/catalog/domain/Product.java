package io.nexora.catalog.domain;

import io.nexora.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.boot.jaxb.mapping.spi.db.JaxbColumnDefinable;

import java.math.BigDecimal;
import java.util.UUID;



@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Min(3)
    private String name;

    private String description;
    @Embedded
    private Money price;

    @Column(columnDefinition = "bigint default 0")
    private int stockQuantity;

    @OneToOne(fetch = FetchType.LAZY)
    private Category category;


}