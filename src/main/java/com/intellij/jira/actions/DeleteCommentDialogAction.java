package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.DeleteCommentDialog;
import com.intellij.jira.util.JiraIssueCommentFactory;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DeleteCommentDialogAction extends JiraIssueAction {

    private static final ActionProperties properties = ActionProperties.of("Delete comment",  AllIcons.General.Remove);

    private String issueKey;
    private JiraIssueCommentFactory commentFactory;

    public DeleteCommentDialogAction(String issueKey, JiraIssueCommentFactory factory) {
        super(properties);
        this.issueKey = issueKey;
        this.commentFactory = factory;
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

        JiraIssueComment commentToEdit = null;
        // Check permissions
        boolean userHasPermission = jiraServer.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_ALL_COMMENTS);
        if(!userHasPermission){
            userHasPermission = jiraServer.userHasPermissionOnIssue(issueKey, JiraPermissionType.DELETE_OWN_COMMENTS);
            if(!userHasPermission){
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Deleted comment failed", "You don't have permission to delete comments."));
                return;
            }

            commentToEdit = jiraServer.getComment(issueKey, commentFactory.create().getId());
            if(nonNull(commentToEdit) && !commentToEdit.getAuthor().getName().equals(jiraServer.getUsername())){
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Deleted comment failed", "This comment not yours. You cannot delete it."));
                return;
            }
        }

        DeleteCommentDialog commentDialog = new DeleteCommentDialog(project, issueKey, commentFactory.create().getId());
        commentDialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(commentFactory.create()));
    }
}
