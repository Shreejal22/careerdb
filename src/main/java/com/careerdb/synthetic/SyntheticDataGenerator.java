package com.careerdb.synthetic;

import com.careerdb.common.CompanyNameNormalizer;
import com.careerdb.company.Company;
import com.careerdb.role.RoleCanonical;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
@Profile("generate")
public class SyntheticDataGenerator implements CommandLineRunner {

    private final GeneratorProperties props;
    private final NameNoiseService noiseService;
    private final JdbcTemplate jdbcTemplate;

    private final Random careerRandom;
    private final Random samplingRandom;

    public SyntheticDataGenerator(GeneratorProperties props, NameNoiseService noiseService, JdbcTemplate jdbcTemplate) {
        this.props = props;
        this.noiseService = noiseService;
        this.jdbcTemplate = jdbcTemplate;
        this.careerRandom = new Random(props.getRandomSeed() ^ 0x9E3779B9L);
        this.samplingRandom = new Random(props.getRandomSeed() ^ 0x2545F491L);
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Starting Synthetic Data Generation...");
        long start = System.currentTimeMillis();

        // 1. Create and save Companies & Roles
        List<Company> companies = createCompanies();
        List<RoleCanonical> roles = createRoles();
        saveCompaniesToDb(companies);
        saveRolesToDb(roles);

        Map<String, Company> patternCompanies = findPatternCompanies(companies);

        // 2. Create and save Persons first
        List<UUID> personIds = new ArrayList<>();
        for (int i = 0; i < props.getPersonCount(); i++) {
            personIds.add(UUID.randomUUID());
        }
        savePersonsToDb(personIds);

        // 3. Simulate Careers
        List<EmploymentRecord> allEmployments = new ArrayList<>();
        List<GroundTruthRecord> allGroundTruth = new ArrayList<>();

        for (UUID personId : personIds) {
            simulateCareer(personId, companies, roles, patternCompanies, allEmployments, allGroundTruth);
        }

        // 4. Save Employments and Ground Truth
        saveEmploymentsToDb(allEmployments);
        saveGroundTruthToDb(allGroundTruth);

        System.out.println("Data Generation Complete in " + (System.currentTimeMillis() - start) + "ms.");
    }

    // --- COMPANY GENERATION ---

    private List<Company> createCompanies() {
        List<Company> companies = new ArrayList<>();
        // 1. Create the 7 explicit pattern companies
        companies.add(createPatternCompany(props.getPatterns().getExodusCompanyName(), "IT_SERVICES"));
        companies.add(createPatternCompany(props.getPatterns().getSlowPromotionCompanyName(), "IT_SERVICES"));
        companies.add(createPatternCompany(props.getPatterns().getFastPromotionCompanyName(), "CONSULTING"));
        companies.add(createPatternCompany(props.getPatterns().getBoomCompanyName(), "FOOD_DELIVERY"));
        companies.add(createPatternCompany(props.getPatterns().getHighBoomerangCompanyName(), "FINTECH"));
        companies.add(createPatternCompany(props.getPatterns().getTalentFlowFrom(), "FOOD_DELIVERY"));
        companies.add(createPatternCompany(props.getPatterns().getTalentFlowTo(), "FOOD_DELIVERY"));

        // 2. Fill the rest using archetype distribution
        int remaining = props.getCompanyCount() - companies.size();
        Set<String> usedNames = new HashSet<>();
        for (Company c : companies) {
            usedNames.add(c.getNormalizedName());
        }

        // Add many more suffixes to allow unique generation
        String[] suffixes = {
                "Solutions", "Digital", "Services", "Group", "Systems", "Labs", "Tech",
                "Consulting", "Software", "Networks", "Analytics", "Cloud", "Data",
                "Innovations", "Ventures", "Partners", "Associates", "Enterprises",
                "Industries", "Technologies", "Works", "Media", "Logistics"
        };

        for (int i = 0; i < remaining; i++) {
            String archetype = WeightedRandomSelector.pick(props.getArchetypes().getDistribution(), samplingRandom);
            String prefix = props.getArchetypes().getNamePrefixes().get(archetype);

            String name;
            String normalized;
            int attempts = 0;
            do {
                name = prefix + " " + suffixes[samplingRandom.nextInt(suffixes.length)];
                normalized = CompanyNameNormalizer.normalize(name);
                attempts++;
                if (attempts > 100) {
                    // If we can't find a unique name after 100 tries, append a number
                    name = prefix + " " + suffixes[samplingRandom.nextInt(suffixes.length)] + " " + i;
                    normalized = CompanyNameNormalizer.normalize(name);
                }
            } while (usedNames.contains(normalized));

            usedNames.add(normalized);

            Company c = new Company();
            c.setId(UUID.randomUUID());
            c.setCanonicalName(name);
            c.setNormalizedName(normalized);
            c.setIndustry(archetype);
            c.setSynthetic(true);
            companies.add(c);
        }
        return companies;
    }

