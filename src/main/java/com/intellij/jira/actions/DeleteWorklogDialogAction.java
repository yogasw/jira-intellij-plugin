package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteWorklogDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class DeleteWorklogDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete Work Log",  AllIcons.General.Remove);

    public DeleteWorklogDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        JiraIssueWorklog worklogToDelete = e.getRequiredData(JiraDataKeys.ISSUE_WORKLOG);

        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_ALL_WORKLOGS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_OWN_WORKLOGS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Delete Work Log Failed", "You don't have permission to delete Work Logs");
            }

            if(!worklogToDelete.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Delete Work Log Failed", "This comment not yours. You cannot delete it");
            }
        }

        JiraIssueTimeTracking issueTimeTracking = e.getRequiredData(JiraDataKeys.ISSUE_TIME_TRACKING);
        DeleteWorklogDialog dialog = new DeleteWorklogDialog(project, issueKey, worklogToDelete.getId(), issueTimeTracking);
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_WORKLOG)));
    }

}
