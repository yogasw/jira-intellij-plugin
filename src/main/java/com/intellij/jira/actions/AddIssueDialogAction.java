package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.CreateIssueDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddIssueDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Create Issue",  AllIcons.General.Add);


    public AddIssueDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        boolean hasPermission = jiraRestApi.userHasPermission(JiraPermissionType.CREATE_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to create issue");
        }

        JiraIssueCreateMetadata issueCreateMeta = jiraRestApi.getIssueCreateMeta();

        CreateIssueDialog createIssueDialog = new CreateIssueDialog(project, issueCreateMeta);
        createIssueDialog.show();
    }
}
