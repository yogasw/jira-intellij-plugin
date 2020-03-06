package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteIssueLinkDialog;
import com.intellij.jira.util.factory.JiraIssueLinkFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.LINK_ISSUES;
import static java.util.Objects.nonNull;

public class DeleteIssueLinkDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Delete link",  AllIcons.General.Remove);

    private String issueKey;
    private JiraIssueLinkFactory issueLink;

    public DeleteIssueLinkDialogAction(String issueKey, JiraIssueLinkFactory issueLink) {
        super(properties);
        this.issueKey = issueKey;
        this.issueLink = issueLink;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, LINK_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to delete an issue link");
        }

        DeleteIssueLinkDialog dialog = new DeleteIssueLinkDialog(project, issueKey, issueLink.create().getId());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
       e.getPresentation().setEnabled(nonNull(issueLink.create()));
    }

}
