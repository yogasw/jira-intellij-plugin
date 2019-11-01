package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class DeleteWorklogTask extends AbstractBackgroundableTask {
    private String issueKey;
    private String worklogId;

    public DeleteWorklogTask(@NotNull Project project, String issueKey, String worklogId) {
        super(project, "Deleting work log...");
        this.issueKey = issueKey;
        this.worklogId = worklogId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueWorklog(issueKey, worklogId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been deleted");
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
        showNotification("Jira", "Work Log deleted successfully");
    }

}
