package com.keima.ui.dateTimePanel;

import com.keima.ui.theme.UIConfig;
import com.keima.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class DateField extends JPanel {

    private final JTextField field = new JTextField(10);
    private final JButton iconButton;
    private LocalDate value;
    private final MainFrame parentFrame;

    public DateField(MainFrame parent) {
        this.parentFrame = parent;
        setOpaque(false);
        setLayout(new BorderLayout());

        // 1. Style the Text Field
        field.setFont(UIConfig.FONT_REGULAR);
        field.setForeground(UIConfig.TEXT_DARK);
        field.setBorder(new EmptyBorder(5, 10, 5, 0));
        field.setOpaque(false);
        field.setBackground(new Color(0, 0, 0, 0));

        // 2. Style the Icon Button
        iconButton = new JButton(UIConfig.ICON_CALENDAR);
        iconButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        iconButton.setForeground(UIConfig.TEXT_DARK);
        iconButton.setContentAreaFilled(false);
        iconButton.setBorderPainted(false);
        iconButton.setFocusPainted(false);
        iconButton.setOpaque(false);
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
            CalendarPopup popup = new CalendarPopup(parentFrame, this);
            parentFrame.showPopup(popup);
        });

        add(container, BorderLayout.CENTER);
    }

    /**
     * Callback method used by CalendarPopup to return the selected date.
     */
    public void setDate(LocalDate pickedDate) {
        if (pickedDate != null) {
            this.value = pickedDate;
            this.field.setText(pickedDate.toString());
        }
    }

    public LocalDate getValue() {
        try {
            String text = field.getText().trim();
            return text.isEmpty() ? value : LocalDate.parse(text);
        } catch (Exception e) {
            return value;
        }
    }

    public void lock() {
        field.setEditable(false);
        field.setEnabled(false);
        iconButton.setEnabled(false);
        // Visual cue for locked state
        field.setForeground(UIConfig.TEXT_LIGHT);
    }
}