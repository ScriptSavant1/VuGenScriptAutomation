package com.rbs.lre.gitlab.model;

public class GitLabItem {
    private String name;
    private String path;
    private boolean isFolder;

    public GitLabItem(String name, String path, boolean isFolder) {
        this.name = name;
        this.path = path;
        this.isFolder = isFolder;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isFolder() { return isFolder; }
}

