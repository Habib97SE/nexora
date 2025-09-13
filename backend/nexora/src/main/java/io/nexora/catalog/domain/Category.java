package io.nexora.catalog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, message = "Name must be at least 3 characters long")
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
