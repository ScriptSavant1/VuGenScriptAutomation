package com.rbs.lre.gitlab.service;

import org.slf4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {
    public String zipFolder(String folderPath, String zipPath) {
        Logger LoggerUtil = null;
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            Path folder = Paths.get(folderPath);
            Files.walk(folder).forEach(path -> {
                try {
                    ZipEntry zipEntry = new ZipEntry(folder.relativize(path).toString());
                    zos.putNextEntry(zipEntry);
                    if (Files.isRegularFile(path)) {
                        Files.copy(path, zos);
                    }
                    zos.closeEntry();
                } catch (IOException e) {
                    LoggerUtil.error("Error while zipping: " + e.getMessage());
                }
            });
            return zipPath;
        } catch (IOException e) {
            LoggerUtil.error("Failed to create zip: " + e.getMessage());
        }
        return null;
    }
}

