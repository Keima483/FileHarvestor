package com.keima.service;

import com.keima.config.AppConfig;
import com.keima.model.TimeRange;
import com.keima.util.DateTimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FolderMonitor implements Runnable{

    private final String folderName;
    private final LocalDate date;
    private final TimeRange timeRange;
    private final Set<Path> processed = new HashSet<>();

    private static final long SLEEP_MS = 2 * 60 * 1000; // 2 minutes

    public FolderMonitor(String folderName, LocalDate date, TimeRange timeRange) {
        this.folderName = folderName;
        this.date = date;
        this.timeRange = timeRange;
    }

    @Override
    public void run() {
        Path sourceDir = AppConfig.SOURCE_ROOT
                .resolve(folderName)
                .resolve(DateTimeUtil.formatDate(date));
        AppLogger.log(AppLogger.Level.INFO, "Thread processing: " + sourceDir + " -> Activated");

        while (!Thread.currentThread().isInterrupted()) {
            try{
                LocalTime lastFileTime = scan(sourceDir);
                AppLogger.log(AppLogger.Level.INFO, folderName + " scanned till: " + lastFileTime + ";\n└> Will wait for new files to drop..." );
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private LocalTime scan(Path sourceDir) {
        if(!Files.exists(sourceDir)) return null;
        LocalTime lastFileTime = LocalTime.of(0, 0, 0);
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for(Path file: stream) {

                Map.Entry<LocalTime, Boolean> fileDetail = getFileDetails(file);

                lastFileTime = fileDetail.getKey();
                boolean isWithTimeRange = fileDetail.getValue();

                if (Files.isRegularFile(file) && !processed.contains(file) && isWithTimeRange) {
                    process(file);
                    processed.add(file);
                }
            }
        } catch(IOException err) {
            AppLogger.log(AppLogger.Level.ERROR, err.getMessage());
        }
        return lastFileTime;
    }

    private Map.Entry<LocalTime, Boolean> getFileDetails(Path file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);

        Instant instant = attrs.lastModifiedTime().toInstant();
        LocalTime time = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        return Map.entry(time, timeRange.contains(time));
    }

    private void process(Path file) throws IOException {

        String destinationFolder = classifyFile(file.toFile());

        Path target = AppConfig.INPUT_BASE.resolve(destinationFolder).resolve(file.getFileName());
        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private String classifyFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isMultipart = false;
            boolean hasHttpMarker = false;
            int lineCount = 0;

            while ((line = reader.readLine()) != null ) {

                if (line.trim().isEmpty()) break;
                String upperLine = line.toUpperCase();

                if(lineCount < 10) {
                    if (upperLine.startsWith("GET ") || upperLine.startsWith("POST ") || upperLine.startsWith("CONNECT ") || upperLine.contains("HTTP/1")) {
                        hasHttpMarker = true;
                    }
                }

                if (upperLine.startsWith("CONTENT-TYPE:") && upperLine.contains("MULTIPART/")) {
                    isMultipart = true;
                }

                lineCount++;

                if(lineCount > 100) break;
            }

            if(hasHttpMarker) return AppConfig.PROTO_FOLDERS.HTTP.name();
            else if(isMultipart) return AppConfig.PROTO_FOLDERS.MMails.name();
            else return AppConfig.PROTO_FOLDERS.SMails.name();

        } catch (IOException exception) {
            return AppConfig.PROTO_FOLDERS.SMails.name();
        }
    }
}
