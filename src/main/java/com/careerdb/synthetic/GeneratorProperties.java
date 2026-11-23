package com.careerdb.synthetic;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor // <--- ADD THIS
@Component
@Validated
@ConfigurationProperties(prefix = "generator")
public class GeneratorProperties {

    @NotNull private int personCount;
    @NotNull private int companyCount;
    @NotNull private long randomSeed;
    @NotNull private LocalDate observationEnd;
    @NotNull private LocalDate simulationStart;

    @NotNull private Archetypes archetypes;
    @NotNull private Attrition attrition;
    @NotNull private Promotion promotion;
    @NotNull private JobChange jobChange;
    @NotNull private Noise noise;
    @NotNull private Patterns patterns;
    @NotNull private Cohort cohort;
    @NotNull private Growth growth;
    @NotNull private PersonBudget personBudget;

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Archetypes {
        @NotEmpty private Map<String, Double> annualAttrition;
        @NotEmpty private Map<String, Double> distribution;
        @NotEmpty private Map<String, String> namePrefixes;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Attrition {
        @NotNull private double juniorMultiplier;
        @NotNull private double midMultiplier;
        @NotNull private double seniorMultiplier;
        @NotNull private double exodusMultiplier;
        @NotNull private MarketSpike marketSpike;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class MarketSpike {
        @NotNull private int fromYear;
        @NotNull private int toYear;
        @NotNull private double multiplier;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Promotion {
        @NotEmpty private Map<Integer, Double> yearsToNextLevel;
        @NotNull private double fastMultiplier;
        @NotNull private double slowMultiplier;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class JobChange {
        @NotNull private double boomerangBaseline;
        @NotNull private double boomerangHigh;
        @NotNull private int minGapMonths;
        @NotNull private int maxGapMonths;
        @NotNull private double talentFlowBoost;
        @NotNull private double hiringBoomBoost;
        @NotNull private double leaveDatasetProbability;
        @NotNull private int minReturnMonths;
        @NotNull private double returnSameLevelProb;
        @NotNull private double returnLevelUpProb;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Noise {
        @NotNull private double aliasRate;
        @NotNull private double typoRate;
        @NotEmpty private Map<String, List<String>> knownVariants;
        @NotEmpty private Map<String, List<String>> heldoutVariants;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Patterns {
        @NotNull private String exodusCompanyName;
        @NotNull private int exodusQuarter;
        @NotNull private int exodusYear;
        @NotNull private String boomCompanyName;
        @NotNull private int boomYear;
        @NotNull private String highBoomerangCompanyName;
        @NotNull private String fastPromotionCompanyName;
        @NotNull private String slowPromotionCompanyName;
        @NotNull private String talentFlowFrom;
        @NotNull private String talentFlowTo;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Cohort {
        @NotEmpty private Map<Integer, Double> monthWeights;
        @NotNull private double experiencedMonthlyRate;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class Growth {
        @NotEmpty private Map<String, GrowthRange> ranges;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class GrowthRange {
        @NotNull private double min;
        @NotNull private double max;
    }

    @Data
    @NoArgsConstructor // <--- ADD THIS
    public static class PersonBudget {
        @NotNull private double fresherShare;
        @NotNull private double lateralShare;
    }
}