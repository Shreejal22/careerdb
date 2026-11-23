CREATE TABLE company_alias (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               company_id UUID NOT NULL REFERENCES company(id),
                               alias_text VARCHAR(255) NOT NULL,
                               normalized_alias VARCHAR(255) NOT NULL,
                               source VARCHAR(20) NOT NULL,           -- GENERATOR | USER | ADMIN | LLM
                               confidence FLOAT NOT NULL DEFAULT 1.0,
                               status VARCHAR(20) NOT NULL DEFAULT 'APPROVED',  -- APPROVED | PENDING
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_company_alias_company_normalized
    ON company_alias (company_id, normalized_alias);

CREATE INDEX idx_company_alias_trgm
    ON company_alias USING GIN (normalized_alias gin_trgm_ops);