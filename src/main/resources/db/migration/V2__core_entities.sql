-- COMPANY
CREATE TABLE company (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         canonical_name VARCHAR(255) NOT NULL,
                         normalized_name VARCHAR(255) NOT NULL,
                         industry VARCHAR(100),
                         size_band VARCHAR(50),
                         hq_region VARCHAR(100),
                         is_synthetic BOOLEAN DEFAULT FALSE,
                         embedding vector(768),
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast typo-tolerant search (F1)
CREATE INDEX idx_company_trgm ON company USING GIN (normalized_name gin_trgm_ops);
-- Index for fast semantic search (F1 fallback)
CREATE INDEX idx_company_embedding ON company USING hnsw (embedding vector_cosine_ops);

-- ROLE CANONICAL
CREATE TABLE role_canonical (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                name VARCHAR(255) NOT NULL,
                                family VARCHAR(100) NOT NULL, -- e.g., 'Engineering', 'Product'
                                level INT NOT NULL CHECK (level BETWEEN 1 AND 5),
                                embedding vector(768)
);
CREATE INDEX idx_role_embedding ON role_canonical USING hnsw (embedding vector_cosine_ops);

-- PERSON (No names! Only anonymous IDs and consent data)
CREATE TABLE person (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        owner_user_id UUID, -- Links to app_user if real submission, null if synthetic
                        is_synthetic BOOLEAN DEFAULT FALSE,
                        region VARCHAR(100),
                        consent_at TIMESTAMP WITH TIME ZONE,
                        deleted_at TIMESTAMP WITH TIME ZONE,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- EMPLOYMENT (The core fact table)
CREATE TABLE employment (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            person_id UUID NOT NULL REFERENCES person(id),
                            company_id UUID REFERENCES company(id), -- Nullable if unresolved
                            role_id UUID REFERENCES role_canonical(id), -- Nullable if unresolved
                            raw_company_text VARCHAR(255),
                            raw_title VARCHAR(255),
                            start_date DATE NOT NULL,
                            end_date DATE, -- Null if currently employed (right-censored)
                            employment_type VARCHAR(50) DEFAULT 'FULL_TIME',
                            resolution_status VARCHAR(50) DEFAULT 'RESOLVED', -- RESOLVED, PENDING, NEEDS_REVIEW
                            resolution_confidence FLOAT,
                            submission_id UUID, -- Links to the original user submission
                            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for fast analytics (Headcount, Hires/Exits queries)
CREATE INDEX idx_employment_company_start ON employment(company_id, start_date);
CREATE INDEX idx_employment_company_end ON employment(company_id, end_date);
CREATE INDEX idx_employment_person_start ON employment(person_id, start_date);