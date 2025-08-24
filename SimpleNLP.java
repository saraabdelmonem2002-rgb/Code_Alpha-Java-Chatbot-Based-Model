package chatbot;

import java.util.Locale;
public class SimpleNLP {
    public static String normalize(String text) {
        if (text == null) return "";
        text = text.trim().toLowerCase(Locale.ROOT);
        text = text.replaceAll("[^\\p{L}\\p{Nd}\\s]", " ");
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }
}

