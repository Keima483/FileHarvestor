package com.keima.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    public enum Level { INFO, ERROR }

    // Ensure this is truly static and initialized
    private static final PropertyChangeSupport support = new PropertyChangeSupport(AppLogger.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(Level level, String message) {
        String timestamp = LocalTime.now().format(timeFormatter);
        String formattedMessage = String.format("[%s] %s: %s", timestamp, level, message);

        // Use firePropertyChange carefully.
        // We pass the Level name as the propertyName to match your listener.
        support.firePropertyChange(level.name(), null, formattedMessage);
    }

    public static void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}