package com.careerdb.synthetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Picks a key from a weight map using a single seeded Random.
 * Uses iteration-stable ordering via LinkedHashMap to keep the choice reproducible across JVM runs.
 */
public final class WeightedRandomSelector {

    private WeightedRandomSelector() {}

    public static <T> T pick(Map<T, Double> weights, Random random) {
        double total = 0.0;
        for (double w : weights.values()) {
            if (w < 0) throw new IllegalArgumentException("Negative weight");
            total += w;
        }
        if (total <= 0) throw new IllegalArgumentException("Total weight must be > 0");

        double r = random.nextDouble() * total;
        double cumulative = 0.0;
        T last = null;
        for (Map.Entry<T, Double> e : weights.entrySet()) {
            cumulative += e.getValue();
            last = e.getKey();
            if (r < cumulative) return e.getKey();
        }
        return last; // floating-point guard
    }

    /** Returns a copy of the map with one key's weight multiplied by a factor. */
    public static <T> Map<T, Double> boost(Map<T, Double> weights, T key, double factor) {
        Map<T, Double> copy = new java.util.LinkedHashMap<>(weights);
        copy.computeIfPresent(key, (k, v) -> v * factor);
        return copy;
    }
}