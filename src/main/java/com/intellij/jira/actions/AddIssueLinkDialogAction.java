package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddIssueLinkDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.LINK_ISSUES;
import static java.util.stream.Collectors.toList;

public class AddIssueLinkDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("New Link",  AllIcons.General.Add);

    private String projectKey;
    private String issueKey;

    public AddIssueLinkDialogAction(String projectKey, String issueKey) {
        super(properties);
        this.projectKey = projectKey;
        this.issueKey = issueKey;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, LINK_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to create issue links");
        }

        List<JiraIssueLinkType> issueLinkTypes = jiraRestApi.getIssueLinkTypes();
        List<String> issues = jiraRestApi.getIssues("project=" + projectKey).stream().map(JiraIssue::getKey).collect(toList());
        issues.remove(issueKey);

        openIssueLinkDialog(project, issueLinkTypes, issues);
    }

    public void openIssueLinkDialog(Project project, List<JiraIssueLinkType> issueLinkTypes, List<String> issues) {
        AddIssueLinkDialog dialog = new AddIssueLinkDialog(project, issueLinkTypes, issues, issueKey);
        dialog.show();
    }

}
