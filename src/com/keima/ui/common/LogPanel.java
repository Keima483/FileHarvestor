package com.keima.ui.common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextPane logPane;

    public LogPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logPane.setBackground(new Color(20, 20, 20)); // Dark background

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void append(String type, String message) {
        SwingUtilities.invokeLater(() -> {
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