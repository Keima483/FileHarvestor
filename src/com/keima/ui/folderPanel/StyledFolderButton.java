package com.keima.ui.folderPanel;

import com.keima.ui.theme.UIConfig;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledFolderButton extends JToggleButton {

    private boolean isHovered = false;

    public StyledFolderButton(String text) {
        super(text);

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(UIConfig.FONT_TITLE);

        // Slightly larger preferred size to breathe
        setPreferredSize(new Dimension(155, 45));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(isEnabled()) { isHovered = true; repaint(); } }
            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. BACKGROUND & BORDER LOGIC
        if (!isEnabled()) {
            // LOCKED/INACTIVE STATE: Very subtle grey wash
            g2.setColor(new Color(240, 240, 245));
            g2.fillRoundRect(0, 0, w-1, h-1, UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);
            g2.setColor(new Color(210, 215, 225));
            g2.drawRoundRect(0, 0, w-1, h-1, UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);
        } else if (isSelected()) {
            // SELECTED STATE: Full vibrant gradient
            GradientPaint grad = new GradientPaint(0, 0, UIConfig.GRADIENT_START, 0, h, UIConfig.GRADIENT_END);
            g2.setPaint(grad);
            g2.fillRoundRect(0, 0, w-1, h-1, UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);
        } else {
            // DEFAULT STATE: White card
            g2.setColor(UIConfig.CARD_BG);
            g2.fillRoundRect(0, 0, w-1, h-1, UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);

            // SUBTLE HOVER: Only a light blue border/glow, not a full block
            if (isHovered) {
                g2.setColor(UIConfig.GRADIENT_START);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(0, 0, w-1, h-1, UIConfig.ROUNDING_MEDIUM, UIConfig.ROUNDING_MEDIUM);
        }

        // 2. ICON & TEXT RENDERING
        // Icon logic
        g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));

        if (!isEnabled()) {
            g2.setColor(UIConfig.TEXT_LIGHT); // Grey icon for locked
        } else if (isSelected()) {
            g2.setColor(Color.WHITE); // White icon for selected
        } else {
            g2.setColor(UIConfig.TEXT_DARK); // Standard icon
        }
        g2.drawString(UIConfig.ICON_FOLDER, 15, h/2 + 5);

        // Text Logic
        g2.setFont(UIConfig.FONT_SMALL);
        String name = getText();
        if(name.length() > 10) name = name.substring(0, 9) + "..";

        // Same color logic for text as the icon
        if (!isEnabled()) g2.setColor(UIConfig.TEXT_LIGHT);
        else if (isSelected()) g2.setColor(Color.WHITE);
        else g2.setColor(UIConfig.TEXT_DARK);

        g2.drawString(name, 40, h/2 + 5);

        g2.dispose();
    }
}