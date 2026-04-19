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
                    if(processed.add(fileName)) {
                        workerPool.submit(new FileProcessorWorker(f, cleanFolder, attachFolder));
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

    public void stop() {
        running.set(false);
    }
}