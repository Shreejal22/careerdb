package com.careerdb.employment;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "employment")
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "raw_company_text")
    private String rawCompanyText;

    @Column(name = "raw_title")
    private String rawTitle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "employment_type")
    private String employmentType = "FULL_TIME";

    @Column(name = "resolution_status")
    private String resolutionStatus = "RESOLVED";

    @Column(name = "resolution_confidence")
    private Float resolutionConfidence;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}