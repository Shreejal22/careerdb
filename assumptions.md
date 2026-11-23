# Synthetic Data Assumptions (CareerDB, India-focused)

Every parameter used by the synthetic data generator is listed here with its value, source and a note.
The dataset describes **fictional companies and invented people**. Real companies appear in this file only as
**calibration references** for public, published figures.

## Status legend

| Status | Meaning |
|---|---|
| **Sourced** | Value comes from a published source. Link provided. Re-check the primary source before citing it in the README. |
| **Derived** | Computed from other parameters, not set directly. |
| **Design** | A choice made for testing or realism, not a real-world statistic. |
| **Assumption** | An educated guess with no verified source. Replace it if a source is found. |
| **TODO** | A source still needs to be found. Do not ship a TODO row without resolving or relabelling it as Assumption. |

## Rules for this file

1. Never invent a source. If a number is a guess, label it **Assumption**.
2. Figures below were taken from news summaries of company results. Before quoting any of them, open the
   company's investor-relations quarterly fact sheet and copy the figure from there.
3. Attrition definitions differ between companies (voluntary vs total, with or without certain business units).
   Do not compare figures from different companies without noting the definition.
4. Annual exit rate is the **driver**; tenure is **derived** from it and then sanity-checked against the benchmarks.

---

## 1. Company archetypes

Real company names are **not** used in the dataset. Fabricated statistics about real, named companies (for example
a planted "mass exodus") would be misleading, so every company is fictional. The real companies below are used only
to decide what each archetype should look like and, where figures are published, to calibrate its parameters.

Fictional names are examples. Before finalising any name, search the web and the Ministry of Corporate Affairs
company search to confirm it does not match a real company.

| Archetype | Fictional example names | Real companies used as calibration references | Notes |
|---|---|---|---|
| Large Indian IT services | Meridian Infotech, Kaveri Digital Services, Aranya Technologies | TCS, Infosys, HCLTech | Very large headcount, big yearly fresher cohorts, published attrition available |
| Global IT and consulting (India operations) | Halden Global Consulting, Corvane Technologies, Brightwell Advisory | IBM, Accenture, Capgemini, Deloitte, KPMG | India-specific attrition rarely published, mostly Assumption |
| Hardware and enterprise product | Orbis Devices | HP, Dell | Mix of engineering, sales and support roles |
| Food delivery and quick commerce | QuickNest, Zipbasket, Tiffin Dash | Zomato, Swiggy, Zepto, Blinkit | High growth, higher attrition, large operations workforce |
| Mobility | Sawari Mobility, RideMitra | Ola, Uber | Mix of tech, operations and city teams |
| E-commerce | BazaarHive | Flipkart | Large tech and supply-chain teams |
| Fintech and payments | PayVeda, Rupee Rail | Razorpay, PhonePe | Product-engineering heavy, fast promotions |

### Planted-pattern companies

These seven fictional companies are created first, with fixed names, exactly one of each, and are excluded from the
random company pool so tests can find them by name.

| Pattern | Company | Archetype | What is planted | Status |
|---|---|---|---|---|
| Mass exodus | Aranya Technologies | IT services | Exit rate x5 during Q3 2022 | Design |
| Slow promotion | Meridian Infotech | IT services | Promotion times x1.4 | Design |
| Fast promotion | Halden Global Consulting | Consulting | Promotion times x0.7 | Design |
| Hiring boom | QuickNest | Food delivery and quick commerce | Hiring weight x3 during 2023, including fresher intake | Design |
| High boomerang | PayVeda | Fintech | Elevated return probability for people who left it | Design |
| Talent-flow pair | Tiffin Dash to Zipbasket | Food delivery and quick commerce | Leavers of Tiffin Dash choose Zipbasket with weight x4 | Design |

## 2. Tenure benchmarks (sanity checks, not generator inputs)

BLS measures how long currently employed workers have been with their present employer. That is ongoing tenure
of existing employees in the US, not the length of completed stints. Use it only to check that the generated
medians are plausible.

| Parameter | Value | Source name | Link | Status | Note |
|---|---|---|---|---|---|
| Median tenure with current employer, all US wage and salary workers, Jan 2026 | 4.1 years | US BLS, Employee Tenure in 2026 | https://www.bls.gov/news.release/archives/tenure_09242026.htm | Sourced | US data. Latest release at time of writing. |
| Median tenure, US private sector, Jan 2026 | 3.9 years | US BLS, Employee Tenure in 2026 | https://www.bls.gov/news.release/tenure.nr0.htm | Sourced | Private-sector figure is the closer analogue to company data. |
| Median tenure, all US wage and salary workers, Jan 2024 | 3.9 years | US BLS, The Economics Daily | https://www.bls.gov/opub/ted/2024/median-tenure-with-current-employer-was-3-9-years-in-january-2024.htm | Sourced | Earlier draft of this file said 4.0 years, which was incorrect. |
| Median tenure, US workers aged 25 to 34, Jan 2024 | 2.7 years | US BLS, The Economics Daily | https://www.bls.gov/opub/ted/2024/median-tenure-with-current-employer-was-3-9-years-in-january-2024.htm | Sourced | Closest benchmark for a young workforce. Check whether a Jan 2026 value exists. |
| India-specific median tenure | n/a | TODO | TODO | TODO | Look for Indian labour or industry survey data on job tenure. |

