package com.keima.ui;

import com.keima.config.AppConfig;
//import com.keima.service.ProcessingManager;
import com.keima.service.AppLogger;
import com.keima.service.ProcessingManager;
import com.keima.ui.common.LogPanel;
import com.keima.ui.common.PrimaryButton;
import com.keima.ui.theme.UIConfig;
import com.keima.model.TimeRange;
import com.keima.service.FolderMonitor;
import com.keima.ui.dateTimePanel.DateTimePanel;
import com.keima.ui.folderPanel.FolderButtonPanel;
import com.keima.ui.folderPanel.StyledFolderButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    private final FolderButtonPanel folderPanel;
    private final DateTimePanel dateTimePanel;
    private final JButton startButton;
    private final JPanel glassPane;
    private final LogPanel logPanel;

    private ProcessingManager currentManager = null;
    private Thread managerThread = null;
    private JButton processButton;

    private final Map<String, Thread> runningMonitors = new HashMap<>();
    private boolean parametersLocked = false;
    private LocalDate lockedDate;
    private TimeRange lockedTimeRange;
    private JPanel currentPopup = null;

    public MainFrame() {
        super("FileHarvestor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 850); // Increased height to accommodate the console comfortably
        setLocationRelativeTo(null);

        // --- 1. Root Setup ---
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConfig.APP_BG);
        setContentPane(root);

        // --- 2. GlassPane Setup ---
        glassPane = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(UIConfig.DIM_OVERLAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        glassPane.setOpaque(false);
        glassPane.setVisible(false);
        setGlassPane(glassPane);

        // --- 3. Content Layout ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(25, 25, 10, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Folders Section
        folderPanel = new FolderButtonPanel(AppConfig.SOURCE_FOLDERS);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        content.add(createCardWrapper(folderPanel, "Select Folders"), gbc);

        // Date/Time Section
        dateTimePanel = new DateTimePanel(this);
        gbc.gridy = 1;
        content.add(createCardWrapper(dateTimePanel, "Date & Time Configuration"), gbc);

        // --- 4. THE LIVE CONSOLE FIX ---
        this.logPanel = new LogPanel();
        // Force the panel to have a white background internally
        this.logPanel.setBackground(Color.WHITE);

        JPanel logCard = createCardWrapper(this.logPanel, "Live Console Output");

        GridBagConstraints gbcLog = new GridBagConstraints();
        gbcLog.gridx = 0;
        gbcLog.gridy = 2;
        gbcLog.weightx = 1.0;
        gbcLog.weighty = 1.0; // Consumes remaining space
        gbcLog.fill = GridBagConstraints.BOTH;
        gbcLog.insets = new Insets(5, 0, 10, 0);

        content.add(logCard, gbcLog);

        // --- 5. Action Bar ---
        processButton = new PrimaryButton("Process");
        processButton.addActionListener(e -> toggleProcessing());

        startButton = new PrimaryButton("Start");
        startButton.addActionListener(e -> onStart());

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 25));
        actionBar.setOpaque(false);
        actionBar.add(processButton);
        actionBar.add(startButton);

        root.add(content, BorderLayout.CENTER);
        root.add(actionBar, BorderLayout.SOUTH);

        // Register the listener AFTER components are added to the layout
        AppLogger.addListener(evt -> {
            if (this.logPanel != null) {
                this.logPanel.append(evt.getPropertyName(), (String) evt.getNewValue());
            }
        });

        setVisible(true);
    }

    private JPanel createCardWrapper(JPanel internalPanel, String title) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConfig.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConfig.FONT_TITLE);
        titleLabel.setForeground(UIConfig.TEXT_DARK);

        JSeparator sep = new JSeparator();
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(sep, BorderLayout.SOUTH);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        card.add(header, BorderLayout.NORTH);
        if (!(internalPanel instanceof LogPanel)) {
            internalPanel.setOpaque(false);
        } else {
            internalPanel.setOpaque(true); // Ensure the white background is drawn
        }
        card.add(internalPanel, BorderLayout.CENTER);

        return card;
    }
    // --- Logic ---

    public void setupDimmer() {
        glassPane.setVisible(true);
    }

    public void showPopup(JPanel popupPanel) {
        this.currentPopup = popupPanel;
        glassPane.removeAll();

        int x = (getWidth() - popupPanel.getPreferredSize().width) / 2;
        int y = (getHeight() - popupPanel.getPreferredSize().height) / 2;
        popupPanel.setBounds(x, y, popupPanel.getPreferredSize().width, popupPanel.getPreferredSize().height);

        glassPane.add(popupPanel);
        glassPane.repaint();
        glassPane.revalidate();
    }

    public void hidePopup() {
        glassPane.setVisible(false);
        glassPane.removeAll();
        currentPopup = null;
    }

    private void createSupportFolders() throws IOException {
        for(var folder: AppConfig.PROTO_FOLDERS.values()) {
            Files.createDirectories(AppConfig.INPUT_BASE.resolve(folder.name()));
        }
    }

    private void onStart() {
        try {
            // Make folders for processing
            createSupportFolders();

            if (!parametersLocked) {
                lockedDate = dateTimePanel.dateField.getValue();
                LocalTime start = dateTimePanel.startField.getValue();
                LocalTime end = dateTimePanel.endField.getValue();

                if (lockedDate == null || start == null || end == null) {
                    throw new Exception("Please ensure Date, Start, and End times are set.");
                }

                lockedTimeRange = new TimeRange(start, end);
                dateTimePanel.lockAll();
                parametersLocked = true;
            }

            List<StyledFolderButton> selectedButtons = folderPanel.getSelected();
            if (selectedButtons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select at least one folder.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (StyledFolderButton btn : selectedButtons) {
                // Remove icon prefix for backend logic
                String folderName = btn.getText().replace(UIConfig.ICON_FOLDER + "  ", "");

                FolderMonitor monitor = new FolderMonitor(folderName, lockedDate, lockedTimeRange);
                Thread thread = new Thread(monitor, "Harvester-" + folderName);

                runningMonitors.put(folderName, thread);
                thread.start();

                btn.setEnabled(false);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleProcessing() {
        if (currentManager == null) {
            // START
            File httpDir = new File(AppConfig.INPUT_BASE.toFile(), AppConfig.PROTO_FOLDERS.HTTP.name());
            File cleanDir = new File(AppConfig.OUTPUT_BASE.toFile(), "Clean_HTTP");
            File attachDir = new File(AppConfig.OUTPUT_BASE.toFile(), "Attachments");

            cleanDir.mkdirs();
            attachDir.mkdirs();

            currentManager = new ProcessingManager(httpDir, cleanDir, attachDir);
            managerThread = new Thread(currentManager, "Manager-Thread");
            managerThread.start();

            processButton.setText("Pause");
            processButton.setBackground(Color.ORANGE); // Visual cue
        } else {
            // PAUSE/STOP
            currentManager.stop();
            managerThread.interrupt();
            currentManager = null;

            processButton.setText("Process");
            processButton.setBackground(UIConfig.GRADIENT_START);
        }
    }

    private void shutdownMonitors() {
        for(var item: runningMonitors.entrySet()) {
            item.getValue().interrupt();
//            AppLogger.log(AppLogger.Level.ERROR, item.getKey() + " Interrupted");
        }
        runningMonitors.clear();
    }
}