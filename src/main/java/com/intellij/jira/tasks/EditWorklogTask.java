package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditWorklogTask extends AbstractBackgroundableTask {

    private String issueKey;
    private String workLogId;
    private List<TransitionFieldHelper.FieldEditorInfo> worklogFields;

    public EditWorklogTask(@NotNull Project project, String issueKey, String workLogId, List<TransitionFieldHelper.FieldEditorInfo> worklogFields) {
        super(project, "Editing Work Log");
        this.issueKey = issueKey;
        this.workLogId = workLogId;
        this.worklogFields = worklogFields;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.editIssueWorklog(issueKey, workLogId, worklogFields);
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
