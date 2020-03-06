package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteCommentDialog;
import com.intellij.jira.util.factory.JiraIssueCommentFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class DeleteCommentDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete comment",  AllIcons.General.Remove);

    private String issueKey;
    private JiraIssueCommentFactory commentFactory;

    public DeleteCommentDialogAction(String issueKey, JiraIssueCommentFactory factory) {
        super(properties);
        this.issueKey = issueKey;
        this.commentFactory = factory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssueComment commentToDelete = commentFactory.create();

        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_ALL_COMMENTS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_OWN_COMMENTS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Deleted comment failed", "You don't have permission to delete comments");
            }

            if(nonNull(commentToDelete) && !commentToDelete.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Deleted comment failed", "This comment not yours. You cannot delete it");
            }
        }

        DeleteCommentDialog commentDialog = new DeleteCommentDialog(project, issueKey, commentToDelete.getId());
        commentDialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(commentFactory.create()));
    }

}
