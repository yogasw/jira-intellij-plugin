package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.EditCommentDialog;
import com.intellij.jira.util.JiraIssueCommentFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class EditCommentDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Edit comment", AllIcons.Actions.Edit);

    private String projectKey;
    private String issueKey;
    private JiraIssueCommentFactory commentFactory;

    public EditCommentDialogAction(String projectKey, String issueKey, JiraIssueCommentFactory factory) {
        super(properties);
        this.projectKey = projectKey;
        this.issueKey = issueKey;
        this.commentFactory = factory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssueComment commentToEdit = null;
        // Check permissions
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_ALL_COMMENTS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_OWN_COMMENTS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Edited comment failed", "You don't have permission to edit comments");
            }

            commentToEdit = jiraRestApi.getComment(issueKey, commentFactory.create().getId());
            if(nonNull(commentToEdit) && !commentToEdit.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Edited comment failed", "This comment not yours. You cannot edit it.");
            }
        }

        if(Objects.nonNull(commentToEdit)){
            List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

            EditCommentDialog dialog = new EditCommentDialog(project, issueKey, projectRoles, commentToEdit);
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(commentFactory.create()));
    }

}
