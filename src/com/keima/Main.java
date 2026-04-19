package com.keima;

import com.keima.service.AppLogger;
import com.keima.ui.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception err) {
            AppLogger.log(AppLogger.Level.ERROR, "Unable to load the theme: \"NimbusLookAndFeel\"");
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}