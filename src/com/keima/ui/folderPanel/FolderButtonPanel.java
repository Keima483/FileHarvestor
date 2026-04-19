package com.keima.ui.folderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FolderButtonPanel extends JPanel {

    private final List<StyledFolderButton> buttons = new ArrayList<>();

    public FolderButtonPanel(String[] folders) {
        setOpaque(false);
        // Using a 5-column grid with 15px gaps as per your design
        setLayout(new GridLayout(0, 5, 15, 15));
        setBorder(null);

        for (String folder : folders) {
            StyledFolderButton btn = new StyledFolderButton(folder);
            buttons.add(btn);
            add(btn);
        }
    }

    public List<StyledFolderButton> getSelected() {
        List<StyledFolderButton> selected = new ArrayList<>();
        for (StyledFolderButton b : buttons) {
            // Only harvest from folders that are both enabled and toggled 'on'
            if (b.isEnabled() && b.isSelected()) {
                selected.add(b);
            }
        }
        return selected;
    }

    public void lockAll() {
        for (StyledFolderButton btn : buttons) {
            btn.setEnabled(false);
        }
    }
}