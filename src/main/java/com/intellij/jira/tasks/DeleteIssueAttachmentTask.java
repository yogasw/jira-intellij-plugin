package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class DeleteIssueAttachmentTask extends AbstractBackgroundableTask {

    private String issueKey;
    private String attachmentId;

    public DeleteIssueAttachmentTask(@NotNull Project project, @NotNull String issueKey, @NotNull String attachmentId) {
        super(project, "Deleting attachment...");
        this.issueKey = issueKey;
        this.attachmentId = attachmentId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueAttachment(attachmentId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been deleted");
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
        showNotification("Jira", "Attachment deleted successfully");
    }

}