## 3. Attrition (annual exit rate, the main driver)

| Parameter | Value | Source name | Link | Status | Note |
|---|---|---|---|---|---|
| Annual attrition, top six listed Indian IT services firms, 2026 | 11% to 15% | Xpheno analysis reported by The Wire, Oct 2026 | https://thewire.in/news/top-six-it-companies-reduce-employee-count-by-24000-hiring-dips | Sourced | Use as the range for the Large Indian IT services archetype. |
| HCLTech LTM attrition, Sept 2026 quarter | 12.8% | People Matters, Oct 2026 | https://www.peoplematters.in/news/recruiting-and-onboarding/hcltech-attrition-rises-to-128percent-as-headcount-grows-by-6450-in-q2-52678 | Sourced | Excludes involuntary attrition and digital process operations. Confirm in HCLTech fact sheet. |
| TCS LTM attrition, IT services, June 2026 quarter | 13.6% | Entrepreneur India | https://india.entrepreneur.com/business-news/from-tcs-to-techm-headcount-declines-echo-vembus-it-hiring-concerns | Sourced | Confirm in TCS quarterly fact sheet. Other reports quote different TCS figures for other quarters. |
| Peak attrition, FY22 to FY23 | 17.4% to 27.7% | Analytics India Magazine | https://analyticsindiamag.com/it-services/indias-top-it-firms-stabilise-workforce-while-driving-ai-focused-reskilling | Sourced | Basis for a planted market-wide attrition spike across 2021 to 2023. Verify in company filings. |
| Market-wide attrition multiplier, 2021 to 2023 | x1.5 on every company's annual rate | none | none | Derived | Chosen so an IT services company at 14% lands near 21%, inside the published FY22 to FY23 peak range above. Adjust if a better source is found. |
| Attrition definitions differ between companies | n/a | People Matters | https://www.peoplematters.in/news/business/indian-it-hiring-in-q2-hcltech-adds-6450-employees-as-wipro-loses-6716-52954 | Sourced | Reason figures cannot be compared directly. |
| Annual attrition, product companies and startups | 18% to 25% | none | none | Assumption | Higher than IT services. TODO: check SEBI BRSR disclosures (employee turnover) of listed companies such as Zomato's parent and Swiggy. |
| Annual attrition, global IT and consulting (India operations) | 13% to 20% | none | none | Assumption | TODO: check whether Accenture and similar firms disclose attrition in earnings material. |
| Annual attrition, hardware and enterprise product | 15% | none | none | Assumption | Added for the Hardware archetype. TODO: look for a source. |
| Annual attrition, unlisted companies (for example fintech, e-commerce) | 18% to 25% | none | none | Assumption | No public figures expected. Keep labelled as Assumption. |
| Attrition multiplier by level | Levels 1 and 2: x1.3. Level 3: x1.0. Levels 4 and 5: x0.6 | none | none | Assumption | These do not average to exactly 1.0 for every level mix. The configured company rate is the company-wide average, and tests compute the expected rate from the simulated level mix. |
| Exodus multiplier | x5 on the monthly exit hazard | none | none | Design | Applied only to Aranya Technologies in Q3 2022, checked at the exit month. |
| Annual rates used by the generator, by archetype | IT services 14%, Consulting 16%, Product 22%, Fintech 22%, E-commerce 22%, Food delivery 22%, Hardware 15% | none | none | Derived | Each value sits inside the range stated for its archetype above. Update the generator configuration and this table together. |
| Monthly exit hazard | 1 - (1 - annual rate)^(1/12) | none | none | Derived | Applied month by month. Tenure is an outcome of this, not an input. |

## 4. Career progression

