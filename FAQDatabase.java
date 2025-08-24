package chatbot;

import java.io.*;
import java.util.*;
public class FAQDatabase {
    private final Map<String, String> faqMap = new LinkedHashMap<>();
    private final File file = new File("faq.csv");

    public FAQDatabase() {
        loadDefaults();
        loadFromFile();
    }

    private void loadDefaults() {
        faqMap.put("hello", "Hi there! How can I help you today?");
        faqMap.put("How are you", "I'm just a bot, but I'm doing great! Thanks for asking.");
        faqMap.put("what is ai", "AI stands for Artificial Intelligence. It's the simulation of human intelligence in machines.");
        faqMap.put("what is your name", "I'm your friendly AI Chatbot.");
        faqMap.put("bye", "Goodbye! Have a great day!");
        faqMap.put("what is python", "Python is a versatile programming language that is easy to learn and widely used for web development, data analysis, artificial intelligence, and automation.");
    }

    private void loadFromFile() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {


                String[] parts = splitCsvLine(line);
                if (parts.length >= 2) {
                    String q = normalize(parts[0]);
                    String a = parts[1];
                    if (!q.isEmpty() && !a.isEmpty()) faqMap.put(q, a);
                }
            }
        } catch (IOException ignored) {}
    }

    private String[] splitCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                cols.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        cols.add(sb.toString());
        return cols.toArray(new String[0]);
    }

    public synchronized void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> e : faqMap.entrySet()) {
                pw.println(escapeCsv(e.getKey()) + "," + escapeCsv(e.getValue()));
            }
        } catch (IOException ignored) {}
    }

    private String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    public synchronized String getExactMatch(String question) {
        return faqMap.get(question);
    }

    public synchronized void addQA(String question, String answer) {
        faqMap.put(question, answer);
        saveToFile();
    }

    public synchronized Set<String> keys() {
        return new LinkedHashSet<>(faqMap.keySet());
    }

    private String normalize(String s) {
        return s == null ? "" : s.toLowerCase().trim();
    }
}

