package com.keima.ui.common;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextArea textArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(20, 20, 20));
        textArea.setForeground(Color.GREEN);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        // Force a minimum size so GridBagLayout doesn't squash it to 0px
        scrollPane.setPreferredSize(new Dimension(400, 250));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void append(String message) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}