| Parameter | Value | Source name | Link | Status | Note |
|---|---|---|---|---|---|
| Time to promote, level 1 to level 2 | 2.0 years | none | none | Assumption | Varies by company. |
| Time to promote, level 2 to level 3 | 3.0 years | none | none | Assumption | Varies by company. |
| Time to promote, level 3 to level 4 | 4.0 years | none | none | Assumption | Varies by company. |
| Fast-promotion companies | 0.7x the times above | none | none | Design | Planted pattern for validating the promotion benchmark. |
| Slow-promotion companies | 1.4x the times above | none | none | Design | Planted pattern for validating the promotion benchmark. |
| Promotions cluster around yearly appraisal cycles | Most promotions effective once a year | none | none | Assumption | General knowledge of Indian corporate practice. Verify and replace if a source is found. |
| Yearly fresher (campus) cohort entering at level 1 | Large for IT services, small for startups | none | none | Assumption | TODO: company fact sheets report fresher hiring numbers for large IT services. |
| Fresher joining-month weights | Jan 8%, Feb 4%, Mar 4%, Apr 4%, May 6%, Jun 18%, Jul 18%, Aug 14%, Sep 8%, Oct 6%, Nov 6%, Dec 4% | none | none | Assumption | Proposed: mostly mid-year after graduation. Weights must sum to 100%. Do not put all hires on 1 January. TODO: find a source or ask recent graduates. |
| Experienced (lateral) hires | Hiring equals exits plus the company's growth, spread over all months | none | none | Assumption | Prevents every company having the same flat growth. |
| Annual net headcount growth per company | Drawn per company within a range. IT services -3% to +8%. Consulting -3% to +10%. Product, fintech, e-commerce -5% to +25%. Food delivery -10% to +30%. Hardware -5% to +5% | none | none | Assumption | Gives headcount charts different shapes per company. |
| Level when joining a new company | Same level 80%, one level up 20%, never down. Capped at level 5 | none | none | Assumption | Keeps promotion data meaningful. |

## 5. Job changes

| Parameter | Value | Source name | Link | Status | Note |
|---|---|---|---|---|---|
| Gap between jobs | Mostly 0 to 1 month, occasionally 1 to 3 months | none | none | Assumption | Notice periods usually let people move directly. Replace if a source is found. |
| Boomerang rate, baseline | 5% | none | none | Assumption | Share of leavers who later return to a previous employer. |
| Boomerang rate, high-boomerang company | 20% | none | none | Assumption | Planted pattern. |
| Talent-flow preference between selected company pair | 4x baseline | none | none | Design | Planted pattern for validating alumni flow. |
| Boomerang rules | Chance depends on the company being returned to. At least 6 months away. Returns at the previous level or one higher | none | none | Design | |
| Leaving the dataset after an exit | 15% of exits have no next record | none | none | Assumption | Range 10% to 20%. Fills the Unknown bucket in alumni flow. Leavers may join employers outside the dataset, take breaks or study. |

## 6. Population and noise

| Parameter | Value | Source name | Link | Status | Note |
|---|---|---|---|---|---|
| Number of synthetic persons | about 20,000 | none | none | Design | Reproducible from a fixed random seed. |
| Number of fictional companies | about 300 | none | none | Design | Split across the archetypes in Section 1. |
| Cities | Bengaluru, Hyderabad, Pune, Chennai, NCR, Mumbai | none | none | Design | Spread weighted by archetype. |
| Simulation window | 1 Jan 2015 to 31 Oct 2026 | none | none | Design | Open stints at the end have an empty end date. No dates after the window end. |
| Person budget | Total persons = freshers + lateral entrants, targeting about 20,000 | none | none | Design | Lateral entrants start with no earlier history. |
| Random seed | 42, with separate random streams for careers, noise and sampling | none | none | Design | Changing one component must not change the others. Use ordered maps for reproducibility. |
| Company name alias rate | 30% | none | none | Design | Tests entity resolution. |
| Company name typo rate | 10% of mentions | none | none | Design | Typo kinds: swapped letters, dropped letter, doubled letter. A typo must always change the string. Never registered as an alias. |
| Company name known-variant rate | Part of the 30% alias share | none | none | Design | Known variants are stored in the company alias table. |
| Held-out variants | 20% of curated variants | none | none | Design | Appear only in employment text, never in the alias table, so the exact-match step cannot solve them. Used for evaluation. |
| Variant difficulty | Include abbreviations, dropped words and reordered words, not only legal suffixes | none | none | Design | Legal suffixes are stripped by normalization, so suffix-only variants test nothing. |
| Noise type label | EXACT, KNOWN_ALIAS, HELDOUT_ALIAS, TYPO | none | none | Design | Stored in the ground-truth table so precision and recall are reported per category. |
| Job title variant rate | 40% | none | none | Design | For example "SDE-2", "Sr. Software Engineer", "Associate Consultant". |
| Mass exodus | Exit rate x5 for one chosen quarter at one company | none | none | Design | Planted pattern. |
| Hiring boom | Hiring rate x3 for one year at one company | none | none | Design | Planted pattern. |

## 7. Open items

1. Open each company's investor-relations quarterly fact sheet and confirm the attrition figures in Section 3.
2. Check SEBI BRSR (Business Responsibility and Sustainability Reporting) filings of listed product companies for employee turnover figures.
3. Look for any published India-specific tenure or job-mobility survey.
4. Replace Assumption rows with sourced values wherever a source is found, and keep the Status column accurate.
5. Confirm no fictional company name matches a real company.
6. Find a source, or ask recent graduates, for the fresher joining-month weights.
7. Check the generator's measured attrition per archetype against Section 3 and update this file if a value had to change.
8. Keep generator configuration and this file in sync: every number in the configuration must appear here with a status.