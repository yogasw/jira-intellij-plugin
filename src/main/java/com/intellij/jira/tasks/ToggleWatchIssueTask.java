package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.MANAGE_WATCHERS;

public class ToggleWatchIssueTask extends AbstractBackgroundableTask {

    private String issueKey;
    private boolean isWatching;

    public ToggleWatchIssueTask(@NotNull Project project, String issueKey, boolean isWatching) {
        super(project, "Editing Issue watchers...");
        this.issueKey = issueKey;
        this.isWatching = isWatching;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, MANAGE_WATCHERS);
        if(!hasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to manage watchers");
        }

        if (isWatching) {
            Result result = jiraRestApi.getCurrentUser();
            if(result.isValid()) {
                JiraIssueUser currentUser = (JiraIssueUser) result.get();
                jiraRestApi.unwatchIssue(issueKey, currentUser.getAccountId(), currentUser.getName());
            }
        } else {
            jiraRestApi.watchIssue(issueKey);
        }

        // Retrieve updated issue
        Result issueResult = jiraRestApi.getIssue(issueKey);
        if(issueResult.isValid()){
            JiraIssue issue = (JiraIssue) issueResult.get();
            // Update panels
            getJiraIssueUpdater().update(issue);
        }

    }

}
