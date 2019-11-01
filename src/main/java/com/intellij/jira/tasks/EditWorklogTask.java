package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class EditWorklogTask extends AbstractBackgroundableTask {

    private String issueKey;
    private String workLogId;
    private String timeSpent;

    public EditWorklogTask(@NotNull Project project, String issueKey, String workLogId, String timeSpent) {
        super(project, "Editing Work Log");
        this.issueKey = issueKey;
        this.workLogId = workLogId;
        this.timeSpent = timeSpent;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.editIssueWorklog(issueKey, workLogId, timeSpent);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been edited");
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
        showNotification("Jira", "Work Log edited successfully");
    }

}
