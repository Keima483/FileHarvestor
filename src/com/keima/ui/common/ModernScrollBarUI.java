package com.keima.ui.common;

import com.keima.ui.theme.UIConfig;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void installComponents() {
        super.installComponents();
        // Replacing default arrows with invisible 0-sized buttons
        incrButton = createZeroButton();
        decrButton = createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Keeping track transparent for that modern "floating" look
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Using centralized scroll thumb color
        g2.setColor(UIConfig.SCROLL_THUMB);

        // Calculate thumb dimensions with slight padding
        int x = thumbBounds.x + 2;
        int y = thumbBounds.y + 2;
        int width = thumbBounds.width - 4;
        int height = thumbBounds.height - 4;

        // Using standard rounding for the "pill" shape
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.dispose();
    }
}