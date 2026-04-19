package com.keima.ui.common;

import com.keima.ui.theme.UIConfig;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextPane logPane;

    public LogPanel() {
        setLayout(new BorderLayout());
        setOpaque(false); // Crucial: let the CardWrapper background show through

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logPane.setBackground(new Color(20, 20, 20)); // Match your dark console look

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setBorder(null); // Remove double borders inside the card
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void append(String type, String message) {
        SwingUtilities.invokeLater(() -> {
            // Use your established Matrix Green for INFO and Red for ERROR
            Color color = type.equals("INFO") ? new Color(0, 255, 65) : new Color(255, 80, 80);
            appendToPane(message + "\n", color);
        });
    }

    private void appendToPane(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        int len = logPane.getDocument().getLength();
        logPane.setSelectionStart(len);
        logPane.setSelectionEnd(len);
        logPane.setCharacterAttributes(aset, false);
        logPane.replaceSelection(msg);
        logPane.setCaretPosition(logPane.getDocument().getLength());
    }
}