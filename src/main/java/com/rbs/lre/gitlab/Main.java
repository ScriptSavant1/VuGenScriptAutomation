package com.rbs.lre.gitlab;

import com.rbs.lre.gitlab.config.Config;
import com.rbs.lre.gitlab.model.GitLabItem;
import com.rbs.lre.gitlab.service.GitLabService;
import com.rbs.lre.gitlab.service.LREUploadService;
import com.rbs.lre.gitlab.service.ZipService;
import com.rbs.lre.gitlab.util.LoggerUtil;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        GitLabService gitLabService = new GitLabService();
        ZipService zipService = new ZipService();
        LREUploadService lreUploadService = new LREUploadService();

        List<GitLabItem> items = gitLabService.fetchRepositoryTree("project_id", "main");

        for (GitLabItem item : items) {
            if (item.getName().endsWith(".usr")) {
                String zipPath = zipService.zipFolder(item.getPath(), Config.RUNNER_ROOT + item.getName() + ".zip");
                if (zipPath != null) {
                    lreUploadService.uploadToLRE(zipPath);
                }
            }
        }
        LoggerUtil.info("Pipeline completed successfully!");
    }
}
