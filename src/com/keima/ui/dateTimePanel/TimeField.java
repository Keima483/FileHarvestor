package com.keima.ui.dateTimePanel;

import com.keima.ui.theme.UIConfig;
import com.keima.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;

public class TimeField extends JPanel {

    private final JTextField field = new JTextField(8);
    private final JButton iconButton;
    private LocalTime value;
    private final MainFrame parentFrame;

    public TimeField(MainFrame parent) {
        this.parentFrame = parent;
        setOpaque(false);
        setLayout(new BorderLayout());

        // 1. Style the Text Field
        field.setFont(UIConfig.FONT_REGULAR);
        field.setForeground(UIConfig.TEXT_DARK);
        field.setBorder(new EmptyBorder(5, 10, 5, 0));
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));

        // 2. Style the Icon Button (Clock Icon)
        iconButton = new JButton(UIConfig.ICON_CLOCK);
        iconButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        iconButton.setForeground(UIConfig.TEXT_DARK);
        iconButton.setContentAreaFilled(false);
        iconButton.setBorderPainted(false);
        iconButton.setFocusPainted(false);
        iconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        iconButton.setBorder(new EmptyBorder(0, 5, 0, 8));

        // 3. The Rounded Container
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(UIConfig.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_SMALL, UIConfig.ROUNDING_SMALL);

                // Border
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_SMALL, UIConfig.ROUNDING_SMALL);

                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.add(field, BorderLayout.CENTER);
        container.add(iconButton, BorderLayout.EAST);

        // 4. Trigger the Overlay
        iconButton.addActionListener(e -> {
            if (!iconButton.isEnabled()) return;
            parentFrame.setupDimmer();
            ClockPopup popup = new ClockPopup(parentFrame, this);
            parentFrame.showPopup(popup);
        });

        add(container, BorderLayout.CENTER);
    }

    /**
     * Callback method used by ClockPopup to return the selected time.
     */
    public void setTime(LocalTime pickedTime) {
        if (pickedTime != null) {
            this.value = pickedTime;
            // Format to HH:mm for the UI
            this.field.setText(String.format("%02d:%02d", pickedTime.getHour(), pickedTime.getMinute()));
        }
    }

    public LocalTime getValue() {
        try {
            String text = field.getText().trim();
            return text.isEmpty() ? value : LocalTime.parse(text);
        } catch (Exception e) {
            return value;
        }
    }

    public void lock() {
        field.setEditable(false);
        field.setEnabled(false);
        iconButton.setEnabled(false);
        // Visual cue: Gray out the text
        field.setForeground(UIConfig.TEXT_LIGHT);
    }
}