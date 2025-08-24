package chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResponseGenerator {
    private final FAQDatabase kb;
    private final TFIDFVectorizer vec = new TFIDFVectorizer();
    private double[] faqVectors;
    private String[] faqKeys;
    private final double THRESHOLD = 0.30; // similarity threshold

    public ResponseGenerator(FAQDatabase kb) {
        this.kb = kb;
        rebuild();
    }

    public synchronized void rebuild() {
        // prepare vectorizer on normalized keys
        Set<String> keys = kb.keys();
        List<String> docs = new ArrayList<>(keys);
        vec.fit(docs);
        faqKeys = docs.toArray(new String[0]);
        faqVectors = new double[faqKeys.length];
    }

    public synchronized String generate(String userInput) {
        String normalized = SimpleNLP.normalize(userInput);

        for (String key : kb.keys()) {
            if (normalized.contains(key)) {
                String ans = kb.getExactMatch(key);
                if (ans != null) return ans;
            }
        }

        if (faqKeys.length == 0) return null;
        double[] qv = vec.transform(normalized);
        int bestIdx = -1;
        double bestSim = 0;
        for (int i = 0; i < faqKeys.length; i++) {
            double[] fv = vec.transform(faqKeys[i]);
            double sim = TFIDFVectorizer.cosine(qv, fv);
            if (sim > bestSim) {
                bestSim = sim;
                bestIdx = i;
            }
        }
        if (bestIdx >= 0 && bestSim >= THRESHOLD) {
            return kb.getExactMatch(faqKeys[bestIdx]);
        }
        return null;
    }
}

