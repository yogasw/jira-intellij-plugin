package com.intellij.jira.actions;

import com.intellij.icons.AllIcons;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.popup.JiraIssueAssignableUsersPopup;
import com.intellij.jira.util.factory.JiraIssueFactory;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.nonNull;

public class JiraIssueAssigneePopupAction extends JiraIssuePopupAction {
    private static final ActionProperties properties = ActionProperties.of("Assign",  AllIcons.General.User);

    private JiraIssueFactory issueFactory;

    public JiraIssueAssigneePopupAction(JiraIssueFactory factory) {
        super(properties);
        this.issueFactory = factory;
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssue issue = issueFactory.create();
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issue.getKey(), JiraPermissionType.EDIT_ISSUES);
        if(!userHasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to change asignment");
        }

        List<JiraIssueUser> assignableUsers = jiraRestApi.getAssignableUsers(issueFactory.create().getKey());
        JiraIssueAssignableUsersPopup popup = new JiraIssueAssignableUsersPopup(createActionGroup(assignableUsers), project);
        popup.showInCenterOf(getComponent());
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(issueFactory.create()));
    }

    private ActionGroup createActionGroup(List<JiraIssueUser> assignableUsers){
        JiraIssueActionGroup group = new JiraIssueActionGroup(getComponent());
        assignableUsers.forEach(u -> group.add(JiraIssueAssignmentExecuteAction.assignUser(u.getDisplayName(), u.getAccountId(), u.getName(), issueFactory.create().getKey())));
        group.add(JiraIssueAssignmentExecuteAction.assignAnyone(issueFactory.create().getKey()));

        return group;
    }

}
