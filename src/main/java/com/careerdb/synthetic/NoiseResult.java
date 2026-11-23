package com.careerdb.synthetic;

/**
 * The result of applying noise to a company name.
 * @param rawText The messy string that gets saved in employment.raw_company_text.
 * @param noiseType The label (EXACT, KNOWN_ALIAS, HELDOUT_ALIAS, TYPO) for the ground-truth table.
 */
public record NoiseResult(String rawText, String noiseType) {}