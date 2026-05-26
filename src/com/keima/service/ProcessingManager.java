package com.keima.service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessingManager implements Runnable {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService workerPool = Executors.newFixedThreadPool(20);
    private final java.util.Set<String> processed = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private final File httpFolder;
    private final File cleanFolder;
    private final File attachFolder;

    public ProcessingManager(File http, File clean, File attach) {
        this.httpFolder = http;
        this.cleanFolder = clean;
        this.attachFolder = attach;
    }

    @Override
    public void run() {
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            File[] files = httpFolder.listFiles((dir, name) -> name.endsWith(".eml"));

            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (!running.get()) break;

                    String fileName = f.getName();

                    // Check if file tracking table already has the element
                    if (!processed.contains(fileName)) {
                        // FIX: Verify file is completely written on disk before submitting task
                        if (f.exists() && isFileReady(f)) {
                            if (processed.add(fileName)) {
                                workerPool.submit(new FileProcessorWorker(f, cleanFolder, attachFolder));
                            }
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000); // Wait for new files to arrive in the folder
            } catch (InterruptedException e) {
                break;
            }
        }
        workerPool.shutdownNow();
    }

    // FIX: Guard logic checking if file length is stable (not actively written to by a separate thread)
    private boolean isFileReady(File file) {
        long fileLengthBefore = file.length();
        if (fileLengthBefore == 0) {
            return false; // File is initialized but has empty contents
        }
        try {
            Thread.sleep(200); // Stability Window check
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return file.exists() && file.length() == fileLengthBefore;
    }

    public void stop() {
        running.set(false);
    }
}