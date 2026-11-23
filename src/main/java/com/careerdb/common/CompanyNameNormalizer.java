package com.careerdb.common;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Single source of truth for company-name normalization.
 * Used by: NameNoiseService (variant generation), entity resolution, analytics tests.
 *
 * Rules: lowercase → strip legal suffixes → collapse whitespace → remove punctuation → trim.
 * Order matters: strip suffixes before punctuation so "Pvt. Ltd." becomes "pvt ltd".
 */
public final class CompanyNameNormalizer {

    private static final List<String> LEGAL_SUFFIXES = List.of(
            "private limited", "pvt ltd", "pvt. ltd.", "pvt. ltd", "pvt ltd.",
            "limited", "ltd", "ltd.", "llp", "inc", "inc.", "corp", "corp.",
            "corporation", "& co", "& co.", "and co", "co."
    );

    private static final Pattern PUNCTUATION = Pattern.compile("[^a-z0-9 ]");
    private static final Pattern WHITESPACE  = Pattern.compile("\\s+");

    private CompanyNameNormalizer() {}

    public static String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).trim();

        // Strip suffix patterns in order of length so "private limited" wins over "limited".
        for (String suffix : LEGAL_SUFFIXES) {
            if (s.endsWith(" " + suffix)) {
                s = s.substring(0, s.length() - suffix.length() - 1).trim();
            }
        }

        s = PUNCTUATION.matcher(s).replaceAll(" ");
        s = WHITESPACE.matcher(s).replaceAll(" ").trim();
        return s;
    }
}