    private Company createPatternCompany(String name, String archetype) {
        Company c = new Company();
        c.setId(UUID.randomUUID());
        c.setCanonicalName(name);
        c.setNormalizedName(CompanyNameNormalizer.normalize(name));
        c.setIndustry(archetype);
        c.setSynthetic(true);
        return c;
    }

    private Map<String, Company> findPatternCompanies(List<Company> companies) {
        Map<String, Company> map = new HashMap<>();
        for (Company c : companies) {
            String name = c.getCanonicalName();
            if (name.equals(props.getPatterns().getExodusCompanyName())) map.put("exodus", c);
            if (name.equals(props.getPatterns().getBoomCompanyName())) map.put("boom", c);
            if (name.equals(props.getPatterns().getHighBoomerangCompanyName())) map.put("boomerang", c);
            if (name.equals(props.getPatterns().getFastPromotionCompanyName())) map.put("fast", c);
            if (name.equals(props.getPatterns().getSlowPromotionCompanyName())) map.put("slow", c);
            if (name.equals(props.getPatterns().getTalentFlowFrom())) map.put("talentFrom", c);
            if (name.equals(props.getPatterns().getTalentFlowTo())) map.put("talentTo", c);
        }
        return map;
    }

    private List<RoleCanonical> createRoles() {
        List<RoleCanonical> roles = new ArrayList<>();
        String[] titles = {"Junior Engineer", "Engineer", "Senior Engineer", "Lead Engineer", "Principal Engineer"};

        for (int i = 0; i < titles.length; i++) {
            RoleCanonical r = new RoleCanonical();
            r.setId(UUID.randomUUID());
            r.setName(titles[i]);
            r.setFamily("Engineering");
            r.setLevel(i + 1);
            roles.add(r);
        }
        return roles;
    }

    // --- CAREER SIMULATION ---

    private void simulateCareer(UUID personId, List<Company> companies, List<RoleCanonical> roles,
                                Map<String, Company> patterns,
                                List<EmploymentRecord> outEmployments, List<GroundTruthRecord> outTruth) {

        boolean isFresher = samplingRandom.nextDouble() < props.getPersonBudget().getFresherShare();

        LocalDate currentDate;
        int currentLevel;
        Company currentCompany;
        RoleCanonical currentRole;

        if (isFresher) {
            currentDate = props.getSimulationStart();
            currentLevel = 1;
            currentCompany = companies.get(samplingRandom.nextInt(companies.size()));
        } else {
            int daysRange = 365 * 5;
            currentDate = props.getSimulationStart().plusDays(samplingRandom.nextInt(daysRange));
            currentLevel = 2 + samplingRandom.nextInt(3);
            currentCompany = companies.get(samplingRandom.nextInt(companies.size()));
        }

        List<Company> pastCompanies = new ArrayList<>();
        int safetyCounter = 0;

        while (currentDate.isBefore(props.getObservationEnd())) {
            safetyCounter++;
            if (safetyCounter > 2000) {
                throw new IllegalStateException("Infinite loop detected for person " + personId + " on date " + currentDate);
            }

            currentRole = findRoleByLevel(roles, currentLevel);

            boolean isHiringBoom = currentCompany.equals(patterns.get("boom")) &&
                    currentDate.getYear() == props.getPatterns().getBoomYear();

            double monthlyExitProb = calculateMonthlyExitProb(currentCompany, currentLevel, currentDate, patterns);

            if (careerRandom.nextDouble() < monthlyExitProb) {
                // THEY EXIT
                LocalDate endDate = currentDate.minusDays(1);
                recordEmployment(personId, currentCompany, currentRole, currentDate, endDate, outEmployments, outTruth);

                if (samplingRandom.nextDouble() < props.getJobChange().getLeaveDatasetProbability()) {
                    break;
                }

                // --- THE CRITICAL FIX ---
                // We force the currentDate to advance by AT LEAST 1 month.
                int gapMonths = props.getJobChange().getMinGapMonths() +
                        samplingRandom.nextInt(props.getJobChange().getMaxGapMonths() - props.getJobChange().getMinGapMonths() + 1);

                currentDate = currentDate.plusMonths(Math.max(1, gapMonths));

                // Boomerang check
                boolean isBoomerang = false;
                if (!pastCompanies.isEmpty() && samplingRandom.nextDouble() < 0.15) {
                    currentCompany = pastCompanies.get(samplingRandom.nextInt(pastCompanies.size()));
                    isBoomerang = true;
                } else {
                    currentCompany = pickNextCompany(companies, currentCompany, patterns, isHiringBoom);
                }

                if (!pastCompanies.contains(currentCompany)) {
                    pastCompanies.add(currentCompany);
                }

                if (isBoomerang) {
                    if (samplingRandom.nextDouble() < props.getJobChange().getReturnLevelUpProb()) {
                        currentLevel = Math.min(5, currentLevel + 1);
                    }
                } else {
                    if (samplingRandom.nextDouble() < 0.2) {
                        currentLevel = Math.min(5, currentLevel + 1);
                    }
                }

            } else {
                // THEY STAY
                boolean isPromoted = careerRandom.nextDouble() < getMonthlyPromotionProb(currentLevel, currentCompany, patterns);

                if (isPromoted && currentLevel < 5) {
                    LocalDate endDate = currentDate.minusDays(1);
                    recordEmployment(personId, currentCompany, currentRole, currentDate, endDate, outEmployments, outTruth);
                    currentLevel++;

                    // Advance by 1 month after a promotion to avoid same-day exit
                    currentDate = currentDate.plusMonths(1);
                } else {
                    // Just advance one month
                    currentDate = currentDate.plusMonths(1);
                }
            }
        }

        // If they didn't leave, they have an open stint
        if (currentDate.isBefore(props.getObservationEnd()) || careerRandom.nextDouble() > 0.5) {
            currentRole = findRoleByLevel(roles, currentLevel);
            recordEmployment(personId, currentCompany, currentRole, currentDate, null, outEmployments, outTruth);
        }
    }

