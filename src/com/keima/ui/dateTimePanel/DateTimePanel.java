package com.keima.ui.dateTimePanel;

import com.keima.ui.theme.UIConfig;
import com.keima.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class DateTimePanel extends JPanel {

    public final DateField dateField;
    public final TimeField startField;
    public final TimeField endField;

    public DateTimePanel(JFrame parent) {
        // Casting parent to MainFrame once for cleaner constructor calls
        MainFrame frame = (MainFrame) parent;

        dateField = new DateField(frame);
        startField = new TimeField(frame);
        endField = new TimeField(frame);

        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Standardized Label Alignment
        c.anchor = GridBagConstraints.WEST;

        // --- Date Section ---
        JLabel dateLabel = new JLabel("Date");
        styleLabel(dateLabel);
        c.gridx = 0; c.insets = new Insets(5, 0, 5, 14);
        add(dateLabel, c);

        c.gridx = 1; c.insets = new Insets(5, 0, 5, 25);
        add(dateField, c);

        // --- Start Time Section ---
        JLabel startLabel = new JLabel("Start");
        styleLabel(startLabel);
        c.gridx = 2; c.insets = new Insets(5, 0, 5, 14);
        add(startLabel, c);

        c.gridx = 3; c.insets = new Insets(5, 0, 5, 25);
        add(startField, c);

        // --- End Time Section ---
        JLabel endLabel = new JLabel("End");
        styleLabel(endLabel);
        c.gridx = 4; c.insets = new Insets(5, 0, 5, 14);
        add(endLabel, c);

        c.gridx = 5; c.insets = new Insets(5, 0, 5, 0); // No right inset on last element
        add(endField, c);

        // --- Layout Spacers ---
        c.weightx = 1.0;
        c.gridx = 6;
        add(Box.createHorizontalGlue(), c);
    }

    /**
     * Helper to apply consistent styling to labels from UIConfig
     */
    private void styleLabel(JLabel label) {
        label.setFont(UIConfig.FONT_BOLD); // Using 15pt Bold from config
        label.setForeground(UIConfig.TEXT_DARK);
    }

    public void lockAll() {
        dateField.lock();
        startField.lock();
        endField.lock();
    }
}