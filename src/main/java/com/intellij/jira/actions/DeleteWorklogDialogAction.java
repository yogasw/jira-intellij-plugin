package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteWorklogDialog;
import com.intellij.jira.util.factory.JiraIssuTimeTrackingFactory;
import com.intellij.jira.util.factory.JiraIssueWorklogFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class DeleteWorklogDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete Work Log",  AllIcons.General.Remove);

    private String issueKey;
    private JiraIssueWorklogFactory worklogFactory;
    private JiraIssuTimeTrackingFactory timeTrackingFactory;

    public DeleteWorklogDialogAction(String issueKey, JiraIssueWorklogFactory factory, JiraIssuTimeTrackingFactory timeTrackingFactory) {
        super(properties);
        this.issueKey = issueKey;
        this.worklogFactory = factory;
        this.timeTrackingFactory = timeTrackingFactory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssueWorklog worklogToDelete = worklogFactory.create();

        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_ALL_WORKLOGS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_OWN_WORKLOGS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Deleted Work Log failed", "You don't have permission to delete Work Logs");
            }

            if(nonNull(worklogToDelete) && !worklogToDelete.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Deleted Work Log failed", "This comment not yours. You cannot delete it");
            }
        }

        DeleteWorklogDialog dialog = new DeleteWorklogDialog(project, issueKey, worklogToDelete.getId(), timeTrackingFactory.create());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(worklogFactory.create()));
    }

}
