-- For evaluating entity resolution
CREATE TABLE synthetic_truth (
                                 employment_id UUID PRIMARY KEY REFERENCES employment(id),
                                 true_company_id UUID NOT NULL REFERENCES company(id),
                                 true_role_id UUID REFERENCES role_canonical(id)
);

-- For evaluating planted patterns (exodus, boomerang, etc.)
CREATE TABLE planted_pattern (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 pattern_type VARCHAR(50) NOT NULL,
                                 company_id UUID REFERENCES company(id),
                                 params JSONB,
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);