package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.dialog.EditCommentDialog;
import com.intellij.jira.util.JiraIssueCommentFactory;
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

        JiraIssueComment comment = jiraServer.getComment(issueKey, commentFactory.create().getId());
        if(Objects.nonNull(comment)){
            List<String> projectRoles = jiraServer.getProjectRoles(projectKey);

            EditCommentDialog dialog = new EditCommentDialog(project, issueKey, projectRoles, comment);
            dialog.show();
        }

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(commentFactory.create()));
    }

}
