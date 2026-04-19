package com.keima.ui.theme;

import java.awt.*;

public class UIConfig {

    // --- Colors: Brand & Surface ---
    public static final Color APP_BG         = new Color(245, 246, 250); // Light grey app background
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color CARD_BORDER    = new Color(220, 225, 235);
    public static final Color TEXT_DARK      = new Color(60, 65, 75);    // Soft off-black
    public static final Color TEXT_LIGHT     = new Color(140, 145, 160); // For labels/weekdays
    public static final Color DIM_OVERLAY    = new Color(0, 0, 0, 120);  // For GlassPane
    public static final Color SCROLL_THUMB   = new Color(200, 200, 205, 180);

    // --- Colors: Gradients ---
    public static final Color GRADIENT_START = new Color(110, 168, 255); // #6EA8FF
    public static final Color GRADIENT_END   = new Color(76, 139, 245);  // #4C8BF5

    // --- Fonts ---
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BOLD       = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.BOLD, 12);

    // --- Icons (Unicode) ---
    public static final String ICON_CALENDAR = "\uD83D\uDCC5"; // 📅
    public static final String ICON_CLOCK    = "\uD83D\uDD52"; // 🕒
    public static final String ICON_FOLDER   = "\uD83D\uDCC1"; // 📁

    // --- Shapes & Dimensions ---
    public static final int ROUNDING_LARGE   = 25; // Popups & Cards
    public static final int ROUNDING_MEDIUM  = 15; // Buttons
    public static final int ROUNDING_SMALL   = 12; // Input Fields

    public static final Dimension POPUP_CALENDAR = new Dimension(320, 360);
    public static final Dimension POPUP_CLOCK    = new Dimension(320, 400);
}