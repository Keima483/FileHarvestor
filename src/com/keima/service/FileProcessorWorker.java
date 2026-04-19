package com.keima.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.*;
import java.util.Properties;

public class FileProcessorWorker implements Runnable {
    private final File sourceFile;
    private final File cleanFolder;
    private final File attachFolder;

    public FileProcessorWorker(File file, File clean, File attach) {
        this.sourceFile = file;
        this.cleanFolder = clean;
        this.attachFolder = attach;
    }

    @Override
    public void run() {
        try {
            Session session = Session.getInstance(System.getProperties(), null);
            try (InputStream is = new FileInputStream(sourceFile)) {
                MimeMessage message = new MimeMessage(session, is);
                processPart(message);
            }
        } catch (Exception e) {
            System.err.println("Error processing " + sourceFile.getName() + ": " + e.getMessage());
        }
    }

    private void processPart(Part part) throws Exception {
        String contentType = part.getContentType().toLowerCase();
        Object content = part.getContent();

        if (content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                processPart(mp.getBodyPart(i));
            }
        }
        // 1. JSON handling (Clean_HTTP/json)
        else if (contentType.contains("application/json")) {
            saveToSubfolder(part, cleanFolder, "json", "data.json");
        }
        // 2. Text/HTML handling (Clean_HTTP/text or Clean_HTTP/html)
        else if (part.isMimeType("text/plain")) {
            saveToSubfolder(part, cleanFolder, "text", "body.txt");
        }
        else if (part.isMimeType("text/html")) {
            saveToSubfolder(part, cleanFolder, "html", "page.html");
        }
        // 3. Greedy Extraction (Attachments/type)
        else {
            String subFolderName = getSubfolderName(part);
            String fileName = part.getFileName();
            if (fileName == null) fileName = "part_" + System.currentTimeMillis();
            saveToSubfolder(part, attachFolder, subFolderName, fileName);
        }
    }

    private void saveToSubfolder(Part part, File rootFolder, String subFolder, String fileName) throws Exception {
        File targetDir = new File(rootFolder, subFolder);
        if (!targetDir.exists()) targetDir.mkdirs();

        // 1. Clean the source filename (remove .eml so it doesn't mess up the extension)
        String baseName = sourceFile.getName().contains(".")
                ? sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.'))
                : sourceFile.getName();

        // 2. Ensure the saved file has the correct extension based on the folder it's going into
        String finalFileName = baseName + "_" + fileName;
        if (!finalFileName.toLowerCase().endsWith("." + subFolder)) {
            finalFileName += "." + subFolder;
        }

        File targetFile = new File(targetDir, finalFileName);

        try (InputStream is = part.getInputStream();
             OutputStream os = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private String getSubfolderName(Part part) throws MessagingException {
        // Takes "image/png; name=..." and returns "png"
        try {
            String ct = part.getContentType().split(";")[0].toLowerCase();
            if (ct.contains("/")) {
                return ct.split("/")[1].trim();
            }
            return "unknown";
        } catch (Exception e) {
            return "other";
        }
    }
}