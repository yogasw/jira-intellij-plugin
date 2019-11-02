package com.intellij.jira.tasks;

import com.google.gson.JsonElement;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddWorklogTask extends AbstractBackgroundableTask {

    private String issueKey;
    private List<TransitionFieldHelper.FieldEditorInfo> worklogFields;
    private String remainingEstimate;

    public AddWorklogTask(@NotNull Project project, String issueKey, List<TransitionFieldHelper.FieldEditorInfo> worklogFields, JsonElement remainingEstimateValue) {
        super(project, "Adding Work Log");
        this.issueKey = issueKey;
        this.worklogFields = worklogFields;
        this.remainingEstimate = remainingEstimateValue.getAsString();
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueWorklog(issueKey, worklogFields, remainingEstimate);
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
