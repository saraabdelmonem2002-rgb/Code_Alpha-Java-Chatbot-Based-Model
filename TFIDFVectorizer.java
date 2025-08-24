package chatbot;

import java.util.*;
public class TFIDFVectorizer {
    private final List<String> docs = new ArrayList<>();
    private final Map<String, Integer> vocab = new LinkedHashMap<>();
    private double[] idf;

    public void fit(Collection<String> texts) {
        docs.clear();
        docs.addAll(texts);

        List<List<String>> tokenized = new ArrayList<>();
        Set<String> all = new LinkedHashSet<>();
        for (String d : docs) {
            List<String> toks = tokenize(d);
            tokenized.add(toks);
            all.addAll(toks);
        }
        int idx = 0;
        for (String t : all) vocab.put(t, idx++);
        int V = vocab.size();
        int N = docs.size();
        int[] df = new int[V];
        for (List<String> toks : tokenized) {
            Set<Integer> seen = new HashSet<>();
            for (String t : toks) {
                Integer i = vocab.get(t);
                if (i != null && seen.add(i)) df[i]++;
            }
        }
        idf = new double[V];
        for (int i = 0; i < V; i++) {
            idf[i] = Math.log((N + 1.0) / (df[i] + 1.0)) + 1.0;
        }
    }

    public double[] transform(String doc) {
        int V = vocab.size();
        double[] tfidf = new double[V];
        if (V == 0) return tfidf;
        List<String> toks = tokenize(doc);
        for (String t : toks) {
            Integer i = vocab.get(t);
            if (i != null) tfidf[i] += 1.0;
        }
        double sum = 0;
        for (double v : tfidf) sum += v;
        if (sum == 0) return tfidf;
        for (int i = 0; i < V; i++) tfidf[i] = (tfidf[i] / sum) * idf[i];
        return tfidf;
    }

    public static double cosine(double[] a, double[] b) {
        if (a == null || b == null) return 0;
        double dot = 0, na = 0, nb = 0;
        int L = Math.min(a.length, b.length);
        for (int i = 0; i < L; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    private List<String> tokenize(String s) {
        s = SimpleNLP.normalize(s);
        if (s.isEmpty()) return Collections.emptyList();
        String[] parts = s.split("\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {

            String t = p;
            if (t.endsWith("ing") && t.length() > 4) t = t.substring(0, t.length()-3);
            if (t.endsWith("ed") && t.length() > 3) t = t.substring(0, t.length()-2);
            if (t.endsWith("s") && t.length() > 2) t = t.substring(0, t.length()-1);
            out.add(t);
        }
        return out;
    }
}