    // --- HELPER METHODS ---

    private double calculateMonthlyExitProb(Company company, int level, LocalDate date, Map<String, Company> patterns) {
        double annualRate = props.getArchetypes().getAnnualAttrition().getOrDefault(company.getIndustry(), 0.15);

        if (date.getYear() >= props.getAttrition().getMarketSpike().getFromYear() &&
                date.getYear() <= props.getAttrition().getMarketSpike().getToYear()) {
            annualRate *= props.getAttrition().getMarketSpike().getMultiplier();
        }

        if (company.equals(patterns.get("exodus")) &&
                date.getYear() == props.getPatterns().getExodusYear() &&
                date.getMonthValue() >= (props.getPatterns().getExodusQuarter() - 1) * 3 + 1 &&
                date.getMonthValue() <= props.getPatterns().getExodusQuarter() * 3) {
            annualRate *= props.getAttrition().getExodusMultiplier();
        }

        double levelMultiplier = level <= 2 ? props.getAttrition().getJuniorMultiplier() :
                level >= 4 ? props.getAttrition().getSeniorMultiplier() :
                        props.getAttrition().getMidMultiplier();
        annualRate *= levelMultiplier;

        return 1 - Math.pow(1 - annualRate, 1.0 / 12.0);
    }

    private double getMonthlyPromotionProb(int level, Company company, Map<String, Company> patterns) {
        double years = props.getPromotion().getYearsToNextLevel().getOrDefault(level, 5.0);
        if (company.equals(patterns.get("fast"))) years *= props.getPromotion().getFastMultiplier();
        if (company.equals(patterns.get("slow"))) years *= props.getPromotion().getSlowMultiplier();
        return 1.0 / (years * 12);
    }

    private Company pickNextCompany(List<Company> companies, Company currentCompany, Map<String, Company> patterns, boolean isBoom) {
        Map<Company, Double> weights = new LinkedHashMap<>();
        for (Company c : companies) {
            if (c.equals(currentCompany)) continue;
            double weight = 1.0;

            if (currentCompany.equals(patterns.get("talentFrom")) && c.equals(patterns.get("talentTo"))) {
                weight *= props.getJobChange().getTalentFlowBoost();
            }
            if (isBoom && c.equals(patterns.get("boom"))) {
                weight *= props.getJobChange().getHiringBoomBoost();
            }
            weights.put(c, weight);
        }
        return WeightedRandomSelector.pick(weights, samplingRandom);
    }

