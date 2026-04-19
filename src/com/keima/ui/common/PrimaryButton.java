package com.keima.ui.common;

import com.keima.ui.theme.UIConfig;
import javax.swing.*;
import java.awt.*;

public class PrimaryButton extends JButton {
    public PrimaryButton(String text) {
        super(text);
        setFont(UIConfig.FONT_TITLE);
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(250, 55));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient Background
        GradientPaint grad = new GradientPaint(0, 0, UIConfig.GRADIENT_START, 0, getHeight(), UIConfig.GRADIENT_END);
        g2.setPaint(grad);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // Text centering
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(Color.WHITE);
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}