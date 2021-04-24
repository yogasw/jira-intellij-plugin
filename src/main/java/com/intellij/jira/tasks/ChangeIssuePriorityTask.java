package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.EDIT_ISSUES;

public class ChangeIssuePriorityTask extends AbstractBackgroundableTask {

    private String priorityName;

    public ChangeIssuePriorityTask(@NotNull Project project, String priorityName, String issueIdOrKey) {
        super(project, "Updating Issue Priority...", issueIdOrKey);
        this.priorityName = priorityName;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        // Check user permissions
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueIdOrKey, EDIT_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to change priority");
        }

        Result result = jiraRestApi.changeIssuePriority(priorityName, issueIdOrKey);
        if(!result.isValid()){
            throw new InvalidResultException("Error", "Issue priority has not been updated");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue priority updated");
    }

}
