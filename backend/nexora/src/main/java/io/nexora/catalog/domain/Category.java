package io.nexora.catalog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotEmpty(message = "Name cannot be left empty")
    @Min(3)
    private String name;


    private String description;
    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    @PastOrPresent
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