    private RoleCanonical findRoleByLevel(List<RoleCanonical> roles, int level) {
        return roles.stream().filter(r -> r.getLevel() == level).findFirst().orElse(roles.get(0));
    }

    private void recordEmployment(UUID personId, Company company, RoleCanonical role,
                                  LocalDate start, LocalDate end,
                                  List<EmploymentRecord> employments, List<GroundTruthRecord> truth) {
        NoiseResult noise = noiseService.applyNoise(company);
        EmploymentRecord rec = new EmploymentRecord(
                UUID.randomUUID(), personId, company.getId(), role.getId(),
                noise.rawText(), role.getName(), start, end
        );
        employments.add(rec);
        truth.add(new GroundTruthRecord(rec.id(), company.getId(), role.getId(), noise.noiseType()));
    }

    // --- BULK INSERTION METHODS ---

    private void saveCompaniesToDb(List<Company> companies) {
        String sql = "INSERT INTO company (id, canonical_name, normalized_name, industry, is_synthetic) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (Company c : companies) {
            batch.add(new Object[]{c.getId(), c.getCanonicalName(), c.getNormalizedName(), c.getIndustry(), true});
        }
        jdbcTemplate.batchUpdate(sql, batch);

        String aliasSql = "INSERT INTO company_alias (id, company_id, alias_text, normalized_alias, source, status) VALUES (?, ?, ?, ?, ?, ?)";
        List<Object[]> aliasBatch = new ArrayList<>();

        for (Company c : companies) {
            List<String> aliases = props.getNoise().getKnownVariants().get(c.getIndustry());
            if (aliases != null) {
                Set<String> usedAliases = new HashSet<>();
                String canonicalNorm = CompanyNameNormalizer.normalize(c.getCanonicalName());

                for (String alias : aliases) {
                    String raw = alias.replace("{name}", c.getCanonicalName());
                    String normalized = CompanyNameNormalizer.normalize(raw);

                    if (normalized.equals(canonicalNorm)) {
                        continue;
                    }
                    if (!usedAliases.add(normalized)) {
                        continue;
                    }
                    aliasBatch.add(new Object[]{UUID.randomUUID(), c.getId(), raw, normalized, "GENERATOR", "APPROVED"});
                }
            }
        }
        jdbcTemplate.batchUpdate(aliasSql, aliasBatch);
    }

    private void saveRolesToDb(List<RoleCanonical> roles) {
        String sql = "INSERT INTO role_canonical (id, name, family, level) VALUES (?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (RoleCanonical r : roles) {
            batch.add(new Object[]{r.getId(), r.getName(), r.getFamily(), r.getLevel()});
        }
        jdbcTemplate.batchUpdate(sql, batch);
    }

    private void savePersonsToDb(List<UUID> personIds) {
        String sql = "INSERT INTO person (id, is_synthetic, region) VALUES (?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (UUID id : personIds) {
            batch.add(new Object[]{id, true, "India"});
        }
        jdbcTemplate.batchUpdate(sql, batch);
    }

    private void saveEmploymentsToDb(List<EmploymentRecord> employments) {
        String sql = "INSERT INTO employment (id, person_id, company_id, role_id, raw_company_text, raw_title, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (EmploymentRecord e : employments) {
            batch.add(new Object[]{
                    e.id(), e.personId(), e.companyId(), e.roleId(),
                    e.rawCompanyText(), e.rawTitle(),
                    Date.valueOf(e.startDate()), e.endDate() != null ? Date.valueOf(e.endDate()) : null
            });
        }
        jdbcTemplate.batchUpdate(sql, batch);
    }

    private void saveGroundTruthToDb(List<GroundTruthRecord> truth) {
        String sql = "INSERT INTO synthetic_truth (employment_id, true_company_id, true_role_id, noise_type) VALUES (?, ?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (GroundTruthRecord t : truth) {
            batch.add(new Object[]{t.employmentId(), t.trueCompanyId(), t.trueRoleId(), t.noiseType()});
        }
        jdbcTemplate.batchUpdate(sql, batch);
    }

    // --- RECORD CLASSES ---

    private record EmploymentRecord(UUID id, UUID personId, UUID companyId, UUID roleId,
                                    String rawCompanyText, String rawTitle,
                                    LocalDate startDate, LocalDate endDate) {}

    private record GroundTruthRecord(UUID employmentId, UUID trueCompanyId, UUID trueRoleId, String noiseType) {}
}