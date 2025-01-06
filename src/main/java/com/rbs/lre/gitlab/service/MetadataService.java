package com.rbs.lre.gitlab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbs.lre.gitlab.util.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing metadata file operations.
 * Tracks last upload date for incremental processing of VuGen scripts.
 */
public class MetadataService {
    private final String metadataFilePath;
    private final ObjectMapper objectMapper;

    public MetadataService(String metadataFilePath) {
        this.metadataFilePath = metadataFilePath;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Initializes the metadata file if it does not exist.
     */
    public void initializeMetadata() {
        try {
            Path metadataPath = Paths.get(metadataFilePath);
            if (!Files.exists(metadataPath)) {
                LoggerUtil.info("Metadata file not found. Creating a new one...");
                Map<String, String> metadata = new HashMap<>();
                metadata.put("lastUpdatedDate", LocalDateTime.now().toString());
                saveMetadata(metadata);
            } else {
                LoggerUtil.info("Metadata file already exists.");
            }
        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize metadata file: " + e.getMessage());
        }
    }

    /**
     * Retrieves the last updated date from the metadata file.
     *
     * @return Last updated date as a String.
     */
    public String getLastUpdatedDate() {
        try {
            File metadataFile = new File(metadataFilePath);
            if (!metadataFile.exists()) {
                LoggerUtil.warn("Metadata file does not exist. Returning current time as last updated date.");
                return LocalDateTime.now().toString();
            }
            Map<String, String> metadata = objectMapper.readValue(metadataFile, Map.class);
            return metadata.getOrDefault("lastUpdatedDate", LocalDateTime.now().toString());
        } catch (IOException e) {
            LoggerUtil.error("Failed to read metadata file: " + e.getMessage());
            return LocalDateTime.now().toString();
        }
    }

    /**
     * Updates the last updated date in the metadata file.
     */
    public void updateLastUpdatedDate() {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("lastUpdatedDate", LocalDateTime.now().toString());
            saveMetadata(metadata);
            LoggerUtil.info("Metadata file updated with the new last updated date.");
        } catch (Exception e) {
            LoggerUtil.error("Failed to update metadata file: " + e.getMessage());
        }
    }

    /**
     * Saves metadata to the file.
     *
     * @param metadata Map containing metadata properties.
     */
    private void saveMetadata(Map<String, String> metadata) {
        try {
            objectMapper.writeValue(new File(metadataFilePath), metadata);
            LoggerUtil.info("Metadata file saved successfully.");
        } catch (IOException e) {
            LoggerUtil.error("Failed to save metadata file: " + e.getMessage());
        }
    }

    /**
     * Deletes the metadata file (if required, e.g., during cleanup).
     */
    public void deleteMetadataFile() {
        try {
            Path metadataPath = Paths.get(metadataFilePath);
            if (Files.exists(metadataPath)) {
                Files.delete(metadataPath);
                LoggerUtil.info("Metadata file deleted successfully.");
            } else {
                LoggerUtil.warn("Metadata file does not exist. No deletion performed.");
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to delete metadata file: " + e.getMessage());
        }
    }
}
