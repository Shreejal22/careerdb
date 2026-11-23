package com.careerdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "generate"})
@Testcontainers
class SyntheticGeneratorTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("pgvector/pgvector:pg16")
                    .asCompatibleSubstituteFor("postgres")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testMassExodusPatternIsRecoverable() {
        String exodusCompanyId = jdbcTemplate.queryForObject(
                "SELECT id FROM company WHERE canonical_name = 'Aranya Technologies'",
                String.class
        );
        assertThat(exodusCompanyId).isNotNull();

        List<Map<String, Object>> exits = jdbcTemplate.queryForList(
                "SELECT " +
                        "  EXTRACT(YEAR FROM end_date) as year, " +
                        "  EXTRACT(QUARTER FROM end_date) as quarter, " +
                        "  COUNT(*) as exit_count " +
                        "FROM employment " +
                        "WHERE company_id = ?::uuid AND end_date IS NOT NULL " +
                        "GROUP BY year, quarter " +
                        "ORDER BY year, quarter",
                exodusCompanyId
        );

        assertThat(exits).isNotEmpty();

        BigDecimal exodusYear = BigDecimal.valueOf(2022);
        BigDecimal exodusQuarter = BigDecimal.valueOf(3);

        Map<String, Object> exodusData = exits.stream()
                .filter(row -> exodusYear.equals(row.get("year")) && exodusQuarter.equals(row.get("quarter")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Exodus quarter not found"));

        long exodusExitCount = ((Number) exodusData.get("exit_count")).longValue();

        List<Long> otherQuartersExits = exits.stream()
                .filter(row -> !(exodusYear.equals(row.get("year")) && exodusQuarter.equals(row.get("quarter"))))
                .map(row -> ((Number) row.get("exit_count")).longValue())
                .toList();

        double avgOtherExits = otherQuartersExits.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(1.0);

        assertThat((double) exodusExitCount)
                .as("Exodus quarter (Q3 2022) should have at least 2x the exits of other quarters")
                .isGreaterThanOrEqualTo(avgOtherExits * 2.0);
    }
}