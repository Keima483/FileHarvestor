package com.keima.ui.dateTimePanel;

import com.keima.ui.theme.UIConfig;
import com.keima.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarPopup extends JPanel {

    private LocalDate selected;
    private YearMonth viewingMonth;
    private final JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final MainFrame parentFrame;
    private final DateField targetField;

    public CalendarPopup(MainFrame parent, DateField field) {
        this.parentFrame = parent;
        this.targetField = field;
        this.viewingMonth = YearMonth.now();

        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(UIConfig.POPUP_CALENDAR); // Use config dimension

        // Main Container with Rounded Edges and Shadow
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                // Draw Background
                g2.setColor(UIConfig.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                // Draw Border
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                g2.dispose();
            }
        };
        mainContainer.setOpaque(false);

        setupUI(mainContainer);
        add(mainContainer);
        refresh();
    }

    private void setupUI(JPanel container) {
        // --- 1. Header (Month Navigation) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 15, 10, 15));

        JButton prevBtn = createNavButton("<");
        JButton nextBtn = createNavButton(">");

        monthLabel.setFont(UIConfig.FONT_TITLE);
        monthLabel.setForeground(UIConfig.TEXT_DARK);

        prevBtn.addActionListener(e -> { viewingMonth = viewingMonth.minusMonths(1); refresh(); });
        nextBtn.addActionListener(e -> { viewingMonth = viewingMonth.plusMonths(1); refresh(); });

        header.add(prevBtn, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(nextBtn, BorderLayout.EAST);

        // --- 2. Calendar Body (Grid) ---
        grid.setOpaque(false);
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 15, 15, 15));
        body.add(grid, BorderLayout.CENTER);

        container.add(header, BorderLayout.NORTH);
        container.add(body, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monospaced", Font.BOLD, 20)); // Keep monospaced for arrows
        b.setForeground(UIConfig.TEXT_DARK);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void refresh() {
        grid.removeAll();
        monthLabel.setText(viewingMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + viewingMonth.getYear());

        // Calculate leading gaps
        LocalDate first = viewingMonth.atDay(1);
        int gap = first.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < gap; i++) grid.add(new JLabel(""));

        // Build days
        for (int d = 1; d <= viewingMonth.lengthOfMonth(); d++) {
            final int day = d;
            JButton b = new JButton(String.valueOf(d));

            b.setFont(UIConfig.FONT_REGULAR);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorder(new EmptyBorder(8, 8, 8, 8));

            // Highlight Today
            if (viewingMonth.equals(YearMonth.now()) && d == LocalDate.now().getDayOfMonth()) {
                b.setForeground(UIConfig.GRADIENT_END);
                b.setFont(UIConfig.FONT_BOLD);
            } else {
                b.setForeground(UIConfig.TEXT_DARK);
            }

            b.addActionListener(e -> {
                targetField.setDate(LocalDate.of(viewingMonth.getYear(), viewingMonth.getMonth(), day));
                parentFrame.hidePopup();
            });
            grid.add(b);
        }
        grid.revalidate();
        grid.repaint();
    }
}