package com.rbs.lre.gitlab.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbs.lre.gitlab.config.Config;
import com.rbs.lre.gitlab.model.GitLabItem;
import com.rbs.lre.gitlab.util.LoggerUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GitLabService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GitLabService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<GitLabItem> fetchRepositoryTree(String projectId, String branch) {
        List<GitLabItem> items = new ArrayList<>();
        try {
            String url = String.format("%s/projects/%s/repository/tree?ref=%s&recursive=true",
                    Config.GITLAB_API_URL, projectId, branch);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("PRIVATE-TOKEN", Config.GITLAB_ACCESS_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                for (JsonNode node : root) {
                    items.add(new GitLabItem(
                            node.get("name").asText(),
                            node.get("path").asText(),
                            "tree".equals(node.get("type").asText())
                    ));
                }
            } else {
                LoggerUtil.error("GitLab API error: " + response.statusCode());
            }
        } catch (Exception e) {
            LoggerUtil.error("Failed to fetch repository tree: " + e.getMessage());
        }
        return items;
    }
}

