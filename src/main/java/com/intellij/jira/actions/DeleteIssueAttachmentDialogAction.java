package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteIssueAttachmentDialog;
import com.intellij.jira.util.factory.JiraIssueAttachmentFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.*;
import static java.util.Objects.nonNull;

public class DeleteIssueAttachmentDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete Attachment",  AllIcons.General.Remove);

    private String issueKey;
    private JiraIssueAttachmentFactory attachmentFactory;

    public DeleteIssueAttachmentDialogAction(@NotNull String issueKey, @NotNull JiraIssueAttachmentFactory attachmentFactory) {
        super(properties);
        this.issueKey = issueKey;
        this.attachmentFactory = attachmentFactory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, DELETE_ALL_ATTACHMENTS);
        if(!hasPermission){
            hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, DELETE_OWN_ATTACHMENTS);
            if (!hasPermission) {
                throw new InvalidPermissionException("Jira", "You don't have permission to delete attached files");
            }
        }

        DeleteIssueAttachmentDialog dialog = new DeleteIssueAttachmentDialog(project, issueKey, attachmentFactory.create().getId());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(attachmentFactory.create()));
    }

}
