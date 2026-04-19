package com.keima.ui.dateTimePanel;

import com.keima.ui.theme.UIConfig;
import com.keima.ui.MainFrame;
import com.keima.ui.common.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;

public class ClockPopup extends JPanel {

    private int hour = 12;
    private int minute = 0;
    private final MainFrame parentFrame;
    private final TimeField targetField;

    public ClockPopup(MainFrame parent, TimeField field) {
        this.parentFrame = parent;
        this.targetField = field;

        setOpaque(false);
        setPreferredSize(UIConfig.POPUP_CLOCK);
        setLayout(new BorderLayout());

        // --- Main Container with Shadow and Rounding ---
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                // Background
                g2.setColor(UIConfig.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                // Border
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);
                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);

        // --- Header ---
        JLabel title = new JLabel("Set Time", SwingConstants.CENTER);
        title.setFont(UIConfig.FONT_TITLE);
        title.setForeground(UIConfig.TEXT_DARK);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        mainContainer.add(title, BorderLayout.NORTH);

        // --- Selectors (Hours & Minutes) ---
        JPanel selectors = new JPanel(new GridLayout(1, 2, 15, 0));
        selectors.setOpaque(false);
        selectors.setBorder(new EmptyBorder(10, 25, 20, 25));

        selectors.add(createScrollList("Hour", 0, 23, true));
        selectors.add(createScrollList("Minute", 0, 59, false));

        mainContainer.add(selectors, BorderLayout.CENTER);

        // --- Footer (Select Button) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 0, 25, 0));

        JButton selectBtn = new JButton("Select Time") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Use Gradient from Config
                GradientPaint gp = new GradientPaint(0, 0, UIConfig.GRADIENT_START, 0, getHeight(), UIConfig.GRADIENT_END);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        selectBtn.setPreferredSize(new Dimension(200, 45));
        selectBtn.setFont(UIConfig.FONT_BOLD);
        selectBtn.setContentAreaFilled(false);
        selectBtn.setBorderPainted(false);
        selectBtn.setFocusPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        selectBtn.addActionListener(e -> {
            targetField.setTime(LocalTime.of(hour, minute));
            parentFrame.hidePopup();
        });

        footer.add(selectBtn);
        mainContainer.add(footer, BorderLayout.SOUTH);

        add(mainContainer);
    }

    private JPanel createScrollList(String title, int start, int end, boolean isHour) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);

        JLabel l = new JLabel(title, SwingConstants.CENTER);
        l.setFont(UIConfig.FONT_SMALL); // Refactored to use FONT_SMALL
        l.setForeground(UIConfig.TEXT_LIGHT); // Refactored to use TEXT_LIGHT
        p.add(l, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (int i = start; i <= end; i++) {
            model.addElement(String.format("%02d", i));
        }

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Keeping specific size for readability
        list.setFixedCellHeight(45);
        list.setSelectionBackground(UIConfig.GRADIENT_START);
        list.setSelectionForeground(Color.WHITE);
        list.setBackground(Color.WHITE);

        list.setSelectedIndex(isHour ? hour : minute);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                int val = Integer.parseInt(list.getSelectedValue());
                if (isHour) hour = val; else minute = val;
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // Modern ScrollBar Implementation
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }
}