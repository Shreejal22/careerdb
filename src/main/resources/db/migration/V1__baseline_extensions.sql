-- Enable required extensions for search and AI embeddings
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create a throwaway table to verify that the 'vector' data type works
CREATE TABLE IF NOT EXISTS _extension_check (
                                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    embedding vector(768)
    );

-- Drop it immediately so it doesn't clutter the schema
DROP TABLE _extension_check;