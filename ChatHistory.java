package chatbot;

import java.util.ArrayList;
import java.util.List;
public class ChatHistory {
    private final List<String> history = new ArrayList<>();

    public void addUser(String text) {
        history.add("You: " + text);
    }

    public void addBot(String text) {
        history.add("Bot: " + text);
    }

    public List<String> all() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }
}

