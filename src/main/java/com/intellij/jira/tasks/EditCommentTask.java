package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.Result;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.jira.rest.model.JiraPermissionType.EDIT_OWN_COMMENTS;

public class EditCommentTask extends AbstractBackgroundableTask {

    private String issueKey;
    private String commentId;
    private String body;
    private String viewableBy;

    public EditCommentTask(@Nullable Project project, String issueKey, String commentId, String body, String viewableBy) {
        super(project, "Editing comment");
        this.issueKey = issueKey;
        this.commentId = commentId;
        this.body = body;
        this.viewableBy = viewableBy;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        // Check user permissions
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, EDIT_OWN_COMMENTS);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to edit comments");
        }

        Result result = jiraRestApi.editIssueComment(issueKey, commentId, body, viewableBy);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been updated");
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
        showNotification("Jira", "Comment edited successfully");
    }

}
