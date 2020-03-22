package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AddIssueAttachmentTask extends AbstractBackgroundableTask {

    private String issueKey;
    private File attachment;

    public AddIssueAttachmentTask(@NotNull Project project, @NotNull String issueKey, @NotNull File attachment) {
        super(project, "Adding issue attachment...");
        this.issueKey = issueKey;
        this.attachment = attachment;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueAttachment(issueKey, attachment);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue attachment has not been added");
        }

        // Retrieve updated issue
        Result issueResult = jiraRestApi.getIssue(issueKey);
        if(issueResult.isValid()){
            JiraIssue issue = (JiraIssue) issueResult.get();
            // Update panels
            getJiraIssueUpdater().update(issue);
        }

    }

    @Override
    public void onSuccess() {
        showNotification("Jira", "Issue attachment added successfully");
    }

}
