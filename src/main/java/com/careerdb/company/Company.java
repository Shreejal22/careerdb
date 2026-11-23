package com.careerdb.company;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "canonical_name", nullable = false)
    private String canonicalName;

    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    private String industry;

    @Column(name = "size_band")
    private String sizeBand;

    @Column(name = "hq_region")
    private String hqRegion;

    @Column(name = "is_synthetic")
    private boolean isSynthetic;

    // We don't map the vector directly here to avoid Hibernate dialect complexity.
    // We will update embeddings via native JdbcTemplate queries.

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}