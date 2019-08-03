package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.AddCommentDialog;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.util.List;

import static java.util.Objects.isNull;

public class AddCommentDialogAction extends JiraIssueAction{
    private static final ActionProperties properties = ActionProperties.of("Add a comment",  AllIcons.General.Add);

    private String projectKey;
    private String issueKey;

    public AddCommentDialogAction(String projectKey, String issueKey) {
        super(properties);
        this.projectKey = projectKey;
        this.issueKey = issueKey;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        JiraServerManager jiraServerManager = project.getComponent(JiraServerManager.class);
        JiraRestApi jiraServer = jiraServerManager.getJiraRestApi();
        if(isNull(jiraServer)){
            return;
        }

        boolean userHasPermission = jiraServer.userHasPermissionOnIssue(issueKey, JiraPermissionType.ADD_COMMENTS);
        if(!userHasPermission){
            Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Add comment failed", "You don't have permission to add comments."));
            return;
        }

        List<String> projectRoles = jiraServer.getProjectRoles(projectKey);

        AddCommentDialog commentDialog = new AddCommentDialog(project, issueKey, projectRoles);
        commentDialog.show();
    }

}
