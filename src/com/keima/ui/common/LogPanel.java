package com.keima.ui.common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextPane logPane;

    public LogPanel() {
        setLayout(new BorderLayout());
        // 1. The main container must be white and opaque
        setOpaque(true);
        setBackground(Color.WHITE);

        logPane = new JTextPane();
        logPane.setEditable(false);
        // 2. The TextPane MUST be opaque to show its own white background
        logPane.setOpaque(true);
        logPane.setBackground(Color.WHITE);
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setBorder(null);

        // 3. THE FIX: The Viewport must be TRANSPARENT
        // This allows the JTextPane (which is opaque) to be the "Master" of the background
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // 4. Apply your ModernScrollBarUI
        scrollPane.getVerticalScrollBar().setUI(new com.keima.ui.common.ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        // Ensure the scrollbar track doesn't turn grey
        scrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setOpaque(true);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void append(String type, String message) {
        SwingUtilities.invokeLater(() -> {
            Color infoColor = new Color(0, 100, 0); // Visible on white
            Color errorColor = new Color(180, 0, 0); // Visible on white
            Color color = "INFO".equals(type) ? infoColor : errorColor;
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