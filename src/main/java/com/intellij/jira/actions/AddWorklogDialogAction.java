package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddWorklogDialog;
import com.intellij.jira.util.JiraIssuTimeTrackingFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddWorklogDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Add Log Work",  AllIcons.General.Add);

    private String issueKey;
    private JiraIssuTimeTrackingFactory timetrackingFactory;

    public AddWorklogDialogAction(String issueKey, JiraIssuTimeTrackingFactory timetrackingFactory) {
        super(properties);
        this.issueKey = issueKey;
        this.timetrackingFactory = timetrackingFactory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, JiraPermissionType.WORK_ON_ISSUES);
        if(!userHasPermission){
            throw new InvalidPermissionException("Add log work failed", "You don't have permission to log work");
        }

        AddWorklogDialog dialog = new AddWorklogDialog(project, issueKey, timetrackingFactory.create());
        dialog.show();
    }
}
