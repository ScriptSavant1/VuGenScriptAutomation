# VuGenScriptAutomation

## Overview

This project automates the process of retrieving VuGen scripts from GitLab, zipping them, and uploading them to LoadRunner Enterprise (LRE). The steps are designed to ensure efficient handling of GitLab repositories and proper metadata management for each pipeline run.

## Table of Contents

1. [Requirements](#requirements)
2. [Setup Instructions](#setup-instructions)
3. [Steps Involved](#steps-involved)
4. [GitLab CI/CD Integration](#gitlab-cicd-integration)
5. [HTTP Request Handling](#http-request-handling)
6. [Metadata Management](#metadata-management)
7. [File Zipping and Uploading](#file-zipping-and-uploading)
8. [Code Structure](#code-structure)
9. [Logger Utility](#logger-utility)
10. [Error Handling](#error-handling)
11. [Cleanup](#cleanup)
12. [Future Enhancements](#future-enhancements)

## Requirements

### 1. Java 11 or higher
Ensure you're using Java 11 or above for compatibility with the `HttpClient` API.

### 2. Maven
You need Maven for dependency management and building the project. Ensure you have Maven installed.

### 3. GitLab Access
You'll need the following:
- GitLab Access Token (`GITLAB_ACCESS_TOKEN`).
- GitLab Project ID (`GITLAB_PROJECT_ID`).
- GitLab Repository URL (`GITLAB_URL`).

### 4. LRE Server Access
You must have access to the LRE (LoadRunner Enterprise) server for uploading VuGen scripts.

## Setup Instructions

### 1. Clone the Repository
Clone the repository into your local machine:

```bash
git clone <repository-url>
cd <repository-directory>
2. Configure the application.properties file
Edit the src/main/resources/application.properties file to include your GitLab and LRE credentials:

properties
Copy code
gitlab.access-token=<your-gitlab-token>
gitlab.project-id=<your-gitlab-project-id>
gitlab.repo-url=<your-gitlab-repository-url>

lre.url=<your-lre-server-url>
lre.api-token=<your-lre-api-token>
3. Build the Project
Use Maven to build the project and package the application.

bash
Copy code
mvn clean install
4. Run the Application
Run the application using the following command:

bash
Copy code
java -jar target/your-jar-file.jar
Steps Involved
1. Retrieve GitLab Projects
The application retrieves all files from a GitLab repository using the GitLab API. It traverses through the repository tree structure to locate folders containing .usr files (VuGen scripts).

2. Traverse Through Folders
The project traverses the GitLab folder structure and identifies the folders that contain .usr files, which are VuGen scripts.

3. Zip VuGen Scripts
If a .usr file is found in a folder, the folder is zipped. Each zip file is named using the pattern parentFolder_currentFolder.zip.

4. Dynamic Test Folder Path
The test folder path follows the pattern: Subject\Scripts\ParentFolder\CurrentFolder\parentFolder_CurrentFolder.zip.

5. Metadata Handling
On the first run, all VuGen scripts are zipped and uploaded to LRE, and a metadata file is created with the lastUpdatedDate. On subsequent runs, only files committed after the lastUpdatedDate are considered. These files are then zipped and uploaded to LRE.

6. Cleanup
After uploading the zip files to LRE, they are removed from the local GitLab runner to ensure no unnecessary files remain.

GitLab CI/CD Integration
To automate the upload process within a GitLab CI/CD pipeline, you need to configure a .gitlab-ci.yaml file. The GitLab runner will be responsible for invoking the Java application, triggering the automation steps to zip and upload files to LRE.

Sample .gitlab-ci.yaml Configuration:
yaml
Copy code
stages:
  - build
  - deploy

variables:
  GITLAB_PROJECT_ID: "<your-gitlab-project-id>"

build:
  stage: build
  script:
    - echo "Building Project"
    - mvn clean install

deploy:
  stage: deploy
  script:
    - java -jar target/your-jar-file.jar
Ensure that you replace the placeholder "<your-gitlab-project-id>" with the actual GitLab project ID. The pipeline will first build the project and then trigger the script for zipping and uploading files.

HTTP Request Handling
We use Java 11 HttpClient to make HTTP requests to GitLab and LRE.

GitLab API Request:
We retrieve the files from the GitLab repository using the GET request:

java
Copy code
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(gitLabApiUrl))
    .header("PRIVATE-TOKEN", Config.GITLAB_ACCESS_TOKEN)
    .GET()
    .build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
LRE API Request for Uploading Zip Files:
We upload the zipped files to LRE using the POST request:

java
Copy code
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(lreApiUrl))
    .header("PRIVATE-TOKEN", Config.LRE_API_TOKEN)
    .header("Content-Type", "multipart/form-data")
    .POST(HttpRequest.BodyPublishers.ofFile(zipFilePath))
    .build();
Metadata Management
The metadata file keeps track of the lastUpdatedDate for each GitLab project. On subsequent runs, the system checks if any files were committed after this date, and only those files are processed. The metadata is updated after each successful upload.

Metadata File Structure:
json
Copy code
{
  "lastUpdatedDate": "2022-03-01T12:00:00"
}
File Zipping and Uploading
When a folder containing .usr files is found, it is zipped and uploaded to LRE. The zip file is named using the parent and current folder names, following the pattern:

python
Copy code
parentFolder_currentFolder.zip
This ensures that each zip file corresponds to a folder in GitLab, allowing for better organization and traceability in LRE.

Code Structure
1. GitLabService.java
Handles the interaction with the GitLab API to retrieve project data.

2. LreService.java
Handles uploading the zipped files to the LRE server.

3. ZipUtil.java
Utility class responsible for zipping files or directories.

4. MetadataService.java
Manages metadata for each GitLab project, including storing and reading lastUpdatedDate.

5. LoggerUtil.java
Handles logging using a custom logger to track the application's activity.

Logger Utility
The project uses a custom LoggerUtil class to manage logging across the application. It helps track the status of the GitLab API requests, file zipping process, and LRE uploads.

Logger Example:
java
Copy code
public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String message, Exception e) {
        logger.error(message, e);
    }
}
Error Handling
The application performs extensive error handling to ensure smooth execution and provide meaningful error messages when something goes wrong, such as API failures or file system errors.

Example Error Handling:
java
Copy code
try {
    // GitLab API Call
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
} catch (IOException | InterruptedException e) {
    LoggerUtil.logError("Error while calling GitLab API", e);
    throw new RuntimeException("GitLab API call failed", e);
}
Future Enhancements
Asynchronous API Calls: Optimize API calls by making them asynchronous.
Error Retry Logic: Implement retry logic for network failures or temporary issues with GitLab or LRE.
Advanced Logging: Add more granular logging levels (debug, info, warn, error) for better traceability.
