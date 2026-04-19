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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        setSize(1000, 750); // Slightly taller for breathing room
        setLocationRelativeTo(null);

        // --- 1. Root Setup ---
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConfig.APP_BG);
        setContentPane(root);

        // --- 2. GlassPane Setup (Dimmer & Popups) ---
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
        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentPopup != null && !currentPopup.getBounds().contains(e.getPoint())) {
                    hidePopup();
                }
            }
        });
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

        // 1. Initialize the panel
        logPanel = new LogPanel();

// 2. Add it to your GridBagLayout content panel
        gbc.gridy = 2;
        gbc.weighty = 1.0; // This makes it fill the vertical space
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 10, 0);

// Wrap in your existing card style
        JPanel logCard = createCardWrapper(logPanel, "Live Console Output");
        content.add(logCard, gbc);

// 3. Register the listener at the VERY END of the constructor
        AppLogger.addListener(evt -> {
            logPanel.append(evt.getPropertyName(), (String) evt.getNewValue());
        });

        // Process button
        processButton = new PrimaryButton("Process"); // Or your custom StyledButton
        processButton.addActionListener(e -> toggleProcessing());

        // Bottom Action Bar ---
        startButton = new PrimaryButton("Start");
        startButton.addActionListener(e -> onStart());

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 25));
        actionBar.setOpaque(false);
        actionBar.add(processButton);
        actionBar.add(startButton);

        root.add(content, BorderLayout.CENTER);
        root.add(actionBar, BorderLayout.SOUTH);

        // Window Lifecycle
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { shutdownMonitors(); }
        });

        setVisible(true);
    }

    // --- Popup / Dimmer Logic ---

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

    // --- UI Components ---

    private JPanel createCardWrapper(JPanel internalPanel, String title) {
        // 1. Main Card Panel
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(UIConfig.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                // Border
                g2.setColor(UIConfig.CARD_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConfig.ROUNDING_LARGE, UIConfig.ROUNDING_LARGE);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        // Outer padding to prevent internal components from touching the rounded border
        card.setBorder(new EmptyBorder(20, 25, 25, 25));

        // 2. Header Section (Title + Separator)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConfig.FONT_TITLE);
        titleLabel.setForeground(UIConfig.TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0)); // Space below text

        JSeparator sep = new JSeparator();
        sep.setForeground(UIConfig.CARD_BORDER);
        sep.setBackground(UIConfig.CARD_BORDER); // Ensure it's visible

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(sep, BorderLayout.SOUTH);
        header.setBorder(new EmptyBorder(0, 0, 15, 0)); // Padding between header and content

        // 3. Assemble
        card.add(header, BorderLayout.NORTH);

        // Ensure the internal panel is truly transparent
        internalPanel.setOpaque(false);
        card.add(internalPanel, BorderLayout.CENTER);

        return card;
    }

    // --- Logic ---

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