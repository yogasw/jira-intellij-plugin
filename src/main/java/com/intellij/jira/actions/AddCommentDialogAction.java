package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddCommentDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddCommentDialogAction extends JiraIssueDialogAction{
    private static final ActionProperties properties = ActionProperties.of("Add a comment",  AllIcons.General.Add);

    private String projectKey;
    private String issueKey;

    public AddCommentDialogAction(String projectKey, String issueKey) {
        super(properties);
        this.projectKey = projectKey;
        this.issueKey = issueKey;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.ADD_COMMENTS);
        if(!userHasPermission){
            throw new InvalidPermissionException("Add comment failed", "You don't have permission to add comments");
        }

        List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

        AddCommentDialog commentDialog = new AddCommentDialog(project, issueKey, projectRoles);
        commentDialog.show();
    }

}
