package com.rbs.lre.gitlab.service;

import com.rbs.lre.gitlab.config.Config;
import com.rbs.lre.gitlab.util.LoggerUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class LREUploadService {
    private final HttpClient httpClient;

    public LREUploadService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void uploadToLRE(String zipFilePath) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.LRE_API_URL))
                    .header("Content-Type", "application/zip")
                    .POST(HttpRequest.BodyPublishers.ofFile(Path.of(zipFilePath)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                LoggerUtil.info("Uploaded to LRE successfully!");
            } else {
                LoggerUtil.error("Failed to upload. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            LoggerUtil.error("LRE upload failed: " + e.getMessage());
        }
    }
}
