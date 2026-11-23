-- F2: Monthly Headcount
CREATE MATERIALIZED VIEW mv_monthly_headcount AS
SELECT
    company_id,
    DATE_TRUNC('month', start_date)::DATE as month,
    COUNT(DISTINCT person_id) as headcount
FROM employment
WHERE company_id IS NOT NULL
GROUP BY company_id, DATE_TRUNC('month', start_date);

-- Unique index required for CONCURRENTLY refreshing without locking reads
CREATE UNIQUE INDEX idx_mv_headcount ON mv_monthly_headcount (company_id, month);

-- F4: Tenure Stats (Mean & Median)
CREATE MATERIALIZED VIEW mv_tenure_stats AS
SELECT
    company_id,
    COUNT(id) as completed_stints,
    AVG(end_date - start_date) as mean_days,
    PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY (end_date - start_date)) as median_days
FROM employment
WHERE end_date IS NOT NULL AND company_id IS NOT NULL
GROUP BY company_id;

CREATE UNIQUE INDEX idx_mv_tenure ON mv_tenure_stats (company_id);