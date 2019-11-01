package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddWorklogTask extends AbstractBackgroundableTask {

    private String issueKey;
    private String timeSpent;

    public AddWorklogTask(@NotNull Project project, String issueKey, String timeSpent) {
        super(project, "Adding Work Log");
        this.issueKey = issueKey;
        this.timeSpent = timeSpent;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueWorklog(issueKey, timeSpent);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been added");
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
        showNotification("Jira", "Work Log added successfully");
    }

}
