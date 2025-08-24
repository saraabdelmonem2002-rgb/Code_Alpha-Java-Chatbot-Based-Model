package chatbot;

import javax.swing.*;
import java.awt.*;
import java.util.List;
public class GUIChat extends JFrame{
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();
    private final JButton sendBtn = new JButton("Send");
    private final JButton exportBtn = new JButton("Export FAQ");
    private final JButton clearBtn = new JButton("Clear Chat");

    private final ChatHistory history;
    private final FAQDatabase faq;
    private final ResponseGenerator responder;

    public GUIChat(ChatHistory history, FAQDatabase faq, ResponseGenerator responder) {
        super("Smart AI Chatbot");
        this.history = history;
        this.faq = faq;
        this.responder = responder;

        initUI();
        appendBot("Hello! I am a self-learning chatbot. Ask me anything.");
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 540);
        setLocationRelativeTo(null);

        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(chatArea);
        sp.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel bottom = new JPanel(new BorderLayout(8,8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        bottom.add(inputField, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.add(sendBtn);
        btns.add(exportBtn);
        btns.add(clearBtn);
        bottom.add(btns, BorderLayout.EAST);

        JLabel hint = new JLabel("Try: \"What is AI?\" or \"What is Python?\"  •  Type 'exit' to close.");
        hint.setForeground(Color.DARK_GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(6,8,0,8));

        JPanel top = new JPanel(new BorderLayout());
        top.add(hint, BorderLayout.WEST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);


        sendBtn.addActionListener(e -> processUserInput());
        inputField.addActionListener(e -> processUserInput());
        clearBtn.addActionListener(e -> {
            history.clear();
            chatArea.setText("");
        });
        exportBtn.addActionListener(e -> {
            faq.saveToFile();
            JOptionPane.showMessageDialog(this, "FAQ exported to faq.csv", "Export", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void processUserInput() {
        String user = inputField.getText().trim();
        if (user.isEmpty()) return;
        inputField.setText("");
        appendUser(user);

        if (user.equalsIgnoreCase("exit")) {
            appendBot("Goodbye!");
            faq.saveToFile();

            SwingUtilities.getWindowAncestor(this).dispose();
            return;
        }


        String answer = responder.generate(user);
        if (answer != null) {
            appendBot(answer);
            return;
        }

        int teach = JOptionPane.showConfirmDialog(this,
                "I don't know this question. Would you mind teaching me the answer?",
                "Teach me?",
                JOptionPane.YES_NO_OPTION);

        if (teach == JOptionPane.YES_OPTION) {
            String ans = JOptionPane.showInputDialog(this, "Please type the correct answer:");
            if (ans != null && !ans.trim().isEmpty()) {
                String normalizedQ = SimpleNLP.normalize(user);
                faq.addQA(normalizedQ, ans.trim());
                responder.rebuild(); // update vectors/keys
                appendBot("Thanks! I learned it.");
            } else {
                appendBot("No answer provided. Maybe next time.");
            }
        } else {
            appendBot("Alright. If you change your mind, you can teach me later.");
        }
    }

    private void appendUser(String s) {
        history.addUser(s);
        chatArea.append("You: " + s + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void appendBot(String s) {
        history.addBot(s);
        chatArea.append("Bot: " + s + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}

