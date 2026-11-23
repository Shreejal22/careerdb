package com.careerdb.synthetic;

import com.careerdb.company.Company;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class NameNoiseService {

    private final GeneratorProperties props;
    private final Random random;


    public NameNoiseService(GeneratorProperties props) {
        this.props = props;
        long seed = props.getRandomSeed() ^ 0x517CC1B7L;
        this.random = new Random(seed);
    }

    public NoiseResult applyNoise(Company company) {
        boolean isTypo = random.nextDouble() < props.getNoise().getTypoRate();
        boolean isAlias = random.nextDouble() < props.getNoise().getAliasRate();

        if (isTypo) {
            return new NoiseResult(makeTypo(company.getCanonicalName()), "TYPO");
        }

        if (isAlias) {
            return pickAlias(company);
        }

        return new NoiseResult(company.getCanonicalName(), "EXACT");
    }

    private NoiseResult pickAlias(Company company) {
        String archetype = company.getIndustry();

        boolean useHeldout = random.nextDouble() < 0.20;

        Map<String, List<String>> variantMap = useHeldout
                ? props.getNoise().getHeldoutVariants()
                : props.getNoise().getKnownVariants();

        List<String> variants = variantMap.get(archetype);

        if (variants == null || variants.isEmpty()) {
            return new NoiseResult(company.getCanonicalName(), "EXACT");
        }

        String chosenVariant = variants.get(random.nextInt(variants.size()));
        String rawText = chosenVariant.replace("{name}", company.getCanonicalName());
        String label = useHeldout ? "HELDOUT_ALIAS" : "KNOWN_ALIAS";

        return new NoiseResult(rawText, label);
    }

    private String makeTypo(String s) {
        if (s.length() < 3) return s;

        for (int attempt = 0; attempt < 5; attempt++) {
            int kind = random.nextInt(3);
            String result = switch (kind) {
                case 0 -> swapAdjacent(s);
                case 1 -> dropLetter(s);
                case 2 -> doubleLetter(s);
                default -> s;
            };

            if (!result.equals(s)) {
                return result;
            }
        }
        return s + (char)('a' + random.nextInt(26));
    }

    private String swapAdjacent(String s) {
        int pos = random.nextInt(s.length() - 1);
        char[] chars = s.toCharArray();
        char temp = chars[pos];
        chars[pos] = chars[pos + 1];
        chars[pos + 1] = temp;
        return new String(chars);
    }

    private String dropLetter(String s) {
        int pos = random.nextInt(s.length());
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    private String doubleLetter(String s) {
        int pos = random.nextInt(s.length());
        char c = s.charAt(pos);
        return s.substring(0, pos) + c + c + s.substring(pos);
    }
}