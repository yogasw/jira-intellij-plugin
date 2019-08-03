package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.EditCommentDialog;
import com.intellij.jira.util.JiraIssueCommentFactory;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class EditCommentDialogAction extends JiraIssueAction {
    private static final ActionProperties properties = ActionProperties.of("Edit comment", AllIcons.Actions.Edit);

    private String projectKey;
    private String issueKey;
    private JiraIssueCommentFactory commentFactory;

    public EditCommentDialogAction(String projectKey, String issueKey, JiraIssueCommentFactory factory) {
        super(properties);
        this.projectKey = projectKey;
        this.issueKey = issueKey;
        this.commentFactory = factory;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
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
        boolean userHasPermission = jiraServer.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_ALL_COMMENTS);
        if(!userHasPermission){
            userHasPermission = jiraServer.userHasPermissionOnIssue(issueKey, JiraPermissionType.EDIT_OWN_COMMENTS);
            if(!userHasPermission){
                // msg: no tienes permisos para editar ningún comentario
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Edited comment failed", "You don't have permission to edit comments."));
                return;
            }

            commentToEdit = jiraServer.getComment(issueKey, commentFactory.create().getId());
            if(nonNull(commentToEdit) && !commentToEdit.getAuthor().getName().equals(jiraServer.getUsername())){
               // elcomentario existe pero no lo has creado tu asique no puedes editarlo
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Edited comment failed", "This comment not yours. You cannot edit it."));
                return;
            }
        }

        if(Objects.nonNull(commentToEdit)){
            List<String> projectRoles = jiraServer.getProjectRoles(projectKey);

            EditCommentDialog dialog = new EditCommentDialog(project, issueKey, projectRoles, commentToEdit);
            dialog.show();
        }

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(commentFactory.create()));
    }

}
