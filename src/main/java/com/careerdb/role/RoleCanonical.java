package com.careerdb.role;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "role_canonical")
public class RoleCanonical {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String family; // e.g., "Engineering", "Product"

    @Column(nullable = false)
    private int level; // 1 to 5
}