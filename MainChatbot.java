package chatbot;

import javax.swing.SwingUtilities;
public class MainChatbot {
    public static void main(String[] args) {

        FAQDatabase faq = new FAQDatabase();
        ResponseGenerator responder = new ResponseGenerator(faq);
        ChatHistory history = new ChatHistory();

        SwingUtilities.invokeLater(() -> {
            GUIChat gui = new GUIChat(history, faq, responder);
            gui.setVisible(true);
        });
    }
}

