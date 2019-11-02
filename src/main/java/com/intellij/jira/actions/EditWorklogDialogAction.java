package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.EditWorklogDialog;
import com.intellij.jira.util.JiraIssuTimeTrackingFactory;
import com.intellij.jira.util.JiraIssueWorklogFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.nonNull;

public class EditWorklogDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Edit Work Log", AllIcons.Actions.Edit);

    private String issueKey;
    private JiraIssueWorklogFactory worklogFactory;
    private JiraIssuTimeTrackingFactory timetrackingFactory;

    public EditWorklogDialogAction(String issueKey, JiraIssueWorklogFactory factory, JiraIssuTimeTrackingFactory timetrackingFactory) {
        super(properties);
        this.issueKey = issueKey;
        this.worklogFactory = factory;
        this.timetrackingFactory = timetrackingFactory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssueWorklog worklogToEdit = jiraRestApi.getWorklog(issueKey, worklogFactory.create().getId());
        // Check permissions
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_ALL_WORKLOGS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_OWN_WORKLOGS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Edited Work Log failed", "You don't have permission to edit work logs");
            }

            if(nonNull(worklogToEdit) && !worklogToEdit.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Edited Work Log failed", "This work log not yours. You cannot edit it.");
            }
        }

        if(Objects.nonNull(worklogToEdit)){
            EditWorklogDialog dialog = new EditWorklogDialog(project, issueKey, worklogToEdit, timetrackingFactory.create(), false);
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(worklogFactory.create()));
    }

}
